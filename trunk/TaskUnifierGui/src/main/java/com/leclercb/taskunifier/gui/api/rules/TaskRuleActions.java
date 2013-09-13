/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.api.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionAddSubTask;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionAddTask;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionApplyTemplate;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionCompleteTasks;
import com.leclercb.taskunifier.gui.api.rules.actions.TaskRuleActionDeleteTasks;

public class TaskRuleActions {
	
	private static TaskRuleActions INSTANCE;
	
	public static TaskRuleActions getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskRuleActions();
		
		return INSTANCE;
	}
	
	private Map<Class<?>, String> actions;
	
	public TaskRuleActions() {
		this.actions = new HashMap<Class<?>, String>();
		
		this.addRuleActionClass(
				TaskRuleActionAddSubTask.class,
				TaskRuleActionAddSubTask.getLabel());
		this.addRuleActionClass(
				TaskRuleActionAddTask.class,
				TaskRuleActionAddTask.getLabel());
		this.addRuleActionClass(
				TaskRuleActionApplyTemplate.class,
				TaskRuleActionApplyTemplate.getLabel());
		this.addRuleActionClass(
				TaskRuleActionCompleteTasks.class,
				TaskRuleActionCompleteTasks.getLabel());
		this.addRuleActionClass(
				TaskRuleActionDeleteTasks.class,
				TaskRuleActionDeleteTasks.getLabel());
	}
	
	public <A extends TaskRuleAction> void addRuleActionClass(
			Class<A> action,
			String label) {
		this.actions.put(action, label);
	}
	
	public String getActionLabel(Class<?> action) {
		return this.actions.get(action);
	}
	
	public List<Class<?>> getActions() {
		return new ArrayList<Class<?>>(this.actions.keySet());
	}
	
}
