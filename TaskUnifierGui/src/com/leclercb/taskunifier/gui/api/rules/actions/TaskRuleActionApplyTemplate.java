package com.leclercb.taskunifier.gui.api.rules.actions;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;

public class TaskRuleActionApplyTemplate implements TaskRuleAction {
	
	private TaskTemplate template;
	
	public TaskRuleActionApplyTemplate(TaskTemplate template) {
		CheckUtils.isNotNull(template);
		this.template = template;
	}
	
	@Override
	public void execute(Task task) {
		this.template.applyTo(task);
	}
	
}
