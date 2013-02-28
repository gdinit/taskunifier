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
package com.leclercb.taskunifier.gui.processes.license;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.HttpUtils;

public class ProcessGetTrial implements Process<String> {
	
	private String firstName;
	private String lastName;
	private String email;
	
	public ProcessGetTrial(String firstName, String lastName, String email) {
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setEmail(email);
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String firstName) {
		CheckUtils.isNotNull(firstName);
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String lastName) {
		CheckUtils.isNotNull(lastName);
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		CheckUtils.isNotNull(email);
		this.email = email;
	}
	
	@Override
	public String execute(final Worker<?> worker) throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		monitor.addMessage(new DefaultProgressMessage(
				Translations.getString("license.get_trial")));
		
		try {
			HttpResponse response = worker.executeInterruptibleAction(
					new Callable<HttpResponse>() {
						
						@Override
						public HttpResponse call() throws Exception {
							List<NameValuePair> parameters = new ArrayList<NameValuePair>();
							
							parameters.add(new BasicNameValuePair(
									"item",
									Constants.ITEM_TRIAL_ID + ""));
							parameters.add(new BasicNameValuePair(
									"first_name",
									ProcessGetTrial.this.firstName));
							parameters.add(new BasicNameValuePair(
									"last_name",
									ProcessGetTrial.this.lastName));
							parameters.add(new BasicNameValuePair(
									"email",
									ProcessGetTrial.this.email));
							parameters.add(new BasicNameValuePair(
									"user_id",
									Main.getCurrentUserId()));
							
							return HttpUtils.getHttpPostResponse(new URI(
									Constants.GET_TRIAL_URL), parameters);
						}
						
					},
					20000);
			
			if (worker.isCancelled())
				return null;
			
			if (!response.isSuccessfull())
				throw new Exception();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response.getContent());
			
			String code = node.get("code").asText();
			String message = node.get("message").asText();
			String license = null;
			
			if (EqualsUtils.equals(code, "0")) {
				license = node.get("data").get("license").asText();
			}
			
			this.showResult(code, message);
			
			return license;
		} catch (Exception e) {
			this.showResult(
					"999",
					"An error occured while generating the trial license key.");
			
			return null;
		}
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
	private void showResult(final String code, final String message) {
		TUSwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				if (EqualsUtils.equals(code, "0")) {
					JOptionPane.showMessageDialog(
							FrameUtils.getCurrentWindow(),
							message,
							Translations.getString("general.information"),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					ErrorInfo info = new ErrorInfo(
							Translations.getString("general.error"),
							message,
							null,
							"GUI",
							null,
							Level.INFO,
							null);
					
					JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
				}
			}
			
		});
	}
	
}
