package com.leclercb.taskunifier.gui.api.rules;

import java.util.ArrayList;
import java.util.List;

import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionAddSubTask;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionAddTask;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionApplyTemplate;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionDeleteSubTask;

public class TaskRuleActions {
	
	private static TaskRuleActions INSTANCE;
	
	public static TaskRuleActions getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskRuleActions();
		
		return INSTANCE;
	}
	
	private List<Class<?>> actions;
	
	public TaskRuleActions() {
		this.actions = new ArrayList<Class<?>>();
		
		this.addRuleActionClass(TaskRuleActionAddSubTask.class);
		this.addRuleActionClass(TaskRuleActionAddTask.class);
		this.addRuleActionClass(TaskRuleActionApplyTemplate.class);
		this.addRuleActionClass(TaskRuleActionDeleteSubTask.class);
	}
	
	public <A extends TaskRuleAction> void addRuleActionClass(Class<A> action) {
		this.actions.add(action);
	}
	
}
