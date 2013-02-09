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
package com.leclercb.commons.gui.swing.lookandfeel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

import com.leclercb.commons.api.utils.CheckUtils;

public class LookAndFeelDecorator extends LookAndFeel {
	
	private static boolean ERROR_FEEDBACK_ENABLED = true;
	
	public static boolean isErrorFeedbackEnabled() {
		return ERROR_FEEDBACK_ENABLED;
	}
	
	public static void setErrorFeedbackEnabled(boolean enabled) {
		ERROR_FEEDBACK_ENABLED = enabled;
	}
	
	private LookAndFeel laf;
	
	public LookAndFeelDecorator(LookAndFeel laf) {
		CheckUtils.isNotNull(laf);
		
		this.laf = laf;
	}
	
	@Override
	public UIDefaults getDefaults() {
		return this.laf.getDefaults();
	}
	
	@Override
	public String getDescription() {
		return this.laf.getDescription();
	}
	
	@Override
	public Icon getDisabledIcon(JComponent component, Icon icon) {
		return this.laf.getDisabledIcon(component, icon);
	}
	
	@Override
	public Icon getDisabledSelectedIcon(JComponent component, Icon icon) {
		return this.laf.getDisabledSelectedIcon(component, icon);
	}
	
	@Override
	public String getID() {
		return this.laf.getID();
	}
	
	@Override
	public LayoutStyle getLayoutStyle() {
		return this.laf.getLayoutStyle();
	}
	
	@Override
	public String getName() {
		return this.laf.getName();
	}
	
	@Override
	public boolean getSupportsWindowDecorations() {
		return this.laf.getSupportsWindowDecorations();
	}
	
	@Override
	public void initialize() {
		this.laf.initialize();
	}
	
	@Override
	public boolean isNativeLookAndFeel() {
		return this.laf.isNativeLookAndFeel();
	}
	
	@Override
	public boolean isSupportedLookAndFeel() {
		return this.laf.isSupportedLookAndFeel();
	}
	
	@Override
	public void provideErrorFeedback(Component component) {
		if (ERROR_FEEDBACK_ENABLED)
			this.laf.provideErrorFeedback(component);
	}
	
	@Override
	public String toString() {
		return this.laf.toString();
	}
	
	@Override
	public void uninitialize() {
		this.laf.uninitialize();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.laf.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.laf.hashCode();
	}
	
}
