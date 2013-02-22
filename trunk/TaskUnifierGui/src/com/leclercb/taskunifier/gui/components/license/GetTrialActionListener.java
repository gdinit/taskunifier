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
package com.leclercb.taskunifier.gui.components.license;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.swing.TUWorker;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.HttpUtils;

public class GetTrialActionListener implements ActionListener {
	
	private String firstName;
	private String lastName;
	private String email;
	
	private String license;
	
	public GetTrialActionListener() {
		
	}
	
	public void setInfo(String firstName, String lastName, String email) {
		CheckUtils.isNotNull(firstName);
		CheckUtils.isNotNull(lastName);
		CheckUtils.isNotNull(email);
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public String getLicense() {
		return this.license;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		TUWorkerDialog<String> dialog = new TUWorkerDialog<String>(
				Translations.getString("license.get_trial"));
		
		ProgressMonitor monitor = new ProgressMonitor();
		monitor.addListChangeListener(dialog);
		
		dialog.setWorker(new TUWorker<String>(monitor) {
			
			@Override
			protected String longTask() throws Exception {
				this.publish(new DefaultProgressMessage(
						Translations.getString("license.get_trial")));
				
				try {
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					
					parameters.add(new BasicNameValuePair(
							"item",
							Constants.ITEM_TRIAL_ID + ""));
					parameters.add(new BasicNameValuePair(
							"first_name",
							GetTrialActionListener.this.firstName));
					parameters.add(new BasicNameValuePair(
							"last_name",
							GetTrialActionListener.this.lastName));
					parameters.add(new BasicNameValuePair(
							"email",
							GetTrialActionListener.this.email));
					
					HttpResponse response = HttpUtils.getHttpPostResponse(
							new URI(Constants.GET_TRIAL_URL),
							parameters);
					
					System.out.println(response.getContent());
					
					if (!response.isSuccessfull())
						throw new Exception();
					
					ObjectMapper mapper = new ObjectMapper();
					JsonNode node = mapper.readTree(response.getContent());
					
					String code = node.get("code").asText();
					String message = node.get("message").asText();
					String license = null;
					
					GetTrialActionListener.this.showResult(code, message);
					
					if (EqualsUtils.equals(code, "0")) {
						license = node.get("data").get("license").asText();
					}
					
					return license;
				} catch (Throwable t) {
					t.printStackTrace();
					GetTrialActionListener.this.showResult(
							"999",
							"An error occured while generating the trial license key.");
					
					return null;
				}
			}
			
		});
		
		dialog.setVisible(true);
		
		this.license = dialog.getResult();
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
