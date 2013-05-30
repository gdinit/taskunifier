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
package com.leclercb.taskunifier.api.models.utils;

import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.beans.ContactBean;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.models.beans.LocationBean;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.models.beans.TaskBean;

public final class ModelFactoryUtils {
	
	private ModelFactoryUtils() {
		
	}
	
	public static Model create(
			ModelType modelType,
			ModelBean bean,
			boolean loadReferenceIds) {
		if (bean == null)
			return null;
		
		switch (modelType) {
			case CONTACT:
				return ContactFactory.getInstance().create(
						(ContactBean) bean,
						loadReferenceIds);
			case CONTEXT:
				return ContextFactory.getInstance().create(
						(ContextBean) bean,
						loadReferenceIds);
			case FOLDER:
				return FolderFactory.getInstance().create(
						(FolderBean) bean,
						loadReferenceIds);
			case GOAL:
				return GoalFactory.getInstance().create(
						(GoalBean) bean,
						loadReferenceIds);
			case LOCATION:
				return LocationFactory.getInstance().create(
						(LocationBean) bean,
						loadReferenceIds);
			case NOTE:
				return NoteFactory.getInstance().create(
						(NoteBean) bean,
						loadReferenceIds);
			case TASK:
				return TaskFactory.getInstance().create(
						(TaskBean) bean,
						loadReferenceIds);
			default:
				return null;
		}
	}
	
	public static Model getModel(ModelType modelType, ModelId modelId) {
		if (modelType == null || modelId == null)
			return null;
		
		switch (modelType) {
			case CONTACT:
				return ContactFactory.getInstance().get(modelId);
			case CONTEXT:
				return ContextFactory.getInstance().get(modelId);
			case FOLDER:
				return FolderFactory.getInstance().get(modelId);
			case GOAL:
				return GoalFactory.getInstance().get(modelId);
			case LOCATION:
				return LocationFactory.getInstance().get(modelId);
			case NOTE:
				return NoteFactory.getInstance().get(modelId);
			case TASK:
				return TaskFactory.getInstance().get(modelId);
			default:
				return null;
		}
	}
	
	public static ModelFactory<?, ?, ?, ?> getFactory(ModelType modelType) {
		if (modelType == null)
			return null;
		
		switch (modelType) {
			case CONTACT:
				return ContactFactory.getInstance();
			case CONTEXT:
				return ContextFactory.getInstance();
			case FOLDER:
				return FolderFactory.getInstance();
			case GOAL:
				return GoalFactory.getInstance();
			case LOCATION:
				return LocationFactory.getInstance();
			case NOTE:
				return NoteFactory.getInstance();
			case TASK:
				return TaskFactory.getInstance();
			default:
				return null;
		}
	}
	
}
