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
package com.leclercb.taskunifier.gui.components.welcome.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.taskunifier.gui.components.license.LicenseUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class LicensePanel extends CardPanel {
	
	public LicensePanel() {
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout(20, 20));
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("general.license"));
		header.setIcon(ImageUtils.getResourceImage("key.png", 32, 32));
		
		this.add(header, BorderLayout.NORTH);
		
		com.leclercb.taskunifier.gui.components.license.LicensePanel licensePanel = new com.leclercb.taskunifier.gui.components.license.LicensePanel();
		licensePanel.setLicense(LicenseUtils.loadLicense());
		licensePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.add(licensePanel, BorderLayout.CENTER);
	}
	
	@Override
	public void saveAndApplyConfig() {
		
	}
	
	@Override
	public void cancelConfig() {
		
	}
	
}