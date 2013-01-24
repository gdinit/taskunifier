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
package com.leclercb.taskunifier.gui.components.taskfiles.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import com.leclercb.taskunifier.api.models.FileList;
import com.leclercb.taskunifier.api.models.FileList.FileItem;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.highlighters.AlternateHighlighter;
import com.leclercb.taskunifier.gui.components.taskfiles.TaskFilesColumnList;
import com.leclercb.taskunifier.gui.components.taskfiles.table.draganddrop.TaskFilesTransferHandler;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUTableColumnModel;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;

public class TaskFilesTable extends JXTable implements SavePropertiesListener {
	
	private TUTableProperties<FileItem> tableProperties;
	
	public TaskFilesTable(TUTableProperties<FileItem> tableProperties) {
		CheckUtils.isNotNull(tableProperties);
		this.tableProperties = tableProperties;
		
		this.initialize();
	}
	
	public FileList getFileGroup() {
		TaskFilesTableModel model = (TaskFilesTableModel) this.getModel();
		return model.getFileGroup();
	}
	
	public void setFileGroup(FileList files) {
		this.commitChanges();
		TaskFilesTableModel model = (TaskFilesTableModel) this.getModel();
		model.setFileGroup(files);
	}
	
	public int getFileItemCount() {
		return this.getRowCount();
	}
	
	public FileItem getFileItem(int row) {
		try {
			int index = this.getRowSorter().convertRowIndexToModel(row);
			return ((TaskFilesTableModel) this.getModel()).getFileItem(index);
		} catch (IndexOutOfBoundsException exc) {
			return null;
		}
	}
	
	public FileItem[] getSelectedFileItems() {
		int[] indexes = this.getSelectedRows();
		
		List<FileItem> items = new ArrayList<FileItem>();
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != -1) {
				FileItem item = this.getFileItem(indexes[i]);
				
				if (item != null)
					items.add(item);
			}
		}
		
		return items.toArray(new FileItem[0]);
	}
	
	public void commitChanges() {
		if (this.getCellEditor() != null)
			this.getCellEditor().stopCellEditing();
	}
	
	private void initialize() {
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		TUTableColumnModel<FileItem> columnModel = new TUTableColumnModel<FileItem>(
				this.tableProperties);
		TaskFilesTableModel tableModel = new TaskFilesTableModel();
		
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
				TaskFilesColumnList.getInstance().get(TaskFilesColumnList.LINK),
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
		this.setTransferHandler(new TaskFilesTransferHandler());
		this.setDropMode(DropMode.INSERT_ROWS);
	}
	
	private void initializeDoubleClick() {
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1
						&& event.getClickCount() == 2) {
					try {
						int rowIndex = TaskFilesTable.this.rowAtPoint(event.getPoint());
						
						rowIndex = TaskFilesTable.this.getRowSorter().convertRowIndexToModel(
								rowIndex);
						
						if (rowIndex == -1)
							return;
						
						int colIndex = TaskFilesTable.this.columnAtPoint(event.getPoint());
						
						PropertyAccessor<FileItem> column = (PropertyAccessor<FileItem>) TaskFilesTable.this.getColumn(
								colIndex).getIdentifier();
						
						if (EqualsUtils.equals(
								column,
								TaskFilesColumnList.getInstance().get(
										TaskFilesColumnList.OPEN))) {
							FileItem item = ((TaskFilesTableModel) TaskFilesTable.this.getModel()).getFileItem(rowIndex);
							
							if (item == null)
								return;
							
							if (item.getFile() == null)
								return;
							
							DesktopUtils.open(new File(item.getFile()));
						}
					} catch (Exception e) {
						
					}
				}
			}
			
		});
	}
	
	private void initializeHighlighters() {
		this.setHighlighters(new AlternateHighlighter());
	}
	
	@Override
	public void saveProperties() {
		Main.getSettings().setBooleanProperty(
				this.tableProperties.getPropertyName()
						+ ".horizontal_scroll_enabled",
				this.isHorizontalScrollEnabled());
	}
	
}
