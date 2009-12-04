package com.atlassian.jira.web.action.admin.index;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.opensymphony.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import ua.bytes.helpers.SearchBuilderHelper;
import ua.bytes.helpers.SearchRequestHelper;

/**
 *
 * @author <a href="http://bytes.org.ua/">Vitaliy Ganzha</a>
 */
public class PartialIssueReindex extends JiraWebActionSupport{

    private Collection<SearchRequest> filters = SearchRequestHelper.getFavouritesFilters(getRemoteUser());
    private String seltype;
    private String linkKey;
    private String filterid;
    private StringBuilder message = new StringBuilder();

    
    @Override
    public String doDefault() throws Exception
    {
        return super.doDefault(); // specifies the action's 'input' view specified in atlassian-plugin.xml
    }

    @Override
    protected void doValidation() {
        super.doValidation();
        if (seltype == null || seltype.length() == 0)
            addError("seltype", "Please, select issue source what you want to reindex");

        if (seltype != null && seltype.equals("key") )
            if (linkKey == null || linkKey.length() ==0)
                addError("linkKey", "Please, input issue keys what you want to reindex");
            else if (!isCorrectIssues(splitStringWithIssues(linkKey)))
                addError("linkKey", "Please, correct inputed issue keys");
        if (seltype != null && seltype.equals("filter"))
            if (filterid == null || filterid.equals("-1"))
            {
                addError("filterid", "Please, select correct search request!");
            }

    }

    @Override
    protected String doExecute() throws Exception
    {
        List<Issue> issues = null;
        if (seltype.equals("key"))
        {
            issues = getIssuesFromList(splitStringWithIssues(linkKey));
        }
        else
        {
            issues = MakeSearchByFilterId(filterid, getRemoteUser());
        }
        IssueIndexManager iim = ComponentManager.getInstance().getIndexManager();
        for (Issue issue : issues)
        {
            iim.reIndex(issue);
            message.append("Issue <a href='/browse/"+issue.getKey()+"'>"+issue.getKey()+" - "+ issue.getSummary() +"</a> was sucessfuly reindexed!<br>");
        }

        return SUCCESS;
    }

    public String getFilterid() {
        return filterid;
    }

    public void setFilterid(String filterid) {
        this.filterid = filterid;
    }


    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }


    public String getSeltype() {
        return seltype;
    }

    public void setSeltype(String seltype) {
        this.seltype = seltype;
    }


    public Collection<SearchRequest> getFilters() {
        return filters;
    }

    public static List<String> splitStringWithIssues(String col)
    {
        col = col.replace(" ", "");
        return Arrays.asList(col.split(","));
    }

    public static boolean isCorrectIssues(List<String> keys)
    {
        IssueManager im = ComponentManager.getInstance().getIssueManager();
        for (String key: keys)
        {
            if (im.getIssueObject(key) == null)
                return false;
        }
        return true;
    }

    public static List<Issue> getIssuesFromList(List<String> keys)
    {
        List<Issue> issues = new ArrayList<Issue>();
        IssueManager im = ComponentManager.getInstance().getIssueManager();
        for (String key: keys)
        {
            issues.add(im.getIssueObject(key));
        }
        return issues;
    }

    public static List<Issue> MakeSearchByFilterId(String id, User user) throws Exception
    {
        List<Issue> issues = new ArrayList<Issue>();
        SearchRequest searchRequest = null;
        JiraAuthenticationContext authenticationContext = (JiraAuthenticationContext) ComponentManager.getComponentInstanceOfType(JiraAuthenticationContext.class);
        authenticationContext.setUser(user);
        
        searchRequest = SearchBuilderHelper.getSearchRequest(id, user);
        SearchResults resultsIssues = ComponentManager.getInstance().getSearchProvider().search(searchRequest, user, PagerFilter.getUnlimitedFilter());
        issues = resultsIssues.getIssues();
        return issues;
    }

    public StringBuilder getMessage() {
        return message;
    }
}
