package learnings.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import learnings.core.serviceimpl.SampleOSGIServiceImpl;
import learnings.core.services.SampleOSGIService;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class UnitTestServletTest {

	private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

	private SampleOSGIService mockSampleOSGIService;

	private Map<String, String> configProps = new HashMap<String, String>();

	UnitTestServlet testServlet = new UnitTestServlet();

	private Map<String, Object> parameterMap = new HashMap<String, Object>();

	@BeforeEach
	void setUp() throws Exception {

		configProps.put("apiEndpoint", "https://postman-echo.com");
		configProps.put("apiKey", "XYZ");
		configProps.put("siteName", "aemlearnings");
		mockSampleOSGIService = aemContext.registerInjectActivateService(new SampleOSGIServiceImpl(), configProps);
	}

	@Test
	void testDoGetSlingHttpServletRequestSlingHttpServletResponse() {
		UnitTestServlet unitTestServletObj = new UnitTestServlet();
		MockSlingHttpServletRequest mockSlingRequest = aemContext.request();
		MockSlingHttpServletResponse mockSlingResponse = aemContext.response();
		parameterMap.put("siteName", "aemlearnings");
		parameterMap.put("osgiService", mockSampleOSGIService);
		mockSlingRequest.setParameterMap(parameterMap);

		unitTestServletObj.setSampleOSGIService(mockSampleOSGIService);
		if (null != unitTestServletObj && null != mockSlingRequest && null != mockSlingResponse) {
			unitTestServletObj.doGet(mockSlingRequest, mockSlingResponse);
		}

		/* Checking content Type */
		assertEquals("application/json", mockSlingResponse.getContentType());

		/* Checking response status code */
		assertEquals(200, mockSlingResponse.getStatus());

		/* Checking output of servlet - Positive case */
		assertTrue(mockSlingResponse.getOutputAsString().contains("aemlearnings"));

		/* Checking output of servlet - Negative case */
		assertFalse(mockSlingResponse.getOutputAsString().contains("dummystring")); // string value other than response
																					// string

		/* Other common logic part of servlet */

	}

}
