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
package com.leclercb.taskunifier.gui.components.timevalueedit;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.utils.TimeValue;

public class EditTimeValueDialogPanel extends TUDialogPanel {
	
	private static EditTimeValueDialogPanel INSTANCE;
	
	protected static EditTimeValueDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EditTimeValueDialogPanel();
		
		return INSTANCE;
	}
	
	private EditTimeValuePanel editTimeValuePanel;
	
	private EditTimeValueDialogPanel() {
		this.initialize();
	}
	
	public TimeValue getTimeValue() {
		return this.editTimeValuePanel.getTimeValue();
	}
	
	public void setTimeValue(TimeValue timeValue) {
		this.editTimeValuePanel.setTimeValue(timeValue);
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.editTimeValuePanel = new EditTimeValuePanel();
		this.editTimeValuePanel.setBorder(BorderFactory.createEmptyBorder(
				10,
				10,
				0,
				10));
		
		this.add(this.editTimeValuePanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("OK")) {
					EditTimeValueDialogPanel.this.editTimeValuePanel.editTimeValue();
				}
				
				EditTimeValueDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		JButton okButton = new TUOkButton(listener);
		JButton cancelButton = new TUCancelButton(listener);
		
		this.setButtons(okButton, okButton, cancelButton);
	}
	
}
