/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditTaskMeta extends AbstractCallTask {
	
	private static final int MAX = 50;
	
	public void editTaskMeta(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Task task) throws SynchronizerException {
		CheckUtils.isNotNull(task);
		
		this.editTasksMeta(accountInfo, accessToken, new Task[] { task });
	}
	
	public void editTasksMeta(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Task[] tasks) throws SynchronizerException {
		List<Task> taskList = new ArrayList<Task>(Arrays.asList(tasks));
		while (taskList.size() != 0) {
			List<Task> list = taskList.subList(
					0,
					(MAX > taskList.size() ? taskList.size() : MAX));
			this.editTasksMetaMax(accountInfo, accessToken, list);
			list.clear();
		}
	}
	
	private void editTasksMetaMax(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			List<Task> tasks) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		CheckUtils.isNotNull(tasks);
		
		for (Task task : tasks)
			if (task.getModelReferenceId("toodledo") == null)
				throw new IllegalArgumentException("You cannot edit a new task");
		
		JSONArray jsonArray = new JSONArray();
		
		try {
			for (Task task : tasks) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("id", task.getModelReferenceId("toodledo"));
				
				CallEditTaskMeta.addMetaData(jsonObject, task);
				
				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
			throw new SynchronizerException(
					false,
					"Error during JSON request creation",
					e);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("tasks", jsonArray.toString()));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(scheme, "/3/tasks/edit.php", params);
		
		this.getResponseMessage(tasks, content, accountInfo);
	}
	
	public static void addMetaData(JSONObject jsonObject, Task task)
			throws JSONException {
		PropertyMap meta = new PropertyMap();
		
		ToodledoTranslations.translateModelList(
				meta,
				task.getContexts(),
				"context");
		ToodledoTranslations.translateModelList(meta, task.getGoals(), "goal");
		ToodledoTranslations.translateModelList(
				meta,
				task.getLocations(),
				"location");
		
		ToodledoTranslations.translateTaskContactList(meta, task.getContacts());
		ToodledoTranslations.translateTaskFileList(meta, task.getFiles());
		ToodledoTranslations.translateTaskTaskList(meta, task.getTasks());
		
		meta.setIntegerProperty("order", task.getOrder());
		meta.setDoubleProperty("progress", task.getProgress());
		
		meta.setIntegerProperty(
				"startDateReminder",
				task.getStartDateReminder());
		meta.setIntegerProperty("dueDateReminder", task.getDueDateReminder());
		
		if (task.getParent() != null
				&& task.getParent().getModelReferenceId("toodledo") != null)
			meta.setStringProperty(
					"parent",
					task.getParent().getModelReferenceId("toodledo"));
		
		for (Object key : task.getProperties().keySet()) {
			meta.setStringProperty(
					"properties_" + (String) key,
					task.getProperties().getStringProperty((String) key));
		}
		
		jsonObject.put("meta", ToodledoTranslations.propertiesToString(meta));
	}
	
}
