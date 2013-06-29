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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.license.License;
import com.leclercb.commons.api.license.LicenseManager;
import com.leclercb.commons.api.license.LicenseType;
import com.leclercb.commons.api.license.exceptions.LicenseExpiredException;
import com.leclercb.commons.api.license.exceptions.LicenseVersionExpiredException;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.commons.values.StringValueLicenseType;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.resources.Resources;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FontUtils;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class LicensePanel extends JPanel {
	
	private License license;
	private boolean expired;
	
	private JPanel licenseInfo;
	private JPanel licenseEnter;
	private JPanel licenseGetTrial;
	
	private TUButtonsPanel licenseInfoButtonsPanel;
	private TUButtonsPanel licenseEnterButtonsPanel;
	private TUButtonsPanel licenseGetTrialButtonsPanel;
	
	private JButton changeLicenseButton;
	
	private JTextArea licenseArea;
	private JButton enterLicenseButton;
	
	private JButton getTrialButton;
	
	public LicensePanel() {
		this.initialize();
	}
	
	public void setLicense(License license, boolean getTrial) {
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
		
		this.licenseArea.setText("");
		this.licenseInfo.revalidate();
		this.getTrialButton.setEnabled(LicenseUtils.isFirstTrialLicense());
		
		if (license == null || !show) {
			if (getTrial && LicenseUtils.isFirstTrialLicense()) {
				CardLayout layout = (CardLayout) this.getLayout();
				layout.show(this, "GET_TRIAL");
			} else {
				CardLayout layout = (CardLayout) this.getLayout();
				layout.show(this, "ENTER");
			}
		} else {
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
	
	public TUButtonsPanel getLicenseGetTrialButtonsPanel() {
		return this.licenseGetTrialButtonsPanel;
	}
	
	private void initialize() {
		this.setLayout(new CardLayout());
		
		this.initializeLicenseInfo();
		this.initializeLicenseEnter();
		this.initializeLicenseGetTrial();
		
		this.setLicense(null, false);
	}
	
	private void initializeLicenseInfo() {
		this.licenseInfo = new JPanel();
		this.licenseInfo.setLayout(new BorderLayout());
		this.licenseInfo.setBackground(Color.WHITE);
		
		LicenseInfoPanel licenseInfoPanel = new LicenseInfoPanel();
		this.licenseInfo.add(licenseInfoPanel, BorderLayout.CENTER);
		
		this.changeLicenseButton = new JButton(
				Translations.getString("license.change_license"));
		this.changeLicenseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) LicensePanel.this.getLayout();
				layout.show(LicensePanel.this, "ENTER");
			}
			
		});
		
		this.licenseInfoButtonsPanel = new TUButtonsPanel(
				this.changeLicenseButton);
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
		
		this.licenseEnter.add(
				new JLabel(Translations.getString("license.enter_license")
						+ ": "),
				BorderLayout.NORTH);
		
		this.licenseArea = new JTextArea();
		
		this.licenseEnter.add(
				ComponentFactory.createJScrollPane(this.licenseArea, true),
				BorderLayout.CENTER);
		
		this.enterLicenseButton = new JButton(
				Translations.getString("license.enter_license"));
		this.enterLicenseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				LicensePanel.this.saveLicense(LicensePanel.this.licenseArea.getText());
			}
			
		});
		
		this.getTrialButton = new JButton(
				Translations.getString("license.get_trial"));
		this.getTrialButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				CardLayout layout = (CardLayout) LicensePanel.this.getLayout();
				layout.show(LicensePanel.this, "GET_TRIAL");
			}
			
		});
		
		this.licenseEnterButtonsPanel = new TUButtonsPanel(
				this.enterLicenseButton,
				this.getTrialButton);
		this.licenseEnter.add(this.licenseEnterButtonsPanel, BorderLayout.SOUTH);
		
		this.add(this.licenseEnter, "ENTER");
	}
	
	public void initializeLicenseGetTrial() {
		this.licenseGetTrial = new JPanel();
		this.licenseGetTrial.setLayout(new BorderLayout(5, 5));
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.get_trial"));
		header.setDescription(Translations.getString("header.description.get_trial"));
		header.setIcon(ImageUtils.getResourceImage("key.png", 32, 32));
		
		this.licenseGetTrial.add(header, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		this.licenseGetTrial.add(panel, BorderLayout.CENTER);
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		final JTextField firstName = new JTextField();
		final JTextField lastName = new JTextField();
		final JTextField email = new JTextField();
		
		builder.appendI15d("license.get_trial.first_name", true, firstName);
		builder.appendI15d("license.get_trial.last_name", true, lastName);
		builder.appendI15d("license.get_trial.email", true, email);
		
		JButton getTrialButton = new JButton(
				Translations.getString("license.get_trial"));
		getTrialButton.addActionListener(new ActionListener() {
			
			GetTrialActionListener listener = new GetTrialActionListener();
			
			@Override
			public void actionPerformed(ActionEvent event) {
				this.listener.setInfo(
						firstName.getText(),
						lastName.getText(),
						email.getText());
				this.listener.actionPerformed(event);
				
				if (this.listener.getLicense() != null) {
					firstName.setText(null);
					lastName.setText(null);
					email.setText(null);
					
					LicensePanel.this.saveLicense(this.listener.getLicense());
				}
			}
			
		});
		
		panel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.licenseGetTrialButtonsPanel = new TUButtonsPanel(getTrialButton);
		panel.add(this.licenseGetTrialButtonsPanel, BorderLayout.SOUTH);
		
		this.add(this.licenseGetTrial, "GET_TRIAL");
	}
	
	private void saveLicense(String license) {
		Exception exception = null;
		
		try {
			InputStream publicKey = Resources.class.getResourceAsStream("public_key");
			LicenseManager lm = new LicenseManager(publicKey, null);
			License l = lm.readLicense(license);
			
			if (l != null) {
				if (l.getLicenseType() == LicenseType.TRIAL) {
					if (!LicenseUtils.isFirstTrialLicense()) {
						ErrorInfo info = new ErrorInfo(
								Translations.getString("general.error"),
								Translations.getString("license.error.one_trial_maximum"),
								null,
								"GUI",
								null,
								Level.INFO,
								null);
						
						JXErrorPane.showDialog(
								FrameUtils.getCurrentWindow(),
								info);
						
						return;
					}
				}
				
				LicenseUtils.saveLicense(license);
				this.setLicense(l, false);
			} else {
				GuiLogger.getLogger().log(
						Level.WARNING,
						"Incorrect license: " + license);
			}
		} catch (Exception e) {
			exception = e;
			
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Incorrect license: " + license,
					e);
		}
		
		ErrorInfo info = new ErrorInfo(
				Translations.getString("general.error"),
				Translations.getString("license.error.invalid_license"),
				null,
				"GUI",
				exception,
				Level.WARNING,
				null);
		
		JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
	}
	
	private class LicenseInfoPanel extends JPanel {
		
		public LicenseInfoPanel() {
			ImageIcon icon = ImageUtils.getResourceImage("license.png");
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
			
			ImageIcon icon = ImageUtils.getResourceImage("license.png");
			
			g2.drawImage(icon.getImage(), 0, 0, null);
			
			if (LicensePanel.this.license != null) {
				Font font = FontUtils.getResourceFont("constantia.ttf");
				int width = 0;
				
				g2.setFont(font.deriveFont((float) 12.0).deriveFont(Font.PLAIN));
				
				String licensedTo = Translations.getString("license.licensed_to");
				
				width = g.getFontMetrics().stringWidth(licensedTo);
				g2.drawString(licensedTo, (this.getWidth() - width) / 2, 120);
				
				g2.setFont(font.deriveFont((float) 16.0).deriveFont(Font.BOLD));
				
				String name = LicensePanel.this.license.getFirstName()
						+ " "
						+ LicensePanel.this.license.getLastName();
				
				width = g.getFontMetrics().stringWidth(name);
				g2.drawString(name, (this.getWidth() - width) / 2, 140);
				
				g2.setFont(font.deriveFont((float) 14.0).deriveFont(Font.PLAIN));
				
				g2.drawString(
						Translations.getString("license.email") + ": ",
						90,
						185);
				g2.drawString(LicensePanel.this.license.getEmail(), 220, 185);
				g2.drawString(Translations.getString("license.purchase_date")
						+ ": ", 90, 210);
				g2.drawString(
						StringValueCalendar.INSTANCE_DATE.getString(LicensePanel.this.license.getPurchaseDate()),
						220,
						210);
				g2.drawString(Translations.getString("license.license_type")
						+ ": ", 90, 235);
				g2.drawString(
						StringValueLicenseType.INSTANCE.getString(LicensePanel.this.license.getLicenseType()),
						220,
						235);
				g2.drawString(
						Translations.getString("license.version") + ": ",
						90,
						260);
				g2.drawString(LicensePanel.this.license.getVersion(), 220, 260);
				
				g2.drawString(Translations.getString("license.expiration")
						+ ": ", 90, 285);
				if (LicensePanel.this.license.getExpiration() == null) {
					g2.drawString(
							Translations.getString("date.never"),
							220,
							285);
				} else {
					g2.drawString(
							StringValueCalendar.INSTANCE_DATE.getString(LicensePanel.this.license.getExpiration()),
							220,
							285);
				}
				
				if (LicensePanel.this.expired) {
					ImageIcon expiredIcon = ImageUtils.getResourceImage("expired.png");
					
					g2.drawImage(
							expiredIcon.getImage(),
							(this.getWidth() - expiredIcon.getIconWidth()) / 2,
							(this.getHeight() - expiredIcon.getIconHeight()) / 2,
							null);
				} else if (LicensePanel.this.license.getLicenseType() == LicenseType.TRIAL) {
					ImageIcon trialIcon = ImageUtils.getResourceImage(
							"trial.png",
							100,
							100);
					
					g2.drawImage(trialIcon.getImage(), 50, 55, null);
				}
			}
		}
		
	}
	
}
