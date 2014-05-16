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
package com.leclercb.taskunifier.gui.components.tasks.table.menu;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.actions.*;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.TemplateUtils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TaskTableMenu extends JPopupMenu implements ListChangeListener, PropertyChangeListener {
	
	private JMenu templatesMenu;
	
	public TaskTableMenu() {
		super(Translations.getString("general.task"));
		
		this.initialize();
	}
	
	private void initialize() {
		this.add(new ActionEditTasks(16, 16));
		this.add(ComponentFactory.createPostponeMenu());
		this.addSeparator();
		this.add(new ActionAddTask(16, 16));
		this.initializeTemplateMenu();
		this.add(new ActionAddSubTask(16, 16));
		this.add(new ActionAddSubTaskAtSameLevel(16, 16));
		this.add(new ActionDuplicateTasks(16, 16));
		this.addSeparator();
		this.add(new ActionRefresh(16, 16));
		this.addSeparator();
		this.add(new JMenuItem(new ActionCollapseAll(16, 16)));
		this.add(new JMenuItem(new ActionExpandAll(16, 16)));
		this.add(new JMenuItem(new ActionSelectParentTasks(16, 16)));
		this.addSeparator();
		this.add(new ActionCreateTaskTemplateFromTask(16, 16));
        this.add(new ActionCreateNoteFromTask(16, 16));
        this.add(new ActionMailTo(16, 16));
		this.add(new ActionPrintSelectedModels(16, 16));
		this.addSeparator();
		this.add(new ActionDelete(16, 16));
	}
	
	private void initializeTemplateMenu() {
		this.templatesMenu = new JMenu(
				Translations.getString("action.add_template_task"));
		
		this.templatesMenu.setToolTipText(Translations.getString("action.add_template_task"));
		
		this.templatesMenu.setIcon(ImageUtils.getResourceImage(
				"template.png",
				16,
				16));
		this.add(this.templatesMenu);
		
		TemplateUtils.updateTemplateList(
				ActionAddTemplateTask.ADD_TASK_LISTENER,
				this.templatesMenu);
		
		TaskTemplateFactory.getInstance().addPropertyChangeListener(
				BasicModel.PROP_MODEL_STATUS,
				new WeakPropertyChangeListener(
						TaskTemplateFactory.getInstance(),
						this));
		
		TaskTemplateFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(
						TaskTemplateFactory.getInstance(),
						this));
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		TemplateUtils.updateTemplateList(
				ActionAddTemplateTask.ADD_TASK_LISTENER,
				this.templatesMenu);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (((ModelStatus) evt.getOldValue()).isEndUserStatus() != ((ModelStatus) evt.getNewValue()).isEndUserStatus()) {
			TemplateUtils.updateTemplateList(
					ActionAddTemplateTask.ADD_TASK_LISTENER,
					this.templatesMenu);
		}
	}
	
}
