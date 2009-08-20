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
    protected void getVelocityParamsForInput(Map arg0) {
        return;
    }

    @Override
    protected void getVelocityParamsForEdit(Map arg0, AbstractDescriptor arg1) {
        return;
    }

    @Override
    protected void getVelocityParamsForView(Map arg0, AbstractDescriptor arg1) {
        return;
    }

    public Map getDescriptorParams(Map arg0) {
        return new HashMap();
    }

}
