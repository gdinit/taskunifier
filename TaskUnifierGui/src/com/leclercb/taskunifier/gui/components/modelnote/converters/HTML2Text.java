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
package com.leclercb.taskunifier.gui.components.modelnote.converters;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import com.leclercb.commons.gui.logger.GuiLogger;

public class HTML2Text extends HTMLEditorKit.ParserCallback {
	
	private static List<HTML.Tag> DEFAULT_TAGS;
	
	static {
		DEFAULT_TAGS = new ArrayList<HTML.Tag>();
		
		DEFAULT_TAGS.add(HTML.Tag.B);
		DEFAULT_TAGS.add(HTML.Tag.I);
		DEFAULT_TAGS.add(HTML.Tag.A);
		DEFAULT_TAGS.add(HTML.Tag.UL);
		DEFAULT_TAGS.add(HTML.Tag.OL);
		DEFAULT_TAGS.add(HTML.Tag.LI);
	}
	
	private List<HTML.Tag> keepTags;
	private StringBuffer stringBuffer;
	private String startTagSeparator;
	private String endTagSeparator;
	
	private HTML2Text() {
		this(DEFAULT_TAGS, null, null);
	}
	
	private HTML2Text(
			List<HTML.Tag> keepTags,
			String startTagSeparator,
			String endTagSeparator) {
		if (keepTags == null)
			this.keepTags = new ArrayList<HTML.Tag>();
		else
			this.keepTags = new ArrayList<HTML.Tag>(keepTags);
		
		this.stringBuffer = new StringBuffer();
		this.startTagSeparator = startTagSeparator;
		this.endTagSeparator = endTagSeparator;
	}
	
	public void parse(Reader in) throws IOException {
		ParserDelegator delegator = new ParserDelegator();
		delegator.parse(in, this, Boolean.TRUE);
	}
	
	@Override
	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (this.keepTags.contains(t)) {
			if (t.equals(HTML.Tag.A)) {
				Object link = a.getAttribute(HTML.Attribute.HREF);
				
				if (link != null)
					this.stringBuffer.append("<"
							+ t
							+ " href=\""
							+ link
							+ "\">");
				else
					this.stringBuffer.append("<" + t + ">");
			} else {
				this.stringBuffer.append("<" + t + ">");
			}
		} else {
			if (this.startTagSeparator != null)
				this.stringBuffer.append(this.startTagSeparator);
		}
		
		if (t.equals(HTML.Tag.P))
			this.stringBuffer.append("\n");
	}
	
	@Override
	public void handleEndTag(HTML.Tag t, int pos) {
		if (this.keepTags.contains(t)) {
			this.stringBuffer.append("</" + t + ">");
		} else {
			if (this.endTagSeparator != null)
				this.stringBuffer.append(this.endTagSeparator);
		}
	}
	
	@Override
	public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (t.equals(HTML.Tag.BR))
			this.stringBuffer.append("\n");
	}
	
	@Override
	public void handleText(char[] text, int pos) {
		this.stringBuffer.append(text);
	}
	
	@Override
	public void handleEndOfLineString(String eol) {
		this.stringBuffer.append("\n");
	}
	
	public String getText() {
		return this.stringBuffer.toString();
	}
	
	public static String convertToBasicHtml(String html) {
		return convert(html, DEFAULT_TAGS, null, null);
	}
	
	public static String convertToPlainText(String html) {
		return convert(html, null, null, "\n");
	}
	
	public static String convert(
			String html,
			List<HTML.Tag> keepTags,
			String startTagSeparator,
			String endTagSeparator) {
		HTML2Text parser = new HTML2Text(
				keepTags,
				startTagSeparator,
				endTagSeparator);
		Reader in = new StringReader(html);
		
		try {
			parser.parse(in);
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Error while parsing html to text",
					e);
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
				
			}
		}
		
		return parser.getText().trim();
	}
	
}
