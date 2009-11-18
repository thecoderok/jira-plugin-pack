package com.gsoft.workflow.conditions;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author User
 */
public class IssueHasUnlosedSubtasks extends  AbstractJiraCondition{

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
         Issue issue = getIssue(transientVars);
         Collection<Issue> subtasks = issue.getSubTaskObjects();
         
         if (subtasks.size() == 0)
             return true;

         Iterator<Issue> i = subtasks.iterator();
         while (i.hasNext())
         {
             Issue item = i.next();
             if (item.getResolutionObject() == null)
                 return false;
         }

         return true;
    }

}
