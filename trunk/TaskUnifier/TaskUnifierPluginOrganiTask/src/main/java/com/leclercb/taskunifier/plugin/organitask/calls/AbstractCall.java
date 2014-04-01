/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.w3c.dom.Node;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.api.utils.HttpUtils;
import com.leclercb.commons.api.utils.XMLUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerHttpException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerNotConnectedException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCall {
	
	public AbstractCall() {
		
	}
	
	protected String getScheme() {
		if (EqualsUtils.equals(
				PluginApi.getUserSettings().getBooleanProperty(
						"toodledo.enable_ssl"),
				true))
			return "https";
		
		return "http";
	}
	
	protected String getScheme(ToodledoAccountInfo accountInfo) {
		if (accountInfo.isProMember()
				&& EqualsUtils.equals(
						PluginApi.getUserSettings().getBooleanProperty(
								"toodledo.enable_ssl"),
						true))
			return "https";
		
		return "http";
	}
	
	protected String callGet(
			String scheme,
			String path,
			List<NameValuePair> parameters) throws SynchronizerException {
		try {
			HttpResponse response = HttpUtils.getHttpGetResponse(
					URIUtils.createURI(
							scheme,
							ToodledoApi.getInstance().getApiUrl(),
							-1,
							path,
							URLEncodedUtils.format(parameters, "UTF-8"),
							null),
					ToodledoApi.getInstance().getProxyHost(),
					ToodledoApi.getInstance().getProxyPort(),
					ToodledoApi.getInstance().getProxyUsername(),
					ToodledoApi.getInstance().getProxyPassword());
			
			PluginLogger.getLogger().fine(
					URIUtils.createURI(
							scheme,
							ToodledoApi.getInstance().getApiUrl(),
							-1,
							path,
							URLEncodedUtils.format(parameters, "UTF-8"),
							null).toString());
			
			if (!response.isSuccessfull()) {
				PluginLogger.getLogger().warning(
						response.getCode() + ": " + response.getMessage());
				
				throw new SynchronizerHttpException(
						false,
						response.getCode(),
						response.getMessage());
			}
			
			PluginLogger.getLogger().fine(response.getContent());
			
			return stripNonValidXMLCharacters(response.getContent());
		} catch (NoRouteToHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (UnknownHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (Exception e) {
			throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
		}
	}
	
	protected String callPost(
			String scheme,
			String path,
			List<NameValuePair> parameters) throws SynchronizerException {
		try {
			URI uri = URIUtils.createURI(
					scheme,
					ToodledoApi.getInstance().getApiUrl(),
					-1,
					path,
					null,
					null);
			
			HttpResponse response = HttpUtils.getHttpPostResponse(
					uri,
					parameters,
					ToodledoApi.getInstance().getProxyHost(),
					ToodledoApi.getInstance().getProxyPort(),
					ToodledoApi.getInstance().getProxyUsername(),
					ToodledoApi.getInstance().getProxyPassword());
			
			StringBuffer logMessage = new StringBuffer();
			
			logMessage.append(uri);
			
			if (parameters != null)
				for (NameValuePair parameter : parameters)
					logMessage.append("\n"
							+ parameter.getName()
							+ " = "
							+ parameter.getValue());
			
			PluginLogger.getLogger().fine(logMessage.toString());
			
			if (!response.isSuccessfull()) {
				PluginLogger.getLogger().warning(
						response.getCode() + ": " + response.getMessage());
				
				throw new SynchronizerHttpException(
						false,
						response.getCode(),
						response.getMessage());
			}
			
			PluginLogger.getLogger().fine(response.getContent());
			
			return stripNonValidXMLCharacters(response.getContent());
		} catch (NoRouteToHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (UnknownHostException e) {
			throw new SynchronizerNotConnectedException(
					true,
					e.getMessage(),
					PluginApi.getTranslation("error.not_connected_internet"));
		} catch (Exception e) {
			throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
		}
	}
	
	protected void throwResponseError(
			ToodledoErrorType type,
			String content,
			Node errorNode) throws SynchronizerException {
		this.throwResponseError((Model) null, type, content, errorNode);
	}
	
	protected void throwResponseError(
			Model model,
			ToodledoErrorType type,
			String content,
			Node errorNode) throws SynchronizerException {
		this.throwResponseError(Arrays.asList(model), type, content, errorNode);
	}
	
	protected <M extends Model> void throwResponseError(
			List<M> models,
			ToodledoErrorType type,
			String content,
			Node errorNode) throws SynchronizerException {
		
		if (!errorNode.getNodeName().equals("error"))
			throw new SynchronizerParsingException(
					"Error while parsing response",
					content);
		
		int code = XMLUtils.getIntAttributeValue(errorNode, "id");
		String message = errorNode.getTextContent();
		
		ToodledoErrors.throwError(models, type, code, message);
	}
	
	private static String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer();
		char current;
		
		if (in == null)
			return null;
		
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i);
			
			if ((current == 0x9)
					|| (current == 0xA)
					|| (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		
		return out.toString();
	}
	
}
