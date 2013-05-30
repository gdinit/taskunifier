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
package com.leclercb.taskunifier.gui.components.tips;

import java.util.Random;
import java.util.logging.Level;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.translations.Tips;

public class TipsDialog extends JXTipOfTheDay {
	
	private static TipsDialog INSTANCE;
	
	public static TipsDialog getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TipsDialog();
		
		return INSTANCE;
	}
	
	public TipsDialog() {
		super(TipLoader.load(Tips.getProperties()));
	}
	
	public void showTipsDialog(boolean startup) {
		try {
			Random r = new Random();
			int i = r.nextInt(this.getModel().getTipCount());
			this.setCurrentTip(i);
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot set current tip",
					t);
		}
		
		this.showDialog(
				FrameUtils.getCurrentWindow(),
				new TUShowOnStartupChoice(),
				!startup);
	}
	
	private static class TUShowOnStartupChoice implements ShowOnStartupChoice {
		
		@Override
		public void setShowingOnStartup(boolean showOnStartup) {
			Main.getSettings().setBooleanProperty(
					"tips.show_on_startup",
					showOnStartup);
		}
		
		@Override
		public boolean isShowingOnStartup() {
			return Main.getSettings().getBooleanProperty("tips.show_on_startup");
		}
		
	}
	
}
