/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoConnection;

final class CallGetTasks extends AbstractCallTask {
	
	private static final int NUM = 1000;
	private static final String FIELDS = "folder,context,goal,location,tag,startdate,duedate,duedatemod,starttime,duetime,remind,repeat,status,star,priority,length,timer,added,note,parent,children,order,meta";
	
	public TaskBean[] getTasks(
			ToodledoAccountInfo accountInfo,
			ToodledoConnection connection,
			String accessToken) throws SynchronizerException {
		int start = 0;
		ToodledoTaskList list = this.getTasks(accountInfo, accessToken, start, NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getTasks(accountInfo, accessToken, start, NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				accessToken = connection.getAccessToken();
				
				list.addAll(this.getTasks(accountInfo, accessToken, start, NUM));
			}
		}
		
		return list.toArray(new TaskBean[0]);
	}
	
	private ToodledoTaskList getTasks(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("start", start + ""));
		params.add(new BasicNameValuePair("num", num + ""));
		params.add(new BasicNameValuePair("fields", FIELDS));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/tasks/get.php", params);
		
		return this.getResponseMessage(null, content, accountInfo);
	}
	
	public TaskBean[] getTasksNotCompleted(
			ToodledoAccountInfo accountInfo,
			ToodledoConnection connection,
			String accessToken) throws SynchronizerException {
		int start = 0;
		ToodledoTaskList list = this.getTasksNotCompleted(
				accountInfo,
				accessToken,
				start,
				NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getTasksNotCompleted(
						accountInfo,
						accessToken,
						start,
						NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				accessToken = connection.getAccessToken();
				
				list.addAll(this.getTasksNotCompleted(
						accountInfo,
						accessToken,
						start,
						NUM));
			}
		}
		
		return list.toArray(new TaskBean[0]);
	}
	
	private ToodledoTaskList getTasksNotCompleted(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("start", start + ""));
		params.add(new BasicNameValuePair("num", num + ""));
		params.add(new BasicNameValuePair("comp", "0"));
		params.add(new BasicNameValuePair("fields", FIELDS));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/tasks/get.php", params);
		
		return this.getResponseMessage(null, content, accountInfo);
	}
	
	public TaskBean[] getTasksModifiedAfter(
			ToodledoAccountInfo accountInfo,
			ToodledoConnection connection,
			String accessToken,
			Calendar modifiedAfter) throws SynchronizerException {
		int start = 0;
		ToodledoTaskList list = this.getTasksModifiedAfter(
				accountInfo,
				accessToken,
				modifiedAfter,
				start,
				NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getTasksModifiedAfter(
						accountInfo,
						accessToken,
						modifiedAfter,
						start,
						NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				accessToken = connection.getAccessToken();
				
				list.addAll(this.getTasksModifiedAfter(
						accountInfo,
						accessToken,
						modifiedAfter,
						start,
						NUM));
			}
		}
		
		return list.toArray(new TaskBean[0]);
	}
	
	private ToodledoTaskList getTasksModifiedAfter(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Calendar modifiedAfter,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("start", start + ""));
		params.add(new BasicNameValuePair("num", num + ""));
		params.add(new BasicNameValuePair("fields", FIELDS));
		
		if (modifiedAfter != null)
			params.add(new BasicNameValuePair(
					"modafter",
					ToodledoTranslations.translateGMTDate(modifiedAfter) + ""));
		
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/tasks/get.php", params);
		
		return this.getResponseMessage(null, content, accountInfo);
	}
	
}
