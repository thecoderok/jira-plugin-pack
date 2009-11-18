package com.gsoft.workflow.functions;

/**
 *
 * @author Ganzha Vitaliy
 */
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.user.util.UserUtil;
import com.googlecode.jsu.util.WorkflowUtils;
import com.gsoft.workflow.functions.factories.SetFieldValueFromUserPropsFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;


public class SetFieldValueFromUserPropsFunction implements FunctionProvider {


    public SetFieldValueFromUserPropsFunction(){}

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String sourcePropKey = (String) args.get(SetFieldValueFromUserPropsFactory.FUNCPARAM);

        String sourceFieldKey = (String) args.get(SetFieldValueFromUserPropsFactory.FIELD);
        Field fieldTo = WorkflowUtils.getFieldFromKey(sourceFieldKey);

        sourceFieldKey = (String) args.get(SetFieldValueFromUserPropsFactory.USER_FIELD);
        Field fieldUser = WorkflowUtils.getFieldFromKey(sourceFieldKey);

        MutableIssue issue = (MutableIssue) transientVars.get("issue");

        User user = GetUserFromField(fieldUser,issue);

        String value = null;
        if (user.getPropertySet().exists(UserUtil.META_PROPERTY_PREFIX+sourcePropKey))
            value = user.getPropertySet().getString(UserUtil.META_PROPERTY_PREFIX+sourcePropKey);

        if (value == null)
            return;

        WorkflowUtils.setFieldValue(issue, fieldTo, value, new DefaultIssueChangeHolder());

        issue.store();
    }

    private User GetUserFromField(Field fieldUser, MutableIssue issue) {
        Object value = WorkflowUtils.getFieldValueFromIssue(issue,fieldUser);
        if (value instanceof String)
        {
            return ComponentManager.getInstance().getUserUtil().getUser((String)value);
        }
        else if (value instanceof User)
        {
            return (User) value;
        }
        return null;
    }
}