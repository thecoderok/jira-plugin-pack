package com.gsoft.workflow.functions;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.security.PermissionManager;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;
import com.atlassian.jira.security.Permissions;
import org.ofbiz.core.entity.GenericValue;
/**
 *
 * @author Ganzha Vitaliy
 */
public class AddUserToWatchersFunction  implements FunctionProvider{

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String selected = (String) args.get("userSelect");
        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        WatcherManager watcherManager = ComponentManager.getInstance().getWatcherManager();

        User user = null;

        if (selected.equals("assignee"))
            user = issue.getAssignee();
        else if (selected.equals("reporter"))
            user = issue.getReporter();
        else
            user = ComponentManager.getInstance().getJiraAuthenticationContext().getUser();

        if (user == null)
            return;

        PermissionManager permissionManager = ComponentManager.getInstance().getPermissionManager();
        if (!permissionManager.hasPermission(Permissions.VIEW_VOTERS_AND_WATCHERS, issue, user))
            return;

        if (watcherManager.isWatching(user, issue.getGenericValue()))
            return;

        watcherManager.startWatching(user, issue.getGenericValue());
    }
}
