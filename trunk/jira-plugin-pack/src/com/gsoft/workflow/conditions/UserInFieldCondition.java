package com.gsoft.workflow.conditions;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ganzha Vitaliy
 */
public class UserInFieldCondition implements Condition{

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

        try{
            WorkflowContext context = (WorkflowContext)transientVars.get("context");
            if (context.getCaller() == null)
                return false;

            User user = null;

            try {
                user = UserManager.getInstance().getUser(context.getCaller());
            } catch (EntityNotFoundException ex) {
                ex.printStackTrace();
            }

            String condition = (String)args.get("comparetype");
            boolean cond = true;
            if (!condition.equalsIgnoreCase("in"))
                cond = false;
            String fieldKey = (String)args.get("userField");
            Issue issue = (Issue) transientVars.get("issue");

            Field field = WorkflowUtils.getFieldFromKey(fieldKey);
            if (field == null)
                throw (new WorkflowException("Field not found with key "+fieldKey));

            Object object = WorkflowUtils.getFieldValueFromIssue(issue, field);
            if (object instanceof User)
            {
                User i = (User) object;
                return cond?i.equals(user):!i.equals(user);
            }
            else if (object instanceof String)
            {
                String i = (String) object;
                if (!UserUtils.existsUser(i))
                    return cond?false:true;

                return cond?user.equals(UserUtils.getUser(i)):!user.equals(UserUtils.getUser(i));
            }
            else if (object instanceof List)
            {
                List i = (List) object;
                Object item = null;
                for (Iterator iterator=i.iterator(); iterator.hasNext(); )
                {
                    item = iterator.next();
                    if (item instanceof User)
                        {
                            User inner = (User) item;
                            if (inner.equals(user))
                                return cond?true:false;
                            else
                                continue;
                        }
                    else if (item instanceof String)
                    {
                        String inner = (String) item;
                        if (!UserUtils.existsUser(inner))
                            continue;

                        if (user.equals(UserUtils.getUser(inner)))
                            return cond?true:false;
                        else
                            continue;
                    }
                }
            }
        }
        catch(EntityNotFoundException ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
    boolean checkObject(Object object, User user) throws EntityNotFoundException
    {
       if (object instanceof User)
            {
                User i = (User) object;
                return i.equals(user);
            }
        else if (object instanceof String)
        {
            String i = (String) object;
            if (!UserUtils.existsUser(i))
                return false;

            return user.equals(UserUtils.getUser(i));
        }
       return false;
    }
}
