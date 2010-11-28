package com.gsoft.workflow.functions;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class GetAssigneeFromGroup implements FunctionProvider {

    public GetAssigneeFromGroup(){}

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        String fieldId = (String) args.get("field.id");
        CustomField grpCF = ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObject(fieldId);
        Object obj = grpCF.getValue(issue);

        ModifiedValue modifiedValue = (ModifiedValue) issue.getModifiedFields().get(fieldId);
        if (modifiedValue != null)
        {
            obj = modifiedValue.getNewValue();
        }


        User assignee = null;
        if (obj instanceof ArrayList)
        {
            ArrayList arrayList = (ArrayList) obj;
            ListIterator iterator = arrayList.listIterator();
            while (iterator.hasNext())
            {
                Object item = iterator.next();
                if (item instanceof Group)
                {
                    Group group = (Group) item;
                    List users = group.getUsers();
                    if (users.size()==0)
                        throw(new WorkflowException("Group "+group.getName()+" is empty"));
                    Iterator ui =users.iterator();
                    while (ui.hasNext())
                    {
                        Object userObj = ui.next();
                        assignee = ComponentManager.getInstance().getUserUtil().getUser(userObj.toString());
                        break;
                    }
                }//if (item instanceof Group)
                else
                    throw (new WorkflowException("ListItem isn't com.opensymphony.user.Group class, but "+item.getClass()));
                if (assignee != null)
                    break;
            }//while (iterator.hasNext())
        }//if (obj instanceof ArrayList)
        else
            throw (new WorkflowException("CustomField Vlaue isn't ArrayList"));

       if (assignee!=null)
       {
         issue.setAssignee(assignee);
         issue.store();
       }
       else
         throw (new WorkflowException("Didn't find assignee!"));
    }
}
