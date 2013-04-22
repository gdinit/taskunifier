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
package com.leclercb.taskunifier.gui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.KeyStroke;

import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionAddTask extends AbstractViewAction {
	
	public ActionAddTask(int width, int height) {
		super(
				Translations.getString("action.add_task"),
				ImageUtils.getResourceImage("task.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.add_task"));
		
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_T,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ActionAddTask.addTask((String) null, true);
	}
	
	public static Task addTask(String title, boolean edit) {
		return addTask(
				TaskTemplateFactory.getInstance().getDefaultTemplate(),
				title,
				edit);
	}
	
	public static Task addTask(TaskTemplate template, String title, boolean edit) {
		ViewType viewType = ViewUtils.getCurrentViewType();
		
		TaskTemplate searcherTemplate = null;
		
		if (viewType != ViewType.TASKS && viewType != ViewType.CALENDAR) {
			ViewUtils.setTaskView(true);
			viewType = ViewUtils.getCurrentViewType();
		} else {
			searcherTemplate = null;
			
			if (ViewUtils.getSelectedTaskSearcher() != null)
				searcherTemplate = ViewUtils.getSelectedTaskSearcher().getTemplate();
		}
		
		Task task = TaskFactory.getInstance().create(
				Translations.getString("task.default.title"));
		
		if (template != null)
			template.applyTo(task);
		
		if (searcherTemplate != null)
			searcherTemplate.applyTo(task);
		
		if (title != null)
			task.setTitle(title);
		
		ViewUtils.addExtraTasks(task);
		
		if (edit) {
			if (viewType == ViewType.CALENDAR
					|| Main.getSettings().getBooleanProperty(
							"task.show_edit_window_on_add")) {
				if (!ActionEditTasks.editTasks(new Task[] { task }, true))
					TaskFactory.getInstance().markDeleted(task);
			} else {
				ViewUtils.getCurrentTaskView().getTaskTableView().setSelectedTaskAndStartEdit(
						task);
			}
		} else {
			if (viewType == ViewType.TASKS)
				ViewUtils.getCurrentTaskView().getTaskTableView().setSelectedTasks(
						new Task[] { task });
		}
		
		return task;
	}
	
	public static synchronized Task addTask(TaskBean taskBean, boolean edit) {
		ViewType viewType = ViewUtils.getCurrentViewType();
		
		if (viewType != ViewType.TASKS && viewType != ViewType.CALENDAR) {
			ViewUtils.setTaskView(true);
			viewType = ViewUtils.getCurrentViewType();
		}
		
		Task task = TaskFactory.getInstance().create(
				Translations.getString("task.default.title"));
		
		if (taskBean != null) {
			taskBean.setModelStatus(ModelStatus.TO_UPDATE);
			taskBean.setModelCreationDate(Calendar.getInstance());
			taskBean.setModelUpdateDate(Calendar.getInstance());
			
			task.loadBean(taskBean, false);
		}
		
		ViewUtils.addExtraTasks(task);
		
		if (edit) {
			if (viewType == ViewType.CALENDAR
					|| Main.getSettings().getBooleanProperty(
							"task.show_edit_window_on_add")) {
				if (!ActionEditTasks.editTasks(new Task[] { task }, true))
					TaskFactory.getInstance().markDeleted(task);
			} else {
				ViewUtils.getCurrentTaskView().getTaskTableView().setSelectedTaskAndStartEdit(
						task);
			}
		} else {
			if (viewType == ViewType.TASKS)
				ViewUtils.getCurrentTaskView().getTaskTableView().setSelectedTasks(
						new Task[] { task });
		}
		
		return task;
	}
	
}
