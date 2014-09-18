/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallGetDeletedTasks extends AbstractCall {
	
	public TaskBean[] getDeletedTasks(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Calendar deletedAfter) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		
		if (deletedAfter != null)
			params.add(new BasicNameValuePair(
					"after",
					ToodledoTranslations.translateGMTDate(deletedAfter) + ""));
		
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/tasks/deleted.php", params);
		
		return this.getResponseMessage(content);
	}
	
	/**
	 * Example :
	 * <deleted num="2">
	 * <task>
	 * <id>12345</id>
	 * <stamp>1234567891</stamp>
	 * </task>
	 * <task>
	 * <id>67890</id>
	 * <stamp>1234567891</stamp>
	 * </task>
	 * </deleted>
	 * 
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	private TaskBean[] getResponseMessage(String content)
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
						ToodledoErrorType.TASK,
						content,
						childNodes.item(0));
			
			NodeList nTasks = childNodes.item(0).getChildNodes();
			
			List<TaskBean> deletedTasks = new ArrayList<TaskBean>();
			
			for (int i = 0; i < nTasks.getLength(); i++) {
				Node nTask = nTasks.item(i);
				NodeList nTaskInfos = nTask.getChildNodes();
				
				String id = null;
				Calendar stamp = null;
				
				for (int j = 0; j < nTaskInfos.getLength(); j++) {
					Node nTaskInfo = nTaskInfos.item(j);
					
					if (nTaskInfo.getNodeName().equals("id"))
						id = nTaskInfo.getTextContent();
					
					if (nTaskInfo.getNodeName().equals("stamp"))
						stamp = ToodledoTranslations.translateGMTDate(Long.parseLong(nTaskInfo.getTextContent()));
				}
				
				TaskBean task = TaskFactory.getInstance().createOriginalBean();
				
				task.getModelReferenceIds().put("toodledo", id);
				task.setModelStatus(ModelStatus.DELETED);
				task.setModelUpdateDate(stamp);
				
				deletedTasks.add(task);
			}
			
			return deletedTasks.toArray(new TaskBean[0]);
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
