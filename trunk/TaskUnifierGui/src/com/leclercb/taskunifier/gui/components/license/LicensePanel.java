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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.leclercb.commons.api.license.License;
import com.leclercb.commons.api.license.LicenseManager;
import com.leclercb.commons.api.license.exceptions.LicenseExpiredException;
import com.leclercb.commons.api.license.exceptions.LicenseVersionExpiredException;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.resources.Resources;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FontUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class LicensePanel extends JPanel {
	
	private License license;
	private boolean expired;
	
	private JPanel licenseInfo;
	private JPanel licenseEnter;
	
	private TUButtonsPanel licenseInfoButtonsPanel;
	private TUButtonsPanel licenseEnterButtonsPanel;
	
	private JButton changeLicense;
	
	private JTextArea licenseArea;
	private JButton enterLicenseButton;
	
	public LicensePanel() {
		this.initialize();
	}
	
	public void setLicense(License license) {
		this.license = license;
		
		boolean show = true;
		this.expired = false;
		
		try {
			LicenseUtils.checkLicense(license);
		} catch (LicenseExpiredException e) {
			this.expired = true;
		} catch (LicenseVersionExpiredException e) {
			this.expired = true;
		} catch (Exception e) {
			show = false;
		}
		
		if (license == null || !show) {
			this.licenseArea.setText("");
			
			CardLayout layout = (CardLayout) this.getLayout();
			layout.show(this, "ENTER");
		} else {
			this.licenseArea.setText("");
			this.licenseInfo.revalidate();
			
			CardLayout layout = (CardLayout) this.getLayout();
			layout.show(this, "INFO");
		}
	}
	
	public TUButtonsPanel getLicenseInfoButtonsPanel() {
		return this.licenseInfoButtonsPanel;
	}
	
	public TUButtonsPanel getLicenseEnterButtonsPanel() {
		return this.licenseEnterButtonsPanel;
	}
	
	private void initialize() {
		this.setLayout(new CardLayout());
		
		this.initializeLicenseInfo();
		this.initializeLicenseEnter();
		
		this.setLicense(null);
	}
	
	private void initializeLicenseInfo() {
		this.licenseInfo = new JPanel();
		this.licenseInfo.setLayout(new BorderLayout());
		this.licenseInfo.setBackground(Color.WHITE);
		
		LicenseInfoPanel licenseInfoPanel = new LicenseInfoPanel();
		this.licenseInfo.add(licenseInfoPanel, BorderLayout.CENTER);
		
		this.changeLicense = new JButton(
				Translations.getString("license.change_license"));
		this.changeLicense.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) LicensePanel.this.getLayout();
				layout.show(LicensePanel.this, "ENTER");
			}
			
		});
		
		this.licenseInfoButtonsPanel = new TUButtonsPanel(this.changeLicense);
		this.licenseInfoButtonsPanel.setOpaque(false);
		
		this.licenseInfo.add(this.licenseInfoButtonsPanel, BorderLayout.SOUTH);
		
		this.add(this.licenseInfo, "INFO");
	}
	
	private void initializeLicenseEnter() {
		this.licenseEnter = new JPanel();
		this.licenseEnter.setLayout(new BorderLayout(5, 5));
		this.licenseEnter.setBorder(BorderFactory.createEmptyBorder(
				10,
				10,
				0,
				10));
		
		this.licenseArea = new JTextArea();
		
		this.licenseEnter.add(
				ComponentFactory.createJScrollPane(this.licenseArea, true),
				BorderLayout.CENTER);
		
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
		
		this.licenseEnterButtonsPanel = new TUButtonsPanel(
				this.enterLicenseButton);
		this.licenseEnter.add(this.licenseEnterButtonsPanel, BorderLayout.SOUTH);
		
		this.add(this.licenseEnter, "ENTER");
	}
	
	private class LicenseInfoPanel extends JPanel {
		
		public LicenseInfoPanel() {
			ImageIcon icon = ImageUtils.getResourceImage("licence.png");
			this.setPreferredSize(new Dimension(
					icon.getIconWidth(),
					icon.getIconHeight()));
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponents(g);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			ImageIcon icon = ImageUtils.getResourceImage("licence.png");
			
			g2.drawImage(icon.getImage(), 0, 0, null);
			
			if (LicensePanel.this.license != null) {
				Font font = FontUtils.getResourceFont("constantia.ttf");
				g2.setFont(font.deriveFont((float) 20.0).deriveFont(Font.BOLD));
				
				int width = g.getFontMetrics().stringWidth(
						LicensePanel.this.license.getName());
				g2.drawString(
						LicensePanel.this.license.getName(),
						(this.getWidth() - width) / 2,
						135);
				
				g2.setFont(font.deriveFont((float) 14.0).deriveFont(Font.PLAIN));
				
				g2.drawString(LicensePanel.this.license.getEmail(), 200, 187);
				g2.drawString(
						StringValueCalendar.INSTANCE_DATE.getString(LicensePanel.this.license.getPurchaseDate()),
						200,
						211);
				g2.drawString(
						LicensePanel.this.license.getLicenseType().name(),
						200,
						235);
				g2.drawString(LicensePanel.this.license.getVersion(), 200, 256);
				g2.drawString(
						StringValueCalendar.INSTANCE_DATE.getString(LicensePanel.this.license.getExpiration()),
						200,
						284);
			}
		}
		
	}
	
}
