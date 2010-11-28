package com.gsoft.workflow.functions;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
//import com.atlassian.jira.issue.customfields.impl.WatcherFieldType;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.util.UserUtil;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author <a href="http://bytes.org.ua/">Ganzha Vitaliy</a>
 * @author <a href="lbinder@folica.com">Lon F. Binder</a>
 */
public class AddUserToWatchersFunction extends MultiUserCFType
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
    private final CustomFieldValuePersister customFieldValuePersister;
    private final StringConverter stringConverter;
    private final GenericConfigManager genericConfigManager;
    private final MultiUserConverter multiUserConverter;
    private final ApplicationProperties applicationProperties;
    private final JiraAuthenticationContext authenticationContext;
    UserPickerSearchService searchService;

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
        super(customFieldValuePersister, stringConverter, genericConfigManager, multiUserConverter, applicationProperties, authenticationContext, searchService, null);
        _WatcherManager = ComponentManager.getInstance().getWatcherManager();
        this.customFieldValuePersister = customFieldValuePersister;
        this.stringConverter = stringConverter;
        this.genericConfigManager = genericConfigManager;
        this.authenticationContext = authenticationContext;
        this.searchService = searchService;
        this.multiUserConverter = multiUserConverter;
        this.applicationProperties = applicationProperties;
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
                user = ComponentManager.getInstance().getJiraAuthenticationContext().getUser();
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
     * Add a list of users as watchers on an issue.
     * 
     * @param issue The issue to add watchers to.
     * @param userList A list of User objects to add as watchers.
     */
    protected void addWatchers(Issue issue, List userList) {
        if (userList != null && isIssueEditable(issue)) {
            for (Iterator i = userList.iterator(); i.hasNext();) {
                User user = (User) i.next();

                if (!_WatcherManager.isWatching(user, issue.getGenericValue())) {
                    _WatcherManager.startWatching(user, issue.getGenericValue());
                }
            }
        }
    }

    /**
     * Checks to see if the issue can be edited.  It checks to see if the issue has been create, if it is
     * editable, and if the authenticated user has permissions.
     * 
     * @param issue The issue being edited.
     * @return True if able to edit, false otherwise.
     */
    protected boolean isIssueEditable(Issue issue) {
        if (issue.isCreated() && issue.isEditable() && isUserPermitted(issue)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a user the authenticated user has the "Manage Watcher List" permission.
     * 
     * @param issue The issue the user is trying to add watchers to.
     * @return True if has permissions, false otherwise.
     */
    public boolean isUserPermitted(Issue issue) {
        return ComponentManager.getInstance().getPermissionManager().hasPermission(
                Permissions.MANAGE_WATCHER_LIST,
                issue.getProjectObject(),
                authenticationContext.getUser());
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
            if (StringUtils.isNotBlank(username) && userUtil.userExists(username)) {
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
        usernames = usernames.replace(", ", ",");
        return usernames.trim().split("[|,]+");
    }

    public static void checkForValidUsers(String[] usernames) throws EntityNotFoundException {
        for (int i = 0; i < usernames.length; ++i) {
            UserUtils.getUser(usernames[i]);
        }
    }
} // Everyone likes the end of class
