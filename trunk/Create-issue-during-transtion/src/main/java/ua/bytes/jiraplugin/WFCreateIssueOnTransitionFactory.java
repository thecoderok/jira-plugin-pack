package ua.bytes.jiraplugin;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfigSchemeImpl;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.project.Project;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author <a href="http://bytes.org.ua/">Vitaliy Ganzha</a>
 */
public class WFCreateIssueOnTransitionFactory extends AbstractWorkflowPluginFactory implements
        WorkflowPluginFunctionFactory {

    private final FieldManager fieldManager;
    private static org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(WFCreateIssueOnTransitionFactory.class);

    public WFCreateIssueOnTransitionFactory(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }

    protected void getVelocityParamsForInput(Map velocityParams) {

        Collection<Project> projectsList = ComponentManager.getInstance().getProjectManager().getProjects();
        velocityParams.put("projectsList", projectsList);
        velocityParams.put("issueTypeManager", ComponentManager.getInstance().getIssueTypeSchemeManager());

        IssueLinkTypeManager issueLinkTypeManager = (IssueLinkTypeManager) ComponentManager.getComponentInstanceOfType(IssueLinkTypeManager.class);
        Collection<IssueLinkType> linkTypes = issueLinkTypeManager.getIssueLinkTypes();

        velocityParams.put("linkTypeList", linkTypes);
        Collection sides = new ArrayList();
        sides.add("Outward");
        sides.add("Inward");
        velocityParams.put("linkSideList", sides);
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        FunctionDescriptor fDescriptor = (FunctionDescriptor) descriptor;

        String issueTypeId = (String) fDescriptor.getArgs().get("issuetypeid");
        String projectId = (String) fDescriptor.getArgs().get("projectid");
        velocityParams.put("issuetypeid", projectId + "_" + issueTypeId);
        Project prj = ComponentManager.getInstance().getProjectManager().getProjectObj(new Long(projectId));
        IssueType it = ComponentManager.getInstance().getConstantsManager().getIssueTypeObject(issueTypeId);
        velocityParams.put("issuetype", it);
        velocityParams.put("project", prj);

        IssueLinkTypeManager issueLinkTypeManager = (IssueLinkTypeManager) ComponentManager.getComponentInstanceOfType(IssueLinkTypeManager.class);

        String propKey = (String) fDescriptor.getArgs().get("linkType");

        velocityParams.put("selectedLType", issueLinkTypeManager.getIssueLinkType(Long.parseLong(propKey)));
        velocityParams.put("selectedSide", fDescriptor.getArgs().get("linkSide"));

        velocityParams.put("CloneLinks", fDescriptor.getArgs().get("CloneLinks"));
        velocityParams.put("CloneAttachments", fDescriptor.getArgs().get("CloneAttachments"));
    }

    public Map getDescriptorParams(Map functionParams) {
        Map params = new HashMap();
        String project = extractSingleParam(functionParams, "issuetypeid");
        String[] vals = project.split("_");

        params.put("projectid", vals[0]);
        params.put("issuetypeid", vals[1]);

        String propKey = extractSingleParam(functionParams, "linkSide");
        params.put("linkSide", propKey);

        propKey = extractSingleParam(functionParams, "linkType");
        params.put("linkType", propKey);

        propKey = extractSingleParam(functionParams, "CloneAttachments");
        params.put("CloneAttachments", propKey);

        propKey = extractSingleParam(functionParams, "CloneLinks");
        params.put("CloneLinks", propKey);
        return params;
    }
}
