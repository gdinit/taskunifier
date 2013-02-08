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
package com.leclercb.taskunifier.gui.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.logging.Level;

import javax.swing.JDialog;

import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.commons.gui.utils.ScreenUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;

public class TUDialog extends JDialog implements SavePropertiesListener {
	
	private String windowProperty;
	
	private TUDialogPanel dialogPanel;
	
	public TUDialog() {
		this((Window) null);
	}
	
	public TUDialog(Window owner) {
		super(owner);
		
		this.windowProperty = null;
		
		this.initialize();
	}
	
	public TUDialog(TUDialogPanel dialogPanel) {
		this(FrameUtils.getCurrentWindow());
		
		CheckUtils.isNotNull(dialogPanel);
		this.dialogPanel = dialogPanel;
		
		this.initializeDialogPanel();
	}
	
	private void initialize() {
		Main.getSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getSettings(), this));
		
		this.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowGainedFocus(WindowEvent event) {
				FrameUtils.setCurrentWindow(TUDialog.this);
			}
			
			@Override
			public void windowLostFocus(WindowEvent event) {
				
			}
			
		});
	}
	
	private void initializeDialogPanel() {
		this.setLayout(new BorderLayout());
		
		this.add(this.dialogPanel, BorderLayout.CENTER);
		this.dialogPanel.setDialog(this);
		
		if (this.dialogPanel.getButtons() != null
				&& this.dialogPanel.getButtons().length != 0) {
			TUButtonsPanel buttonsPanel = new TUButtonsPanel(
					this.dialogPanel.getButtons());
			this.add(buttonsPanel, BorderLayout.SOUTH);
		}
		
		if (this.dialogPanel.getDefaultButton() != null) {
			this.getRootPane().setDefaultButton(
					this.dialogPanel.getDefaultButton());
		}
		
		this.pack();
	}
	
	@Override
	public void dispose() {
		if (this.dialogPanel != null)
			this.dialogPanel.setDialog(null);
		
		super.dispose();
	}
	
	public void loadWindowSettings(final String windowProperty) {
		try {
			CheckUtils.isNotNull(windowProperty);
			this.windowProperty = windowProperty;
			
			int width = Main.getSettings().getIntegerProperty(
					this.windowProperty + ".width");
			int height = Main.getSettings().getIntegerProperty(
					this.windowProperty + ".height");
			int locationX = Main.getSettings().getIntegerProperty(
					this.windowProperty + ".location_x");
			int locationY = Main.getSettings().getIntegerProperty(
					this.windowProperty + ".location_y");
			
			this.setSize(width, height);
			
			if (ScreenUtils.isLocationInScreen(new Point(locationX, locationY)))
				this.setLocation(locationX, locationY);
			else if (this.getOwner() != null)
				this.setLocationRelativeTo(this.getOwner());
			else
				this.setLocation(0, 0);
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Cannot load window settings",
					e);
		}
	}
	
	@Override
	public void saveProperties() {
		try {
			if (this.windowProperty == null)
				return;
			
			Main.getSettings().setIntegerProperty(
					this.windowProperty + ".width",
					TUDialog.this.getWidth());
			Main.getSettings().setIntegerProperty(
					this.windowProperty + ".height",
					TUDialog.this.getHeight());
			Main.getSettings().setIntegerProperty(
					this.windowProperty + ".location_x",
					TUDialog.this.getX());
			Main.getSettings().setIntegerProperty(
					this.windowProperty + ".location_y",
					TUDialog.this.getY());
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"Cannot save dialog settings",
					e);
		}
	}
	
}
