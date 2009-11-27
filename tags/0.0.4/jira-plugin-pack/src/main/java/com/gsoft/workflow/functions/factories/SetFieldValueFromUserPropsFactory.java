package com.gsoft.workflow.functions.factories;
/**
 *
 * @author Ganzha Vitaliy
 */
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowFactoryUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFieldValueFromUserPropsFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory{

    public static final String VELOPARAM = "userPropKey";
    public static final String FUNCPARAM = "user.property.key";
    public static final String FIELD = "field";
    public static final String USER_FIELD = "fieldUser";
    public static final String SELECTED_FIELD = "selectedField";
    public static final String SELECTED_USER_FIELD = "selectedUserField";
    public static final String FIELD_LIST = "fieldList";
    private final FieldManager fieldManager;

    public SetFieldValueFromUserPropsFactory(FieldManager fieldManager)
    {
        this.fieldManager = fieldManager;
    }

    @Override
    protected void getVelocityParamsForInput(Map velocityParams) {
        velocityParams.put(VELOPARAM,"");
        List<Field> customFields = null;
        customFields = (List<Field>) CommonPluginUtils.getAllEditableFields();
        velocityParams.put(FIELD_LIST, Collections.unmodifiableList(customFields));
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String propsKeyString = (String) functionDescriptor.getArgs().get(FUNCPARAM);
        velocityParams.put(VELOPARAM, propsKeyString);

        velocityParams.put(SELECTED_FIELD, WorkflowFactoryUtils.getFieldByName(descriptor, FIELD));

        velocityParams.put(SELECTED_USER_FIELD, WorkflowFactoryUtils.getFieldByName(descriptor, USER_FIELD));
    }

    public Map getDescriptorParams(Map velocityParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        String sourceField = extractSingleParam(velocityParams, FIELD);
        params.put(FIELD, sourceField);

        String userField = extractSingleParam(velocityParams, USER_FIELD);
        params.put(USER_FIELD, userField);

        String propKey = extractSingleParam(velocityParams, VELOPARAM);
        params.put(FUNCPARAM, propKey);

        return params;
    }

}
