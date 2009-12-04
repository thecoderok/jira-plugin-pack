package ua.bytes.commonutils;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issueoperation.AbstractPluggableIssueOperation;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.opensymphony.user.User;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vmganzha
 */
public class ClearResolutionIssueOperation extends AbstractPluggableIssueOperation{

    private JiraAuthenticationContext authenticationContext;

    public ClearResolutionIssueOperation(JiraAuthenticationContext authenticationContext)
    {
        this.authenticationContext = authenticationContext;
    }

    public String getHtml(Issue issue) {
        final Map params = new HashMap();
        params.put("issueoperation", this);
        params.put("issue", issue);
        return getBullet() + descriptor.getHtml("view", params);
    }

    public boolean showOperation(Issue issue) {
        if (issue.getResolutionObject() == null)
            return false;
        User user = authenticationContext.getUser();
        return user != null && user.inGroup("jira-administrators");
    }
}
