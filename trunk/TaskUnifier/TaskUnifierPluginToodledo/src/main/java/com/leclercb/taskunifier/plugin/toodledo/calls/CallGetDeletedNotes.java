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
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallGetDeletedNotes extends AbstractCall {
	
	public NoteBean[] getDeletedNotes(
			ToodledoAccountInfo accountInfo,
			String key,
			Calendar deletedAfter) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		
		if (deletedAfter != null)
			params.add(new BasicNameValuePair(
					"after",
					ToodledoTranslations.translateGMTDate(deletedAfter) + ""));
		
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(
				scheme,
				"/3/notes/deleted.php",
				params);
		
		return this.getResponseMessage(content);
	}
	
	/**
	 * Example :
	 * <deleted num="2">
	 * <note>
	 * <id>12345</id>
	 * <stamp>2008-02-25 07:46:42</stamp>
	 * </note>
	 * <note>
	 * <id>67890</id>
	 * <stamp>2008-03-12 14:11:12</stamp>
	 * </note>
	 * </deleted>
	 * 
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	private NoteBean[] getResponseMessage(String content)
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
						ToodledoErrorType.NOTE,
						content,
						childNodes.item(0));
			
			NodeList nNotes = childNodes.item(0).getChildNodes();
			
			List<NoteBean> deletedNotes = new ArrayList<NoteBean>();
			
			for (int i = 0; i < nNotes.getLength(); i++) {
				Node nNote = nNotes.item(i);
				NodeList nNoteInfos = nNote.getChildNodes();
				
				String id = null;
				Calendar stamp = null;
				
				for (int j = 0; j < nNoteInfos.getLength(); j++) {
					Node nNoteInfo = nNoteInfos.item(j);
					
					if (nNoteInfo.getNodeName().equals("id"))
						id = nNoteInfo.getTextContent();
					
					if (nNoteInfo.getNodeName().equals("stamp"))
						stamp = ToodledoTranslations.translateGMTDate(Long.parseLong(nNoteInfo.getTextContent()));
				}
				
				NoteBean note = NoteFactory.getInstance().createOriginalBean();
				
				note.getModelReferenceIds().put("toodledo", id);
				note.setModelStatus(ModelStatus.DELETED);
				note.setModelUpdateDate(stamp);
				
				deletedNotes.add(note);
			}
			
			return deletedNotes.toArray(new NoteBean[0]);
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
