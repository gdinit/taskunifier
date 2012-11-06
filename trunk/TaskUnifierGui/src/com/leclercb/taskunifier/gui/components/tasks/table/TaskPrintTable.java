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
package com.leclercb.taskunifier.gui.components.tasks.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.JXTable;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.components.print.PrintTable;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumn;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskPrintTable extends JXTable implements PrintTable {
	
	public TaskPrintTable(
			TUTableProperties<TaskColumn> tableProperties,
			Task[] tasks) {
		this.initialize(tableProperties, tasks);
	}
	
	private void initialize(
			final TUTableProperties<TaskColumn> tableProperties,
			final Task[] tasks) {
		List<TaskColumn> allColumns = new ArrayList<TaskColumn>(
				Arrays.asList(TaskColumn.getUsableColumns(false)));
		List<TaskColumn> columns = new ArrayList<TaskColumn>();
		
		for (TaskColumn column : allColumns) {
			if (!tableProperties.get(column).isVisible())
				continue;
			
			columns.add(column);
		}
		
		Collections.sort(columns, new Comparator<TaskColumn>() {
			
			@Override
			public int compare(TaskColumn c1, TaskColumn c2) {
				Integer o1 = tableProperties.get(c1).getOrder();
				Integer o2 = tableProperties.get(c2).getOrder();
				
				return o1.compareTo(o2);
			}
			
		});
		
		String[][] data = TaskUtils.toStringData(
				tasks,
				columns.toArray(new TaskColumn[0]));
		String[] columnNames = data[0];
		data = ArrayUtils.remove(data, 0);
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		
		this.setModel(model);
		this.setColumnControlVisible(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.packAll();
	}
	
	@Override
	public JTable getJTable() {
		return this;
	}
	
}
