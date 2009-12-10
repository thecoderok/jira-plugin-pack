package ua.bytes.jiraplugin;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.workflow.WorkflowFunctionUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

public class CreateIssueOnTransition implements FunctionProvider {

    public CreateIssueOnTransition() {
    }
    private boolean isCloneIssueLinks;
    private boolean isCloneAttachments;
    private IssueLinkType cloneIssueLinkType;
    private String cloneIssueLinkTypeName;
    private static ComponentManager cm = ComponentManager.getInstance();
    private IssueLinkManager issueLinkManager = cm.getIssueLinkManager();
    private static IssueLinkTypeManager issueLinkTypeManager = (IssueLinkTypeManager) ComponentManager.getComponentInstanceOfType(IssueLinkTypeManager.class);
    private final ApplicationProperties applicationProperties = cm.getApplicationProperties();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        try {
            MutableIssue originalIssue = (MutableIssue) transientVars.get("issue");
            String projectId = (String) args.get("projectid");
            String issueTypeId = (String) args.get("issuetypeid");
            IssueLinkType linkTo = issueLinkTypeManager.getIssueLinkType(Long.parseLong((String) args.get("linkType")));
            String direction = (String) args.get("linkSide");

            String checkValue = (String) args.get("CloneLinks");
            isCloneIssueLinks = checkValue.equals("1") ? true : false;

            checkValue = (String) args.get("CloneAttachments");
            isCloneAttachments = checkValue.equals("1") ? true : false;

            //cm = ComponentManager.getInstance();
            User user = cm.getJiraAuthenticationContext().getUser();

            Project project = cm.getProjectManager().getProjectObj(new Long(projectId));
            IssueType issueType = cm.getConstantsManager().getIssueTypeObject(issueTypeId);

            MutableIssue issueObject = cm.getIssueFactory().getIssue();

            issueObject.setProjectId(project.getId());
            issueObject.setSummary(originalIssue.getSummary());
            issueObject.setDescription(originalIssue.getDescription());
            issueObject.setIssueType(issueType.getGenericValue());
            issueObject.setReporter(originalIssue.getReporter());
            issueObject.setPriority(originalIssue.getPriority());

            issueObject.setCreated(null);
            issueObject.setUpdated(null);
            issueObject.setKey(null);
            issueObject.setVotes(null);
            issueObject.setStatus(null);
            //issueObject.setWorkflowId(null);
            // Ensure that the 'time spent' and 'remaining estimated' are not cloned - JRA-7165
            // We need to copy the value of 'original estimate' to the value 'remaining estimate' as they must be kept in synch
            // until work is logged on an issue.
            issueObject.setEstimate(originalIssue.getOriginalEstimate());
            issueObject.setTimeSpent(null);

            Map fields = new HashMap();
            fields.put("issue", issueObject);

            // Give the CustomFields a chance to set their default values JRA-11762
            List customFieldObjects = ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObjects(issueObject);
            for (Iterator iterator = customFieldObjects.iterator(); iterator.hasNext();) {
                CustomField customField = (CustomField) iterator.next();
                issueObject.setCustomFieldValue(customField, customField.getDefaultValue(issueObject));
            }

            // Retrieve custom fields for the issue type and project of the clone issue (same as original issue)
            List customFields = getCustomFields(originalIssue);

            Map modFields = originalIssue.getModifiedFields();
            Object value = null;
            ModifiedValue modValue;
            
            for (Iterator iterator = customFields.iterator(); iterator.hasNext();) {
                CustomField customField = (CustomField) iterator.next();
                FieldConfig fc = customField.getRelevantConfig(issueObject);
                if (fc == null) {
                    continue;
                }
                // Set the custom field value of the clone to the value set in the original issue
                modValue = (ModifiedValue) modFields.get(customField.getId());
                if (modValue != null)
                    value = modValue.getNewValue();

                if (value == null)
                    value = customField.getValue(originalIssue);

                if (value != null) {
                    issueObject.setCustomFieldValue(customField, value);
                value = null;
                }
            }


            //GenericValue newIssue = cm.getIssueManager().createIssue(user, issueObject);
            fields.put("issue", issueObject);
            GenericValue newIssue = ManagerFactory.getIssueManager().createIssue(user, fields);

            if (isCloneIssueLinks) {
                cloneIssueLinks(originalIssue, cm.getIssueManager().getIssueObject(newIssue.getLong("id")), user);
            }

            //Linking Original and Created issue
            if (direction.equals("Outward")) {
                issueLinkManager.createIssueLink(originalIssue.getId(), newIssue.getLong("id"), linkTo.getId(), new Long(0), user);
            } else {
                issueLinkManager.createIssueLink(newIssue.getLong("id"), originalIssue.getId(), linkTo.getId(), new Long(0), user);
            }

            if (isCloneAttachments) {
                AttachmentManager mgr = ComponentManager.getInstance().getAttachmentManager();
                List<Attachment> files = mgr.getAttachments(originalIssue);
                Date dt = new Date();
                Timestamp time = new Timestamp(dt.getTime());
                for (Attachment a : files) {
                    try {
                        mgr.createAttachment(newIssue, user, a.getMimetype(), a.getFilename(), a.getFilesize(), null, time);
                    } catch (GenericEntityException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            newIssue.store();
        } catch (Throwable ex) {
            ex.printStackTrace();
            //we want let to know about some requiered fields, that not filled in this transition
            throw (new WorkflowException(ex));
        }

    }

    private Collection filterArchivedVersions(Collection versions) {
        // Remove archived versions
        List tempVers = new ArrayList();
        for (Iterator versionsIt = versions.iterator(); versionsIt.hasNext();) {
            Version version = (Version) versionsIt.next();
            if (!version.isArchived()) {
                tempVers.add(version);
            }
        }
        return tempVers;
    }

    private void cloneIssueLinks(Issue issueBeingCloned, Issue clone, User user) throws CreateException {
        // Clone the links if the user has chosen to do so and linking is actually enabled.
        if (isCloneLinks() && cm.getIssueLinkManager().isLinkingEnabled()) {
            Collection inwardLinks = cm.getIssueLinkManager().getInwardLinks(issueBeingCloned.getId());
            for (Iterator iterator = inwardLinks.iterator(); iterator.hasNext();) {
                IssueLink issueLink = (IssueLink) iterator.next();
                if (copyLink(issueLink)) {
                    issueLinkManager.createIssueLink(issueLink.getSourceId(), clone.getId(), issueLink.getIssueLinkType().getId(), null, user);
                }
            }

            Collection outwardLinks = issueLinkManager.getOutwardLinks(issueBeingCloned.getId());
            for (Iterator iterator = outwardLinks.iterator(); iterator.hasNext();) {
                IssueLink issueLink = (IssueLink) iterator.next();
                if (copyLink(issueLink)) {
                    issueLinkManager.createIssueLink(clone.getLong("id"), issueLink.getDestinationId(), issueLink.getIssueLinkType().getId(), null, user);
                }
            }
        }
    }

    private boolean isCloneLinks() {
        return isCloneIssueLinks;
    }

    // Retrieve the issue link type specified by the clone link name in the properties file.
    // If the name is unset - issue linking of originals to clones is not required - returns null.
    // Otherwise, returns null if the issue link type with the specified name cannot be found in the system.
    public IssueLinkType getCloneIssueLinkType() {
        if (cloneIssueLinkType == null) {
            final Collection cloneIssueLinkTypes = issueLinkTypeManager.getIssueLinkTypesByName(getCloneLinkTypeName());

            if (!TextUtils.stringSet(getCloneLinkTypeName())) {
                // Issue linking is not required
                cloneIssueLinkType = null;
            } else if (cloneIssueLinkTypes == null || cloneIssueLinkTypes.isEmpty()) {
//                log.warn("The clone link type '" + getCloneLinkTypeName() + "' does not exist. A link to the original issue will not be created.");
                cloneIssueLinkType = null;
            } else {
                for (Iterator iterator = cloneIssueLinkTypes.iterator(); iterator.hasNext();) {
                    IssueLinkType issueLinkType = (IssueLinkType) iterator.next();
                    if (issueLinkType.getName().equals(getCloneLinkTypeName())) {
                        cloneIssueLinkType = issueLinkType;
                    }
                }
            }
        }

        return cloneIssueLinkType;
    }

    public String getCloneLinkTypeName() {
        if (cloneIssueLinkTypeName == null) {
            cloneIssueLinkTypeName = applicationProperties.getDefaultBackedString(APKeys.JIRA_CLONE_LINKTYPE_NAME);
        }

        return cloneIssueLinkTypeName;
    }

    private boolean copyLink(IssueLink issueLink) {
        // Do not copy system links types and do not copy the cloners link type, as it is used to record the relationship between cloned issues
        // So if the cloners link type does not exists, or the link is not of cloners link type, and is not a system link, then copy it
        return !issueLink.isSystemLink() &&
                (getCloneIssueLinkType() == null || !getCloneIssueLinkType().getId().equals(issueLink.getIssueLinkType().getId()));
    }

    public List getCustomFields(Issue issue) {
        return cm.getCustomFieldManager().getCustomFieldObjects(issue.getProjectObject().getId(), issue.getIssueType().getString("id"));
    }
}