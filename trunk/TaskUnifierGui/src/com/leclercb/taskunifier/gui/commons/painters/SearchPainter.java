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
package com.leclercb.taskunifier.gui.commons.painters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.text.Normalizer;

import javax.swing.JLabel;

import org.jdesktop.swingx.painter.Painter;

import com.leclercb.commons.api.utils.StringUtils;

public class SearchPainter implements Painter<JLabel> {
	
	private String searchText;
	
	public SearchPainter() {
		
	}
	
	public String getSearchText() {
		return this.searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	@Override
	public void paint(Graphics2D g, JLabel object, int width, int height) {
		if (this.searchText == null || this.searchText.length() == 0)
			return;
		
		String text = object.getText();
		String searchText = this.searchText;
		
		text = text.toLowerCase();
		searchText = searchText.toString().toLowerCase();
		
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = text.replaceAll("[^\\p{ASCII}]", "");
		
		searchText = Normalizer.normalize(searchText, Normalizer.Form.NFD);
		searchText = searchText.replaceAll("[^\\p{ASCII}]", "");
		
		if (!StringUtils.containsLocalized(text, searchText))
			return;
		
		FontMetrics metrics = object.getFontMetrics(object.getFont());
		
		String start = text.substring(0, text.indexOf(searchText));
		
		int startX = object.getIcon().getIconWidth()
				+ object.getIconTextGap()
				+ metrics.stringWidth(start);
		
		int length = metrics.stringWidth(searchText);
		
		g.setColor(Color.YELLOW);
		g.fillRect(startX, 0, length + 1, height);
	}
	
}
