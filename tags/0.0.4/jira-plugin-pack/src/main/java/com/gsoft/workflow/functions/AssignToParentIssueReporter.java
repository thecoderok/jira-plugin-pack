package com.gsoft.workflow.functions;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;

/**
 *
 * @author Ganzha Vitaliy
 */
public class AssignToParentIssueReporter implements FunctionProvider{

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        JiraAuthenticationContext authenticationContext = ComponentManager.getInstance().getJiraAuthenticationContext();
        User user = authenticationContext.getUser();

        PermissionManager permissionManager = ComponentManager.getInstance().getPermissionManager();

        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        if (!permissionManager.hasPermission(Permissions.ASSIGN_ISSUE, issue, user))
            return;

        Issue parent = issue.getParentObject();
        if (parent == null)
            return;

        user = parent.getReporter();
        if (!permissionManager.hasPermission(Permissions.ASSIGNABLE_USER, parent, user))
            return;

        issue.setAssignee(user);
    }

}
