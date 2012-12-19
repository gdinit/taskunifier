package com.leclercb.taskunifier.gui.api.rules.actions;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.actions.ActionAddSubTask;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;

public class TaskRuleActionAddSubTask implements TaskRuleAction {
	
	private TaskTemplate template;
	
	public TaskRuleActionAddSubTask() {
		this(null);
	}
	
	public TaskRuleActionAddSubTask(TaskTemplate template) {
		this.template = template;
	}
	
	@Override
	public void execute(Task task) {
		ActionAddSubTask.addSubTask(this.template, task, false);
	}
	
}
