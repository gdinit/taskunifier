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
package com.leclercb.taskunifier.gui.components.tasks.table.sorter;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.components.tasks.table.TaskTableModel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskRowFilter extends RowFilter<TableModel, Integer> {
	
	private TaskFilter filter;
	
	public TaskRowFilter(TaskFilter filter) {
		this.setFilter(filter);
	}
	
	public TaskFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(TaskFilter filter) {
		CheckUtils.isNotNull(filter);
		this.filter = filter;
	}
	
	@Override
	public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
		TaskTableModel taskTableModel = (TaskTableModel) entry.getModel();
		Task task = taskTableModel.getTask(entry.getIdentifier());
		
		return this.include(task);
	}
	
	public boolean include(Task task) {
		boolean indentSubtasks = Main.getSettings().getBooleanProperty(
				"task.indent_subtasks");
		
		if (indentSubtasks)
			return TaskUtils.showTask(task, null, this.filter);
		else
			return TaskUtils.showUnindentTask(task, null, this.filter);
	}
	
}
