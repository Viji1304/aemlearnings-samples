package learnings.core.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class UserUnitTestModelTest {

	private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
	private ArrayList<Group> grpsList = new ArrayList<Group>();
	private final String GROUP_TO_CHECK = "workflow-users";

	@Mock
	private User mockUser;

	@Mock
	private UserManager mockUserMgr;

	@Mock
	private Group mockGrp;

	@BeforeEach
	void setUp() throws Exception {
		aemContext.registerAdapter(ResourceResolver.class, User.class, mockUser);
		aemContext.registerAdapter(ResourceResolver.class, Group.class, mockGrp);
		aemContext.registerAdapter(ResourceResolver.class, UserManager.class, mockUserMgr);
	}

	@Test
	void testForAdminUser() {
		lenient().when(mockUser.isAdmin()).thenReturn(true);
		boolean adminFlag = mockUser.isAdmin();
		assertTrue(adminFlag);

	}

	@Test
	void testForNonAdminUser() throws RepositoryException {
		lenient().when(mockUserMgr.createGroup(any(String.class))).thenReturn(mockGrp);
		boolean grpToCheckExists = false;
		Iterator<Group> userGrps = addGrpsList().iterator();
		lenient().when(mockUser.isAdmin()).thenReturn(false);
		lenient().when(mockUser.memberOf()).thenReturn(userGrps);
		lenient().when(mockGrp.getID()).thenReturn(GROUP_TO_CHECK);

		boolean adminFlag = mockUser.isAdmin();
		if (!adminFlag) {

			Iterator<Group> userGrpsItr = mockUser.memberOf();
			while (userGrpsItr.hasNext()) {
				mockGrp = userGrpsItr.next();
				if (mockGrp.getID().equals(GROUP_TO_CHECK)) {
					grpToCheckExists = true;
				}
			}

		}
		assertTrue(grpToCheckExists);
	}

	private ArrayList<Group> addGrpsList() throws AuthorizableExistsException, RepositoryException {
		grpsList.add(mockUserMgr.createGroup("workflow-users"));
		grpsList.add(mockUserMgr.createGroup("contributor"));
		grpsList.add(mockUserMgr.createGroup("everyone"));
		grpsList.add(mockUserMgr.createGroup("projects-users"));
		return grpsList;
	}

}
