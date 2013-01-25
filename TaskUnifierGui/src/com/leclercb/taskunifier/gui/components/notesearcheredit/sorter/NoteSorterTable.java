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
package com.leclercb.taskunifier.gui.components.notesearcheredit.sorter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorter;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorterElement;
import com.leclercb.taskunifier.gui.commons.values.StringValueSortOrder;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.notesearcheredit.sorter.draganddrop.NoteSorterTransferHandler;
import com.leclercb.taskunifier.gui.components.notesearcheredit.sorter.renderers.NoteSorterSortOrderRenderer;

public class NoteSorterTable extends JTable {
	
	private static final DefaultTableCellRenderer ORDER_RENDERER;
	private static final DefaultTableCellRenderer COLUMN_RENDERER;
	private static final DefaultTableCellRenderer SORT_ORDER_RENDERER;
	
	private static final DefaultCellEditor COLUMN_EDITOR;
	private static final DefaultCellEditor SORT_ORDER_EDITOR;
	
	static {
		// RENDERERS
		ORDER_RENDERER = new DefaultTableCellRenderer();
		COLUMN_RENDERER = new DefaultTableCellRenderer();
		SORT_ORDER_RENDERER = new NoteSorterSortOrderRenderer();
		
		// EDITORS
		COLUMN_EDITOR = new DefaultCellEditor(new JComboBox(
				NoteColumnList.getInstance().getAccessors().toArray()));
		
		JComboBox comboBox = null;
		
		comboBox = new JComboBox(SortOrder.values());
		comboBox.setRenderer(new DefaultListRenderer(
				StringValueSortOrder.INSTANCE));
		
		SORT_ORDER_EDITOR = new DefaultCellEditor(comboBox);
	}
	
	public NoteSorterTable(NoteSorter sorter) {
		this.initialize(sorter);
	}
	
	public NoteSorter getNoteSorter() {
		return ((NoteSorterTableModel) this.getModel()).getNoteSorter();
	}
	
	public NoteSorterElement getNoteSorterElement(int row) {
		try {
			return ((NoteSorterTableModel) this.getModel()).getNoteSorterElement(row);
		} catch (IndexOutOfBoundsException exc) {
			return null;
		}
	}
	
	public NoteSorterElement[] getSelectedNoteSorterElements() {
		int[] indexes = this.getSelectedRows();
		
		List<NoteSorterElement> elements = new ArrayList<NoteSorterElement>();
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != -1) {
				NoteSorterElement element = this.getNoteSorterElement(indexes[i]);
				
				if (element != null)
					elements.add(element);
			}
		}
		
		return elements.toArray(new NoteSorterElement[0]);
	}
	
	private void initialize(NoteSorter sorter) {
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		NoteSorterTableModel tableModel = new NoteSorterTableModel(sorter);
		
		this.setModel(tableModel);
		this.getTableHeader().setReorderingAllowed(false);
		
		this.initializeDragAndDrop();
	}
	
	private void initializeDragAndDrop() {
		this.setDragEnabled(true);
		this.setTransferHandler(new NoteSorterTransferHandler());
		this.setDropMode(DropMode.INSERT_ROWS);
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int col) {
		switch (col) {
			case 0:
				return super.getCellEditor(row, col);
			case 1:
				return COLUMN_EDITOR;
			case 2:
				return SORT_ORDER_EDITOR;
			default:
				return super.getCellEditor(row, col);
		}
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int col) {
		switch (col) {
			case 0:
				return ORDER_RENDERER;
			case 1:
				return COLUMN_RENDERER;
			case 2:
				return SORT_ORDER_RENDERER;
			default:
				return super.getCellRenderer(row, col);
		}
	}
	
}
