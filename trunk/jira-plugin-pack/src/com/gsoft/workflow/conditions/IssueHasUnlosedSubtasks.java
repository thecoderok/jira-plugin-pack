package com.gsoft.workflow.conditions;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;

/**
 *
 * @author User
 */
public class IssueHasUnlosedSubtasks implements Condition{

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
