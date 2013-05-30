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
package com.leclercb.taskunifier.gui.components.statusbar;

import javax.swing.JComponent;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;

public class MacStatusBar extends BottomBar implements StatusBar {
	
	private StatusBarElements elements;
	
	public MacStatusBar(int frameId) {
		super(BottomBarSize.LARGE);
		
		this.elements = new StatusBarElements(frameId);
		
		this.initialize(frameId);
	}
	
	private void initialize(int frameId) {
		MacWidgetFactory.makeEmphasizedLabel(this.elements.getSynchronizerStatusLabel());
		this.addComponentToLeft(this.elements.getSynchronizerStatusLabel());
		
		MacWidgetFactory.makeEmphasizedLabel(this.elements.getScheduledSyncStatusLabel());
		this.addComponentToCenter(this.elements.getScheduledSyncStatusLabel());
		
		MacWidgetFactory.makeEmphasizedLabel(this.elements.getLastSynchronizationDateLabel());
		this.addComponentToCenter(
				this.elements.getLastSynchronizationDateLabel(),
				20);
		
		MacWidgetFactory.makeEmphasizedLabel(this.elements.getRowCountLabel());
		this.addComponentToRight(this.elements.getRowCountLabel());
		
		MacWidgetFactory.makeEmphasizedLabel(this.elements.getCurrentDateTime());
		this.addComponentToRight(this.elements.getCurrentDateTime(), 20);
	}
	
	@Override
	public JComponent getStatusBar() {
		return this.getComponent();
	}
	
}
