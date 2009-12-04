package ua.bytes.helpers;

import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.issue.search.SearchRequest;
import com.opensymphony.user.User;

/**
 *
 * @author Vmganzha
 */
public class SearchBuilderHelper {

    public static SearchRequest getSearchRequest(String FilterId, User user)
      throws Exception, ObjectConfigurationException
      {
	      return buildSearchRequestFromFilter(FilterId,user);
      }
	//----------------------------------------------------------------------------------------------------------
   	public static SearchRequest buildSearchRequestFromFilter(String filterId,User user)
    throws Exception
    	{
            com.atlassian.jira.bc.JiraServiceContext ctx = new JiraServiceContextImpl(user);
			SearchRequest searchRequest = ComponentManager.getInstance().getSearchRequestService().getFilter(ctx, new Long(filterId));
			if(searchRequest != null)
				return searchRequest;
			else
			    throw new Exception("Search Filter with id " + filterId + " did not exist");
    	}
	//----------------------------------------------------------------------------------------------------------
}