/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gsoft.workflow.functions.factories;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class AssignToParentIssueAssigneePluginFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory{

    @Override
    protected void getVelocityParamsForInput(Map velocityParams) {
        return;
    }

    @Override
    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        return;
    }

    @Override
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        return;
    }

    public Map getDescriptorParams(Map velocityParams) {
        return new HashMap();
    }

}
