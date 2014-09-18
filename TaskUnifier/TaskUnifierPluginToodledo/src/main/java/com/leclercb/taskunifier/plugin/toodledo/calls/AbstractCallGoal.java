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
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallGoal extends AbstractCall {
	
	/**
	 * Example : <goal> <id>123</id> <name>Get a Raise</name> <level>0</level>
	 * <archived>1</archived> <contributes>0</contributes> <note></note> </goal>
	 * 
	 * @param goal
	 * @param accountInfo
     * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected GoalBean[] getResponseMessage(
			Goal goal,
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
			
			if (!childNodes.item(0).getNodeName().equals("goals"))
				this.throwResponseError(
						goal,
						ToodledoErrorType.GOAL,
						content,
						childNodes.item(0));
			
			NodeList nGoals = childNodes.item(0).getChildNodes();
			List<GoalBean> goals = new ArrayList<GoalBean>();
			
			for (int i = 0; i < nGoals.getLength(); i++) {
				NodeList nGoal = nGoals.item(i).getChildNodes();
				
				String id = null;
				int level = 0;
				String contributes = null;
				String title = null;
				boolean archived = false;
				
				for (int j = 0; j < nGoal.getLength(); j++) {
					if (nGoal.item(j).getNodeName().equals("id"))
						id = nGoal.item(j).getTextContent();
					
					if (nGoal.item(j).getNodeName().equals("level"))
						level = Integer.parseInt(nGoal.item(j).getTextContent());
					
					if (nGoal.item(j).getNodeName().equals("contributes"))
						if (!nGoal.item(j).getTextContent().equals("0"))
							contributes = nGoal.item(j).getTextContent();
					
					if (nGoal.item(j).getNodeName().equals("name"))
						title = nGoal.item(j).getTextContent();
					
					if (nGoal.item(j).getNodeName().equals("archived"))
						archived = nGoal.item(j).getTextContent().equals("1");
				}
				
				GoalBean bean = GoalFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelUpdateDate(accountInfo.getLastGoalEdit());
				bean.setTitle(title);
				bean.setLevel(ToodledoTranslations.translateGoalLevel(level));
				bean.setContributes(ToodledoTranslations.getModelOrCreateShell(
						ModelType.GOAL,
						contributes));
				bean.setArchived(archived);
				goals.add(bean);
			}
			
			return goals.toArray(new GoalBean[0]);
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
