package learnings.core.servicesconfig;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="OCD for Factory Config Demo", description="OCD for Factory config demo")
public @interface OSGIFactoryConfigOCD {
	
	@AttributeDefinition(name="Endpoint URL", description="Sample Endpoint URL for connecting to third party", type=AttributeType.STRING)
	String apiEndpoint() default "http://xyz.com";
	
	@AttributeDefinition(name="API Key", description="Sample API key for connecting to third party", type=AttributeType.STRING)
	String apiKey() default "XYZ";
	
	@AttributeDefinition(name="SiteName", description="Sample Site name used as an identifier for respective factory config", type=AttributeType.STRING)
	String siteNameIdentifier() default "demosite";
	

}
