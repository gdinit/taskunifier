/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.beans.converters.CalendarConverter;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class OrganiTaskDeletedContact {

    @XStreamAlias("modelid")
    private ModelId modelId;

    @XStreamAlias("modelupdatedate")
    @XStreamConverter(CalendarConverter.class)
    private Calendar modelUpdateDate;

    public OrganiTaskDeletedContact() {
        this((ModelId) null);
    }

    public OrganiTaskDeletedContact(ModelId modelId) {
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

    public static OrganiTaskDeletedContact[] decodeBeansFromXML(InputStream input) {
        XStream xstream = new TUXStream(
                new PureJavaReflectionProvider(),
                new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.setClassLoader(OrganiTaskDeletedContact.class.getClassLoader());
        xstream.alias("deleted", OrganiTaskDeletedContact[].class);
        xstream.alias("contact", OrganiTaskDeletedContact.class);
        xstream.processAnnotations(OrganiTaskDeletedContact.class);

        return (OrganiTaskDeletedContact[]) xstream.fromXML(input);
    }

    public static void encodeBeansToXML(
            OutputStream output,
            OrganiTaskDeletedContact[] beans) {
        XStream xstream = new TUXStream(
                new PureJavaReflectionProvider(),
                new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.setClassLoader(OrganiTaskDeletedContact.class.getClassLoader());
        xstream.alias("deleted", OrganiTaskDeletedContact[].class);
        xstream.alias("contact", OrganiTaskDeletedContact.class);
        xstream.processAnnotations(OrganiTaskDeletedContact.class);

        xstream.toXML(beans, output);
    }

}
