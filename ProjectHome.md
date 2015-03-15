Features

Issue hasn’t unclosed subtask (workflow condition) – allow to execute transition only if all issue’s subtasks are closed:

Add condition to workflow
View Condition

User in/not in specified group (workflow condition) – allow only users in (or not in) a given group to execute a transition:

user is in group JIRA plugin pack
user is in group 3 JIRA plugin pack
user is in group 2 JIRA plugin pack

User in/not in specified field (workflow condition) – allow only users in (or not in) a given field to execute a transition:

user in field JIRA plugin pack
JIRA plugin pack
user in field 3 JIRA plugin pack

Assign to parent issue assignee (workflow post function) – The  issue (sub-task) will be assigned to the parent issue assignee:

assign to parent issue assignee function JIRA plugin pack

Regex replace field value (workflow post function) – The value of selected field will be replaced with regular expression (Supports Perl-style patterns - http://www.perl.com/doc/manual/html/pod/perlre.html):

reg replace field value JIRA plugin pack
reg replace field value 2 JIRA plugin pack

Set field value from User (User gets from field) Property value (workflow post function) – The value of selected field will be taken from Properties of User from field:
Add user to watcher list (workflow post function) – Add user (Assignee, Current, Reporter) to watcher list:

add user to watcherlist JIRA plugin pack
set-cfvalue-from-userprops

Assign issue to group member (workflow post function) – Issue is assigned to the first member of group from selected Group Select Custom Field:

assign from group customfield function JIRA plugin pack

Assign to parent issue reporter (workflow post function) – The issue (sub-task) will be assigned to the parent issue reporter;

Regular Expression Match Validator – The value of the field must mutch to the specified pattern (Supports Perl-style patterns – http://www.perl.com/doc/manual/html/pod/perlre.html)

regular expression match validator JIRA plugin pack

Compare Date Field With Current Date (workflow validator) -Compare date field with current date during a workflow transition. Based on JIRA Suite Utilities plugin:

dateCompareNow validator JIRA plugin pack
dateCompareNow validator 2 JIRA plugin pack

Administer user dashboards (webwork action) – allows to set custom dashboards to users or groups. After you have this plugin installed, in your administration menu appear link: User Dashboards