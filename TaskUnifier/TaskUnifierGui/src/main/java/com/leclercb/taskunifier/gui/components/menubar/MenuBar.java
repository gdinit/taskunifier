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
package com.leclercb.taskunifier.gui.components.menubar;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.actions.*;
import com.leclercb.taskunifier.gui.actions.publish.ActionPublish;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronize;
import com.leclercb.taskunifier.gui.actions.synchronize.ActionSynchronizeAndPublish;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.TemplateUtils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MenuBar extends JMenuBar implements ListChangeListener, PropertyChangeListener {
	
	private JMenu templatesMenu;
	
	public MenuBar() {
		this.initialize();
	}
	
	private void initialize() {
		this.initializeFileMenu();
		this.initializeEditMenu();
		this.initializeViewMenu();
		this.initializeContactsMenu();
		this.initializeNotesMenu();
		this.initializeTasksMenu();
		this.initializeSynchronizeMenu();
		this.initializeHelpMenu();
	}
	
	private void initializeFileMenu() {
		JMenu fileMenu = new JMenu(Translations.getString("menu.file"));
		this.add(fileMenu);
		
		//fileMenu.add(new ActionManageLicense(16, 16));
		//fileMenu.addSeparator();
		
		fileMenu.add(new ActionNewWindow(16, 16));
		fileMenu.add(new ActionCloseWindow(16, 16));
		fileMenu.addSeparator();
		
		JMenu addTabMenu = new JMenu(new ActionAddTab(16, 16));
		fileMenu.add(addTabMenu);
		for (ViewType type : ViewType.values()) {
			addTabMenu.add(new JMenuItem(new ActionAddTab(16, 16, type)));
		}
		fileMenu.addSeparator();
		
		fileMenu.add(new ActionChangeDataFolderLocation(16, 16));
		fileMenu.addSeparator();
		
		JMenu importMenu = new JMenu(Translations.getString("general.import"));
		importMenu.setIcon(ImageUtils.getResourceImage("download.png", 16, 16));
		
		importMenu.add(new ActionImportComFile(16, 16));
		importMenu.add(new ActionImportModels(16, 16));
		importMenu.add(new ActionImportSettings(16, 16));
		importMenu.add(new ActionImportNoteSearchers(16, 16));
		if (Main.isDeveloperMode())
			importMenu.add(new ActionImportTaskRules(16, 16));
		importMenu.add(new ActionImportTaskSearchers(16, 16));
		importMenu.add(new ActionImportTaskTemplates(16, 16));
		importMenu.add(new ActionImportVCard(16, 16));
		fileMenu.add(importMenu);
		
		JMenu exportMenu = new JMenu(Translations.getString("general.export"));
		exportMenu.setIcon(ImageUtils.getResourceImage("upload.png", 16, 16));
		
		exportMenu.add(new ActionExportModels(16, 16));
		exportMenu.add(new ActionExportSettings(16, 16));
		exportMenu.add(new ActionExportNoteSearchers(16, 16));
		if (Main.isDeveloperMode())
			exportMenu.add(new ActionExportTaskRules(16, 16));
		exportMenu.add(new ActionExportTaskSearchers(16, 16));
		exportMenu.add(new ActionExportTaskTemplates(16, 16));
		exportMenu.add(new ActionExportVCard(16, 16));
		fileMenu.add(exportMenu);
		
		fileMenu.addSeparator();
		fileMenu.add(new ActionConfiguration(16, 16));
		fileMenu.add(new ActionManageUsers(16, 16));
		fileMenu.add(new ActionManageModels(16, 16));
		fileMenu.add(new ActionManageTaskTemplates(16, 16));
		fileMenu.addSeparator();
		fileMenu.add(new ActionManagePublisherPlugins(16, 16));
		fileMenu.add(new ActionManageSynchronizerPlugins(16, 16));
		fileMenu.addSeparator();
		fileMenu.add(new ActionPrint(16, 16));
		fileMenu.add(new ActionPrintSelectedModels(16, 16));
		fileMenu.addSeparator();
		fileMenu.add(new ActionCreateNewBackup(16, 16));
		fileMenu.add(new ActionManageBackups(16, 16));
		fileMenu.addSeparator();
		fileMenu.add(new ActionQuit(16, 16));
	}
	
	private void initializeEditMenu() {
		JMenu editMenu = new JMenu(Translations.getString("menu.edit"));
		this.add(editMenu);
		
		editMenu.add(Constants.UNDO_SUPPORT.getUndoAction());
		editMenu.add(Constants.UNDO_SUPPORT.getRedoAction());
		
		editMenu.addSeparator();
		
		editMenu.add(new ActionCut(16, 16));
		editMenu.add(new ActionCopy(16, 16));
		editMenu.add(new ActionPaste(16, 16));
		
		editMenu.addSeparator();
		
		editMenu.add(new ActionSearch(16, 16));
	}
	
	private void initializeViewMenu() {
		JMenu viewMenu = new JMenu(Translations.getString("menu.view"));
		this.add(viewMenu);
		
		viewMenu.add(new ActionChangeView(16, 16));
		viewMenu.add(new ActionRefresh(16, 16));
	}
	
	private void initializeContactsMenu() {
		JMenu contactsMenu = new JMenu(Translations.getString("menu.contacts"));
		this.add(contactsMenu);
		
		contactsMenu.add(new ActionMailTo(16, 16));
		
		contactsMenu.addSeparator();
		
		contactsMenu.add(new ActionImportComFile(16, 16));
	}
	
	private void initializeNotesMenu() {
		JMenu notesMenu = new JMenu(Translations.getString("menu.notes"));
		this.add(notesMenu);
		
		notesMenu.add(new ActionAddNote(16, 16));
		notesMenu.add(new ActionDuplicateNotes(16, 16));
		
		notesMenu.addSeparator();
		
		notesMenu.add(new ActionDelete(16, 16));
	}
	
	private void initializeTasksMenu() {
		JMenu tasksMenu = new JMenu(Translations.getString("menu.tasks"));
		this.add(tasksMenu);
		
		tasksMenu.add(new ActionAddTask(16, 16));
		tasksMenu.add(new ActionAddSubTask(16, 16));
		tasksMenu.add(new ActionAddSubTaskAtSameLevel(16, 16));
		
		this.initializeTemplateMenu(tasksMenu);
		
		tasksMenu.add(new ActionBatchAddTasks(16, 16));
		tasksMenu.add(new ActionDuplicateTasks(16, 16));
		
		tasksMenu.addSeparator();
		
		tasksMenu.add(new ActionEditTasks(16, 16));
		tasksMenu.add(ComponentFactory.createPostponeMenu());
		tasksMenu.add(new ActionCompleteTasks(16, 16));
		
		tasksMenu.addSeparator();
		
		tasksMenu.add(new ActionManageTaskCustomColumns(16, 16));
		if (Main.isDeveloperMode())
			tasksMenu.add(new ActionManageTaskRules(16, 16));
		tasksMenu.add(new ActionManageTaskTemplates(16, 16));
		tasksMenu.add(new ActionCreateTaskTemplateFromTask(16, 16));
        tasksMenu.add(new ActionCreateNoteFromTask(16, 16));
        tasksMenu.add(new ActionTaskReminders(16, 16));
		
		tasksMenu.addSeparator();
		
		tasksMenu.add(new ActionCollapseAll(16, 16));
		tasksMenu.add(new ActionExpandAll(16, 16));
		tasksMenu.add(new ActionSelectParentTasks(16, 16));
		
		tasksMenu.addSeparator();
		
		tasksMenu.add(new ActionDelete(16, 16));
	}
	
	private void initializeSynchronizeMenu() {
		JMenu synchronizeMenu = new JMenu(
				Translations.getString("menu.synchronize"));
		this.add(synchronizeMenu);
		
		synchronizeMenu.add(new ActionSynchronize(16, 16, false));
		synchronizeMenu.add(new ActionPublish(16, 16, false));
		synchronizeMenu.addSeparator();
		synchronizeMenu.add(new ActionSynchronizeAndPublish(16, 16, false));
		synchronizeMenu.addSeparator();
		synchronizeMenu.add(new ActionScheduledSync(16, 16));
	}
	
	private void initializeTemplateMenu(JMenu tasksMenu) {
		this.templatesMenu = new JMenu(
				Translations.getString("action.add_template_task"));
		
		this.templatesMenu.setToolTipText(Translations.getString("action.add_template_task"));
		
		this.templatesMenu.setIcon(ImageUtils.getResourceImage(
				"template.png",
				16,
				16));
		tasksMenu.add(this.templatesMenu);
		
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
	
	private void initializeHelpMenu() {
		JMenu helpMenu = new JMenu(Translations.getString("menu.help"));
		this.add(helpMenu);
		
		helpMenu.add(new ActionCheckVersion(16, 16, false));
		helpMenu.add(new ActionCheckPluginVersion(16, 16, false));
		helpMenu.addSeparator();
		helpMenu.add(new ActionHelp(16, 16));
		helpMenu.add(new ActionShowTips(16, 16));
		helpMenu.add(new ActionAbout(16, 16));
		helpMenu.addSeparator();
		helpMenu.add(new ActionGetLogs(16, 16));
		helpMenu.addSeparator();
		helpMenu.add(new ActionOpenForum());
		helpMenu.add(new ActionLogBug());
		helpMenu.add(new ActionLogFeatureRequest());
		helpMenu.addSeparator();
		helpMenu.add(new ActionDonate(16, 16));
		helpMenu.add(new ActionReview(16, 16));
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
