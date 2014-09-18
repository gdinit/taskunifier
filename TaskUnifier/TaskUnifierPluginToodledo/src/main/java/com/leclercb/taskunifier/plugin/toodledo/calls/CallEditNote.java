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
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditNote extends AbstractCallNote {
	
	private static final int MAX = 10;
	
	public void editNote(ToodledoAccountInfo accountInfo, String key, Note note)
			throws SynchronizerException {
		CheckUtils.isNotNull(note);
		
		this.editNotes(accountInfo, key, new Note[] { note });
	}
	
	public void editNotes(
			ToodledoAccountInfo accountInfo,
			String key,
			Note[] notes) throws SynchronizerException {
		List<Note> noteList = new ArrayList<Note>(Arrays.asList(notes));
		while (noteList.size() != 0) {
			List<Note> list = noteList.subList(
					0,
					(MAX > noteList.size() ? noteList.size() : MAX));
			this.editNotesMax(accountInfo, key, list);
			list.clear();
		}
	}
	
	private void editNotesMax(
			ToodledoAccountInfo accountInfo,
			String key,
			List<Note> notes) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(notes);
		
		for (Note note : notes)
			if (note.getModelReferenceId("toodledo") == null)
				throw new IllegalArgumentException("You cannot edit a new note");
		
		JSONArray jsonArray = new JSONArray();
		
		try {
			for (Note note : notes) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("id", note.getModelReferenceId("toodledo"));
				jsonObject.put("title", note.getTitle());
				
				if (note.getFolder() != null
						&& note.getFolder().getModelReferenceId("toodledo") != null)
					jsonObject.put(
							"folder",
							note.getFolder().getModelReferenceId("toodledo"));
				else
					jsonObject.put("folder", "0");
				
				jsonObject.put("text", note.getNote());
				
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
		params.add(new BasicNameValuePair("notes", jsonArray.toString()));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(scheme, "/3/notes/edit.php", params);
		
		this.getResponseMessage(notes, content);
	}
	
}
