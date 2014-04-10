/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;
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
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCall {
	
	public AbstractCall() {
		
	}
	
	protected String getScheme() {
		if (EqualsUtils.equals(
				PluginApi.getUserSettings().getBooleanProperty(
						"plugin.organitask.enable_ssl"),
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
							OrganiTaskApi.getInstance().getApiUrl(),
							-1,
							path,
							URLEncodedUtils.format(parameters, "UTF-8"),
							null),
					OrganiTaskApi.getInstance().getProxyHost(),
					OrganiTaskApi.getInstance().getProxyPort(),
					OrganiTaskApi.getInstance().getProxyUsername(),
					OrganiTaskApi.getInstance().getProxyPassword());
			
			PluginLogger.getLogger().fine(
					URIUtils.createURI(
							scheme,
							OrganiTaskApi.getInstance().getApiUrl(),
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
			
			return response.getContent();
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
	
	protected String call(
            String requestMethod,
			String scheme,
			String path,
			String body) throws SynchronizerException {
		try {
			URI uri = URIUtils.createURI(
					scheme,
					OrganiTaskApi.getInstance().getApiUrl(),
					-1,
					path,
					null,
					null);
			
			HttpResponse response = HttpUtils.getHttpResponse(
                    requestMethod,
					uri,
					body,
                    "application/json",
					OrganiTaskApi.getInstance().getProxyHost(),
					OrganiTaskApi.getInstance().getProxyPort(),
					OrganiTaskApi.getInstance().getProxyUsername(),
					OrganiTaskApi.getInstance().getProxyPassword());
			
			PluginLogger.getLogger().fine(uri + "\nBody:\n" + body);
			
			if (!response.isSuccessfull()) {
				PluginLogger.getLogger().warning(
						response.getCode() + ": " + response.getMessage());
				
				throw new SynchronizerHttpException(
						false,
						response.getCode(),
						response.getMessage());
			}
			
			PluginLogger.getLogger().fine(response.getContent());
			
			return response.getContent();
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
	
}
