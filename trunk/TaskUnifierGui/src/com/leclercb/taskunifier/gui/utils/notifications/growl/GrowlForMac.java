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
package com.leclercb.taskunifier.gui.utils.notifications.growl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.utils.notifications.NotificationList;
import com.leclercb.taskunifier.gui.utils.notifications.Notifier;
import com.leclercb.taskunifier.gui.utils.notifications.exceptions.NotifierException;
import com.leclercb.taskunifier.gui.utils.notifications.exceptions.NotifierOSException;

public class GrowlForMac implements Notifier {
	
	private static final String GROWL_APPLICATION = "com.Growl.GrowlHelperApp";
	
	private ScriptEngine appleScriptEngine;
	
	public GrowlForMac() {
		
	}
	
	@Override
	public String getName() {
		return "Growl for Mac";
	}
	
	@Override
	public void open() throws NotifierException {
		if (!SystemUtils.IS_OS_MAC)
			throw new NotifierOSException();
		
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		this.appleScriptEngine = scriptEngineManager.getEngineByName("AppleScript");
		
		if (this.appleScriptEngine == null) {
			throw new NotifierException("No AppleScriptEngine available");
		}
		
		if (!this.isGrowlEnabled()) {
			throw new NotifierException("No Growl process was found");
		}
		
		this.registerApplication();
	}
	
	private boolean isGrowlEnabled() {
		String script = this.script().add("tell application ").quote(
				"System Events").nextRow(
				"return count of (every process whose bundle identifier is ").quote(
				GROWL_APPLICATION).add(") > 0").nextRow("end tell").get();
		
		long count = this.executeScript(script, 0L);
		return count > 0;
	}
	
	private void registerApplication() throws NotifierException {
		String script = this.script().add("tell application id ").quote(
				GROWL_APPLICATION).nextRow("set the allNotificationsList to ").array(
				NotificationList.getAllNotificationsList()).nextRow(
				"set the enabledNotificationsList to ").array(
				NotificationList.getEnabledNotificationsList()).nextRow(
				"register as application ").quote(Constants.TITLE).add(
				" all notifications allNotificationsList default notifications enabledNotificationsList").nextRow(
				"end tell").get();
		
		this.executeScript(script);
	}
	
	@Override
	public void notify(NotificationList list, String title)
			throws NotifierException {
		this.notify(list, title, null);
	}
	
	@Override
	public void notify(NotificationList list, String title, String description)
			throws NotifierException {
		if (description == null)
			description = "";
		
		String script = this.script().add("tell application id ").quote(
				GROWL_APPLICATION).nextRow("notify with name ").quote(
				list.getName()).add(" title ").quote(title).add(" description ").quote(
				description).add(" application name ").quote(Constants.TITLE).nextRow(
				"end tell").get();
		
		this.executeScript(script);
	}
	
	@Override
	public void close() throws NotifierException {
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T executeScript(String script, T defaultValue) {
		try {
			return (T) this.executeScript(script);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	private Object executeScript(String script) throws NotifierException {
		try {
			return this.appleScriptEngine.eval(
					script,
					this.appleScriptEngine.getContext());
		} catch (Exception e) {
			throw new NotifierException("Cannot execute script", e);
		}
	}
	
	private ScriptBuilder script() {
		return new ScriptBuilder();
	}
	
	private static class ScriptBuilder {
		
		StringBuilder builder = new StringBuilder();
		
		public ScriptBuilder add(String text) {
			this.builder.append(text);
			return this;
		}
		
		public ScriptBuilder quote(String text) {
			this.builder.append("\"");
			this.builder.append(text);
			this.builder.append("\"");
			return this;
		}
		
		public ScriptBuilder nextRow(String text) {
			this.builder.append("\n");
			this.builder.append(text);
			return this;
		}
		
		public String get() {
			return this.builder.toString();
		}
		
		public ScriptBuilder array(String[] array) {
			this.builder.append("{");
			
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					this.builder.append(", ");
				}
				
				this.builder.append("\"");
				this.builder.append(array[i]);
				this.builder.append("\"");
			}
			
			this.builder.append("}");
			return this;
		}
		
	}
	
}
