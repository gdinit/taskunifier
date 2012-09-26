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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.leclercb.taskunifier.gui.main.frames.ShortcutKey;

public class TUShortcutField extends JTextField {
	
	public static final String PROP_SHORTCUT_KEY = "shortcutKey";
	
	private ShortcutKey shortcutKey;
	
	public TUShortcutField() {
		this.shortcutKey = null;
		
		this.initialize();
	}
	
	private void initialize() {
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent event) {}
			
			@Override
			public void keyReleased(KeyEvent event) {
				if (TUShortcutField.this.shortcutKey == null)
					TUShortcutField.this.setShortcutKey(null);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				ShortcutKey shortcutKey = null;
				
				if (event.getModifiers() != 0
						&& event.getKeyCode() != KeyEvent.VK_ALT
						&& event.getKeyCode() != KeyEvent.VK_ALT_GRAPH
						&& event.getKeyCode() != KeyEvent.VK_CONTROL
						&& event.getKeyCode() != KeyEvent.VK_SHIFT)
					shortcutKey = new ShortcutKey(
							event.getKeyCode(),
							event.getModifiers());
				
				TUShortcutField.this.setShortcutKey(shortcutKey);
			}
			
		});
	}
	
	public ShortcutKey getShortcutKey() {
		return this.shortcutKey;
	}
	
	public void setShortcutKey(ShortcutKey shortcutKey) {
		ShortcutKey oldShortcutKey = this.shortcutKey;
		this.shortcutKey = shortcutKey;
		
		if (shortcutKey == null)
			this.setText(null);
		else
			this.setText(KeyEvent.getKeyModifiersText(shortcutKey.getModifiers())
					+ " + "
					+ KeyEvent.getKeyText(shortcutKey.getKeyCode()));
		
		this.firePropertyChange(PROP_SHORTCUT_KEY, oldShortcutKey, shortcutKey);
	}
	
}
