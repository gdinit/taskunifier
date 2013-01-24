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
package com.leclercb.taskunifier.gui.components.tasktasks.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;

import org.jdesktop.swingx.JXTable;

import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskList;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;
import com.leclercb.taskunifier.gui.actions.ActionEditTasks;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksColumnList;
import com.leclercb.taskunifier.gui.components.tasktasks.table.draganddrop.TaskTasksTransferHandler;
import com.leclercb.taskunifier.gui.components.tasktasks.table.highlighters.TaskAlternateHighlighter;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUTableColumnModel;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;

public class TaskTasksTable extends JXTable implements SavePropertiesListener {
	
	private TUTableProperties<TaskItem> tableProperties;
	
	public TaskTasksTable(TUTableProperties<TaskItem> tableProperties) {
		CheckUtils.isNotNull(tableProperties);
		this.tableProperties = tableProperties;
		
		this.initialize();
	}
	
	public TaskList getTaskGroup() {
		TaskTasksTableModel model = (TaskTasksTableModel) this.getModel();
		return model.getTaskGroup();
	}
	
	public void setTaskGroup(TaskList tasks) {
		this.commitChanges();
		TaskTasksTableModel model = (TaskTasksTableModel) this.getModel();
		model.setTaskGroup(tasks);
	}
	
	public int getTaskItemCount() {
		return this.getRowCount();
	}
	
	public TaskItem getTaskItem(int row) {
		try {
			int index = this.getRowSorter().convertRowIndexToModel(row);
			return ((TaskTasksTableModel) this.getModel()).getTaskItem(index);
		} catch (IndexOutOfBoundsException exc) {
			return null;
		}
	}
	
	public TaskItem[] getSelectedTaskItems() {
		int[] indexes = this.getSelectedRows();
		
		List<TaskItem> items = new ArrayList<TaskItem>();
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != -1) {
				TaskItem item = this.getTaskItem(indexes[i]);
				
				if (item != null)
					items.add(item);
			}
		}
		
		return items.toArray(new TaskItem[0]);
	}
	
	public void commitChanges() {
		if (this.getCellEditor() != null)
			this.getCellEditor().stopCellEditing();
	}
	
	private void initialize() {
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		TUTableColumnModel<TaskItem> columnModel = new TUTableColumnModel<TaskItem>(
				this.tableProperties);
		TaskTasksTableModel tableModel = new TaskTasksTableModel();
		
		this.setModel(tableModel);
		this.setColumnModel(columnModel);
		this.setRowHeight(24);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setShowGrid(true, false);
		
		this.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
		this.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
		
		this.setSortable(true);
		this.setSortsOnUpdates(false);
		this.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING);
		this.setColumnControlVisible(true);
		this.setSortOrder(
				TaskTasksColumnList.getInstance().get(TaskTasksColumnList.LINK),
				SortOrder.ASCENDING);
		
		this.initializeSettings();
		this.initializeDragAndDrop();
		this.initializeDoubleClick();
		this.initializeHighlighters();
	}
	
	private void initializeSettings() {
		this.setHorizontalScrollEnabled(Main.getSettings().getBooleanProperty(
				this.tableProperties.getPropertyName()
						+ ".horizontal_scroll_enabled",
				false));
	}
	
	private void initializeDragAndDrop() {
		this.setDragEnabled(true);
		this.setTransferHandler(new TaskTasksTransferHandler());
		this.setDropMode(DropMode.INSERT_ROWS);
	}
	
	private void initializeDoubleClick() {
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1
						&& event.getClickCount() == 2) {
					try {
						int rowIndex = TaskTasksTable.this.rowAtPoint(event.getPoint());
						
						if (rowIndex == -1)
							return;
						
						rowIndex = TaskTasksTable.this.getRowSorter().convertRowIndexToModel(
								rowIndex);
						
						int colIndex = TaskTasksTable.this.columnAtPoint(event.getPoint());
						
						PropertyAccessor<TaskItem> column = (PropertyAccessor<TaskItem>) TaskTasksTable.this.getColumn(
								colIndex).getIdentifier();
						
						if (EqualsUtils.equals(
								column,
								TaskTasksColumnList.getInstance().get(
										TaskTasksColumnList.EDIT))
								|| EqualsUtils.equals(
										column,
										TaskTasksColumnList.getInstance().get(
												TaskTasksColumnList.SELECT))) {
							TaskItem item = ((TaskTasksTableModel) TaskTasksTable.this.getModel()).getTaskItem(rowIndex);
							
							if (item == null)
								return;
							
							if (item.getTask() == null)
								return;
							
							if (EqualsUtils.equals(
									column,
									TaskTasksColumnList.getInstance().get(
											TaskTasksColumnList.EDIT))) {
								ActionEditTasks.editTasks(
										new Task[] { item.getTask() },
										false);
							}
							
							if (EqualsUtils.equals(
									column,
									TaskTasksColumnList.getInstance().get(
											TaskTasksColumnList.SELECT))) {
								ViewUtils.selectDefaultTaskSearcher();
								ViewUtils.setSelectedTasks(new Task[] { item.getTask() });
							}
						}
					} catch (Exception e) {
						
					}
				}
			}
			
		});
	}
	
	private void initializeHighlighters() {
		this.setHighlighters(new TaskAlternateHighlighter());
	}
	
	@Override
	public void saveProperties() {
		Main.getSettings().setBooleanProperty(
				this.tableProperties.getPropertyName()
						+ ".horizontal_scroll_enabled",
				this.isHorizontalScrollEnabled());
	}
	
}
