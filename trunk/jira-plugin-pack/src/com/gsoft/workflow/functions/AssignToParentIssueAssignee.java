package com.gsoft.workflow.functions;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;

/**
 *
 * @author User
 */
public class AssignToParentIssueAssignee implements FunctionProvider{

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        Issue parent = issue.getParentObject();
        if (parent == null)
            return;
        if (parent.getAssignee() == null)
            return;

        issue.setAssignee(parent.getAssignee());
    }

}
