package learnings.core.models;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

public class SampleWCMUsePojo extends WCMUsePojo {

	private String pageTitle;

	private String pagePath;

	private final String LINK_EXTENSION = ".html";

	@Override
	public void activate() throws Exception {

		/* If title from dialog is null, then get it from Page Title */
		pageTitle = getProperties().get("pageTitle", String.class);
		if (null == pageTitle || StringUtils.isEmpty(pageTitle)) {
			pageTitle = getCurrentPage().getTitle();
		}

		/* If path from dialog is null, then get it from Page Path */
		pagePath = getProperties().get("pagePath", String.class);
		if (null == pagePath || StringUtils.isEmpty(pagePath)) {
			pagePath = getCurrentPage().getPath();
		}
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public String getPagePath() {
		return pagePath + LINK_EXTENSION;
	}

}
