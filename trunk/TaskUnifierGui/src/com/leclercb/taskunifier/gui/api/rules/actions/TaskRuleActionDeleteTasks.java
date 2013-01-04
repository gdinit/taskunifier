/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.api.rules.actions;

import javax.swing.border.EmptyBorder;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleActionConfigurationDialog;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterEditPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.TaskUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskRuleActionDeleteTasks implements TaskRuleAction {
	
	private static TaskRuleActionConfigurationDialog DIALOG;
	private static TaskFilterEditPanel TASK_FILTER_PANEL;
	
	@XStreamAlias("filter")
	private TaskFilter filter;
	
	public TaskRuleActionDeleteTasks() {
		this(new TaskFilter());
	}
	
	public TaskRuleActionDeleteTasks(TaskFilter filter) {
		this.setFilter(filter);
	}
	
	public TaskFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(TaskFilter filter) {
		CheckUtils.isNotNull(filter);
		this.filter = filter;
	}
	
	@Override
	public void execute(Task task) {
		for (Task t : TaskFactory.getInstance().getList()) {
			if (!t.getModelStatus().isEndUserStatus())
				continue;
			
			if (!TaskUtils.showUnindentTask(t, task, this.filter))
				continue;
			
			TaskFactory.getInstance().markToDelete(t);
		}
	}
	
	@Override
	public void configure() {
		if (DIALOG == null) {
			DIALOG = new TaskRuleActionConfigurationDialog(
					"title",
					"description");
			
			TASK_FILTER_PANEL = new TaskFilterEditPanel();
			TASK_FILTER_PANEL.setAllowCompareModel(true);
			TASK_FILTER_PANEL.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			DIALOG.addTab(
					"b",
					ComponentFactory.createJScrollPane(TASK_FILTER_PANEL, false));
		}
		
		TASK_FILTER_PANEL.setFilter(this.filter);
		DIALOG.setVisible(true);
	}
	
	@Override
	public String toString() {
		return Translations.getString("taskrule.action.delete_tasks");
	}
	
	public static String getLabel() {
		return Translations.getString("taskrule.action.delete_tasks");
	}
	
}
