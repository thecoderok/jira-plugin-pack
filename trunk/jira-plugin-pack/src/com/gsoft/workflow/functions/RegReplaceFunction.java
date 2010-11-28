package com.gsoft.workflow.functions;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author User
 */
public class RegReplaceFunction implements FunctionProvider{

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = (MutableIssue) transientVars.get("issue");
        String fieldId = (String) args.get("selectedField");
        Field field = WorkflowUtils.getFieldFromKey(fieldId);

        String pattern = (String) args.get("patternEdit");

        String replacement = (String) args.get("replacementEdit");

        Pattern p = Pattern.compile(pattern);
        Object objValue = WorkflowUtils.getFieldValueFromIssue(issue, field);
        if (objValue == null)
            return;

        String value = (String) objValue;

        Matcher m = p.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);

        WorkflowUtils.setFieldValue(issue, field, sb.toString(), new DefaultIssueChangeHolder());
    }
}
