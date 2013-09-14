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
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallFolder extends AbstractCall {
	
	/**
	 * Example : <folders> <folder> <id>123</id> <private>0</private>
	 * <archived>0</archived> <order>1</order> <name>Shopping</name> </folder>
	 * </folders>
	 * 
	 * @param url
	 * @param inputStream
	 * @return
	 * @throws SynchronizerException
	 */
	protected FolderBean[] getResponseMessage(
			Folder folder,
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
			
			if (!childNodes.item(0).getNodeName().equals("folders"))
				this.throwResponseError(
						folder,
						ToodledoErrorType.FOLDER,
						content,
						childNodes.item(0));
			
			NodeList nFolders = childNodes.item(0).getChildNodes();
			List<FolderBean> folders = new ArrayList<FolderBean>();
			
			for (int i = 0; i < nFolders.getLength(); i++) {
				NodeList nFolder = nFolders.item(i).getChildNodes();
				
				String id = null;
				String title = null;
				boolean archived = false;
				
				for (int j = 0; j < nFolder.getLength(); j++) {
					if (nFolder.item(j).getNodeName().equals("id"))
						id = nFolder.item(j).getTextContent();
					
					if (nFolder.item(j).getNodeName().equals("name"))
						title = nFolder.item(j).getTextContent();
					
					if (nFolder.item(j).getNodeName().equals("archived"))
						archived = nFolder.item(j).getTextContent().equals("1");
				}
				
				FolderBean bean = FolderFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelUpdateDate(accountInfo.getLastFolderEdit());
				bean.setTitle(title);
				bean.setArchived(archived);
				
				folders.add(bean);
			}
			
			return folders.toArray(new FolderBean[0]);
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
