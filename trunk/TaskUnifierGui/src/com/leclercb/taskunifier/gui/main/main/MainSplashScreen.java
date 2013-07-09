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
package com.leclercb.taskunifier.gui.main.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;

import com.leclercb.taskunifier.gui.main.Main;

public final class MainSplashScreen {
	
	private static MainSplashScreen INSTANCE;
	
	public static MainSplashScreen getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MainSplashScreen();
		
		return INSTANCE;
	}
	
	private Graphics2D g2d;
	
	private MainSplashScreen() {
		
	}
	
	public void show() {
		try {
			if (SplashScreen.getSplashScreen() == null)
				return;
			
			this.g2d = SplashScreen.getSplashScreen().createGraphics();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void update(String message) {
		try {
			if (this.g2d == null)
				return;
			
			Font font = this.g2d.getFont();
			FontMetrics metrics;
			int stringWidth;
			
			this.g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			if (Main.isProVersion()) {
				String proMsg = "PRO";
				
				this.g2d.setColor(new Color(234, 255, 234));
				this.g2d.fillRect(180, 85, 250, 40);
				
				this.g2d.setFont(font);
				this.g2d.setFont(this.g2d.getFont().deriveFont(Font.BOLD).deriveFont(
						(float) 16.0));
				metrics = this.g2d.getFontMetrics(this.g2d.getFont());
				stringWidth = metrics.stringWidth(proMsg);
				
				this.g2d.setColor(new Color(40, 155, 60));
				this.g2d.drawString(proMsg, 420 - stringWidth, 105);
			}
			
			this.g2d.setColor(new Color(234, 255, 234));
			this.g2d.fillRect(140, 130, 290, 40);
			
			this.g2d.setFont(font);
			this.g2d.setFont(this.g2d.getFont().deriveFont(Font.BOLD));
			metrics = this.g2d.getFontMetrics(this.g2d.getFont());
			stringWidth = metrics.stringWidth(message);
			
			this.g2d.setColor(Color.GRAY);
			this.g2d.drawString(message, 420 - stringWidth, 160);
			
			this.g2d.setFont(font);
			
			SplashScreen.getSplashScreen().update();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (SplashScreen.getSplashScreen() == null)
				return;
			
			this.g2d = null;
			SplashScreen.getSplashScreen().close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
