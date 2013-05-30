/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
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
package com.leclercb.taskunifier.api.models.templates;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

public class NoteTemplateFactory extends AbstractTemplateFactory<NoteTemplate> {
	
	private static NoteTemplateFactory FACTORY;
	
	public static NoteTemplateFactory getInstance() {
		if (FACTORY == null)
			FACTORY = new NoteTemplateFactory();
		
		return FACTORY;
	}
	
	private NoteTemplateFactory() {
		
	}
	
	public NoteTemplate create() {
		NoteTemplate template = new NoteTemplate();
		this.register(template);
		return template;
	}
	
	@Override
	public NoteTemplate create(String title) {
		NoteTemplate template = new NoteTemplate(title);
		this.register(template);
		return template;
	}
	
	@Override
	public NoteTemplate create(ModelId modelId, String title) {
		NoteTemplate template = new NoteTemplate(modelId, title);
		this.register(template);
		return template;
	}
	
	@Override
	public void decodeFromXML(InputStream input) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new Xpp3DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("templates", NoteTemplate[].class);
		xstream.alias("template", NoteTemplate.class);
		xstream.processAnnotations(NoteTemplate.class);
		
		NoteTemplate[] templates = null;
		
		try {
			templates = (NoteTemplate[]) xstream.fromXML(new InputStreamReader(
					input,
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			templates = (NoteTemplate[]) xstream.fromXML(input);
		}
		
		for (NoteTemplate template : templates) {
			this.register(template);
		}
	}
	
	@Override
	public void encodeToXML(OutputStream output) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new Xpp3DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("templates", NoteTemplate[].class);
		xstream.alias("template", NoteTemplate.class);
		xstream.processAnnotations(NoteTemplate.class);
		
		try {
			xstream.toXML(
					this.getList().toArray(new NoteTemplate[0]),
					new OutputStreamWriter(output, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			xstream.toXML(this.getList().toArray(new NoteTemplate[0]), output);
		}
	}
	
}
