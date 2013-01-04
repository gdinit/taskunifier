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
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleActionConfigurationDialog;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterEditPanel;
import com.leclercb.taskunifier.gui.components.tasktemplates.TaskTemplatePanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.TaskUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskRuleActionApplyTemplate implements TaskRuleAction {
	
	private static TaskRuleActionConfigurationDialog DIALOG;
	private static TaskTemplatePanel TASK_TEMPLATE_PANEL;
	private static TaskFilterEditPanel TASK_FILTER_PANEL;
	
	@XStreamAlias("filter")
	private TaskFilter filter;
	
	@XStreamAlias("template")
	private TaskTemplate template;
	
	public TaskRuleActionApplyTemplate() {
		this(new TaskFilter(), new TaskTemplate());
	}
	
	public TaskRuleActionApplyTemplate(TaskFilter filter, TaskTemplate template) {
		this.setFilter(filter);
		this.setTemplate(template);
	}
	
	public TaskFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(TaskFilter filter) {
		CheckUtils.isNotNull(filter);
		this.filter = filter;
	}
	
	public TaskTemplate getTemplate() {
		return this.template;
	}
	
	public void setTemplate(TaskTemplate template) {
		CheckUtils.isNotNull(template);
		this.template = template;
	}
	
	@Override
	public String toString() {
		return Translations.getString("taskrule.action.apply_template");
	}
	
	@Override
	public void execute(Task task) {
		if (this.template == null)
			return;
		
		for (Task t : TaskFactory.getInstance().getList()) {
			if (!t.getModelStatus().isEndUserStatus())
				continue;
			
			if (!TaskUtils.showUnindentTask(t, task, this.filter))
				continue;
			
			this.template.applyTo(t);
		}
	}
	
	@Override
	public void configure() {
		if (DIALOG == null) {
			DIALOG = new TaskRuleActionConfigurationDialog(
					"title",
					"description");
			
			TASK_TEMPLATE_PANEL = new TaskTemplatePanel();
			TASK_TEMPLATE_PANEL.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			TASK_FILTER_PANEL = new TaskFilterEditPanel();
			TASK_FILTER_PANEL.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			DIALOG.addTab("a", ComponentFactory.createJScrollPane(
					TASK_TEMPLATE_PANEL,
					false));
			
			DIALOG.addTab(
					"b",
					ComponentFactory.createJScrollPane(TASK_FILTER_PANEL, false));
		}
		
		TASK_TEMPLATE_PANEL.setTemplate(this.template);
		TASK_FILTER_PANEL.setFilter(this.filter);
		DIALOG.setVisible(true);
	}
	
	public static String getLabel() {
		return Translations.getString("taskrule.action.apply_template");
	}
	
}
