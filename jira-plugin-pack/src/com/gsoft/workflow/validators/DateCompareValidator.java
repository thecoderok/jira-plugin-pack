package com.gsoft.workflow.validators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.helpers.ConditionManager;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.helpers.YesNoManager;
import com.googlecode.jsu.helpers.YesNoType;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.googlecode.jsu.workflow.validator.GenericValidator;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 *
 * @author Ganzha Vitaliy
 */
public class DateCompareValidator extends GenericValidator {

	@Argument("date2Selected")
	private String date;

	@Argument("conditionSelected")
	private String condition;

	@Argument("includeTimeSelected")
	private String includeTime;

	protected void validate() throws InvalidInputException, WorkflowException {

		Field fldDate = WorkflowUtils.getFieldFromKey(date);
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(condition));
		YesNoType ynTime = YesNoManager.getManager().getOption(new Integer(includeTime));

		// Compare Dates.
		if (fldDate != null) {
			checkDatesCondition(fldDate, cond.getValue(), ynTime.getValue());
		}
	}

	/**
	 * @param fldDate1
	 * @param fldDate2
	 * @param cond
	 * @param includeTime with or wothout the time part.
	 *
	 * It makes the comparison properly this.
	 */
	private void checkDatesCondition(Field fldDate, String cond, String includeTime) {
		boolean condOK = false;

		Object objDate = WorkflowUtils.getFieldValueFromIssue(getIssue(), fldDate);

		if (objDate != null) {
			// It Takes the Locale for inicialize dates.
			ApplicationProperties ap = ManagerFactory.getApplicationProperties();
			Locale locale = ap.getDefaultLocale();

			Calendar calDate1 = Calendar.getInstance(locale);
			Calendar calDate2 = Calendar.getInstance(locale);


			calDate2.setTime((Date) objDate);

			// If the comparison is only date part, cleans the time part.
			if (includeTime.equals(WorkflowUtils.BOOLEAN_NO)) {
				CommonPluginUtils.clearCalendarTimePart(calDate1);
				CommonPluginUtils.clearCalendarTimePart(calDate2);
			} else {
				calDate1.clear(Calendar.SECOND);
				calDate1.clear(Calendar.MILLISECOND);

				calDate2.clear(Calendar.SECOND);
				calDate2.clear(Calendar.MILLISECOND);
			}

			Date date1 = calDate1.getTime();
			Date date2 = calDate2.getTime();

			int comparacion = date1.compareTo(date2);

			if ((comparacion == 0) && ((cond.equals("<=")) || (cond.equals("=")) || (cond.equals(">="))))
				condOK = true;

			if ((comparacion < 0) && ((cond.equals("<")) || (cond.equals("<="))))
				condOK = true;

			if ((comparacion > 0) && ((cond.equals(">")) || (cond.equals(">="))))
				condOK = true;

			if ((comparacion != 0) && (cond.equals(WorkflowUtils.CONDITION_DIFFERENT)))
				condOK = true;

			// Dates are different. Throws an exception.
			if (!condOK) {
				String condString = WorkflowUtils.getConditionString(cond);

				// Formats date to current locale, for display the Exception.
				SimpleDateFormat formatter = null;
				SimpleDateFormat defaultFormatter = null;

				if (includeTime.equals(WorkflowUtils.BOOLEAN_NO)) {
					defaultFormatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT));
					formatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT), locale);
				}else{
					defaultFormatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT));
					formatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT), locale);
				}

				String errorMsg = "";

				try{
					errorMsg = " ( " + formatter.format(objDate) + " )";
				} catch (IllegalArgumentException e) {
					try {
						errorMsg = " ( " + defaultFormatter.format(objDate) + " )";
					} catch(Exception e1) {
						errorMsg = " ( " + objDate + " )";
					}
				}

				this.setExceptionMessage(
						fldDate,
						"The field "+fldDate.getName() + " can't be " + condString + " than current date " + errorMsg,
						"The field "+fldDate.getName() + " can't be " + condString + " than current date " + errorMsg
				);
			}
		}
	}
}