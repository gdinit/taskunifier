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
package com.leclercb.taskunifier.gui.utils;

import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.Main;

public final class FontUtils {
	
	private FontUtils() {
		
	}
	
	private static Map<String, Font> fonts = new HashMap<String, Font>();
	
	private static final String FONTS_FOLDER = Main.getResourcesFolder()
			+ File.separator
			+ "fonts";
	
	public static String getResourceFile(String file) {
		return FONTS_FOLDER + File.separator + file;
	}
	
	public static Font getResourceFont(String file) {
		return getFont(FONTS_FOLDER + File.separator + file);
	}
	
	public static Font getFont(String file) {
		if (fonts.containsKey(file))
			return fonts.get(file);
		
		Font instance;
		
		try {
			instance = Font.createFont(Font.TRUETYPE_FONT, new File(file));
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Cannot load font", e);
			
			return null;
		}
		
		fonts.put(file, instance);
		
		return instance;
	}
	
}
