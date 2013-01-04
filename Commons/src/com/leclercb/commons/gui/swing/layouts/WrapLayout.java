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
package com.leclercb.commons.gui.swing.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class WrapLayout extends FlowLayout {
	
	public WrapLayout() {
		super();
	}
	
	public WrapLayout(int align) {
		super(align);
	}
	
	public WrapLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}
	
	@Override
	public Dimension preferredLayoutSize(Container target) {
		return this.layoutSize(target, true);
	}
	
	@Override
	public Dimension minimumLayoutSize(Container target) {
		Dimension minimum = this.layoutSize(target, false);
		minimum.width -= (this.getHgap() + 1);
		return minimum;
	}
	
	private Dimension layoutSize(Container target, boolean preferred) {
		synchronized (target.getTreeLock()) {
			int targetWidth = target.getSize().width;
			
			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;
			
			int hgap = this.getHgap();
			int vgap = this.getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left
					+ insets.right
					+ (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;
			
			Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;
			
			int nmembers = target.getComponentCount();
			
			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				
				if (m.isVisible()) {
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
					
					if (rowWidth + d.width > maxWidth) {
						this.addRow(dim, rowWidth, rowHeight);
						rowWidth = 0;
						rowHeight = 0;
					}
					
					if (rowWidth != 0) {
						rowWidth += hgap;
					}
					
					rowWidth += d.width;
					rowHeight = Math.max(rowHeight, d.height);
				}
			}
			
			this.addRow(dim, rowWidth, rowHeight);
			
			dim.width += horizontalInsetsAndGap;
			dim.height += insets.top + insets.bottom + vgap * 2;
			
			Container scrollPane = SwingUtilities.getAncestorOfClass(
					JScrollPane.class,
					target);
			
			if (scrollPane != null) {
				dim.width -= (hgap + 1);
			}
			
			return dim;
		}
	}
	
	private void addRow(Dimension dim, int rowWidth, int rowHeight) {
		dim.width = Math.max(dim.width, rowWidth);
		
		if (dim.height > 0) {
			dim.height += this.getVgap();
		}
		
		dim.height += rowHeight;
	}
	
}
