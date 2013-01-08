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
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.license.License;
import com.leclercb.taskunifier.gui.license.LicenseManager;
import com.leclercb.taskunifier.gui.resources.Resources;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FormBuilder;

public class LicensePanel extends JPanel {
	
	private JPanel licenseInfo;
	private JPanel licenseEnter;
	
	private JLabel nameLabel;
	private JLabel emailLabel;
	private JLabel licenseTypeLabel;
	private JLabel expirationLabel;
	private JLabel versionLabel;
	private JLabel referenceLabel;
	
	private JButton changeLicense;
	
	private JTextArea licenseArea;
	private JButton enterLicenseButton;
	
	public LicensePanel() {
		this.initialize();
	}
	
	public void setLicense(License license) {
		if (license == null) {
			this.nameLabel.setText("");
			this.emailLabel.setText("");
			this.licenseTypeLabel.setText("");
			this.expirationLabel.setText("");
			this.versionLabel.setText("");
			this.referenceLabel.setText("");
			
			this.licenseArea.setText("");
			
			CardLayout layout = (CardLayout) this.getLayout();
			layout.show(this, "ENTER");
		} else {
			this.nameLabel.setText(license.getName());
			this.emailLabel.setText(license.getEmail());
			this.licenseTypeLabel.setText(license.getLicenseType().name());
			this.expirationLabel.setText(StringValueCalendar.INSTANCE_DATE.getString(license.getExpiration()));
			this.versionLabel.setText(license.getVersion());
			this.referenceLabel.setText(license.getReference());
			
			this.licenseArea.setText("");
			
			CardLayout layout = (CardLayout) this.getLayout();
			layout.show(this, "INFO");
		}
	}
	
	private void initialize() {
		this.setLayout(new CardLayout());
		
		this.licenseInfo = new JPanel();
		this.licenseInfo.setLayout(new BorderLayout());
		
		this.nameLabel = new JLabel();
		this.emailLabel = new JLabel();
		this.licenseTypeLabel = new JLabel();
		this.expirationLabel = new JLabel();
		this.versionLabel = new JLabel();
		this.referenceLabel = new JLabel();
		
		this.changeLicense = new JButton(
				Translations.getString("license.change_license"));
		this.changeLicense.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) LicensePanel.this.getLayout();
				layout.show(LicensePanel.this, "ENTER");
			}
			
		});
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		builder.appendI15d("license.name", true, this.nameLabel);
		builder.appendI15d("license.email", true, this.emailLabel);
		builder.appendI15d("license.license_type", true, this.licenseTypeLabel);
		builder.appendI15d("license.expiration", true, this.expirationLabel);
		builder.appendI15d("license.version", true, this.versionLabel);
		builder.appendI15d("license.reference", true, this.referenceLabel);
		builder.append("", this.changeLicense);
		
		this.licenseInfo.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.licenseEnter = new JPanel();
		this.licenseEnter.setLayout(new BorderLayout(5, 5));
		
		this.licenseArea = new JTextArea();
		this.enterLicenseButton = new JButton(
				Translations.getString("license.enter_license"));
		this.enterLicenseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					InputStream publicKey = Resources.class.getResourceAsStream("public_key");
					LicenseManager lm = new LicenseManager(publicKey, null);
					License l = lm.readLicense(LicensePanel.this.licenseArea.getText());
					
					if (l != null) {
						LicenseUtils.saveLicense(LicensePanel.this.licenseArea.getText());
						LicensePanel.this.setLicense(l);
					} else {
						GuiLogger.getLogger().log(
								Level.WARNING,
								"Incorrect license: "
										+ LicensePanel.this.licenseArea.getText());
					}
				} catch (Exception e) {
					GuiLogger.getLogger().log(
							Level.WARNING,
							"Incorrect license: "
									+ LicensePanel.this.licenseArea.getText(),
							e);
				}
				
				// TODO: license incorrect message
			}
			
		});
		
		this.licenseEnter.add(
				ComponentFactory.createJScrollPane(this.licenseArea, true),
				BorderLayout.CENTER);
		this.licenseEnter.add(this.enterLicenseButton, BorderLayout.SOUTH);
		
		this.add(this.licenseInfo, "INFO");
		this.add(this.licenseEnter, "ENTER");
		
		this.setLicense(null);
	}
	
}
