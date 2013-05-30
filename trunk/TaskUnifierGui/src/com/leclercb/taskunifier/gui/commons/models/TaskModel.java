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
package com.leclercb.taskunifier.gui.commons.models;

import java.util.List;

import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;

public class TaskModel extends AbstractBasicModelSortedModel {
	
	private Task hiddenTask;
	
	public TaskModel(boolean firstNull) {
		this(firstNull, null);
	}
	
	public TaskModel(boolean firstNull, Task hiddenTask) {
		this.hiddenTask = hiddenTask;
		this.initialize(firstNull);
	}
	
	public Task getHiddenTask() {
		return this.hiddenTask;
	}
	
	public void setHiddenTask(Task hiddenTask) {
		Task oldHiddenTask = this.hiddenTask;
		this.hiddenTask = hiddenTask;
		
		if (oldHiddenTask != null)
			this.addElement(oldHiddenTask);
		
		if (hiddenTask != null)
			this.removeElement(hiddenTask);
	}
	
	@Override
	public void addElement(Object element) {
		if (this.hiddenTask != null
				&& EqualsUtils.equals(this.hiddenTask, element))
			return;
		
		super.addElement(element);
	}
	
	private void initialize(boolean firstNull) {
		if (firstNull)
			this.addElement(null);
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks)
			this.addElement(task);
		
		TaskFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(TaskFactory.getInstance(), this));
		TaskFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(TaskFactory.getInstance(), this));
	}
	
}
