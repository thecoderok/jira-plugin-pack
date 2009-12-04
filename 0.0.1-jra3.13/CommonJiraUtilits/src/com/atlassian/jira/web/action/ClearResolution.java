package com.atlassian.jira.web.action;

import com.atlassian.core.action.ActionUtils;
import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.MutableIssue;
import java.util.Map;
import webwork.dispatcher.ActionResult;

/**
 *
 * @author Vmganzha
 */
public class ClearResolution extends JiraWebActionSupport{

    private String id;
    private String key;

    @Override
    public String doDefault() throws Exception
    {
        return super.doDefault(); // specifies the action's 'input' view specified in atlassian-plugin.xml
    }

    @Override
    protected String doExecute() throws Exception
    {
        if (id == null || id.equals(""))
            return SUCCESS;

        MutableIssue issue = ComponentManager.getInstance().getIssueManager().getIssueObject(Long.parseLong(id));
        if (issue == null)
            return SUCCESS;

        setKey(issue.getKey());
        if (issue.getResolutionObject() == null)
            return SUCCESS;

        issue.setResolution(null);
        Map parameters = EasyMap.build( "issueObject", issue, "issue", issue.getGenericValue(), "remoteUser", getRemoteUser() );
        ActionResult aResult = CoreFactory.getActionDispatcher().execute(com.atlassian.jira.action.ActionNames.ISSUE_UPDATE, parameters );
        ActionUtils.checkForErrors( aResult );

        return SUCCESS;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

}
