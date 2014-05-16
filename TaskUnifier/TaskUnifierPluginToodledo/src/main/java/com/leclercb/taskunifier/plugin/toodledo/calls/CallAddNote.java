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
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;

final class CallAddNote extends AbstractCallNote {
	
	private static final int MAX = 50;
	
	public List<String> addNote(
			ToodledoAccountInfo accountInfo,
			String key,
			Note note) throws SynchronizerException {
		CheckUtils.isNotNull(note);
		
		return this.addNotes(accountInfo, key, new Note[] { note });
	}
	
	public List<String> addNotes(
			ToodledoAccountInfo accountInfo,
			String key,
			Note[] notes) throws SynchronizerException {
		List<String> ids = new ArrayList<String>();
		List<Note> noteList = new ArrayList<Note>(Arrays.asList(notes));
		while (noteList.size() != 0) {
			List<Note> list = noteList.subList(
					0,
					(MAX > noteList.size() ? noteList.size() : MAX));
			ids.addAll(this.addNotesMax(accountInfo, key, list));
			list.clear();
		}
		
		return ids;
	}
	
	private List<String> addNotesMax(
			ToodledoAccountInfo accountInfo,
			String key,
			List<Note> notes) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(notes);
		
		JSONArray jsonArray = new JSONArray();
		
		try {
			for (Note note : notes) {
				JSONObject jsonObject = new JSONObject();
				
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
			throw new SynchronizerParsingException(
					"Error during JSON request creation",
					jsonArray.toString(),
					e);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("notebooks", jsonArray.toString()));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(scheme, "/2/notebooks/add.php", params);
		
		ToodledoNoteList tNotes = this.getResponseMessage(notes, content);
		
		if (tNotes.size() != notes.size())
			throw new SynchronizerException(false, "Adding notes has failed");
		
		List<String> ids = new ArrayList<String>();
		
		for (int i = 0; i < notes.size(); i++)
			ids.add(tNotes.get(i).getModelReferenceIds().get("toodledo"));
		
		return ids;
	}
	
}