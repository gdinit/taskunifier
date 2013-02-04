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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.leclercb.commons.api.license.License;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCloseButton;
import com.leclercb.taskunifier.gui.translations.Translations;

public class LicenseDialog extends JDialog {
	
	private static LicenseDialog INSTANCE;
	
	public static LicenseDialog getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LicenseDialog();
			INSTANCE.setLicense(LicenseUtils.loadLicense());
		}
		
		return INSTANCE;
	}
	
	private LicensePanel licensePanel;
	
	private LicenseDialog() {
		this.initialize();
	}
	
	public void setLicense(License license) {
		this.licensePanel.setLicense(license);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			this.setLocationRelativeTo(FrameUtils.getCurrentFrame());
		}
		
		super.setVisible(visible);
	}
	
	private void initialize() {
		this.setModal(true);
		this.setTitle(Translations.getString("general.license"));
		this.setSize(520, 280);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.add(panel, BorderLayout.CENTER);
		
		this.licensePanel = new LicensePanel();
		panel.add(this.licensePanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel(panel);
	}
	
	private void initializeButtonsPanel(JPanel panel) {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LicenseDialog.this.setVisible(false);
			}
			
		};
		
		JButton closeButton = new TUCloseButton(listener);
		JPanel buttonsPanel = new TUButtonsPanel(closeButton);
		
		panel.add(buttonsPanel, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(closeButton);
	}
	
}
