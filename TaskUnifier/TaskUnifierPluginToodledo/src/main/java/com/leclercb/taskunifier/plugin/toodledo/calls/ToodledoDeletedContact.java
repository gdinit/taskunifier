/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.beans.converters.CalendarConverter;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ToodledoDeletedContact {
	
	@XStreamAlias("modelid")
	private ModelId modelId;
	
	@XStreamAlias("modelupdatedate")
	@XStreamConverter(CalendarConverter.class)
	private Calendar modelUpdateDate;
	
	public ToodledoDeletedContact() {
		this((ModelId) null);
	}
	
	public ToodledoDeletedContact(ModelId modelId) {
		this.setModelId(modelId);
		this.setModelUpdateDate(Calendar.getInstance());
	}
	
	public ModelId getModelId() {
		return this.modelId;
	}
	
	public void setModelId(ModelId modelId) {
		this.modelId = modelId;
	}
	
	public Calendar getModelUpdateDate() {
		return this.modelUpdateDate;
	}
	
	public void setModelUpdateDate(Calendar modelUpdateDate) {
		this.modelUpdateDate = modelUpdateDate;
	}
	
	public static ToodledoDeletedContact[] decodeBeansFromXML(InputStream input) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new DomDriver("UTF-8"));
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.setClassLoader(ToodledoDeletedContact.class.getClassLoader());
		xstream.alias("deleted", ToodledoDeletedContact[].class);
		xstream.alias("contact", ToodledoDeletedContact.class);
		xstream.processAnnotations(ToodledoDeletedContact.class);
		
		return (ToodledoDeletedContact[]) xstream.fromXML(input);
	}
	
	public static void encodeBeansToXML(
			OutputStream output,
			ToodledoDeletedContact[] beans) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new DomDriver("UTF-8"));
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.setClassLoader(ToodledoDeletedContact.class.getClassLoader());
		xstream.alias("deleted", ToodledoDeletedContact[].class);
		xstream.alias("contact", ToodledoDeletedContact.class);
		xstream.processAnnotations(ToodledoDeletedContact.class);
		
		xstream.toXML(beans, output);
	}
	
}
