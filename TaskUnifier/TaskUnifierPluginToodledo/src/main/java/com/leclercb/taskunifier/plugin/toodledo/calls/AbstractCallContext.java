/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.beans.ContextBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallContext extends AbstractCall {
	
	/**
	 * Example : <contexts> <context> <id>123</id> <name>Work</name> </context>
	 * </contexts>
	 * 
	 * @param context
     * @param accountInfo
	 * @return
	 * @throws SynchronizerException
	 */
	protected ContextBean[] getResponseMessage(
			Context context,
			ToodledoAccountInfo accountInfo,
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
			
			if (!childNodes.item(0).getNodeName().equals("contexts"))
				this.throwResponseError(
						context,
						ToodledoErrorType.CONTEXT,
						content,
						childNodes.item(0));
			
			NodeList nContexts = childNodes.item(0).getChildNodes();
			List<ContextBean> contexts = new ArrayList<ContextBean>();
			
			for (int i = 0; i < nContexts.getLength(); i++) {
				NodeList nContext = nContexts.item(i).getChildNodes();
				
				String id = null;
				String title = null;
				
				for (int j = 0; j < nContext.getLength(); j++) {
					if (nContext.item(j).getNodeName().equals("id"))
						id = nContext.item(j).getTextContent();
					
					if (nContext.item(j).getNodeName().equals("name"))
						title = nContext.item(j).getTextContent();
				}
				
				ContextBean bean = ContextFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelUpdateDate(accountInfo.getLastContextEdit());
				bean.setTitle(title);
				
				contexts.add(bean);
			}
			
			return contexts.toArray(new ContextBean[0]);
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
