package com.gsoft.workflow.conditions;

/**
 *
 * @author Ganzha Vitaliy
 */
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import java.util.Map;
import com.opensymphony.user.*;
import com.opensymphony.workflow.WorkflowContext;

public class UserGroupCondition implements Condition{

    public UserGroupCondition()
    {
    }

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
    {
        User user;
        WorkflowContext context = (WorkflowContext)transientVars.get("context");
        if (context.getCaller() == null)
            return false;
        String condition = (String)args.get("comparetype");

        try{
            user = UserManager.getInstance().getUser(context.getCaller());
            if (condition.equalsIgnoreCase("in"))
               return user.inGroup((String)args.get("group"));
            return !user.inGroup((String)args.get("group"));
        }
        catch(EntityNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }
}
