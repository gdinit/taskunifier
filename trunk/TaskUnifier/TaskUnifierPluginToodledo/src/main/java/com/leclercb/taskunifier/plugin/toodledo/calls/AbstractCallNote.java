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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.XMLUtils;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallNote extends AbstractCall {
	
	/**
	 * Example :
	 * <notebooks num="2" total="2">
	 * <notebook>
	 * <id>1234</id>
	 * <title>My Ideas</title>
	 * <folder>123</folder>
	 * <private>0</private>
	 * <modified>1234567890</modified>
	 * <added>1234567890</added>
	 * <text>The text of my notebook</text>
	 * </notebook>
	 * <notebook>
	 * <id>1235</id>
	 * <title>Favorite Movies</title>
	 * <folder>123</folder>
	 * <private>0</private>
	 * <modified>1234567890</modified>
	 * <added>1234567890</added>
	 * <text>Star Wars</text>
	 * </notebook>
	 * </notebooks>
	 * 
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected ToodledoNoteList getResponseMessage(
			List<Note> notes,
			String content) throws SynchronizerException {
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
			
			if (!childNodes.item(0).getNodeName().equals("notebooks"))
				this.throwResponseError(
						notes,
						ToodledoErrorType.NOTE,
						content,
						childNodes.item(0));
			
			Integer total = XMLUtils.getIntAttributeValue(
					childNodes.item(0),
					"total");
			if (total == null)
				total = -1;
			
			NodeList nNotes = childNodes.item(0).getChildNodes();
			ToodledoNoteList beans = new ToodledoNoteList(total);
			
			for (int i = 0; i < nNotes.getLength(); i++) {
				if (!nNotes.item(i).getNodeName().equals("notebook"))
					this.throwResponseError(
							(i < notes.size() ? notes.get(i) : null),
							ToodledoErrorType.NOTE,
							content,
							nNotes.item(i));
				
				NodeList nNote = nNotes.item(i).getChildNodes();
				
				String id = null;
				String title = null;
				String folder = null;
				Calendar added = null;
				Calendar modified = null;
				String text = null;
				
				for (int j = 0; j < nNote.getLength(); j++) {
					Node nNoteInfo = nNote.item(j);
					
					if (nNoteInfo.getNodeName().equals("id"))
						id = nNoteInfo.getTextContent();
					
					if (nNoteInfo.getNodeName().equals("title"))
						title = nNoteInfo.getTextContent();
					
					if (nNoteInfo.getNodeName().equals("folder"))
						if (!nNoteInfo.getTextContent().equals("0"))
							folder = nNoteInfo.getTextContent();
					
					if (nNoteInfo.getNodeName().equals("added"))
						added = ToodledoTranslations.translateGMTDate(Long.parseLong(nNoteInfo.getTextContent()));
					
					if (nNoteInfo.getNodeName().equals("modified"))
						modified = ToodledoTranslations.translateGMTDate(Long.parseLong(nNoteInfo.getTextContent()));
					
					if (nNoteInfo.getNodeName().equals("text"))
						text = nNoteInfo.getTextContent();
				}
				
				NoteBean bean = NoteFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelCreationDate(added);
				bean.setModelUpdateDate(modified);
				bean.setTitle(title);
				bean.setFolder(ToodledoTranslations.getModelOrCreateShell(
						ModelType.FOLDER,
						folder));
				bean.setNote(text);
				
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
