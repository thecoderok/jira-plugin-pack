package com.gsoft.workflow.functions.factories;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author User
 *
 * $Rev$
 */
public class RegReplaceFunctionFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory{

    @Override
    protected void getVelocityParamsForInput(Map velocityParams)
    {
        List<Field> fields = CommonPluginUtils.getAllEditableFields();
        velocityParams.put("fieldsList", fields);
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams,descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String fieldKey = (String) functionDescriptor.getArgs().get("selectedField");
        velocityParams.put("selectedField", WorkflowUtils.getFieldFromKey(fieldKey));
        velocityParams.put("pattern", functionDescriptor.getArgs().get("patternEdit"));
        velocityParams.put("replacement", functionDescriptor.getArgs().get("replacementEdit"));
    }

    public Map getDescriptorParams(Map velocityParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        String value = extractSingleParam(velocityParams, "fields");
        params.put("selectedField", value);

        value = extractSingleParam(velocityParams, "patternEdit");
        params.put("patternEdit", value);

        value = extractSingleParam(velocityParams, "replacementEdit");
        params.put("replacementEdit", value);

        return params;
    }
}
