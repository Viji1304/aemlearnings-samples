package learnings.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

import javax.script.Bindings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.scripting.WCMBindingsConstants;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class SampleWCMUsePojoTest {

	private final AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
	private final String RESC_MOCK_JSON = "/learnings/core/models/SampleWCMUsePojo.json";
	private final String CMP_CONTENT_PATH = "/content/learnings/en/wcmusepojo-unit-test-demo/jcr:content/root/responsivegrid/samplewcmusepojo";
	private final String PAGE_MOCK_JSON = "/learnings/core/models/SampleWCMUsePojoPageCnt.json";
	private final String PAGE_CONTENT_PATH = "/content/learnings/en/sample";
	SampleWCMUsePojo pojoObj;
	private final String LINK_EXTENSION = ".html";

	@Mock
	private Bindings mockBindings;

	/**
	 * Loads component content resource and Page resource to aemContext object.
	 * Instantiate WCMUsePojo class under test.
	 */
	@BeforeEach
	void setUp() throws Exception {
		/* Component resource Mock */
		aemContext.load().json(RESC_MOCK_JSON, CMP_CONTENT_PATH);

		/* Page resource Mock */
		aemContext.load().json(PAGE_MOCK_JSON, PAGE_CONTENT_PATH);

		/* Instantiate POJO */
		pojoObj = new SampleWCMUsePojo();

	}

	/**
	 * Get Title from Page Object Scenario to test title if it is not authored in
	 * dialog
	 */
	@Test
	void testGetPageTitleFromPage() {

		Resource pageResc = aemContext.currentResource(PAGE_CONTENT_PATH);
		Page pageObj = pageResc.adaptTo(Page.class);

		/* Dummy implementation for bindings - Starts */
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_PROPERTIES)).thenReturn(pageResc.getValueMap());
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_CURRENT_PAGE)).thenReturn(pageObj);
		/* Dummy implementation for bindings - Ends */

		/*
		 * Calling init(Binding bindings) of Pojo class to initialize java object with
		 * current Bindings.
		 */
		pojoObj.init(mockBindings);

		/* Below calls the method under Test */
		String actualTitle = pojoObj.getPageTitle();

		String expectedTitle = "WCMUsePojo Unit Test Demo";
		assertEquals(expectedTitle, actualTitle);
	}

	/**
	 * Get Title from component content Resc / value authored in dialog Scenario to
	 * test title if it is authored in dialog.
	 */
	@Test
	void testGetPageTitleFromResc() {
		Resource cmpResc = aemContext.currentResource(CMP_CONTENT_PATH);
		ValueMap cmpRescValues = cmpResc.getValueMap();
		/* Dummy implementation for bindings - Starts */
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_PROPERTIES)).thenReturn(cmpResc.getValueMap());
		/* Dummy implementation for bindings - Ends */
		pojoObj.init(mockBindings);
		String actualTitle = pojoObj.getPageTitle();
		String expectedTitle = "Sample Title from dialog";
		assertEquals(expectedTitle, actualTitle);
	}

	/**
	 * Get path from Page Object Scenario to test path if it is not authored in
	 * dialog
	 */
	@Test
	void testGetPagePathFromResc() {
		Resource cmpResc = aemContext.currentResource(CMP_CONTENT_PATH);
		ValueMap cmpRescValues = cmpResc.getValueMap();
		/* Dummy implementation for bindings - Starts */
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_PROPERTIES)).thenReturn(cmpResc.getValueMap());
		/* Dummy implementation for bindings - Ends */
		pojoObj.init(mockBindings);
		String actualPath = pojoObj.getPagePath();
		String expectedPath = "/content/learnings" + LINK_EXTENSION;
		assertEquals(expectedPath, actualPath);
	}

	/**
	 * Get path from component content Resc / value authored in dialog Scenario to
	 * test path if it is authored in dialog.
	 */
	@Test
	void testGetPagePathFromPage() {
		Resource pageResc = aemContext.currentResource(PAGE_CONTENT_PATH);
		Page pageObj = pageResc.adaptTo(Page.class);
		/* Dummy implementation for bindings - Starts */
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_PROPERTIES)).thenReturn(pageResc.getValueMap());
		lenient().when(mockBindings.get(WCMBindingsConstants.NAME_CURRENT_PAGE)).thenReturn(pageObj);
		/* Dummy implementation for bindings - Ends */
		pojoObj.init(mockBindings);
		String actualPath = pojoObj.getPagePath();
		String expectedPath = PAGE_CONTENT_PATH + LINK_EXTENSION;
		assertEquals(expectedPath, actualPath);
	}
}
