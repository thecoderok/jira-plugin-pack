package com.gsoft.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.MultipleSettableCustomFieldType;
import com.atlassian.jira.issue.customfields.config.item.SettableOptionsConfigItem;
import com.atlassian.jira.issue.customfields.converters.SelectConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.imports.project.customfield.ProjectCustomFieldImporter;
import com.atlassian.jira.imports.project.customfield.SelectCustomFieldImporter;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.option.LazyLoadedOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="http://bytes.org.ua/">Vitaliy Ganzha</a>
 */
public class RegExpCF extends TextCFType implements MultipleSettableCustomFieldType{

    private final SelectConverter selectConverter;
    private final OptionsManager optionsManager;
    private final ProjectCustomFieldImporter projectCustomFieldImporter;

    public RegExpCF(CustomFieldValuePersister customFieldValuePersister, StringConverter stringConverter,
                        SelectConverter selectConverter, OptionsManager optionsManager, GenericConfigManager genericConfigManager)
    {
        super(customFieldValuePersister, stringConverter, genericConfigManager);
        this.selectConverter = selectConverter;
        this.optionsManager = optionsManager;
        this.projectCustomFieldImporter = new SelectCustomFieldImporter(this.optionsManager);
    }

    @Override
    public List getConfigurationItemTypes()
    {
        final List configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new SettableOptionsConfigItem(this, optionsManager));
        return configurationItemTypes;
    }

    /**
     * Ensures that the {@link CustomFieldParams} of Strings is a valid representation of the Custom Field values.
     * Any errors should be added to the {@link ErrorCollection} under the appropriate key as required.
     *
     * @param relevantParams parameter object of Strings
     * @param errorCollectionToAddTo errorCollection to which any erros should be added (never null)
     * @param config
     */

    @Override
    public void	validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config)
    {
        List<LazyLoadedOption> rootOptions = optionsManager.getOptions(config).getRootOptions();
        if (rootOptions == null || rootOptions.size() == 0)
            return;
        String patternStr = rootOptions.get(0).getValue();
        Collection<String> value = relevantParams.getValuesForNullKey();
        String fieldValue = null;

        for (String s: value)
        {
            fieldValue = s;
        }

        if (fieldValue == null)
            return;

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(fieldValue);

        boolean found = false;
        while (matcher.find())
        {
            found = true;
            break;
        }

        if (!found)
        {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), "Value entered does not match the given format!");
        }
    }

    /**
     * Returns a Set with of Long Objects representing the issue ids that the value has been set for
     *
     * @param field       the CustomField to search on
     * @param option the Object representing a single value to search on.
     * @return Set of Longs
     */
    
    public Set getIssueIdsWithValue(CustomField field, Option option)
    {
        if (option != null)
        {
            return customFieldValuePersister.getIssueIdsWithValue(field, PersistenceFieldType.TYPE_LIMITED_TEXT, option.getValue());
        }
        else
        {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * This default implementation will remove all values from the custom field for an issue. Since there can only be
     * one value for each CustomField instance, this implemenation can safely ignore the objectValue
     *
     * @param field
     * @param issue
     * @param option - ignored
     */
    public void removeValue(CustomField field, Issue issue, Option option)
    {
        updateValue(field, issue, null);
    }

    public Options getOptions(FieldConfig config, JiraContextNode jiraContextNode)
    {
        return optionsManager.getOptions(config);
    }

}
