/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.TimeZone;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.ContactList;
import com.leclercb.taskunifier.api.models.ContactList.ContactItem;
import com.leclercb.taskunifier.api.models.FileList;
import com.leclercb.taskunifier.api.models.FileList.FileItem;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.TaskList;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;
import com.leclercb.taskunifier.api.models.beans.ContactListBean;
import com.leclercb.taskunifier.api.models.beans.ContactListBean.ContactItemBean;
import com.leclercb.taskunifier.api.models.beans.FileListBean;
import com.leclercb.taskunifier.api.models.beans.FileListBean.FileItemBean;
import com.leclercb.taskunifier.api.models.beans.ModelBeanList;
import com.leclercb.taskunifier.api.models.beans.TaskListBean;
import com.leclercb.taskunifier.api.models.beans.TaskListBean.TaskItemBean;
import com.leclercb.taskunifier.api.models.enums.GoalLevel;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.leclercb.taskunifier.api.properties.ModelIdCoder;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

final class ToodledoTranslations {
	
	private ToodledoTranslations() {
		
	}
	
	public static Calendar translateGMTDate(long timeStamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStamp * 1000);
		return calendar;
	}
	
	public static long translateGMTDate(Calendar calendar) {
		if (calendar == null)
			return 0;
		
		return calendar.getTimeInMillis() / 1000;
	}
	
	public static void translateTaskContactList(
			PropertyMap properties,
			ContactList contacts) {
		CheckUtils.isNotNull(properties);
		properties.addCoder(new ModelIdCoder());
		
		if (contacts == null)
			return;
		
		for (int i = 0; i < contacts.size(); i++) {
			ContactItem item = contacts.get(i);
			
			String link = "";
			ModelId contact = null;
			
			if (item.getLink() != null)
				link = item.getLink();
			
			if (item.getContact() != null)
				contact = item.getContact().getModelId();
			
			properties.setObjectProperty(
					"contact." + (i + 1) + ".contact",
					ModelId.class,
					contact);
			properties.setStringProperty("contact." + (i + 1) + ".link", link);
		}
	}
	
	public static ContactListBean translateTaskContactList(
			PropertyMap properties) {
		CheckUtils.isNotNull(properties);
		properties.addCoder(new ModelIdCoder());
		
		ContactListBean contactList = new ContactListBean();
		
		int count = 1;
		while (true) {
			String contactKey = "contact." + count + ".contact";
			ModelId contactValue = properties.getObjectProperty(
					contactKey,
					ModelId.class,
					null);
			
			String linkKey = "contact." + count + ".link";
			String linkValue = properties.getStringProperty(linkKey);
			
			if (contactValue == null && linkValue == null) {
				break;
			}
			
			contactList.add(new ContactItemBean(contactValue, linkValue));
			
			count++;
		}
		
		return contactList;
	}
	
	public static void translateTaskFileList(
			PropertyMap properties,
			FileList files) {
		CheckUtils.isNotNull(properties);
		properties.addCoder(new ModelIdCoder());
		
		if (files == null)
			return;
		
		for (int i = 0; i < files.size(); i++) {
			FileItem item = files.get(i);
			
			String link = "";
			String file = "";
			
			if (item.getLink() != null)
				link = item.getLink();
			
			if (item.getFile() != null)
				file = item.getFile();
			
			properties.setStringProperty("file." + (i + 1) + ".file", file);
			properties.setStringProperty("file." + (i + 1) + ".link", link);
		}
	}
	
	public static FileListBean translateTaskFileList(PropertyMap properties) {
		CheckUtils.isNotNull(properties);
		properties.addCoder(new ModelIdCoder());
		
		FileListBean fileList = new FileListBean();
		
		int count = 1;
		while (true) {
			String fileKey = "file." + count + ".file";
			String fileValue = properties.getStringProperty(fileKey);
			
			String linkKey = "file." + count + ".link";
			String linkValue = properties.getStringProperty(linkKey);
			
			if (fileValue == null && linkValue == null) {
				break;
			}
			
			fileList.add(new FileItemBean(fileValue, linkValue));
			
			count++;
		}
		
		return fileList;
	}
	
	public static void translateTaskTaskList(
			PropertyMap properties,
			TaskList tasks) {
		CheckUtils.isNotNull(properties);
		
		if (tasks == null)
			return;
		
		for (int i = 0; i < tasks.size(); i++) {
			TaskItem item = tasks.get(i);
			
			String link = "";
			String task = null;
			
			if (item.getLink() != null)
				link = item.getLink();
			
			if (item.getTask() != null)
				task = item.getTask().getModelReferenceId("toodledo");
			
			properties.setStringProperty("task." + (i + 1) + ".task_v3", task);
			properties.setStringProperty("task." + (i + 1) + ".link", link);
		}
	}
	
	public static TaskListBean translateTaskTaskList(PropertyMap properties) {
		CheckUtils.isNotNull(properties);
		properties.addCoder(new ModelIdCoder());
		
		TaskListBean taskList = new TaskListBean();
		
		int count = 1;
		while (true) {
			String taskKey = "task." + count + ".task_v3";
			String taskValue = properties.getStringProperty(taskKey, null);
			
			String linkKey = "task." + count + ".link";
			String linkValue = properties.getStringProperty(linkKey);
			
			if (taskValue == null && linkValue == null) {
				break;
			}
			
			taskList.add(new TaskItemBean(getModelOrCreateShell(
					ModelType.TASK,
					taskValue), linkValue));
			
			count++;
		}
		
		return taskList;
	}
	
	public static <M extends Model> void translateModelList(
			PropertyMap properties,
			ModelList<M> list,
			String propertyName) {
		CheckUtils.isNotNull(properties);
		
		if (list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			Model model = list.get(i);
			
			properties.setStringProperty(
					propertyName + "." + (i + 1),
					model.getModelReferenceId("toodledo"));
		}
	}
	
	public static ModelBeanList translateModelList(
			PropertyMap properties,
			String propertyName,
			ModelType modelType) {
		CheckUtils.isNotNull(properties);
		
		ModelBeanList list = new ModelBeanList();
		
		int count = 2;
		while (true) {
			String modelKey = propertyName + "." + count;
			String modelValue = properties.getStringProperty(modelKey, null);
			
			if (modelValue == null) {
				break;
			}
			
			list.add(getModelOrCreateShell(modelType, modelValue));
			
			count++;
		}
		
		return list;
	}
	
	public static String propertiesToString(PropertyMap properties) {
		try {
			StringWriter writer = new StringWriter();
			properties.store(writer, null);
			return writer.toString();
		} catch (IOException exc) {
			return null;
		}
	}
	
	public static ModelId getModelOrCreateShell(
			final ModelType modelType,
			final String foreignId) {
		Model model = ModelFactoryUtils.getFactory(modelType).get(
				"toodledo",
				foreignId);
		
		if (model == null && foreignId != null && foreignId.length() != 0) {
			PluginApi.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					Model model = ModelFactoryUtils.getFactory(modelType).createShell(
							new ModelId());
					model.addModelReferenceId("toodledo", foreignId);
				}
				
			});
			
			model = ModelFactoryUtils.getFactory(modelType).get(
					"toodledo",
					foreignId);
		}
		
		return (model == null ? null : model.getModelId());
	}
	
}
