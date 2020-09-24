package learnings.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * @author viji
 * Servlet for full text search of content from Page and from CF used in the Page 
 */
@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/fulltextsearch" })
public class FullTextSearchServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private static final String HTML_EXTENSION = ".html";
	private static final String SEARCH_DAM_ROOT = "/content/dam/learnings/contentfragments";
	private static final String SEARCH_PAGE_ROOT = "/content/learnings";

	Map<String, String> finalResultMap = new HashMap<String, String>();

	@Reference
	private QueryBuilder queryBuilder;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
		resp.setContentType("text/html");
		Map<String, String> predicatesMap = new HashMap<String, String>();

		ResourceResolver rescResolver = req.getResourceResolver();
		Session session = rescResolver.adaptTo(Session.class);
		PageManager pageMgr = rescResolver.adaptTo(PageManager.class);
		String searchParam = req.getParameter("searchParam");

		/* Query predicates for fulltext search first query - Starts */
		if (null != searchParam) {
			predicatesMap.put("3_group.1_group.path", SEARCH_DAM_ROOT);
			predicatesMap.put("3_group.1_group.type", "dam:Asset");
			predicatesMap.put("3_group.2_group.path", SEARCH_PAGE_ROOT);
			predicatesMap.put("3_group.2_group.type", "cq:PageContent");
			predicatesMap.put("fulltext", searchParam);
			predicatesMap.put("3_group.p.or", "true");
			predicatesMap.put("p.limit", "-1");
		}
		/* Query predicates for fulltext search first query - Ends */

		Query query = queryBuilder.createQuery(PredicateGroup.create(predicatesMap), session);
		SearchResult queryResults = query.getResult();
		LOG.info("Total number of results of first query={}", queryResults.getTotalMatches());
		try {
			resp.getWriter().write("<html><body>");

			/* Getting Results - Starts */
			for (Hit hit : queryResults.getHits()) {

				String resultPath = hit.getPath();
				LOG.info("Result Path ={}", resultPath);
				/*
				 * Handling content fragments in result set and execute second query to get its
				 * containing Page - Starts
				 */
				if (resultPath.contains(SEARCH_DAM_ROOT)) {
					executeSecondQuery(resultPath, session, queryBuilder, pageMgr);
				}
				/*
				 * Handling content fragments in result set and execute second query to get its
				 * containing Page - Ends
				 */
				else {
					Page page = pageMgr.getPage(resultPath);
					if (null != page) {
						String pageTitle = page.getTitle();
						String pagePath = page.getPath() + HTML_EXTENSION;
						finalResultMap.put(pageTitle, pagePath);
					}
				}

			}
			/* Getting Results - Ends */
			LOG.info("FinalResults Map={}", finalResultMap.size());

			/* Final result display to response - Starts */
			resp.getWriter().write("<h2>Total number of Search results=" + finalResultMap.size() + "</h2>");
			for (Map.Entry<String, String> entry : finalResultMap.entrySet()) {
				resp.getWriter().write("<a href='" + entry.getValue() + "'>" + entry.getKey() + "</a><br>");
			}
			resp.getWriter().write("</body></html>");
			/* Final result display to response - Ends */

		} catch (IOException e) {
			LOG.error("IO Exception={}", e.getMessage());
		} catch (RepositoryException e) {
			LOG.error("Repository Exception={}", e.getMessage());
		}

	}

	private void executeSecondQuery(String resultPath, Session session, QueryBuilder queryBuilder,
			PageManager pageMgr) {
		Map<String, String> predicatesMap = new HashMap<String, String>();
		/* Query predicates for second query - Starts */
		if (null != resultPath) {
			predicatesMap.put("path", SEARCH_PAGE_ROOT);
			predicatesMap.put("type", "nt:unstructured");
			/* Can add content fragment component specific to project as an additional check */
			/*predicatesMap.put("1_property", "sling:resourceType");
			predicatesMap.put("1_property.value", "learnings/components/content/contentfragment");*/
			predicatesMap.put("2_property", "fragmentPath");
			predicatesMap.put("2_property.value", resultPath);
			predicatesMap.put("p.limit", "-1");
		}
		/* Query predicates for second query - Ends */
		Query query = queryBuilder.createQuery(PredicateGroup.create(predicatesMap), session);
		SearchResult queryResults = query.getResult();
		LOG.info("Total number of results of second query={}", queryResults.getTotalMatches());
		for (Hit hit : queryResults.getHits()) {
			try {
				String fragmentPagePath = hit.getPath();
				LOG.info("Fragment page path={}", fragmentPagePath);
				Page page = pageMgr.getContainingPage(fragmentPagePath);
				if (null != page) {
					String pageTitle = page.getTitle();
					String pagePath = page.getPath() + HTML_EXTENSION;
					finalResultMap.put(pageTitle, pagePath);
				}

			} catch (RepositoryException e) {
				LOG.error("Repo exception={}", e.getMessage());
			}
		}
	}

}