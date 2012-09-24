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
package com.leclercb.taskunifier.gui.components.statusbar;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;

public class MacStatusBar extends BottomBar implements StatusBar {
	
	private JLabel synchronizerStatus;
	private JLabel lastSynchronizationDate;
	private JLabel scheduledSyncStatus;
	private JLabel rowCount;
	private JLabel currentDateTime;
	
	public MacStatusBar(int frameId) {
		super(BottomBarSize.LARGE);
		
		this.initialize(frameId);
	}
	
	private void initialize(int frameId) {
		this.synchronizerStatus = StatusBarElements.createSynchronizerStatus();
		MacWidgetFactory.makeEmphasizedLabel(this.synchronizerStatus);
		this.addComponentToLeft(this.synchronizerStatus);
		
		this.scheduledSyncStatus = StatusBarElements.createScheduledSyncStatus();
		MacWidgetFactory.makeEmphasizedLabel(this.scheduledSyncStatus);
		this.addComponentToCenter(this.scheduledSyncStatus);
		
		this.lastSynchronizationDate = StatusBarElements.createLastSynchronizationDate();
		MacWidgetFactory.makeEmphasizedLabel(this.lastSynchronizationDate);
		this.addComponentToCenter(this.lastSynchronizationDate, 20);
		
		this.rowCount = StatusBarElements.createRowCount(frameId);
		MacWidgetFactory.makeEmphasizedLabel(this.rowCount);
		this.addComponentToRight(this.rowCount);
		
		this.currentDateTime = StatusBarElements.createCurrentDateTime();
		MacWidgetFactory.makeEmphasizedLabel(this.currentDateTime);
		this.addComponentToRight(this.currentDateTime, 20);
	}
	
	@Override
	public JComponent getStatusBar() {
		return this.getComponent();
	}
	
}
