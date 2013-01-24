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
package com.leclercb.taskunifier.gui.commons.transfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.utils.NoteUtils;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class ModelTransferData implements Serializable {
	
	private ModelType type;
	private ModelId[] ids;
	
	public ModelTransferData(ModelType type, ModelId id) {
		CheckUtils.isNotNull(type);
		CheckUtils.isNotNull(id);
		
		this.type = type;
		this.ids = new ModelId[] { id };
	}
	
	public ModelTransferData(ModelType type, ModelId[] ids) {
		CheckUtils.isNotNull(type);
		CheckUtils.doesNotContainNull(ids);
		
		this.type = type;
		this.ids = Arrays.copyOf(ids, ids.length);
	}
	
	public ModelType getType() {
		return this.type;
	}
	
	public ModelId[] getIds() {
		return this.ids;
	}
	
	public String getPlainData() {
		return this.getTextData(false);
	}
	
	public String getHtmlData() {
		if (this.type == ModelType.TASK) {
			List<Task> tasks = new ArrayList<Task>();
			for (ModelId id : this.ids) {
				Task task = TaskFactory.getInstance().get(id);
				
				if (task == null)
					continue;
				
				if (!task.getModelStatus().isEndUserStatus())
					continue;
				
				tasks.add(task);
			}
			
			if (tasks.size() == 1)
				return this.getTextData(true);
			
			return TaskUtils.toHtml(
					tasks.toArray(new Task[0]),
					TaskColumnList.getInstance().getUsedColumns());
		}
		
		if (this.type == ModelType.NOTE) {
			List<Note> notes = new ArrayList<Note>();
			for (ModelId id : this.ids) {
				Note note = NoteFactory.getInstance().get(id);
				
				if (note == null)
					continue;
				
				if (!note.getModelStatus().isEndUserStatus())
					continue;
				
				notes.add(note);
			}
			
			if (notes.size() == 1)
				return this.getTextData(true);
			
			return NoteUtils.toHtml(
					notes.toArray(new Note[0]),
					NoteColumnList.getInstance().getUsedColumns());
		}
		
		return null;
	}
	
	public String getTextData(boolean html) {
		if (this.type == ModelType.TASK) {
			List<Task> tasks = new ArrayList<Task>();
			for (ModelId id : this.ids) {
				Task task = TaskFactory.getInstance().get(id);
				
				if (task == null)
					continue;
				
				if (!task.getModelStatus().isEndUserStatus())
					continue;
				
				tasks.add(task);
			}
			
			return TaskUtils.toText(
					tasks.toArray(new Task[0]),
					TaskColumnList.getInstance().getUsedColumns(),
					html);
		}
		
		if (this.type == ModelType.NOTE) {
			List<Note> notes = new ArrayList<Note>();
			for (ModelId id : this.ids) {
				Note note = NoteFactory.getInstance().get(id);
				
				if (note == null)
					continue;
				
				if (!note.getModelStatus().isEndUserStatus())
					continue;
				
				notes.add(note);
			}
			
			return NoteUtils.toText(
					notes.toArray(new Note[0]),
					NoteColumnList.getInstance().getUsedColumns(),
					html);
		}
		
		return null;
	}
	
}
