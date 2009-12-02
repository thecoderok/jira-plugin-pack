package com.atlassian.jira.web.action;

import com.atlassian.core.user.GroupUtils;
import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.favourites.FavouritesManager;
import com.atlassian.jira.portal.PortalPage;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;
import java.util.Collection;
import java.util.List;

/**
 * $Rev$
 * @author Vmganzha
 */
public class AdministerUserDashboards extends JiraWebActionSupport {

    private String groupName;
    private String userName;
    private String dashboardId;
    private long lDashboardId;
    private String clearFavourites = "no";
    private Collection portals;

    @Override
    public String doDefault() throws Exception {
        return super.doDefault(); // specifies the action's 'input' view specified in atlassian-plugin.xml
    }

    @Override
    protected void doValidation()
    {
        super.doValidation();
        if (groupName == null || "".equals(groupName)&& (userName == null || "".equals(userName)))
            {
                addError("groupName", "You must specify user name or group");
                addError("userName", "You must specify user name or group");
                return;
            }
        if (!(groupName == null || "".equals(groupName)))
        {
            if (!GroupUtils.existsGroup(groupName))
                addError("groupName", "Selected Group does not exists");
        }
        if (!(userName == null || "".equals(userName)))
            if (!ComponentManager.getInstance().getUserUtil().userExists(userName))
            {
                addError("userName", "Selected used does not exists");
            }

        if (dashboardId == null || "".equals(dashboardId))
            addError("dashboardId", "You must choose the dashboard");
    }

    @Override
    protected String doExecute() throws Exception
    {
        if (!(groupName == null || "".equals(groupName)))
            return proccesGroup();
        else
            return processUser();
    }
    //*******************Encapsulated fields************************************
    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the dashboardId
     */
    public String getDashboardId() {
        return dashboardId;
    }

    /**
     * @param dashboardId the dashboardId to set
     */
    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    private String proccesGroup(){
        Group group = GroupUtils.getGroup(groupName);
        List<String> users = group.getUsers();
        User user;

        PortalPage page = ComponentManager.getInstance().getPortalPageService().getPortalPage(getJiraServiceContext(), Long.parseLong(dashboardId));
        FavouritesManager fm = (FavouritesManager) ComponentManager.getComponentInstanceOfType(FavouritesManager.class);

        

        String copy = null;
        try
        {
            for (String item :users)
            {
                user = UserUtils.getUser(item);

                if (getClearFavourites().equals("yes"))
                {
                    fm.removeFavouritesForUser(user, page.ENTITY_TYPE);
                }

                copy = item;
                fm.addFavourite(user, page);
            }
        }
        catch(EntityNotFoundException ex)
        {
            addError("groupName", "Member of group is not found: "+copy);
            return INPUT;
        }
        catch(PermissionException ex)
        {
            addError("groupName", "Member of group hasn't permission to use Dashboard: "+copy);
            return INPUT;
        }

        return SUCCESS;
    }

    private String processUser() {
        try
        {
            User user = UserUtils.getUser(userName);

            PortalPage page = ComponentManager.getInstance().getPortalPageService().getPortalPage(getJiraServiceContext(), Long.parseLong(dashboardId));
            FavouritesManager fm = (FavouritesManager) ComponentManager.getComponentInstanceOfType(FavouritesManager.class);

            if (getClearFavourites().equals("yes"))
            {
                fm.removeFavouritesForUser(user, page.ENTITY_TYPE);
            }

            fm.addFavourite(user, page);
        }
        catch(EntityNotFoundException ex)
        {
            addError("userName", "User is not found: "+userName);
            return INPUT;
        }
        catch(PermissionException ex)
        {
            addError("userName", "User hasn't permission to use Dashboard: "+userName);
            return INPUT;
        }

        return SUCCESS;
    }

    /**
     * @return the portals
     */
    public Collection getPortals() {
        portals = ComponentManager.getInstance().getPortalPageService().getFavouritePortalPages(getRemoteUser());
        return portals;
    }

    /**
     * @return the clearFavourites
     */
    public String getClearFavourites() {
        return clearFavourites;
    }

    /**
     * @param clearFavourites the clearFavourites to set
     */
    public void setClearFavourites(String clearFavourites) {
        this.clearFavourites = clearFavourites;
    }

    /**
     * @return the lDashboardId
     */
    public long getlDashboardId() {
        if (dashboardId == null || "".equals(dashboardId))
            lDashboardId = -1;
        else
            lDashboardId = Long.parseLong(dashboardId);
        return lDashboardId;
    }

}
