package com.gsoft.workflow.functions.factories;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.customfields.impl.MultiGroupCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.apache.log4j.Logger;


public class GetAssigneeFromGroupPluginFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory{

    private final FieldManager fieldManager;
    private static final Logger log = Logger.getLogger(GetAssigneeFromGroupPluginFactory.class);

    public GetAssigneeFromGroupPluginFactory(FieldManager fieldManager)
    {
        this.fieldManager = fieldManager;
    }

    protected void getVelocityParamsForInput(Map velocityParams)
    {
        Map fieldMap = new HashMap();
        try
        {
            List customFields = ManagerFactory.getCustomFieldManager().getCustomFieldObjects();
            CustomField cf = null;
            for (Iterator cfit = customFields.iterator(); cfit.hasNext();)
            {
                cf = (CustomField) cfit.next();
                if (cf.getCustomFieldType() instanceof MultiGroupCFType)
                  fieldMap.put(cf.getId(), cf.getNameKey());
            }
        } catch (Exception e)
        {

        }
        velocityParams.put("fields", fieldMap);
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForInput(velocityParams);
        if (!(descriptor instanceof FunctionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        } else
        {
            FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
            velocityParams.put("fieldId", functionDescriptor.getArgs().get("field.id"));
            return;
        }
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        if (!(descriptor instanceof FunctionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        } else
        {
            FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
            velocityParams.put("fieldId", fieldManager.getCustomField(
                    (String) functionDescriptor.getArgs().get("field.id")).getNameKey());
            return;
        }
    }

    public Map getDescriptorParams(Map conditionParams)
    {
        Map params = new HashMap();
        String fieldId = extractSingleParam(conditionParams, "fieldId");
        params.put("field.id", fieldId);

        return params;
    }

}
