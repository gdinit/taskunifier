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
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallDeleteNote extends AbstractCall {
	
	private static final int MAX = 50;
	
	public void deleteNote(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Note note) throws SynchronizerException {
		CheckUtils.isNotNull(note);
		
		this.deleteNotes(accountInfo, accessToken, new Note[] { note });
	}
	
	public void deleteNotes(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Note[] notes) throws SynchronizerException {
		List<Note> noteList = new ArrayList<Note>(Arrays.asList(notes));
		while (noteList.size() != 0) {
			List<Note> list = noteList.subList(
					0,
					(MAX > noteList.size() ? noteList.size() : MAX));
			this.deleteNotesMax(accountInfo, accessToken, list);
			list.clear();
		}
	}
	
	private void deleteNotesMax(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			List<Note> notes) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		CheckUtils.isNotNull(notes);
		
		List<String> ids = new ArrayList<String>();
		for (Note note : notes) {
			ids.add(note.getModelReferenceId("toodledo"));
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("notes", "[\""
				+ StringUtils.join(ids, "\",\"")
				+ "\"]"));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callPost(
				scheme,
				"/3/notes/delete.php",
				params);
		
		List<ModelId> deletedIds = this.getResponseMessage(notes, content);
		
		if (deletedIds.size() != ids.size())
			throw new SynchronizerException(
					false,
					"Deletion of notes has failed");
	}
	
	/**
	 * Example : <deleted> <id>1234</id> <id>1235</id> </deleted>
	 * 
	 * @param notes
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected List<ModelId> getResponseMessage(List<Note> notes, String content)
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
						notes,
						ToodledoErrorType.NOTE,
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
