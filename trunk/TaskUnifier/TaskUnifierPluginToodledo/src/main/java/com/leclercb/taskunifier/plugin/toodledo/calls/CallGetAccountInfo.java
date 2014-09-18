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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

final class CallGetAccountInfo extends AbstractCall {
	
	public ToodledoAccountInfo getAccountInfo(String key)
			throws SynchronizerException {
		CheckUtils.isNotNull(key);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String content = super.callGet("http", "/3/account/get.php", params);
		
		return this.getResponseMessage(content);
	}
	
	/**
	 * Example :
	 * <account>
	 * <userid>a1b2c3d4e5f6</userid>
	 * <alias>John</alias>
	 * <pro>0</pro>
	 * <dateformat>0</dateformat>
	 * <timezone>-6</timezone>
	 * <hidemonths>2</hidemonths>
	 * <hotlistpriority>3</hotlistpriority>
	 * <hotlistduedate>2</hotlistduedate>
	 * <showtabnums>1</showtabnums>
	 * <lastedit_folder>1281457337</lastedit_folder>
	 * <lastedit_context>1281457997</lastedit_context>
	 * <lastedit_goal>1280441959</lastedit_goal>
	 * <lastedit_location>1280441959</lastedit_location>
	 * <lastedit_task>1281458832</lastedit_task>
	 * <lastdelete_task>1280898329</lastdelete_task>
	 * <lastedit_note>1280894728</lastedit_note>
	 * <lastdelete_note>1280894728</lastdelete_note>
	 * </account>
	 * 
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	private ToodledoAccountInfo getResponseMessage(String content)
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
			
			if (!childNodes.item(0).getNodeName().equals("account"))
				this.throwResponseError(
						ToodledoErrorType.ACCOUNT,
						content,
						childNodes.item(0));
			
			NodeList nAccount = childNodes.item(0).getChildNodes();
			
			ToodledoAccountInfo accountInfo = new ToodledoAccountInfo();
			
			for (int i = 0; i < nAccount.getLength(); i++) {
				Node nInfo = nAccount.item(i);
				
				if (nInfo.getNodeName().equals("userid"))
					accountInfo.setUserId(nInfo.getTextContent());
				
				if (nInfo.getNodeName().equals("alias"))
					accountInfo.setAlias(nInfo.getTextContent());
				
				if (nInfo.getNodeName().equals("pro"))
					accountInfo.setProMember(!nInfo.getTextContent().equals("0"));
				
				if (nInfo.getNodeName().equals("dateformat"))
					accountInfo.setDateFormat(ToodledoTranslations.translateDateFormat(Integer.parseInt(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("timezone"))
					accountInfo.setTimeZone(Integer.parseInt(nInfo.getTextContent()));
				
				if (nInfo.getNodeName().equals("showtabnums"))
					accountInfo.setShowTabNums(Integer.parseInt(nInfo.getTextContent()));
				
				if (nInfo.getNodeName().equals("hidemonths"))
					accountInfo.setHideMonths(Integer.parseInt(nInfo.getTextContent()));
				
				if (nInfo.getNodeName().equals("hotlistpriority"))
					accountInfo.setHotListPriority(ToodledoTranslations.translateTaskPriority(Integer.parseInt(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("hotlistduedate"))
					accountInfo.setHotListDueDate(Integer.parseInt(nInfo.getTextContent()));
			}
			
			for (int i = 0; i < nAccount.getLength(); i++) {
				Node nInfo = nAccount.item(i);
				
				if (nInfo.getNodeName().equals("lastedit_context"))
					accountInfo.setLastContextEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastedit_folder"))
					accountInfo.setLastFolderEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastedit_goal"))
					accountInfo.setLastGoalEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastedit_location"))
					accountInfo.setLastLocationEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastedit_note"))
					accountInfo.setLastNoteEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastdelete_note"))
					accountInfo.setLastNoteDelete(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastedit_task"))
					accountInfo.setLastTaskEdit(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
				
				if (nInfo.getNodeName().equals("lastdelete_task"))
					accountInfo.setLastTaskDelete(ToodledoTranslations.translateGMTDate(Long.parseLong(nInfo.getTextContent())));
			}
			
			return accountInfo;
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
