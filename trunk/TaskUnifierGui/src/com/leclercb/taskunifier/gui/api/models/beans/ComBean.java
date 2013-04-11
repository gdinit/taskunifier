/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.api.models.beans;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.leclercb.taskunifier.api.models.templates.NoteTemplateFactory;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.leclercb.taskunifier.gui.api.models.beans.converters.ComNoteBeanWithTemplateConverter;
import com.leclercb.taskunifier.gui.api.models.beans.converters.ComTaskBeanWithTemplateConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

@XStreamAlias("com")
public class ComBean {
	
	@XStreamAlias("applicationname")
	private String applicationName;
	
	@XStreamAlias("arguments")
	private String[] arguments;
	
	@XStreamAlias("notes")
	private ComNoteBean[] notes;
	
	@XStreamAlias("tasks")
	private ComTaskBean[] tasks;
	
	@XStreamAlias("quicktasks")
	private ComQuickTaskBean[] quickTasks;
	
	public ComBean() {
		this.setApplicationName(null);
		this.setArguments(null);
		this.setNotes(null);
		this.setTasks(null);
		this.setQuickTasks(null);
	}
	
	public String getApplicationName() {
		return this.applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String[] getArguments() {
		return this.arguments;
	}
	
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}
	
	public ComNoteBean[] getNotes() {
		return this.notes;
	}
	
	public void setNotes(ComNoteBean[] notes) {
		this.notes = notes;
	}
	
	public ComTaskBean[] getTasks() {
		return this.tasks;
	}
	
	public void setTasks(ComTaskBean[] tasks) {
		this.tasks = tasks;
	}
	
	public ComQuickTaskBean[] getQuickTasks() {
		return this.quickTasks;
	}
	
	public void setQuickTasks(ComQuickTaskBean[] quickTasks) {
		this.quickTasks = quickTasks;
	}
	
	public void encodeToXML(OutputStream output) {
		encodeToXML(output, this);
	}
	
	public static void encodeToXML(OutputStream output, ComBean bean) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new Xpp3DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.processAnnotations(ComBean.class);
		xstream.alias("note", ComNoteBean.class);
		xstream.alias("task", ComTaskBean.class);
		xstream.alias("quicktask", ComQuickTaskBean.class);
		
		xstream.registerConverter(new ComNoteBeanWithTemplateConverter(
				xstream.getMapper(),
				xstream.getReflectionProvider(),
				NoteTemplateFactory.getInstance().getDefaultTemplate()));
		
		xstream.registerConverter(new ComTaskBeanWithTemplateConverter(
				xstream.getMapper(),
				xstream.getReflectionProvider(),
				TaskTemplateFactory.getInstance().getDefaultTemplate()));
		
		try {
			xstream.toXML(bean, new OutputStreamWriter(output, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			xstream.toXML(bean, output);
		}
	}
	
	public static ComBean decodeFromXML(InputStream input) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new Xpp3DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.processAnnotations(ComBean.class);
		xstream.alias("note", ComNoteBean.class);
		xstream.alias("task", ComTaskBean.class);
		xstream.alias("quicktask", ComQuickTaskBean.class);
		
		xstream.registerConverter(new ComNoteBeanWithTemplateConverter(
				xstream.getMapper(),
				xstream.getReflectionProvider(),
				NoteTemplateFactory.getInstance().getDefaultTemplate()));
		
		xstream.registerConverter(new ComTaskBeanWithTemplateConverter(
				xstream.getMapper(),
				xstream.getReflectionProvider(),
				TaskTemplateFactory.getInstance().getDefaultTemplate()));
		
		try {
			return (ComBean) xstream.fromXML(new InputStreamReader(
					input,
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return (ComBean) xstream.fromXML(input);
		}
	}
	
}
