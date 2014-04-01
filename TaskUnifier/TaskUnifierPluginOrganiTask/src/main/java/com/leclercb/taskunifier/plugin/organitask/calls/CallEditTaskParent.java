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

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditTaskParent extends AbstractCallTask {
	
	private static final int MAX = 50;
	
	public void editTaskParent(
			ToodledoAccountInfo accountInfo,
			String key,
			Task task) throws SynchronizerException {
		CheckUtils.isNotNull(task);
		
		this.editTasksParent(accountInfo, key, new Task[] { task });
	}
	
	public void editTasksParent(
			ToodledoAccountInfo accountInfo,
			String key,
			Task[] tasks) throws SynchronizerException {
		List<Task> taskList = new ArrayList<Task>(Arrays.asList(tasks));
		while (taskList.size() != 0) {
			List<Task> list = taskList.subList(
					0,
					(MAX > taskList.size() ? taskList.size() : MAX));
			this.editTasksParentMax(accountInfo, key, list);
			list.clear();
		}
	}
	
	private void editTasksParentMax(
			ToodledoAccountInfo accountInfo,
			String key,
			List<Task> tasks) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(tasks);
		
		for (Task task : tasks)
			if (task.getModelReferenceId("toodledo") == null)
				throw new IllegalArgumentException("You cannot edit a new task");
		
		JSONArray jsonArray = new JSONArray();
		
		try {
			for (Task task : tasks) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("id", task.getModelReferenceId("toodledo"));
				
				if (task.getRoot() != null
						&& task.getRoot().getModelReferenceId("toodledo") != null)
					jsonObject.put(
							"parent",
							task.getRoot().getModelReferenceId("toodledo"));
				else
					jsonObject.put("parent", "0");
				
				jsonObject.put("repeat", task.getRepeat());
				jsonObject.put(
						"repeatfrom",
						ToodledoTranslations.translateTaskRepeatFrom(task.getRepeatFrom()));
				
				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
			throw new SynchronizerException(
					false,
					"Error during JSON request creation",
					e);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("tasks", jsonArray.toString()));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(scheme, "/2/tasks/edit.php", params);
		
		this.getResponseMessage(tasks, content, accountInfo);
	}
	
}
