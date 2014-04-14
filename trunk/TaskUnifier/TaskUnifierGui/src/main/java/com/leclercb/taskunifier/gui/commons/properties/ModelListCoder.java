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
package com.leclercb.taskunifier.gui.commons.properties;

import java.util.ArrayList;
import java.util.List;

import javax.help.UnsupportedOperationException;

import com.leclercb.commons.api.properties.PropertiesCoder;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;

public class ModelListCoder extends PropertiesCoder<ModelList> {
	
	public ModelListCoder() {
		
	}
	
	@Override
	public Class<ModelList> getCoderClass() {
		return ModelList.class;
	}
	
	@Override
	public ModelList decode(String value) throws Exception {
		if (value == null || value.length() == 0)
			return null;
		
		String[] values = value.split(";");
		
		List<Model> list = new ArrayList<Model>();
		
		ModelType type = ModelType.valueOf(values[0]);
		
		if (type == null)
			return null;
		
		for (String modelId : values) {
			if (modelId.trim().length() == 0)
				continue;
			
			Model model = ModelFactoryUtils.getModel(type, new ModelId(modelId));
			
			if (model == null)
				continue;
			
			if (model.getModelStatus().isEndUserStatus())
				list.add(model);
		}
		
		switch (type) {
			case CONTACT:
				ModelList<Contact> contactModelList = new ModelList<Contact>(
						type);
				for (Model model : list)
					contactModelList.add((Contact) model);
				
				return contactModelList;
			case CONTEXT:
				ModelList<Context> contextModelList = new ModelList<Context>(
						type);
				for (Model model : list)
					contextModelList.add((Context) model);
				
				return contextModelList;
			case FOLDER:
				ModelList<Folder> folderModelList = new ModelList<Folder>(type);
				for (Model model : list)
					folderModelList.add((Folder) model);
				
				return folderModelList;
			case GOAL:
				ModelList<Goal> goalModelList = new ModelList<Goal>(type);
				for (Model model : list)
					goalModelList.add((Goal) model);
				
				return goalModelList;
			case LOCATION:
				ModelList<Location> locationModelList = new ModelList<Location>(
						type);
				for (Model model : list)
					locationModelList.add((Location) model);
				
				return locationModelList;
			case NOTE:
				ModelList<Note> noteModelList = new ModelList<Note>(type);
				for (Model model : list)
					noteModelList.add((Note) model);
				
				return noteModelList;
			case TASK:
				ModelList<Task> taskModelList = new ModelList<Task>(type);
				for (Model model : list)
					taskModelList.add((Task) model);
				
				return taskModelList;
            case TASK_STATUS:
                ModelList<TaskStatus> taskStatusModelList = new ModelList<TaskStatus>(type);
                for (Model model : list)
                    taskStatusModelList.add((TaskStatus) model);

                return taskStatusModelList;
			default:
				throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public String encode(ModelList value) throws Exception {
		if (value == null)
			return null;
		
		StringBuffer buffer = new StringBuffer(value.getModelType().name());
		for (Object model : value) {
			buffer.append(";" + ((Model) model).getModelId());
		}
		
		return buffer.toString();
	}
	
}
