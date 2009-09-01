package com.gsoft.workflow.validators;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.util.WorkflowUtils;
import com.googlecode.jsu.workflow.validator.GenericValidator;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Ganzha Vitaliy
 */
public class regularExpressionValidator extends GenericValidator{

    @Argument("selectedField")
    private String selectedField;

    @Argument("patternEdit")
    private String patternEdit;

    @Override
    protected void validate() throws InvalidInputException, WorkflowException {
        Field field = WorkflowUtils.getFieldFromKey(selectedField);
        final Issue issue = getIssue();
        Object value = WorkflowUtils.getFieldValueFromIssue(issue, field);
        String string = value.toString();

        Pattern pattern = Pattern.compile(patternEdit);
        Matcher matcher = pattern.matcher(string);

        boolean found = false;
        while (matcher.find())
        {
            found = true;
            break;
        }

        if (!found)
        {
            this.setExceptionMessage(field, 
                                     "The value don't match for pattern "+ pattern,
                                     "The value of field " + field.getName() +" don't match for pattern "+ pattern
                                     );
        }
    }

}
