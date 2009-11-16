package com.gsoft.workflow.conditions.factories;

/**
 *
 * @author Ganzha Vitaliy
 */
import com.atlassian.core.user.GroupUtils;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.opensymphony.user.Group;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;


public class WorkflowIsUserInGroupConditionFactoryImpl extends AbstractWorkflowPluginFactory
    implements WorkflowPluginConditionFactory{

    public WorkflowIsUserInGroupConditionFactoryImpl(){}
    protected void getVelocityParamsForInput(Map velocityParams)
    {
        Map groupMap = new ListOrderedMap();
        Collection groups = GroupUtils.getGroups();
        Group group;
        for(Iterator iterator = groups.iterator(); iterator.hasNext(); groupMap.put(group.getName(), group.getName()))
            group = (Group)iterator.next();

        velocityParams.put("groups", groupMap);

        Map compareMap = new ListOrderedMap();
        compareMap.put("in","in");
        compareMap.put("not in","not in");
        velocityParams.put("comparetypes", compareMap);
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        if(!(descriptor instanceof ConditionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        } else
        {
            ConditionDescriptor conditionDescriptor = (ConditionDescriptor)descriptor;
            velocityParams.put("comparetype", conditionDescriptor.getArgs().get("comparetype"));
            velocityParams.put("group", conditionDescriptor.getArgs().get("group"));
            return;
        }
    }

    public Map getDescriptorParams(Map conditionParams)
    {
        Map args = new ListOrderedMap();
        args.put("group", extractSingleParam(conditionParams, "group"));
        args.put("comparetype",extractSingleParam(conditionParams, "comparetype"));
        return args;
    }
}
