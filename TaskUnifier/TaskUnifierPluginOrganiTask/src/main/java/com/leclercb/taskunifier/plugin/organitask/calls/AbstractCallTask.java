/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.StringReader;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.XMLUtils;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.beans.ContactListBean;
import com.leclercb.taskunifier.api.models.beans.FileListBean;
import com.leclercb.taskunifier.api.models.beans.ModelBeanList;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.beans.TaskListBean;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallTask extends AbstractCall {
	
	/**
	 * Example :
	 * <tasks num="2" total="45">
	 * <task>
	 * <id>1234</id>
	 * <title>Buy Milk</title>
	 * <tag>After work</tag>
	 * <folder>123</folder>
	 * <context>456</context>
	 * <goal>789</goal>
	 * <location>123</location>
	 * <parent>1122</parent>
	 * <children>0</children>
	 * <order>0</order>
	 * <duedate>1280877483</duedate>
	 * <duedatemod>0</duedatemod>
	 * <startdate>1280877483</startdate>
	 * <duetime>1280877483</duetime>
	 * <starttime>1280877483</starttime>
	 * <remind>60</remind>
	 * <repeat>Every 1 Week</repeat>
	 * <repeatfrom>0</repeatfrom>
	 * <status>4</status>
	 * <length>20</length>
	 * <priority>2</priority>
	 * <star>0</star>
	 * <modified>1280877483</modified>
	 * <completed>1280877483</completed>
	 * <added>1280877483</added>
	 * <timer>0</timer>
	 * <timeron>1280877483</timeron>
	 * <note></note>
	 * </task>
	 * </tasks>
	 * 
	 * @param url
	 * @param inputStream
	 * @return
	 * @throws SynchronizerException
	 */
	protected ToodledoTaskList getResponseMessage(
			List<Task> tasks,
			String content,
			ToodledoAccountInfo accountInfo) throws SynchronizerException {
		CheckUtils.isNotNull(content);
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader reader = new StringReader(content);
			InputSource inputSource = new InputSource(reader);
			Document document = builder.parse(inputSource);
			reader.close();
			NodeList childNodes = document.getChildNodes();
			
			if (!childNodes.item(0).getNodeName().equals("tasks"))
				this.throwResponseError(
						tasks,
						ToodledoErrorType.TASK,
						content,
						childNodes.item(0));
			
			Integer total = XMLUtils.getIntAttributeValue(
					childNodes.item(0),
					"total");
			if (total == null)
				total = -1;
			
			NodeList nTasks = childNodes.item(0).getChildNodes();
			ToodledoTaskList beans = new ToodledoTaskList(total);
			
			Boolean syncReminderFieldValue = true;
			if (PluginApi.getUserSettings().getBooleanProperty(
					"synchronizer.sync_reminder_field") != null)
				syncReminderFieldValue = PluginApi.getUserSettings().getBooleanProperty(
						"synchronizer.sync_reminder_field");
			
			for (int i = 0; i < nTasks.getLength(); i++) {
				if (!nTasks.item(i).getNodeName().equals("task"))
					this.throwResponseError(
							(i < tasks.size() ? tasks.get(i) : null),
							ToodledoErrorType.TASK,
							content,
							nTasks.item(i));
				
				NodeList nTask = nTasks.item(i).getChildNodes();
				
				String id = null;
				String title = null;
				int order = 0;
				String tags = null;
				String folder = null;
				String context = null;
				ModelBeanList contexts = new ModelBeanList();
				String goal = null;
				ModelBeanList goals = new ModelBeanList();
				String location = null;
				ModelBeanList locations = new ModelBeanList();
				String parent = null;
				long dueDate = 0;
				long startDate = 0;
				long dueTime = 0;
				long startTime = 0;
				int dueDateReminder = 0;
				int startDateReminder = 0;
				String repeat = null;
				TaskRepeatFrom repeatFrom = null;
				String status = null;
				int length = 0;
				long timerValue = 0;
				Calendar timerStartDate = null;
				TaskPriority priority = null;
				boolean star = false;
				Calendar added = null;
				Calendar modified = null;
				double progress = 0;
				Calendar completed = null;
				String note = null;
				ContactListBean contactGroup = null;
				FileListBean fileGroup = null;
				TaskListBean taskGroup = null;
				
				TaskBean bean = TaskFactory.getInstance().createOriginalBean();
				
				for (int j = 0; j < nTask.getLength(); j++) {
					Node nTaskInfo = nTask.item(j);
					
					if (nTaskInfo.getNodeName().equals("id"))
						id = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("title"))
						title = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("tag"))
						tags = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("folder"))
						if (!nTaskInfo.getTextContent().equals("0"))
							folder = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("context"))
						if (!nTaskInfo.getTextContent().equals("0"))
							context = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("goal"))
						if (!nTaskInfo.getTextContent().equals("0"))
							goal = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("location"))
						if (!nTaskInfo.getTextContent().equals("0"))
							location = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("parent"))
						if (accountInfo.isProMember())
							if (!nTaskInfo.getTextContent().equals("0"))
								parent = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("duedate"))
						if (!nTaskInfo.getTextContent().equals("0"))
							dueDate = Long.parseLong(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("startdate"))
						if (!nTaskInfo.getTextContent().equals("0"))
							startDate = Long.parseLong(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("duetime"))
						if (!nTaskInfo.getTextContent().equals("0"))
							dueTime = Long.parseLong(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("starttime"))
						if (!nTaskInfo.getTextContent().equals("0"))
							startTime = Long.parseLong(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("remind"))
						if (syncReminderFieldValue)
							dueDateReminder = Integer.parseInt(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("repeat"))
						repeat = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("repeatfrom"))
						repeatFrom = ToodledoTranslations.translateTaskRepeatFrom(Integer.parseInt(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("status"))
						status = ToodledoTranslations.translateTaskStatus(Integer.parseInt(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("length"))
						length = Integer.parseInt(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("timer"))
						timerValue = Long.parseLong(nTaskInfo.getTextContent());
					
					if (nTaskInfo.getNodeName().equals("timeron"))
						if (!nTaskInfo.getTextContent().equals("0"))
							timerStartDate = ToodledoTranslations.translateGMTDate(Long.parseLong(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("priority"))
						priority = ToodledoTranslations.translateTaskPriority(Integer.parseInt(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("star"))
						star = nTaskInfo.getTextContent().equals("1");
					
					if (nTaskInfo.getNodeName().equals("added"))
						added = ToodledoTranslations.translateGMTDate(Long.parseLong(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("modified"))
						modified = ToodledoTranslations.translateGMTDate(Long.parseLong(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("completed"))
						if (!nTaskInfo.getTextContent().equals("0"))
							completed = ToodledoTranslations.translateGMTDate(Long.parseLong(nTaskInfo.getTextContent()));
					
					if (nTaskInfo.getNodeName().equals("note"))
						note = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("meta")) {
						try {
							PropertyMap meta = new PropertyMap();
							meta.load(IOUtils.toInputStream(nTaskInfo.getTextContent()));
							
							contexts = ToodledoTranslations.translateModelList(
									meta,
									"context",
									ModelType.CONTEXT);
							goals = ToodledoTranslations.translateModelList(
									meta,
									"goal",
									ModelType.GOAL);
							locations = ToodledoTranslations.translateModelList(
									meta,
									"location",
									ModelType.LOCATION);
							
							contactGroup = ToodledoTranslations.translateTaskContactList(meta);
							fileGroup = ToodledoTranslations.translateTaskFileList(meta);
							taskGroup = ToodledoTranslations.translateTaskTaskList(meta);
							
							if (meta.containsKey("order")) {
								order = meta.getIntegerProperty("order");
							}
							
							if (meta.containsKey("progress")) {
								progress = meta.getDoubleProperty("progress");
							}
							
							if (meta.containsKey("parent")) {
								if (!accountInfo.isProMember())
									parent = meta.getStringProperty("parent");
							}
							
							if (meta.containsKey("startDateReminder")) {
								startDateReminder = meta.getIntegerProperty("startDateReminder");
							}
							
							if (meta.containsKey("dueDateReminder")) {
								if (!syncReminderFieldValue)
									dueDateReminder = meta.getIntegerProperty("dueDateReminder");
							}
							
							for (Object key : meta.keySet()) {
								if (((String) key).startsWith("properties_")) {
									bean.getProperties().setStringProperty(
											((String) key).substring("properties_".length()),
											meta.getStringProperty((String) key));
								}
							}
						} catch (Throwable t) {
							
						}
					}
				}
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelCreationDate(added);
				bean.setModelUpdateDate(modified);
				bean.setTitle(title);
				bean.setOrder(order);
				bean.setTags(TagList.fromString(tags));
				bean.setFolder(ToodledoTranslations.getModelOrCreateShell(
						ModelType.FOLDER,
						folder));
				bean.getContexts().add(
						ToodledoTranslations.getModelOrCreateShell(
								ModelType.CONTEXT,
								context));
				bean.getContexts().addAll(contexts);
				bean.getGoals().add(
						ToodledoTranslations.getModelOrCreateShell(
								ModelType.GOAL,
								goal));
				bean.getGoals().addAll(goals);
				bean.getLocations().add(
						ToodledoTranslations.getModelOrCreateShell(
								ModelType.LOCATION,
								location));
				bean.getLocations().addAll(locations);
				bean.setParent(ToodledoTranslations.getModelOrCreateShell(
						ModelType.TASK,
						parent));
				
				if (dueDate != 0) {
					Calendar date = ToodledoTranslations.translateGMTDateUserInput(dueDate);
					
					if (dueTime != 0) {
						Calendar time = ToodledoTranslations.translateGMTDateUserInput(dueTime);
						
						date.set(
								date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH),
								time.get(Calendar.HOUR_OF_DAY),
								time.get(Calendar.MINUTE),
								time.get(Calendar.SECOND));
					} else {
						date.set(
								date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH),
								0,
								0,
								0);
					}
					
					bean.setDueDate(date);
				}
				
				if (startDate != 0) {
					Calendar date = ToodledoTranslations.translateGMTDateUserInput(startDate);
					
					if (startTime != 0) {
						Calendar time = ToodledoTranslations.translateGMTDateUserInput(startTime);
						
						date.set(
								date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH),
								time.get(Calendar.HOUR_OF_DAY),
								time.get(Calendar.MINUTE),
								time.get(Calendar.SECOND));
					} else {
						date.set(
								date.get(Calendar.YEAR),
								date.get(Calendar.MONTH),
								date.get(Calendar.DAY_OF_MONTH),
								0,
								0,
								0);
					}
					
					bean.setStartDate(date);
				}
				
				bean.setDueDateReminder(dueDateReminder);
				bean.setStartDateReminder(startDateReminder);
				bean.setRepeat(repeat);
				bean.setRepeatFrom(repeatFrom);
				bean.setStatus(status);
				bean.setLength(length);
				bean.setTimer(new Timer(timerValue, timerStartDate));
				bean.setPriority(priority);
				bean.setStar(star);
				bean.setProgress(progress);
				bean.setCompleted(completed != null);
				bean.setCompletedOn(completed);
				bean.setNote(note);
				bean.setContacts(contactGroup);
				bean.setFiles(fileGroup);
				bean.setTasks(taskGroup);
				
				beans.add(bean);
			}
			
			return beans;
		} catch (SynchronizerException e) {
			throw e;
		} catch (Exception e) {
			throw new SynchronizerParsingException(
					"Error while parsing response ("
							+ this.getClass().getName()
							+ ")",
					content,
					e);
		}
	}
	
}
