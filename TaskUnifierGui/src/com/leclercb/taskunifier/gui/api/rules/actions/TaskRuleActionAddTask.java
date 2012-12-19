package com.leclercb.taskunifier.gui.api.rules.actions;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.actions.ActionAddTask;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;

public class TaskRuleActionAddTask implements TaskRuleAction {
	
	private TaskTemplate template;
	
	public TaskRuleActionAddTask() {
		this(null);
	}
	
	public TaskRuleActionAddTask(TaskTemplate template) {
		this.template = template;
	}
	
	@Override
	public void execute(Task task) {
		ActionAddTask.addTask(this.template, null, false);
	}
	
}
