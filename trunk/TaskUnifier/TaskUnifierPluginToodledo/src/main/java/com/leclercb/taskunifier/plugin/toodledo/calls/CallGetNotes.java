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
			String key) throws SynchronizerException {
		int start = 0;
		ToodledoNoteList list = this.getNotes(accountInfo, key, start, NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getNotes(accountInfo, key, start, NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				key = connection.getKey();
				
				list.addAll(this.getNotes(accountInfo, key, start, NUM));
			}
		}
		
		return list.toArray(new NoteBean[0]);
	}
	
	private ToodledoNoteList getNotes(
			ToodledoAccountInfo accountInfo,
			String key,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
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
			String key,
			Calendar modifiedAfter) throws SynchronizerException {
		int start = 0;
		ToodledoNoteList list = this.getNotesModifiedAfter(
				accountInfo,
				key,
				modifiedAfter,
				start,
				NUM);
		while (list.getTotal() != list.size()) {
			start = start + NUM;
			
			try {
				list.addAll(this.getNotesModifiedAfter(
						accountInfo,
						key,
						modifiedAfter,
						start,
						NUM));
			} catch (SynchronizerConnectionException e) {
				connection.disconnect();
				connection.connect();
				key = connection.getKey();
				
				list.addAll(this.getNotesModifiedAfter(
						accountInfo,
						key,
						modifiedAfter,
						start,
						NUM));
			}
		}
		
		return list.toArray(new NoteBean[0]);
	}
	
	private ToodledoNoteList getNotesModifiedAfter(
			ToodledoAccountInfo accountInfo,
			String key,
			Calendar modifiedAfter,
			int start,
			int num) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
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
