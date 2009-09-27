package com.gsoft.workflow.validators.factories;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ganzha Vitaliy
 */
public class RegularExpressionValidatorFactory extends AbstractWorkflowPluginFactory
		implements WorkflowPluginValidatorFactory{

    @Override
    protected void getVelocityParamsForInput(Map velocityParams) {
        List<Field> fields = CommonPluginUtils.getAllEditableFields();
        velocityParams.put("fieldsList", fields);
        
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams,descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        String fieldKey = (String) validatorDescriptor.getArgs().get("selectedField");
        velocityParams.put("selectedField", WorkflowUtils.getFieldFromKey(fieldKey));
        velocityParams.put("pattern", validatorDescriptor.getArgs().get("patternEdit"));
    }

    public Map getDescriptorParams(Map velocityParams) {
        Map<String, String> params = new HashMap<String, String>();
        String value = extractSingleParam(velocityParams, "fields");
        params.put("selectedField", value);

        value = extractSingleParam(velocityParams, "patternEdit");
        params.put("patternEdit", value);

        return params;
    }
}