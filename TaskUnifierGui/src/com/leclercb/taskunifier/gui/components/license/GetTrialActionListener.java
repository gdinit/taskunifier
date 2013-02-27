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

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.processes.license.ProcessGetTrial;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;

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
		
		ProcessGetTrial process = new ProcessGetTrial(
				this.firstName,
				this.lastName,
				this.email);
		
		dialog.setWorker(new Worker<String>(process));
		
		dialog.setVisible(true);
		
		this.license = dialog.getResult();
	}
	
}
