package learnings.core.serviceimpl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import learnings.core.services.OSGIFactoryConfigDemo;
import learnings.core.servicesconfig.OSGIFactoryConfigOCD;

@Component(service=OSGIFactoryConfigDemo.class,immediate=true,configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd=OSGIFactoryConfigOCD.class, factory=true)
public class OSGiFactoryConfigDemoImpl implements OSGIFactoryConfigDemo {
	
	private String apiEndPoint;
	private String apiKey;
	private String siteName;

	@Override
	public String getAPIEndpoint() {
		return apiEndPoint;
	}

	@Override
	public String getAPIKey() {		
		return apiKey;
	}

	@Override
	public String getSiteName() {		
		return siteName;
	}
	
	@Activate
	protected void activate(OSGIFactoryConfigOCD config) {
		this.apiEndPoint = config.apiEndpoint();
		this.apiKey = config.apiKey();
		this.siteName = config.siteNameIdentifier();
		
	}

}
