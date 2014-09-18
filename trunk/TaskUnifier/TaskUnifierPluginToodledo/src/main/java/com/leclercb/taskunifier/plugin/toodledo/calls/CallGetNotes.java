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
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerConnectionException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoConnection;

final class CallGetNotes extends AbstractCallNote {
	
	private static final int NUM = 1000;
	
	public NoteBean[] getNotes(
			ToodledoAccountInfo accountInfo,
			ToodledoConnection connection,
			String accessToken) throws SynchronizerException {
		int start = 0;
		ToodledoNoteList list = this.getNotes(accountInfo, accessToken, start, NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getNotes(accountInfo, accessToken, start, NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				accessToken = connection.getAccessToken();
				
				list.addAll(this.getNotes(accountInfo, accessToken, start, NUM));
			}
		}
		
		return list.toArray(new NoteBean[0]);
	}
	
	private ToodledoNoteList getNotes(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("start", start + ""));
		params.add(new BasicNameValuePair("num", num + ""));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/notes/get.php", params);
		
		return this.getResponseMessage(null, content);
	}
	
	public NoteBean[] getNotesModifiedAfter(
			ToodledoAccountInfo accountInfo,
			ToodledoConnection connection,
			String accessToken,
			Calendar modifiedAfter) throws SynchronizerException {
		int start = 0;
		ToodledoNoteList list = this.getNotesModifiedAfter(
				accountInfo,
				accessToken,
				modifiedAfter,
				start,
				NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getNotesModifiedAfter(
						accountInfo,
						accessToken,
						modifiedAfter,
						start,
						NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				accessToken = connection.getAccessToken();
				
				list.addAll(this.getNotesModifiedAfter(
						accountInfo,
						accessToken,
						modifiedAfter,
						start,
						NUM));
			}
		}
		
		return list.toArray(new NoteBean[0]);
	}
	
	private ToodledoNoteList getNotesModifiedAfter(
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
		
		if (modifiedAfter != null)
			params.add(new BasicNameValuePair(
					"modafter",
					ToodledoTranslations.translateGMTDate(modifiedAfter) + ""));
		
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/notes/get.php", params);
		
		return this.getResponseMessage(null, content);
	}
	
}
