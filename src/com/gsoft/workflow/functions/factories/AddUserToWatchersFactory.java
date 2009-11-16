package com.gsoft.workflow.functions.factories;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ganzha Vitaliy
 */
public class AddUserToWatchersFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory{

    @Override
    protected void getVelocityParamsForInput(Map velocityParams) {
        return;
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams,descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String item = (String) functionDescriptor.getArgs().get("userSelect");
        velocityParams.put("val", item);
    }

    public Map getDescriptorParams(Map functionParams) {
        Map<String, String> params = new HashMap<String, String>();
        String value = extractSingleParam(functionParams, "userSelect");
        params.put("userSelect", value);

        return params;
    }

}
