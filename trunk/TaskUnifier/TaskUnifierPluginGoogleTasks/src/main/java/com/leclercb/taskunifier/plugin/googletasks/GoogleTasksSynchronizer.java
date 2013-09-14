/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks.TasksOperations.Move;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage.ProgressMessageType;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerUpdatedModelsProgressMessage;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googletasks.translations.PluginTranslations;

public class GoogleTasksSynchronizer implements Synchronizer {
	
	private GoogleTasksConnection connection;
	
	GoogleTasksSynchronizer(GoogleTasksConnection connection) {
		CheckUtils.isNotNull(connection);
		
		this.connection = connection;
	}
	
	public GoogleTasksConnection getConnection() {
		return this.connection;
	}
	
	@Override
	public void publish() throws SynchronizerException {
		this.publish(null);
	}
	
	@Override
	public void publish(ProgressMonitor monitor) throws SynchronizerException {
		if (!this.connection.isConnected())
			throw new SynchronizerException(
					false,
					"Cannot publish if not connected");
		
		if (monitor != null)
			monitor.addMessage(new SynchronizerMainProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_START));
		
		try {
			this.publishTasks(monitor);
		} catch (SynchronizerException e) {
			throw e;
		} catch (Exception e) {
			throw new SynchronizerException(
					false,
					PluginTranslations.getString("error.try_again"),
					e);
		}
		
