/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

final class CallEditTask extends AbstractCallTask {
	
	private static final int MAX = 10;
	
	public void editTask(
			ToodledoAccountInfo accountInfo,
			String key,
			Task task,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		CheckUtils.isNotNull(task);
		
		this.editTasks(
				accountInfo,
				key,
				new Task[] { task },
				syncSubTasks,
				syncMeta);
	}
	
	public void editTasks(
			ToodledoAccountInfo accountInfo,
			String key,
			Task[] tasks,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
		List<Task> taskList = new ArrayList<Task>(Arrays.asList(tasks));
		while (taskList.size() != 0) {
			List<Task> list = taskList.subList(
					0,
					(MAX > taskList.size() ? taskList.size() : MAX));
			this.editTasksMax(accountInfo, key, list, syncSubTasks, syncMeta);
			list.clear();
		}
	}
	
	private void editTasksMax(
			ToodledoAccountInfo accountInfo,
			String key,
			List<Task> tasks,
			boolean syncSubTasks,
			boolean syncMeta) throws SynchronizerException {
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
				
				if (task.getTitle().trim().length() != 0)
					jsonObject.put("title", task.getTitle());
				else
					jsonObject.put(
							"title",
							PluginApi.getTranslation("task.default.title"));
				
				jsonObject.put("tag", task.getTags().toString());
				
				if (task.getFolder() != null
						&& task.getFolder().getModelReferenceId("toodledo") != null)
					jsonObject.put(
							"folder",
							task.getFolder().getModelReferenceId("toodledo"));
				else
					jsonObject.put("folder", "0");
				
				if (task.getContexts().size() != 0
						&& task.getContexts().get(0).getModelReferenceId(
								"toodledo") != null)
					jsonObject.put(
							"context",
							task.getContexts().get(0).getModelReferenceId(
									"toodledo"));
				else
					jsonObject.put("context", "0");
				
				if (task.getGoals().size() != 0
						&& task.getGoals().get(0).getModelReferenceId(
								"toodledo") != null)
					jsonObject.put(
							"goal",
							task.getGoals().get(0).getModelReferenceId(
									"toodledo"));
				else
					jsonObject.put("goal", "0");
				
				if (task.getLocations().size() != 0
						&& task.getLocations().get(0).getModelReferenceId(
								"toodledo") != null)
					jsonObject.put(
							"location",
							task.getLocations().get(0).getModelReferenceId(
									"toodledo"));
				else
					jsonObject.put("location", "0");
				
				if (syncSubTasks
						&& task.getRoot() != null
						&& task.getRoot().getModelReferenceId("toodledo") != null)
					jsonObject.put(
							"parent",
							task.getRoot().getModelReferenceId("toodledo"));
				else
					jsonObject.put("parent", "0");
				
				if (task.isCompleted())
					jsonObject.put(
							"completed",
							ToodledoTranslations.translateGMTDate(task.getCompletedOn())
									+ "");
				else
					jsonObject.put("completed", "0");
				
				if (task.getDueDate() != null) {
					Calendar dueDate = DateUtils.cloneCalendar(task.getDueDate());
					dueDate.set(Calendar.HOUR_OF_DAY, 12);
					dueDate.set(Calendar.MINUTE, 0);
					dueDate.set(Calendar.SECOND, 0);
					
					jsonObject.put(
							"duedate",
							ToodledoTranslations.translateGMTDateUserInput(dueDate)
									+ "");
				} else {
					jsonObject.put("duedate", "0");
				}
				
				if (task.getStartDate() != null) {
					Calendar startDate = DateUtils.cloneCalendar(task.getStartDate());
					startDate.set(Calendar.HOUR_OF_DAY, 12);
					startDate.set(Calendar.MINUTE, 0);
					startDate.set(Calendar.SECOND, 0);
					
					jsonObject.put(
							"startdate",
							ToodledoTranslations.translateGMTDateUserInput(startDate)
									+ "");
				} else {
					jsonObject.put("startdate", "0");
				}
				
				boolean useDueTime = true;
				if (PluginApi.getSettings().getBooleanProperty(
						"date.use_due_time") != null)
					useDueTime = PluginApi.getSettings().getBooleanProperty(
							"date.use_due_time");
				
				if (task.getDueDate() != null) {
					if (useDueTime
							&& (task.getDueDate().get(Calendar.HOUR_OF_DAY) != 0 || task.getDueDate().get(
									Calendar.MINUTE) != 0))
						jsonObject.put(
								"duetime",
								ToodledoTranslations.translateGMTDateUserInput(task.getDueDate())
										+ "");
					else
						jsonObject.put("duetime", "0");
				} else {
					jsonObject.put("duetime", "0");
				}
				
				boolean useStartTime = true;
				if (PluginApi.getSettings().getBooleanProperty(
						"date.use_start_time") != null)
					useStartTime = PluginApi.getSettings().getBooleanProperty(
							"date.use_start_time");
				
				if (task.getStartDate() != null) {
					if (useStartTime
							&& (task.getStartDate().get(Calendar.HOUR_OF_DAY) != 0 || task.getStartDate().get(
									Calendar.MINUTE) != 0))
						jsonObject.put(
								"starttime",
								ToodledoTranslations.translateGMTDateUserInput(task.getStartDate())
										+ "");
					else
						jsonObject.put("starttime", "0");
				} else {
					jsonObject.put("starttime", "0");
				}
				
				jsonObject.put("remind", task.getDueDateReminder() + "");
				jsonObject.put("repeat", task.getRepeat());
				jsonObject.put(
						"repeatfrom",
						ToodledoTranslations.translateTaskRepeatFrom(task.getRepeatFrom()));
				jsonObject.put(
						"status",
						ToodledoTranslations.translateTaskStatus(task.getStatus()));
				jsonObject.put("length", task.getLength() + "");
				jsonObject.put("timer", task.getTimer().getValue() + "");
				jsonObject.put(
						"timeron",
						ToodledoTranslations.translateGMTDate(task.getTimer().getStartDate())
								+ "");
				jsonObject.put(
						"priority",
						ToodledoTranslations.translateTaskPriority(task.getPriority()));
				jsonObject.put("star", (task.isStar() ? "1" : "0"));
				jsonObject.put("note", task.getNote());
				
				jsonObject.put("reschedule", "0");
				
				if (syncMeta) {
					CallEditTaskMeta.addMetaData(jsonObject, task);
				}
				
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
