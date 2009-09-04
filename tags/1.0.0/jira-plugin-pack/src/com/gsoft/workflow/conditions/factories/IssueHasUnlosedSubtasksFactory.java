package com.gsoft.workflow.conditions.factories;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;

/**
 *
 * @author Ganzha Vitaliy
 */
public class IssueHasUnlosedSubtasksFactory extends AbstractWorkflowPluginFactory
    implements WorkflowPluginConditionFactory{

    public static final String CLOSED_KEY = "closed";
    public static final String UNCLOSED_KEY = "unclosed";

    public IssueHasUnlosedSubtasksFactory(){}

    @Override
    protected void getVelocityParamsForInput(Map velocityParams)
    {
        return;
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }
    
    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        return;
    }

    public Map getDescriptorParams(Map conditionParams) {
        Map args = new ListOrderedMap();
        //args.put("condition", extractSingleParam(conditionParams, "conditions"));
        return args;
    }

}
