package ua.bytes.helpers;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.search.SearchRequest;
import com.opensymphony.user.User;
import java.util.Collection;

/**
 *
 * @author Vmganzha
 */
public class SearchRequestHelper {

    public static Collection<SearchRequest> getFavouritesFilters(User user)
    {
        Collection<SearchRequest> filters = ComponentManager.getInstance().getSearchRequestService().getFavouriteFilters(user);
        return filters;
    }

}
