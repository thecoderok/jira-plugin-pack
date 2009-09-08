package com.gsoft.workflow.conditions.factories;

/**
 *
 * @author Ganzha Vitaliy
 */
import com.atlassian.core.user.GroupUtils;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.opensymphony.user.Group;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;

public class UserInFieldFactoryImpl extends AbstractWorkflowPluginFactory
    implements WorkflowPluginConditionFactory{

    @Override
    protected void getVelocityParamsForInput(Map velocityParams) {
        List fields = CommonPluginUtils.getAllEditableFields();

        velocityParams.put("fieldList", fields);

        Map compareMap = new ListOrderedMap();
        compareMap.put("in","in");
        compareMap.put("not in","not in");
        velocityParams.put("comparetypes", compareMap);
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        if(!(descriptor instanceof ConditionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        } else
        {
            ConditionDescriptor conditionDescriptor = (ConditionDescriptor)descriptor;
            velocityParams.put("comparetype", conditionDescriptor.getArgs().get("comparetype"));
            velocityParams.put("userField", conditionDescriptor.getArgs().get("userField"));
            return;
        }
    }

    public Map getDescriptorParams(Map conditionParams) {
        Map args = new ListOrderedMap();
        args.put("userField", extractSingleParam(conditionParams, "userField"));
        args.put("comparetype",extractSingleParam(conditionParams, "comparetype"));
        return args;
    }

}
