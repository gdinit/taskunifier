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
package com.leclercb.taskunifier.gui.swing.buttons;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.leclercb.commons.gui.swing.layouts.WrapLayout;

public class TUButtonsPanel extends JPanel {
	
	private boolean removeText;
	
	private JPanel leftPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	
	public TUButtonsPanel(JButton... buttons) {
		this(false, buttons);
	}
	
	public TUButtonsPanel(boolean removeText, JButton... buttons) {
		this(removeText, true, buttons);
	}
	
	public TUButtonsPanel(boolean removeText, boolean wrap, JButton... buttons) {
		this.removeText = removeText;
		
		this.initialize(wrap, buttons);
	}
	
	private void initialize(boolean wrap, JButton... buttons) {
		this.setLayout(new BorderLayout(10, 0));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.leftPanel = new JPanel();
		this.leftPanel.setOpaque(false);
		
		if (wrap)
			this.leftPanel.setLayout(new WrapLayout(FlowLayout.LEFT));
		else
			this.leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(this.leftPanel, BorderLayout.WEST);
		
		this.centerPanel = new JPanel();
		this.centerPanel.setOpaque(false);
		
		if (wrap)
			this.centerPanel.setLayout(new WrapLayout(FlowLayout.CENTER));
		else
			this.centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		this.add(this.centerPanel, BorderLayout.CENTER);
		
		this.rightPanel = new JPanel();
		this.rightPanel.setOpaque(false);
		
		if (wrap)
			this.rightPanel.setLayout(new WrapLayout(FlowLayout.RIGHT));
		else
			this.rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.add(this.rightPanel, BorderLayout.EAST);
		
		if (buttons == null)
			return;
		
		for (JButton button : buttons)
			this.addButton(button);
	}
	
	public void addLeftButton(JButton button) {
		if (button == null)
			return;
		
		if (this.removeText)
			button.setText("");
		
		this.leftPanel.add(button);
	}
	
	public void addCenterButton(JButton button) {
		if (button == null)
			return;
		
		if (this.removeText)
			button.setText("");
		
		this.centerPanel.add(button);
	}
	
	public void addRightButton(JButton button) {
		if (button == null)
			return;
		
		if (this.removeText)
			button.setText("");
		
		this.rightPanel.add(button);
	}
	
	public void addButton(JButton button) {
		this.addRightButton(button);
	}
	
}
