package com.gsoft.workflow.functions.factories;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.gsoft.workflow.functions.AddUserToWatchersFunction;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author <a href="http://bytes.org.ua/">Ganzha Vitaliy</a>
 * @author <a href="lbinder@folica.com">Lon F. Binder</a>
 */
public class AddUserToWatchersFactory extends AbstractWorkflowPluginFactory
		implements WorkflowPluginFunctionFactory {

	/* ----- FIELDS ----- */

	private ApplicationProperties applicationProperties;
	private JiraAuthenticationContext authenticationContext;
	private UserPickerSearchService searchService;

	/* ----- CONSTRUCTORS ----- */

	/**
	 * 
	 * 
	 * @param applicationProperties
	 * @param authenticationContext
	 * @param searchService
	 */
	public AddUserToWatchersFactory(
			ApplicationProperties applicationProperties,
			JiraAuthenticationContext authenticationContext,
			UserPickerSearchService searchService) {
		super();
		this.applicationProperties = applicationProperties;
		this.authenticationContext = authenticationContext;
		this.searchService = searchService;
	}

	/**
	 * 
	 * @param resourceName
	 * @param descriptor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map getVelocityParams(String resourceName,
			AbstractDescriptor descriptor) {
		// TODO Auto-generated method stub
		Map velocityParams = super.getVelocityParams(resourceName, descriptor);
		
		// For picker
        boolean canPickUsers = ManagerFactory.getPermissionManager().hasPermission(Permissions.USER_PICKER, authenticationContext.getUser());
//        final List<?> currentUsers = UserBean.convertUsersToUserBeans(currentUsers);
		velocityParams.put("userUtil", ComponentManager.getComponentInstanceOfType(UserUtil.class));
//		velocityParams.put("currentSelections", currentUsers);
		//velocityParams.put("i18n", i18n);
		velocityParams.put("canPick", Boolean.valueOf(canPickUsers));
		velocityParams.put("windowName", "UserPicker");

		JiraServiceContext ctx = new JiraServiceContextImpl(
				authenticationContext.getUser());

		boolean canPerformAjaxSearch = searchService.canPerformAjaxSearch(ctx);
		if (canPerformAjaxSearch) {
			velocityParams.put("canPerformAjaxSearch", "true");
			velocityParams.put("ajaxLimit", applicationProperties
					.getDefaultBackedString(APKeys.JIRA_AJAX_AUTOCOMPLETE_LIMIT));
			WebResourceManager webResourceManager = ComponentManager
					.getInstance().getWebResourceManager();
			/* Deprecated:
			webResourceManager.requireResource("jira.webresources:dwr");
			webResourceManager.requireResource("jira.webresources:browser-detect");
			webResourceManager.requireResource("jira.webresources:yui");
			webResourceManager.requireResource("jira.webresources:yui-autocomplete");
			webResourceManager.requireResource("jira.webresources:ajax-userpicker");
			*/
			//For JIRA 4.x
			webResourceManager.requireResource("jira.webresources:autocomplete");
		}

		if (canPickUsers) {
			velocityParams.put("pickusers", "true");
		}

		return velocityParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getVelocityParamsForInput(Map velocityParams) {
		return;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		getVelocityParamsForView(velocityParams, descriptor);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;

		// If the userSelect value is a specified user, we need to get the name
		String newWatcherName = (String) functionDescriptor.getArgs().get(
				AddUserToWatchersFunction.ARGSKEY_USER_SELECT);
		velocityParams.put("val", newWatcherName);
		
		if (AddUserToWatchersFunction.SELECTED_USER.equals(newWatcherName)) {
			String usernames = (String) functionDescriptor.getArgs()
				.get(AddUserToWatchersFunction.ARGSKEY_SELECT_USERS);
			
			velocityParams.put("value", AddUserToWatchersFunction
					.convertUsernamesToArray(usernames));
                        velocityParams.put("val", usernames);
		}

	}
	
	
	@SuppressWarnings("unchecked")
	public Map getDescriptorParams(Map functionParams) {
		Map params = new HashMap();
		String value = extractSingleParam(functionParams, 
				AddUserToWatchersFunction.ARGSKEY_USER_SELECT);
		params.put(AddUserToWatchersFunction.ARGSKEY_USER_SELECT, value);
		
		// If specific users have been named, let's track those as well
		if (AddUserToWatchersFunction.SELECTED_USER.equals(value)) {
			String names = extractSingleParam(functionParams,
					"UserPicker");

        params.put(AddUserToWatchersFunction.ARGSKEY_SELECT_USERS,
			AddUserToWatchersFunction.trimUsernames(names));
		}

		return params;
	}

} // Everyone likes the end of class

