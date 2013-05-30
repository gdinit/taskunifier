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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text2HTML {
	
	public static String convert(String text) {
		if (text == null || text.trim().length() == 0)
			return "<p style=\"margin-top: 0\"></p>";
		
		text = text.trim();
		
		text = convertTags(text);
		text = convertNlToBr(text);
		text = convertToHtmlUrl(text);
		
		return "<p style=\"margin-top: 0\">" + text + "</p>";
	}
	
	private static String convertTags(String text) {
		StringBuffer buffer = new StringBuffer(text);
		
		int position = 0;
		
		while (true) {
			int index = buffer.indexOf("<", position);
			
			if (index == -1)
				break;
			
			String substring = buffer.substring(index);
			
			// Tags: <b>, <i>, <a>, <ul>, <ol>, <li>
			if (!(substring.startsWith("<b>")
					|| substring.startsWith("<i>")
					|| substring.startsWith("<a ")
					|| substring.startsWith("<ul>")
					|| substring.startsWith("<ol>")
					|| substring.startsWith("<li>")
					|| substring.startsWith("</b>")
					|| substring.startsWith("</i>")
					|| substring.startsWith("</a>")
					|| substring.startsWith("</ul>")
					|| substring.startsWith("</ol>") || substring.startsWith("</li>")))
				buffer.replace(index, index + 1, "&lt;");
			
			position = index + 1;
		}
		
		return buffer.toString();
	}
	
	private static String convertToHtmlUrl(String text) {
		StringBuffer buffer = new StringBuffer(text);
		
		Pattern p = Pattern.compile("(href=['\"]{1})?((https?|ftp|file):((//)|(\\\\))+[\\w\\d:#@%/;$~_?\\+\\-=\\\\.&]*)");
		Matcher m = null;
		int position = 0;
		
		while (true) {
			m = p.matcher(buffer.toString());
			
			if (!m.find(position))
				break;
			
			position = m.end();
			String firstGroup = m.group(1);
			
			if (firstGroup == null)
				firstGroup = "";
			
			if (firstGroup.contains("href"))
				continue;
			
			String url = firstGroup
					+ "<a href=\""
					+ m.group(2)
					+ "\">"
					+ m.group(2)
					+ "</a>";
			
			buffer.replace(m.start(), m.end(), url);
			
			position = m.start() + url.length() - 1;
		}
		
		return buffer.toString();
	}
	
	private static String convertNlToBr(String text) {
		StringBuffer buffer = new StringBuffer();
		
		text = text.replace("\n", "\n ");
		String[] lines = text.split("\n");
		String[] prefixes = new String[] {
				"<ul",
				"<ol",
				"<li",
				"</ul",
				"</ol",
				"</li" };
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			
			line = line.trim();
			buffer.append(line);
			if (startsWith(line, prefixes))
				if (i + 1 < lines.length
						&& startsWith(lines[i + 1].trim(), prefixes))
					continue;
			
			buffer.append("<br />");
		}
		
		return buffer.toString();
	}
	
	private static boolean startsWith(String str, String... prefix) {
		for (String p : prefix) {
			if (str.startsWith(p))
				return true;
		}
		
		return false;
	}
	
}
