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
package com.leclercb.taskunifier.gui.components.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.painter.Painter;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.taskunifier.api.models.ContactList.ContactItem;
import com.leclercb.taskunifier.api.models.FileList.FileItem;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.components.modelnote.ModelNotePanel;
import com.leclercb.taskunifier.gui.components.modelnote.ModelNoteView;
import com.leclercb.taskunifier.gui.components.quickaddtask.QuickAddTaskPanel;
import com.leclercb.taskunifier.gui.components.taskcontacts.TaskContactsColumnList;
import com.leclercb.taskunifier.gui.components.taskcontacts.TaskContactsPanel;
import com.leclercb.taskunifier.gui.components.taskfiles.TaskFilesColumnList;
import com.leclercb.taskunifier.gui.components.taskfiles.TaskFilesPanel;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.components.tasks.TaskTableView;
import com.leclercb.taskunifier.gui.components.tasks.table.TaskTable;
import com.leclercb.taskunifier.gui.components.tasksearchertree.TaskSearcherPanel;
import com.leclercb.taskunifier.gui.components.tasksearchertree.TaskSearcherView;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksColumnList;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksPanel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TUIndentSubtasksCheckBox;
import com.leclercb.taskunifier.gui.swing.TUShowCompletedTasksCheckBox;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class DefaultTaskView extends JPanel implements TaskView, SavePropertiesListener {
	
	private JSplitPane horizontalSplitPane;
	private JSplitPane verticalSplitPane;
	
	private JXSearchField searchField;
	private JCheckBox showCompletedTasksCheckBox;
	private JCheckBox indentSubtasksCheckBox;
	
	private TaskSearcherPanel taskSearcherPanel;
	private QuickAddTaskPanel quickAddTaskPanel;
	private TaskTable taskTable;
	private ModelNotePanel taskNote;
	private TaskContactsPanel taskContacts;
	private TaskTasksPanel taskTasks;
	private TaskFilesPanel taskFiles;
	private JTabbedPane infoTabbedPane;
	
	public DefaultTaskView() {
		this.initialize();
	}
	
	@Override
	public JPanel getViewContent() {
		return this;
	}
	
	@Override
	public TaskSearcherView getTaskSearcherView() {
		return this.taskSearcherPanel;
	}
	
	@Override
	public TaskTableView getTaskTableView() {
		return this.taskTable;
	}
	
	@Override
	public ModelNoteView getModelNoteView() {
		return this.taskNote;
	}
	
	@Override
	public void setSelectedInfoTab(InfoTab tab) {
		CheckUtils.isNotNull(tab);
		this.infoTabbedPane.setSelectedIndex(tab.ordinal());
	}
	
	@Override
	public void focusAndSearch(String text) {
		if (text != null)
			this.searchField.setText(text);
		
		this.searchField.requestFocus();
	}
	
	private void initialize() {
		Main.getSettings().addSavePropertiesListener(this);
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isSytemLookAndFeel()) {
			this.horizontalSplitPane = ComponentFactory.createThinJSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			
			this.horizontalSplitPane = new JSplitPane(
					JSplitPane.HORIZONTAL_SPLIT);
		}
		
		this.horizontalSplitPane.setOpaque(false);
		this.horizontalSplitPane.setOneTouchExpandable(true);
		
		this.verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.verticalSplitPane.setOpaque(false);
		this.verticalSplitPane.setOneTouchExpandable(true);
		this.verticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel searcherPane = new JPanel();
		searcherPane.setLayout(new BorderLayout());
		
		JPanel middlePane = new JPanel();
		middlePane.setOpaque(false);
		middlePane.setLayout(new BorderLayout(5, 5));
		
		this.infoTabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		
		this.horizontalSplitPane.setLeftComponent(searcherPane);
		this.horizontalSplitPane.setRightComponent(this.verticalSplitPane);
		
		this.verticalSplitPane.setTopComponent(middlePane);
		this.verticalSplitPane.setBottomComponent(this.infoTabbedPane);
		
		this.add(this.horizontalSplitPane, BorderLayout.CENTER);
		
		this.loadSplitPaneSettings();
		
		this.initializeSearchField();
		this.showCompletedTasksCheckBox = new TUShowCompletedTasksCheckBox();
		this.indentSubtasksCheckBox = new TUIndentSubtasksCheckBox();
		this.initializeSearcherList(searcherPane);
		this.initializeQuickAddTask(middlePane);
		this.initializeTaskTable(middlePane);
		this.initializeModelNote(this.infoTabbedPane);
		this.initializeTaskContacts(this.infoTabbedPane);
		this.initializeTaskTasks(this.infoTabbedPane);
		this.initializeTaskFiles(this.infoTabbedPane);
		
		this.taskSearcherPanel.refreshTaskSearcher();
	}
	
	private void loadSplitPaneSettings() {
		int hSplit = Main.getSettings().getIntegerProperty(
				"view.tasks.window.horizontal_split");
		int vSplit = Main.getSettings().getIntegerProperty(
				"view.tasks.window.vertical_split");
		
		this.horizontalSplitPane.setDividerLocation(hSplit);
		this.verticalSplitPane.setDividerLocation(vSplit);
	}
	
	@Override
	public void saveProperties() {
		Main.getSettings().setIntegerProperty(
				"view.tasks.window.horizontal_split",
				this.horizontalSplitPane.getDividerLocation());
		Main.getSettings().setIntegerProperty(
				"view.tasks.window.vertical_split",
				this.verticalSplitPane.getDividerLocation());
	}
	
	private void initializeSearchField() {
		this.searchField = new JXSearchField(
				Translations.getString("general.search"));
		this.searchField.setColumns(15);
		
		this.searchField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTaskView.this.taskSearcherPanel.setSearchFilter(e.getActionCommand());
			}
			
		});
	}
	
	private void initializeSearcherList(JPanel searcherPane) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(new SourceListStandardColorScheme().getActiveBackgroundColor());
		
		JPanel northPanel = new JPanel(new GridLayout(0, 1));
		northPanel.setBackground(new SourceListStandardColorScheme().getActiveBackgroundColor());
		
		panel.add(northPanel, BorderLayout.NORTH);
		
		Color color = UIManager.getColor("TabbedPane.background");
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isSytemLookAndFeel())
			color = new Color(228, 228, 228);
		
		final Color finalColor = color;
		
		JXPanel searchPanel = new JXPanel(new BorderLayout());
		searchPanel.setBackgroundPainter(new Painter<JXPanel>() {
			
			@Override
			public void paint(
					Graphics2D g,
					JXPanel object,
					int width,
					int height) {
				g.setPaint(new GradientPaint(
						0.0f,
						0.0f,
						finalColor,
						0.0f,
						height,
						new SourceListStandardColorScheme().getActiveBackgroundColor()));
				g.fillRect(0, 0, width, height);
			}
			
		});
		searchPanel.add(this.searchField);
		
		northPanel.add(searchPanel);
		northPanel.add(this.showCompletedTasksCheckBox);
		northPanel.add(this.indentSubtasksCheckBox);
		
		searcherPane.add(panel, BorderLayout.CENTER);
		
		this.initializeTaskSearcherList(panel);
	}
	
	private void initializeTaskSearcherList(JPanel searcherPane) {
		this.taskSearcherPanel = new TaskSearcherPanel("tasksearcher.tasks");
		
		this.taskSearcherPanel.addPropertyChangeListener(
				TaskSearcherPanel.PROP_TITLE_FILTER,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						String filter = (String) evt.getNewValue();
						if (!DefaultTaskView.this.searchField.getText().equals(
								filter))
							DefaultTaskView.this.searchField.setText(filter);
					}
					
				});
		
		searcherPane.add(this.taskSearcherPanel);
	}
	
	private void initializeQuickAddTask(JPanel middlePane) {
		this.quickAddTaskPanel = new QuickAddTaskPanel();
		
		JPanel panel = new JPanel(new BorderLayout(3, 3));
		panel.setOpaque(false);
		panel.add(this.quickAddTaskPanel, BorderLayout.CENTER);
		panel.add(
				Help.getInstance().getHelpButton("task_quick_add"),
				BorderLayout.EAST);
		
		middlePane.add(panel, BorderLayout.NORTH);
	}
	
	private void initializeTaskTable(JPanel middlePane) {
		this.taskTable = new TaskTable(new TUTableProperties<Task>(
				TaskColumnList.getInstance(),
				"task",
				false));
		
		JPanel taskPanel = new JPanel(new BorderLayout());
		taskPanel.add(
				ComponentFactory.createJScrollPane(this.taskTable, false),
				BorderLayout.CENTER);
		
		this.taskSearcherPanel.addTaskSearcherSelectionChangeListener(this.taskTable);
		this.taskSearcherPanel.addPropertyChangeListener(
				TaskSearcherPanel.PROP_TITLE_FILTER,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						DefaultTaskView.this.taskTable.setSearchText(DefaultTaskView.this.taskSearcherPanel.getSearchFilter());
						
						DefaultTaskView.this.taskTable.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
								evt.getSource(),
								DefaultTaskView.this.taskSearcherPanel.getSelectedTaskSearcher()));
					}
					
				});
		
		middlePane.add(taskPanel, BorderLayout.CENTER);
	}
	
	private void initializeModelNote(JTabbedPane tabbedPane) {
		this.taskNote = new ModelNotePanel("view.tasks.modelnote");
		this.taskNote.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.taskTable.addModelSelectionChangeListener(this.taskNote);
		tabbedPane.addTab(
				Translations.getString("general.notes"),
				ImageUtils.getResourceImage("note.png", 16, 16),
				this.taskNote);
	}
	
	private void initializeTaskContacts(JTabbedPane tabbedPane) {
		this.taskContacts = new TaskContactsPanel(
				new TUTableProperties<ContactItem>(
						TaskContactsColumnList.getInstance(),
						"taskcontacts",
						false));
		this.taskContacts.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.taskTable.addModelSelectionChangeListener(this.taskContacts);
		tabbedPane.addTab(
				Translations.getString("general.task.contacts"),
				ImageUtils.getResourceImage("user.png", 16, 16),
				this.taskContacts);
	}
	
	private void initializeTaskTasks(JTabbedPane tabbedPane) {
		this.taskTasks = new TaskTasksPanel(new TUTableProperties<TaskItem>(
				TaskTasksColumnList.getInstance(),
				"tasktasks",
				false));
		this.taskTasks.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.taskTable.addModelSelectionChangeListener(this.taskTasks);
		tabbedPane.addTab(
				Translations.getString("general.task.tasks"),
				ImageUtils.getResourceImage("task.png", 16, 16),
				this.taskTasks);
	}
	
	private void initializeTaskFiles(JTabbedPane tabbedPane) {
		this.taskFiles = new TaskFilesPanel(new TUTableProperties<FileItem>(
				TaskFilesColumnList.getInstance(),
				"taskfiles",
				false));
		this.taskFiles.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.taskTable.addModelSelectionChangeListener(this.taskFiles);
		tabbedPane.addTab(
				Translations.getString("general.task.files"),
				ImageUtils.getResourceImage("folder.png", 16, 16),
				this.taskFiles);
	}
	
}
