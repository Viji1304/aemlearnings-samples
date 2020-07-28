package learnings.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learnings.core.services.SampleOSGIService;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/unittestservlet" })
public class UnitTestServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 3831757055797538382L;
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	String apiEndpointDomain = null;
	String siteIdentifier = null;
	private SampleOSGIService sampleOSGIService;

	@Reference(service = SampleOSGIService.class, cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "unsetSampleOSGIService")
	protected void setSampleOSGIService(SampleOSGIService sampleOSGIService) {
		this.sampleOSGIService = sampleOSGIService;
	}

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
		resp.setContentType("application/json");
		siteIdentifier = req.getParameter("siteName");
		String siteNameInConfig = sampleOSGIService.getSiteName();
		if (null != siteNameInConfig) {
			if (siteIdentifier.equalsIgnoreCase(siteNameInConfig)) {
				apiEndpointDomain = sampleOSGIService.getAPIEndpoint();
				String responseStr = callToAPI();
				if (null != responseStr) {
					try {
						resp.getWriter().write("Response String from Servlet=" + responseStr);
					} catch (IOException e) {
						LOG.error("IO exception={}", e.getMessage());
					}
				}
			}
		}

	}

	private String callToAPI() {
		CloseableHttpResponse response = null;
		String responseStr = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(apiEndpointDomain + "/get" + "?siteName=" + siteIdentifier);		
		try {
			response = httpClient.execute(request);
			responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (ClientProtocolException e) {
			LOG.error("Exception={}", e.getMessage());
		} catch (IOException e) {
			LOG.error("Exception= {}", e.getMessage());
		}

		return responseStr;
	}

}