		if (monitor != null)
			monitor.addMessage(new SynchronizerMainProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_END));
	}
	
	private void publishTasks(ProgressMonitor monitor) throws Exception {
		List<Task> tasks = new ArrayList<Task>(
				TaskFactory.getInstance().getList());
		
		Collections.sort(tasks, new Comparator<Task>() {
			
			@Override
			public int compare(Task o1, Task o2) {
				if (EqualsUtils.equals(o1.getParent(), o2.getParent()))
					return 0;
				
				if (o2.getAllParents().contains(o1))
					return -1;
				
				if (o1.getAllParents().contains(o2))
					return 1;
				
				return 1;
			}
			
		});
		
		com.google.api.services.tasks.Tasks service = this.connection.getService();
		
		// Compute action count
		int actionCount = 0;
		
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus())
				continue;
			
			if (task.isCompleted())
				continue;
			
			actionCount++;
		}
		
		if (monitor != null && actionCount > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_START,
					ModelType.TASK,
					actionCount));
		
		String taskListName = PluginApi.getUserSettings().getStringProperty(
				"plugin.googletasks.task_list_name",
				"TaskUnifier");
		
		// Find task list
		String taskListId = null;
		
		TaskLists taskLists = service.tasklists().list().execute();
		for (TaskList taskList : taskLists.getItems()) {
			if (taskListName.equals(taskList.getTitle())) {
				taskListId = taskList.getId();
				break;
			}
		}
		
		// Retrieve tasks
		List<com.google.api.services.tasks.model.Task> googleTasks = new ArrayList<com.google.api.services.tasks.model.Task>();
		
		if (taskListId != null) {
			Tasks t = service.tasks().list(taskListId).execute();
			
			if (t.getItems() != null)
				googleTasks.addAll(t.getItems());
			
			while (true) {
				String pageToken = t.getNextPageToken();
				
				if (pageToken != null && !pageToken.isEmpty()) {
					t = service.tasks().list(taskListId).setPageToken(pageToken).execute();
					
					if (t.getItems() != null)
						googleTasks.addAll(t.getItems());
				} else {
					break;
				}
			}
		}
		
		// Create task list
		if (taskListId == null) {
			TaskList taskList = new TaskList();
			taskList.setTitle(taskListName);
			
			taskList = service.tasklists().insert(taskList).execute();
			taskListId = taskList.getId();
		}
		
		// Publish tasks
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus())
				continue;
			
			if (task.isCompleted())
				continue;
			
			com.google.api.services.tasks.model.Task existingGoogleTask = null;
			
			for (com.google.api.services.tasks.model.Task t : googleTasks) {
				if (t.getId().equals(task.getModelReferenceId("googletasks"))) {
					existingGoogleTask = t;
					break;
				}
			}
			
			if (existingGoogleTask == null) {
				for (com.google.api.services.tasks.model.Task t : googleTasks) {
					if (this.equalGoogleTasks(
							true,
							t,
							this.fillTask(task, null))) {
						existingGoogleTask = t;
						break;
					}
				}
			}
			
			googleTasks.remove(existingGoogleTask);
			
			com.google.api.services.tasks.model.Task newGoogleTask = null;
			
			if (existingGoogleTask == null) {
				newGoogleTask = new com.google.api.services.tasks.model.Task();
			} else {
				newGoogleTask = (com.google.api.services.tasks.model.Task) existingGoogleTask.clone();
			}
			
			this.fillTask(task, newGoogleTask);
			
			// Check if existing and new are equal
			if (this.equalGoogleTasks(false, existingGoogleTask, newGoogleTask)) {
				// Move existing task
				if (!EqualsUtils.equalsString(
						existingGoogleTask.getParent(),
						newGoogleTask.getParent())) {
					try {
						Move move = service.tasks().move(
								taskListId,
								task.getModelReferenceId("googletasks"));
						
						if (task.getParent() != null)
							move.setParent(task.getParent().getModelReferenceId(
									"googletasks"));
						
						move.execute();
						
						PluginLogger.getLogger().info(
								"Move task: " + task.getTitle());
						
					} catch (Throwable t) {
						PluginLogger.getLogger().info(
								"Cannot move task: " + task.getTitle());
					}
				}
				
				continue;
			}
			
			if (existingGoogleTask == null) {
				// Insert new task
				newGoogleTask = service.tasks().insert(
						taskListId,
						newGoogleTask).execute();
				task.addModelReferenceId("googletasks", newGoogleTask.getId());
				
				PluginLogger.getLogger().info("Insert task: " + task.getTitle());
				
				// Move new task
				if (task.getParent() != null) {
					try {
						Move move = service.tasks().move(
								taskListId,
								task.getModelReferenceId("googletasks"));
						move.setParent(task.getParent().getModelReferenceId(
								"googletasks"));
						move.execute();
						
						PluginLogger.getLogger().info(
								"Move task: " + task.getTitle());
						
					} catch (Throwable t) {
						PluginLogger.getLogger().info(
								"Cannot move task: " + task.getTitle());
					}
				}
			} else {
				// Update existing task
				service.tasks().update(
						taskListId,
						existingGoogleTask.getId(),
						newGoogleTask).execute();
				
				PluginLogger.getLogger().info("Update task: " + task.getTitle());
				
				// Move existing task
				if (!EqualsUtils.equals(
						existingGoogleTask.getParent(),
						newGoogleTask.getParent())) {
					try {
						Move move = service.tasks().move(
								taskListId,
								task.getModelReferenceId("googletasks"));
						
						if (task.getParent() != null)
							move.setParent(task.getParent().getModelReferenceId(
									"googletasks"));
						
						move.execute();
						
						PluginLogger.getLogger().info(
								"Move task: " + task.getTitle());
						
					} catch (Throwable t) {
						PluginLogger.getLogger().info(
								"Cannot move task: " + task.getTitle());
					}
				}
			}
		}
		
		if (monitor != null && actionCount > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_END,
					ModelType.TASK,
					actionCount));
		
		if (monitor != null && googleTasks.size() > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_START,
					ModelType.TASK,
					googleTasks.size()));
		
		// Delete tasks
		for (com.google.api.services.tasks.model.Task task : googleTasks) {
			service.tasks().delete(taskListId, task.getId()).execute();
			
			PluginLogger.getLogger().info("Delete task: " + task.getTitle());
		}
		
		if (monitor != null && googleTasks.size() > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleTasksPlugin.ID),
					ProgressMessageType.PUBLISHER_END,
					ModelType.TASK,
					googleTasks.size()));
	}
	
	private com.google.api.services.tasks.model.Task fillTask(
			Task task,
			com.google.api.services.tasks.model.Task googleTask) {
		if (googleTask == null)
			googleTask = new com.google.api.services.tasks.model.Task();
		
		googleTask.setTitle(task.getTitle());
		googleTask.setNotes(task.getNote());
		
		if (task.getParent() == null) {
			googleTask.setParent(null);
		} else {
			googleTask.setParent(task.getParent().getModelReferenceId(
					"googletasks"));
		}
		
		if (!task.isCompleted()) {
			googleTask.setStatus("needsAction");
			googleTask.setCompleted(null);
		} else {
			googleTask.setStatus("completed");
			googleTask.setCompleted(new DateTime(
					false,
					GoogleTasksTranslations.translateGMTDateUserInput(task.getCompletedOn()),
					0));
		}
		
		if (task.getDueDate() == null) {
			googleTask.setDue(null);
		} else {
			Calendar dueDate = task.getDueDate();
			DateUtils.removeTime(dueDate);
			
			googleTask.setDue(new DateTime(
					false,
					GoogleTasksTranslations.translateGMTDateUserInput(dueDate),
					0));
		}
		
		return googleTask;
	}
	
	private boolean equalGoogleTasks(
			boolean checkParent,
			com.google.api.services.tasks.model.Task existingGoogleTask,
			com.google.api.services.tasks.model.Task newGoogleTask) {
		if (existingGoogleTask == null)
			return false;
		
		if (checkParent)
			if (!EqualsUtils.equalsString(
					existingGoogleTask.getParent(),
					newGoogleTask.getParent()))
				return false;
		
		if (!EqualsUtils.equalsString(
				existingGoogleTask.getTitle(),
				newGoogleTask.getTitle()))
			return false;
		
		if (!EqualsUtils.equalsString(
				existingGoogleTask.getNotes(),
				newGoogleTask.getNotes()))
			return false;
		
		if (!EqualsUtils.equalsString(
				existingGoogleTask.getStatus(),
				newGoogleTask.getStatus()))
			return false;
		
		Long newDue = null;
		Long existingDue = null;
		
		if (newGoogleTask.getDue() != null)
			newDue = newGoogleTask.getDue().getValue() / 1000;
		
		if (existingGoogleTask.getDue() != null)
			existingDue = existingGoogleTask.getDue().getValue() / 1000;
		
		if (!EqualsUtils.equals(newDue, existingDue))
			return false;
		
		return true;
	}
	
	@Override
	public void synchronize() throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(SynchronizerChoice choice)
			throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(ProgressMonitor monitor)
			throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(SynchronizerChoice choice, ProgressMonitor monitor)
			throws SynchronizerException {
		
	}
	
	@Override
	public void loadParameters(Properties properties) {
		
	}
	
	@Override
	public void saveParameters(Properties properties) {
		
	}
	
}
