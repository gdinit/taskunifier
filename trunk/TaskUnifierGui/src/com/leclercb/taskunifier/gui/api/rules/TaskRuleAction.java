package com.leclercb.taskunifier.gui.api.rules;

import com.leclercb.taskunifier.api.models.Task;

public interface TaskRuleAction {
	
	public abstract void execute(Task task);
	
}
