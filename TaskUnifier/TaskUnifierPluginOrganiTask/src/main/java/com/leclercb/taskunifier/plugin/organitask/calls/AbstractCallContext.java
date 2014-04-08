/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	 * Example : [{"team_id":4,"parent_id":null,"id":20,"creation_date":"1396878407","update_date":"1396878407","title":"@Work","color":"FF0000","contexts":[]},{"team_id":4,"parent_id":null,"id":19,"creation_date":"1396878407","update_date":"1396878407","title":"@Home","color":"0000FF","contexts":[]}]
	 * 
	 * @param context
	 * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected ContextBean[] getResponseMessage(
			Context context,
			String content) throws SynchronizerException {
		CheckUtils.isNotNull(content);
		
		try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

			List<ContextBean> contexts = new ArrayList<ContextBean>();
			
			while (root.iterator().hasNext()) {
				ContextBean bean = ContextFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("organitask", root.path("id").textValue());
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
