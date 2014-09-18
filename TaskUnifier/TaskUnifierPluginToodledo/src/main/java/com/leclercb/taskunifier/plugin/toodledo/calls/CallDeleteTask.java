/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallDeleteTask extends AbstractCall {
	
	private static final int MAX = 50;
	
	public void deleteTask(
			ToodledoAccountInfo accountInfo,
			String key,
			Task task) throws SynchronizerException {
		CheckUtils.isNotNull(task);
		
		this.deleteTasks(accountInfo, key, new Task[] { task });
	}
	
	public void deleteTasks(
			ToodledoAccountInfo accountInfo,
			String key,
			Task[] tasks) throws SynchronizerException {
		List<Task> taskList = new ArrayList<Task>(Arrays.asList(tasks));
		while (taskList.size() != 0) {
			List<Task> list = taskList.subList(
					0,
					(MAX > taskList.size() ? taskList.size() : MAX));
			this.deleteTasksMax(accountInfo, key, list);
			list.clear();
		}
	}
	
	private void deleteTasksMax(
			ToodledoAccountInfo accountInfo,
			String key,
			List<Task> tasks) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(tasks);
		
		List<String> ids = new ArrayList<String>();
		for (Task task : tasks) {
			ids.add(task.getModelReferenceId("toodledo"));
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("tasks", "[\""
				+ StringUtils.join(ids, "\",\"")
				+ "\"]"));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(scheme, "/3/tasks/delete.php", params);
		
		List<ModelId> deletedIds = this.getResponseMessage(tasks, content);
		
		if (deletedIds.size() != ids.size())
			throw new SynchronizerException(
					false,
					"Deletion of tasks has failed");
	}
	
	/**
	 * Example : <deleted> <id>1234</id> <id>1235</id> </deleted>
	 * 
	 * @param tasks
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected List<ModelId> getResponseMessage(List<Task> tasks, String content)
			throws SynchronizerException {
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
			
			if (!childNodes.item(0).getNodeName().equals("deleted"))
				this.throwResponseError(
						tasks,
						ToodledoErrorType.TASK,
						content,
						childNodes.item(0));
			
			NodeList nDeleted = childNodes.item(0).getChildNodes();
			List<ModelId> ids = new ArrayList<ModelId>();
			
			for (int i = 0; i < nDeleted.getLength(); i++) {
				NodeList nId = nDeleted.item(i).getChildNodes();
				
				ModelId id = null;
				
				for (int j = 0; j < nId.getLength(); j++) {
					if (nId.item(j).getNodeName().equals("id"))
						id = new ModelId(nId.item(j).getTextContent());
				}
				
				ids.add(id);
			}
			
			return ids;
		} catch (SynchronizerException e) {
			throw e;
		} catch (Exception e) {
			throw new SynchronizerParsingException(
					"Error while parsing response",
					content,
					e);
		}
	}
	
}
