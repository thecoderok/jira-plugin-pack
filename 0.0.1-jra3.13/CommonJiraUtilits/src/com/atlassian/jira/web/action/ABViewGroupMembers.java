package com.atlassian.jira.web.action;

import com.atlassian.core.user.GroupUtils;
import com.atlassian.core.user.UserUtils;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Vmganzha
 */
public class ABViewGroupMembers extends JiraWebActionSupport{

    private String gname = null;
    private Group group;
    private Collection members = null;

    public Collection getMembers() {
        Group g = getGroup();
        if (g == null)
            return null;

        members = new ArrayList();
        for (Object userName: g.getUsers())
        {
            try {
                members.add(UserUtils.getUser((String) userName));
            } catch (EntityNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return members;
    }


    @Override
    public String doDefault() throws Exception
    {
        return super.doDefault(); // specifies the action's 'input' view specified in atlassian-plugin.xml
    }

    @Override
    protected void doValidation() {
        super.doValidation();
        if (!GroupUtils.existsGroup(getGname()))
            addError("gname","Не найдена группа "+getGname());
    }



    /**
     * Get the value of gname
     *
     * @return the value of gname
     */
    public String getGname() {
        return gname;
    }

    /**
     * Set the value of gname
     *
     * @param gname new value of gname
     */
    public void setGname(String gname) {
        this.gname = gname;
    }
    /**
     * @return the group
     */
    public Group getGroup() {
        return GroupUtils.getGroup(getGname());
    }


}
