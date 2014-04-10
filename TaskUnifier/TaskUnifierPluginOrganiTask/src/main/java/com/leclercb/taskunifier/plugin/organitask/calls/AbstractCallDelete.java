/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallDelete extends AbstractCall {
	
	/**
	 * Example: [{"team_id":1,"id":41,"deletion_date":"1396508858"}]
	 * 
	 * @param url
	 * @param inputStream
	 * @return
	 * @throws SynchronizerException
	 */
	protected String getResponseMessage(
			Model model,
			ToodledoErrorType type,
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
			
			Node nDeleted = childNodes.item(0);
			
			if (!nDeleted.getNodeName().equals("deleted"))
				this.throwResponseError(model, type, content, nDeleted);
			
			return nDeleted.getTextContent();
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
