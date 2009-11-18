package com.gsoft.workflow.functions;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.WatcherFieldType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;


/**
 *
 * @author Ganzha Vitaliy
 * @author <a href="lbinder@folica.com">Lon F. Binder</a>
 */
public class AddUserToWatchersFunction extends WatcherFieldType 
		implements FunctionProvider {

	/* -----  CONSTANTS ----- */
	public static final String ASSIGNEE = "assignee";
	public static final String REPORTER = "reporter";
	public static final String CURRENT_USER = "currentuser";
	public static final String SELECTED_USER = "selectuser";
	/**
	 * This key indicates the full name of the new watcher user(s)
	 */
	public static final String ARGSKEY_NEW_WATCHER = "newWatcher";
	public static final String ARGSKEY_USER_SELECT = "userSelect";
	/**
	 * The list of selected users.
	 */
	public static final String ARGSKEY_SELECT_USERS = "selectedUsers";
	private static final String STR_COMMA = ",";
	private static final String STR_COMMA_SPACED = ", ";
	


	/* ----- FIELDS ----- */
	private final WatcherManager _WatcherManager;
	
	
	/* ----- CONSTRUCTORS ----- */
	
	/**
	 * Overridden, calls super constructor.
	 *
	 * @param customFieldValuePersister
	 * @param stringConverter
	 * @param genericConfigManager
	 * @param multiUserConverter
	 * @param applicationProperties
	 * @param authenticationContext
	 * @param searchService
	 * @see com.atlassian.jira.issue.customfields.impl.MultiUserCFType
	 */
	public AddUserToWatchersFunction(
			CustomFieldValuePersister customFieldValuePersister,
			StringConverter stringConverter,
			GenericConfigManager genericConfigManager,
			MultiUserConverter multiUserConverter,
			ApplicationProperties applicationProperties,
			JiraAuthenticationContext authenticationContext,
			UserPickerSearchService searchService) {
		super(customFieldValuePersister, stringConverter, genericConfigManager,
				multiUserConverter, applicationProperties, authenticationContext,
				searchService);
		_WatcherManager = ComponentManager.getInstance().getWatcherManager();
	}


	/* ----- METHODS ----- */
	
	/**
	 * This is the function executed by the workflow (during the post 
	 * transition).
	 *
	 * @param transientVars
	 * @param args
	 * @param ps
	 * @throws WorkflowException
	 */
	@SuppressWarnings("unchecked")
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String selected = (String) args.get(ARGSKEY_USER_SELECT);
        MutableIssue issue = (MutableIssue) transientVars.get("issue");

        User user = null;
        String userFullname = null;

        if (SELECTED_USER.equals(selected)) {
        	String selectedUsernames = (String) args.get(ARGSKEY_SELECT_USERS);
        	List<User> selectedUsers = convertUsernames(selectedUsernames);
    		addWatchers(issue, selectedUsers);
    		
    		StringBuilder sbUserFullNames = 
    			new StringBuilder(selectedUsernames.length());
    		boolean atLeastOneNewWatcher = false;
    		for (User newUser : selectedUsers) {
                // Ensure user is not already watching
                if (_WatcherManager.isWatching(newUser, issue.getGenericValue())) {
                    continue;
                }
                
                atLeastOneNewWatcher = true;
                
    			if (sbUserFullNames.length() != 0) {
    				sbUserFullNames.append(STR_COMMA_SPACED);
    			}
				sbUserFullNames.append(newUser.getFullName());
    		}
    		
    		if (!atLeastOneNewWatcher) {
    			return;
    		}
    		
    		userFullname = sbUserFullNames.toString();
    		
        } else {
            if (ASSIGNEE.equals(selected)) {
                user = issue.getAssignee();
            } else if (REPORTER.equals(selected)) {
                user = issue.getReporter();
            } else /*if (CURRENT_USER.equals(selected))*/ {
                user = ComponentManager.getInstance()
                	.getJiraAuthenticationContext().getUser();
            }

            // Ensure user is not already watching
            if (_WatcherManager.isWatching(user, issue.getGenericValue())) {
                return;
            }

            _WatcherManager.startWatching(user, issue.getGenericValue());
            userFullname = user.getFullName();
        }
    		

        // No user selected, something went wrong...
        if (userFullname == null) {
            return;
        }

        // Ensure that the user has the appropriate permissions to watch the issue
        // TODO: fix this to handle when multiple users are selected
//        if (!_PermissionManager.hasPermission(
//        		Permissions.VIEW_VOTERS_AND_WATCHERS, issue, user)) {
//            return;
//        }

        args.put(ARGSKEY_NEW_WATCHER, userFullname);
    }

	/**
	 * Converts a comma-delimited list of user names to a List of Users.
	 *
	 * @param usernames
	 * @return List of Users.
	 */
	public static List<User> convertUsernames(String usernames) {
		ArrayList<User> users = new ArrayList<User>();
		StringTokenizer st = new StringTokenizer(usernames, STR_COMMA);
		UserUtil userUtil = ComponentManager.getInstance().getUserUtil();
		while (st.hasMoreTokens()) {
			String username = st.nextToken().trim();
			if (StringUtils.isNotBlank(username)) {
				users.add(userUtil.getUser(username));
			}
		}
		
		return users;
	}
	
	/**
	 * Removes any extra commas or whitespace from the username list string.
	 *
	 * @param usernames A dirty list of usernames
	 * @return A clean list of usernames
	 */
	public static String trimUsernames(String usernames) {
		if (StringUtils.isBlank(usernames)) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder(usernames.length());
		StringTokenizer st = new StringTokenizer(usernames, STR_COMMA);
		while (st.hasMoreTokens()) {
			String username = st.nextToken().trim();
			if (StringUtils.isNotBlank(username)) {
				if (sb.length() > 0) {
					sb.append(STR_COMMA_SPACED);
				}
				sb.append(username);
			}
		}
		
		return sb.toString();
	}

	/**
	 * Convert a comma-delimited list of usernames to an array of usernames.
	 *
	 * @param usernames
	 * @return
	 */
	public static String[] convertUsernamesToArray(String usernames) {
		if (StringUtils.isBlank(usernames)) {
			return null;
		}
		
		return usernames.trim().split("[\\s|,]+");
	}
	
} // Everyone likes the end of class
