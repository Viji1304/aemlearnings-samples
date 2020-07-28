package learnings.core.serviceimpl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import learnings.core.services.SampleOSGIService;
import learnings.core.servicesconfig.SampleOSGIServiceConfigOCD;

@Component(service = SampleOSGIService.class, immediate = true)
@Designate(ocd = SampleOSGIServiceConfigOCD.class)
public class SampleOSGIServiceImpl implements SampleOSGIService {

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
	protected void activate(SampleOSGIServiceConfigOCD config) {
		this.apiEndPoint = config.apiEndpoint();
		this.apiKey = config.apiKey();
		this.siteName = config.siteName();

	}

}
