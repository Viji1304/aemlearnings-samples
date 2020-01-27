package learnings.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

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

import learnings.core.services.OSGIFactoryConfigDemo;
import learnings.core.servicesconfig.OSGIFactoryConfigOCD;

@Component(service=Servlet.class, property={
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/osgifactorydemo"
})
public class OSGIFactoryConfigDemoServlet extends SlingSafeMethodsServlet{	
	
	private static final long serialVersionUID = 1L;
	private Map<String,OSGIFactoryConfigDemo> configMap;	
	
	@Reference(name = "osgiFactoryConfigDemo", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected synchronized void bindOSGIFactoryConfigDemo(final OSGIFactoryConfigDemo config) {       
        if (configMap == null) {
        	configMap = new HashMap<>();
         }
        configMap.put(config.getSiteName(), config);
        }
   
    protected synchronized void unbindOSGIFactoryConfigDemo(final OSGIFactoryConfigDemo config) {
       
        configMap.remove(config.getSiteName());
    }
	
    @Reference(target="(siteNameIdentifier=demosite)")
    private OSGIFactoryConfigDemo osgiDemo;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) {
		
		String siteIdentifier = req.getParameter("siteidentifier");
		
		try {
			OSGIFactoryConfigDemo configValues = configMap.get(siteIdentifier);
			String apiEndpoint = configValues.getAPIEndpoint();
			String apiKey = configValues.getAPIKey();			
			log.info("Config values of identifier={} are {}, {}", siteIdentifier, apiEndpoint, apiKey);			
			resp.getWriter().write("Factory config objects count="+configMap.size());
			resp.getWriter().write("Specific instance of a factory config="+osgiDemo.getAPIEndpoint());
		} catch (IOException e) {
			log.error("IOException");
		}
	}

}
