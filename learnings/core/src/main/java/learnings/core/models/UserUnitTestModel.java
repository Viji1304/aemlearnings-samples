package learnings.core.models;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UserUnitTestModel {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private final String LINK_EXTENSION = ".html";
	private final String WF_GROUP_ID = "workflow-users";

	@ValueMapValue
	private String pageTitle;

	@ValueMapValue
	private String pagePath;

	@Inject
	private Page currentPage;

	public String getPageTitle() {
		return pageTitle;
	}

	public String getPagePath() {
		return pagePath + LINK_EXTENSION;
	}

	@Self
	private SlingHttpServletRequest slingRequest;

	@PostConstruct
	protected void init() {

		/* For Page Title */
		if (null == pageTitle || StringUtils.isEmpty(pageTitle)) {
			pageTitle = currentPage.getTitle();
		}

		/* For Page Path */
		if (null == pagePath || StringUtils.isEmpty(pagePath)) {
			pagePath = currentPage.getPath();
		}

		/* User related */
		User aemUser = slingRequest.getResourceResolver().adaptTo(User.class);
		if (aemUser.isAdmin()) {
			try {
				LOG.info("{} is an admin user", aemUser.getID());
			} catch (RepositoryException e) {
				LOG.error("Repository Exception={}", e.getMessage());
			}
		} else {
			try {
				Iterator<Group> aemUserGrp = aemUser.memberOf();
				while (aemUserGrp.hasNext()) {
					Group userGrp = aemUserGrp.next();					
					if (userGrp.getID().equals(WF_GROUP_ID)) {
						LOG.info("WF User group is available for the user={}", aemUser.getID());
					}
				}
			} catch (RepositoryException e) {
				LOG.error("Repository Exception={}", e.getMessage());
			}

		}
	}

}
