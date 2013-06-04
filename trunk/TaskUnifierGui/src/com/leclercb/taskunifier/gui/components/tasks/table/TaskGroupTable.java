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
package com.leclercb.taskunifier.gui.components.tasks.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable.PrintMode;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionListener;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.components.print.PrintUtils;
import com.leclercb.taskunifier.gui.components.print.TablePrintable;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.components.tasks.TaskTableView;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.TaskGrouperUtils;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskGroupTable extends JPanel implements TaskTableView {
	
	private boolean main;
	
	private TUTableProperties<Task> tableProperties;
	private List<TaskTableView> tables;
	private TaskSearcher searcher;
	private List<TaskSearcher> searchers;
	
	public TaskGroupTable(TUTableProperties<Task> tableProperties) {
		this(tableProperties, true);
	}
	
	private TaskGroupTable(TUTableProperties<Task> tableProperties, boolean main) {
		this.main = main;
		
		this.tableProperties = tableProperties;
		this.tables = new ArrayList<TaskTableView>();
		this.searcher = null;
		
		this.initialize();
	}
	
	private void initialize() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		
		this.updateTables();
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}
	
	@Override
	public Task[] getSelectedTasks() {
		List<Task> tasks = new ArrayList<Task>();
		
		for (TaskTableView table : this.tables) {
			tasks.addAll(Arrays.asList(table.getSelectedTasks()));
		}
		
		return tasks.toArray(new Task[0]);
	}
	
	@Override
	public void setSelectedTasks(Task[] tasks) {
		for (TaskTableView table : this.tables) {
			table.setSelectedTasks(tasks);
		}
	}
	
	@Override
	public void addModelSelectionChangeListener(ModelSelectionListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeModelSelectionChangeListener(
			ModelSelectionListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void taskSearcherSelectionChange(
			TaskSearcherSelectionChangeEvent event) {
		this.searcher = event.getSelectedTaskSearcher();
		this.searchers = TaskGrouperUtils.getFilters(this.searcher);
		this.updateTables();
	}
	
	@Override
	public Task[] getTasks() {
		List<Task> tasks = new ArrayList<Task>();
		
		for (TaskTableView table : this.tables) {
			tasks.addAll(Arrays.asList(table.getTasks()));
		}
		
		return tasks.toArray(new Task[0]);
	}
	
	@Override
	public int getTaskCount() {
		int count = 0;
		
		for (TaskTableView table : this.tables) {
			count += table.getTaskCount();
		}
		
		return count;
	}
	
	@Override
	public void setSelectedTaskAndStartEdit(Task task) {
		for (TaskTableView table : this.tables) {
			table.setSelectedTaskAndStartEdit(task);
		}
	}
	
	@Override
	public void refreshTasks() {
		for (TaskTableView table : this.tables) {
			table.refreshTasks();
		}
	}
	
	@Override
	public void setSearchText(String searchText) {
		for (TaskTableView table : this.tables) {
			table.setSearchText(searchText);
		}
	}
	
	@Override
	public void printTasks(boolean selection) throws Exception {
		Task[] tasks = null;
		
		if (selection)
			tasks = this.getSelectedTasks();
		else
			tasks = this.getTasks();
		
		TablePrintable tablePrintable = new TablePrintable(
				new TaskPrintTable(new TUTableProperties<Task>(
						TaskColumnList.getInstance(),
						this.tableProperties.getPropertyName() + ".print",
						false), tasks),
				PrintMode.NORMAL,
				0.7,
				new MessageFormat(Constants.TITLE
						+ " - "
						+ this.searcher.getTitle()),
				new MessageFormat(this.getTaskCount() + " tasks | Page - {0}"));
		
		PrintUtils.printTable("view.tasks.print", tablePrintable);
	}
	
	@Override
	public void pasteTask() {
		this.tables.get(0).pasteTask();
	}
	
	@Override
	public void commitChanges() {
		for (TaskTableView table : this.tables) {
			table.commitChanges();
		}
	}
	
	public void updateTables() {
		this.tables.clear();
		
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.validate();
		this.repaint();
		
		if (this.searchers == null
				|| this.searchers.size() == 0
				|| !this.doesContainDisplayedTasks(this.searcher)) {
			if (this.main) {
				JPanel tablePanel = new JPanel(new BorderLayout());
				tablePanel.setOpaque(false);
				TaskTable table = new TaskTable(this.tableProperties);
				table.setSelectionModel(new TaskGroupTableSelectionModel(this));
				
				if (this.searcher != null)
					table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
							this,
							this.searcher));
				
				tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
				tablePanel.add(table, BorderLayout.CENTER);
				this.add(tablePanel);
				
				this.tables.add(table);
			}
			
			this.add(Box.createVerticalGlue());
			
			this.validate();
			this.repaint();
			
			return;
		}
		
		for (TaskSearcher searcher : this.searchers) {
			if (this.doesContainDisplayedTasks(searcher)) {
				JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				titlePanel.setOpaque(false);
				titlePanel.add(new JLabel(searcher.getTitle()));
				this.add(titlePanel);
				
				if (searcher.getGrouper().getElementCount() == 0) {
					JPanel tablePanel = new JPanel(new BorderLayout());
					tablePanel.setOpaque(false);
					TaskTable table = new TaskTable(this.tableProperties);
					table.setSelectionModel(new TaskGroupTableSelectionModel(
							this));
					table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
							this,
							searcher));
					tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
					tablePanel.add(table, BorderLayout.CENTER);
					this.add(tablePanel);
					
					this.tables.add(table);
				} else {
					TaskGroupTable table = new TaskGroupTable(
							this.tableProperties);
					table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
							this,
							searcher));
					this.add(table);
					
					this.tables.add(table);
				}
			}
		}
		
		this.add(Box.createVerticalGlue());
		
		this.validate();
		this.repaint();
	}
	
	private boolean doesContainDisplayedTasks(TaskSearcher searcher) {
		List<Task> tasks = TaskFactory.getInstance().getList();
		
		boolean indentSubtasks = Main.getSettings().getBooleanProperty(
				"task.indent_subtasks");
		
		for (Task task : tasks) {
			if (indentSubtasks) {
				if (TaskUtils.showTask(task, null, searcher.getFilter())) {
					return true;
				}
			} else {
				if (TaskUtils.showUnindentTask(
						task,
						null,
						searcher.getFilter(),
						false)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
