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

final class CallGetToken extends AbstractCall {
	
	public String getToken(String userId) throws SynchronizerException {
		CheckUtils.isNotNull(userId);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair(
				"appid",
				com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getApplicationId()));
		params.add(new BasicNameValuePair("vers", ""
				+ com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getVersion()));
		if (com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getDevice() != null)
			params.add(new BasicNameValuePair("device", ""
					+ com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getDevice()));
		params.add(new BasicNameValuePair("os", ""
				+ com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getOS()));
		params.add(new BasicNameValuePair(
				"sig",
				new CallGetSignature().getSignature(userId)));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme();
		String content = super.callGet(scheme, "/2/account/token.php", params);
		
		return this.getResponseMessage(content);
	}
	
	/**
	 * Example : <token>td12345678901234</token>
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
			
			Node nToken = childNodes.item(0);
			
			if (!nToken.getNodeName().equals("token"))
				this.throwResponseError(
						ToodledoErrorType.ACCOUNT,
						content,
						nToken);
			
			return nToken.getTextContent();
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
