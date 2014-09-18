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
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.beans.LocationBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;

abstract class AbstractCallLocation extends AbstractCall {
	
	/**
	 * Example : <locations> <location> <id>123</id> <lat>12.345</lat>
	 * <lon>-45.678</lon> <name>Work</name> <description>123 Main Street
	 * </description> </location> </locations>
	 * 
	 * @param location
	 * @param accountInfo
     * @param content
	 * @return
	 * @throws SynchronizerException
	 */
	protected LocationBean[] getResponseMessage(
			Location location,
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
			
			if (!childNodes.item(0).getNodeName().equals("locations"))
				this.throwResponseError(
						location,
						ToodledoErrorType.LOCATION,
						content,
						childNodes.item(0));
			
			NodeList nLocations = childNodes.item(0).getChildNodes();
			List<LocationBean> locations = new ArrayList<LocationBean>();
			
			for (int i = 0; i < nLocations.getLength(); i++) {
				NodeList nLocation = nLocations.item(i).getChildNodes();
				
				String id = null;
				String description = null;
				double latitude = 0;
				double longitude = 0;
				String title = null;
				
				for (int j = 0; j < nLocation.getLength(); j++) {
					if (nLocation.item(j).getNodeName().equals("id"))
						id = nLocation.item(j).getTextContent();
					
					if (nLocation.item(j).getNodeName().equals("description"))
						description = nLocation.item(j).getTextContent();
					
					if (nLocation.item(j).getNodeName().equals("lat"))
						latitude = Double.parseDouble(nLocation.item(j).getTextContent());
					
					if (nLocation.item(j).getNodeName().equals("lon"))
						longitude = Double.parseDouble(nLocation.item(j).getTextContent());
					
					if (nLocation.item(j).getNodeName().equals("name"))
						title = nLocation.item(j).getTextContent();
				}
				
				LocationBean bean = LocationFactory.getInstance().createOriginalBean();
				
				bean.getModelReferenceIds().put("toodledo", id);
				bean.setModelStatus(ModelStatus.LOADED);
				bean.setModelUpdateDate(accountInfo.getLastLocationEdit());
				bean.setTitle(title);
				bean.setDescription(description);
				bean.setLatitude(latitude);
				bean.setLongitude(longitude);
				
				locations.add(bean);
			}
			
			return locations.toArray(new LocationBean[0]);
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
