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

final class CallGetUserId extends AbstractCall {
	
	public String getUserId(String email, String password)
			throws SynchronizerException {
		CheckUtils.hasContent(email);
		CheckUtils.hasContent(password);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("pass", password));
		params.add(new BasicNameValuePair(
				"appid",
				com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getApplicationId()));
		params.add(new BasicNameValuePair(
				"sig",
				new CallGetSignature().getSignature(email)));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme();
		String content = super.callGet(scheme, "/2/account/lookup.php", params);
		
		return this.getResponseMessage(content);
	}
	
	/**
	 * Example :
	 * <userid>123456abcdef</userid>
	 * 
	 * @param url
	 * @param inputStream
	 * @return
	 * @throws SynchronizerException
	 */
	private String getResponseMessage(String content)
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
			
			Node nUserid = childNodes.item(0);
			
			if (!nUserid.getNodeName().equals("userid"))
				this.throwResponseError(
						ToodledoErrorType.ACCOUNT,
						content,
						nUserid);
			
			return nUserid.getTextContent();
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
