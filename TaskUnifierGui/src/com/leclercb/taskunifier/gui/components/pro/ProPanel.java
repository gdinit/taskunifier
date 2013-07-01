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
package com.leclercb.taskunifier.gui.components.pro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXLabel;

import com.leclercb.commons.api.event.action.ActionSupport;
import com.leclercb.commons.api.event.action.ActionSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.actions.ActionManageLicense;
import com.leclercb.taskunifier.gui.components.license.LicenseDialog;
import com.leclercb.taskunifier.gui.components.license.LicenseUtils;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;

public class ProPanel extends JPanel implements ActionSupported, PropertyChangeListener {
	
	public static final String ACTION_MORE_INFO = "MORE_INFO";
	public static final String ACTION_GET_TRIAL = "GET_TRIAL";
	public static final String ACTION_BUY_LICENSE = "BUY_LICENSE";
	public static final String ACTION_ENTER_LICENSE = "ENTER_LICENSE";
	
	private ActionSupport actionSupport;
	
	private boolean feature;
	
	private TUButtonsPanel buttonsPanel;
	
	private JButton moreInfoButton;
	private JButton getTrialButton;
	private JButton buyLicenseButton;
	private JButton enterLicenseButton;
	
	public ProPanel(boolean feature) {
		this.actionSupport = new ActionSupport(this);
		
		this.feature = feature;
		
		this.initialize();
	}
	
	public TUButtonsPanel getButtonsPanel() {
		return this.buttonsPanel;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		this.add(panel, BorderLayout.CENTER);
		
		JPanel insidePanel = new JPanel();
		insidePanel.setLayout(new BorderLayout());
		insidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		panel.add(insidePanel, BorderLayout.CENTER);
		
		String message = Translations.getString("license.feature_pro_version_required");
		
		if (!this.feature)
			message = Translations.getString("license.pro_version_required");
		
		JXLabel label = new JXLabel(message);
		label.setLineWrap(true);
		
		insidePanel.add(label, BorderLayout.CENTER);
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (EqualsUtils.equals("MORE_INFO", event.getActionCommand()))
					ProPanel.this.moreInfo();
				
				if (EqualsUtils.equals("GET_TRIAL", event.getActionCommand()))
					ProPanel.this.getTrial();
				
				if (EqualsUtils.equals("BUY_LICENSE", event.getActionCommand()))
					ProPanel.this.buyLicense();
				
				if (EqualsUtils.equals(
						"ENTER_LICENSE",
						event.getActionCommand()))
					ProPanel.this.enterLicense();
			}
			
		};
		
		this.moreInfoButton = new JButton(
				Translations.getString("license.more_info"));
		this.moreInfoButton.setActionCommand("MORE_INFO");
		this.moreInfoButton.addActionListener(listener);
		
		this.getTrialButton = new JButton(
				Translations.getString("license.get_trial"));
		this.getTrialButton.setActionCommand("GET_TRIAL");
		this.getTrialButton.addActionListener(listener);
		
		this.getTrialButton.setEnabled(LicenseUtils.isFirstTrialLicense());
		
		Main.getSettings().addPropertyChangeListener(
				"license.trial.used",
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		this.buyLicenseButton = new JButton(
				Translations.getString("license.buy_license"));
		this.buyLicenseButton.setActionCommand("BUY_LICENSE");
		this.buyLicenseButton.addActionListener(listener);
		
		this.enterLicenseButton = new JButton(
				Translations.getString("license.enter_license"));
		this.enterLicenseButton.setActionCommand("ENTER_LICENSE");
		this.enterLicenseButton.addActionListener(listener);
		
		this.buttonsPanel = new TUButtonsPanel(
				false,
				false,
				this.moreInfoButton,
				this.getTrialButton,
				this.buyLicenseButton,
				this.enterLicenseButton);
		
		this.add(this.buttonsPanel, BorderLayout.SOUTH);
	}
	
	public void moreInfo() {
		DesktopUtils.browse(Constants.PRO_URL);
		this.actionSupport.fireActionPerformed(0, ACTION_MORE_INFO);
	}
	
	public void getTrial() {
		LicenseDialog dialog = new LicenseDialog();
		dialog.setLicense(null, true);
		dialog.setVisible(true);
		dialog.dispose();
		
		this.actionSupport.fireActionPerformed(0, ACTION_GET_TRIAL);
	}
	
	public void buyLicense() {
		DesktopUtils.browse(Constants.PRO_URL);
		this.actionSupport.fireActionPerformed(0, ACTION_BUY_LICENSE);
	}
	
	public void enterLicense() {
		ActionManageLicense.manageLicense();
		this.actionSupport.fireActionPerformed(0, ACTION_ENTER_LICENSE);
	}
	
	@Override
	public void addActionListener(ActionListener listener) {
		this.actionSupport.addActionListener(listener);
	}
	
	@Override
	public void removeActionListener(ActionListener listener) {
		this.actionSupport.removeActionListener(listener);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.getTrialButton.setEnabled(LicenseUtils.isFirstTrialLicense());
	}
	
}
