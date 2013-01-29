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
package com.leclercb.commons.gui.swing.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class ScrollablePanel extends JPanel implements Scrollable, SwingConstants {
	
	public enum ScrollableSizeHint {
		NONE,
		FIT,
		STRETCH;
	}
	
	public enum IncrementType {
		PERCENT,
		PIXELS;
	}
	
	private ScrollableSizeHint scrollableHeight = ScrollableSizeHint.NONE;
	private ScrollableSizeHint scrollableWidth = ScrollableSizeHint.NONE;
	
	private IncrementInfo horizontalBlock;
	private IncrementInfo horizontalUnit;
	private IncrementInfo verticalBlock;
	private IncrementInfo verticalUnit;
	
	public ScrollablePanel() {
		this(new FlowLayout());
	}
	
	public ScrollablePanel(LayoutManager layout) {
		super(layout);
		
		IncrementInfo block = new IncrementInfo(IncrementType.PERCENT, 100);
		IncrementInfo unit = new IncrementInfo(IncrementType.PERCENT, 10);
		
		this.setScrollableBlockIncrement(HORIZONTAL, block);
		this.setScrollableBlockIncrement(VERTICAL, block);
		this.setScrollableUnitIncrement(HORIZONTAL, unit);
		this.setScrollableUnitIncrement(VERTICAL, unit);
	}
	
	public ScrollableSizeHint getScrollableHeight() {
		return this.scrollableHeight;
	}
	
	public void setScrollableHeight(ScrollableSizeHint scrollableHeight) {
		this.scrollableHeight = scrollableHeight;
		this.revalidate();
	}
	
	public ScrollableSizeHint getScrollableWidth() {
		return this.scrollableWidth;
	}
	
	public void setScrollableWidth(ScrollableSizeHint scrollableWidth) {
		this.scrollableWidth = scrollableWidth;
		this.revalidate();
	}
	
	public IncrementInfo getScrollableBlockIncrement(int orientation) {
		return orientation == SwingConstants.HORIZONTAL ? this.horizontalBlock : this.verticalBlock;
	}
	
	public void setScrollableBlockIncrement(
			int orientation,
			IncrementType type,
			int amount) {
		IncrementInfo info = new IncrementInfo(type, amount);
		this.setScrollableBlockIncrement(orientation, info);
	}
	
	public void setScrollableBlockIncrement(int orientation, IncrementInfo info) {
		switch (orientation) {
			case SwingConstants.HORIZONTAL:
				this.horizontalBlock = info;
				break;
			case SwingConstants.VERTICAL:
				this.verticalBlock = info;
				break;
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
		}
	}
	
	public IncrementInfo getScrollableUnitIncrement(int orientation) {
		return orientation == SwingConstants.HORIZONTAL ? this.horizontalUnit : this.verticalUnit;
	}
	
	public void setScrollableUnitIncrement(
			int orientation,
			IncrementType type,
			int amount) {
		IncrementInfo info = new IncrementInfo(type, amount);
		this.setScrollableUnitIncrement(orientation, info);
	}
	
	public void setScrollableUnitIncrement(int orientation, IncrementInfo info) {
		switch (orientation) {
			case SwingConstants.HORIZONTAL:
				this.horizontalUnit = info;
				break;
			case SwingConstants.VERTICAL:
				this.verticalUnit = info;
				break;
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
		}
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}
	
	@Override
	public int getScrollableUnitIncrement(
			Rectangle visible,
			int orientation,
			int direction) {
		switch (orientation) {
			case SwingConstants.HORIZONTAL:
				return this.getScrollableIncrement(
						this.horizontalUnit,
						visible.width);
			case SwingConstants.VERTICAL:
				return this.getScrollableIncrement(
						this.verticalUnit,
						visible.height);
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
		}
	}
	
	@Override
	public int getScrollableBlockIncrement(
			Rectangle visible,
			int orientation,
			int direction) {
		switch (orientation) {
			case SwingConstants.HORIZONTAL:
				return this.getScrollableIncrement(
						this.horizontalBlock,
						visible.width);
			case SwingConstants.VERTICAL:
				return this.getScrollableIncrement(
						this.verticalBlock,
						visible.height);
			default:
				throw new IllegalArgumentException("Invalid orientation: "
						+ orientation);
		}
	}
	
	protected int getScrollableIncrement(IncrementInfo info, int distance) {
		if (info.getIncrement() == IncrementType.PIXELS)
			return info.getAmount();
		else
			return distance * info.getAmount() / 100;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if (this.scrollableWidth == ScrollableSizeHint.NONE)
			return false;
		
		if (this.scrollableWidth == ScrollableSizeHint.FIT)
			return true;
		
		if (this.getParent() instanceof JViewport) {
			return (((JViewport) this.getParent()).getWidth() > this.getPreferredSize().width);
		}
		
		return false;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		if (this.scrollableHeight == ScrollableSizeHint.NONE)
			return false;
		
		if (this.scrollableHeight == ScrollableSizeHint.FIT)
			return true;
		
		if (this.getParent() instanceof JViewport) {
			return (((JViewport) this.getParent()).getHeight() > this.getPreferredSize().height);
		}
		
		return false;
	}
	
	private static class IncrementInfo {
		
		private IncrementType type;
		private int amount;
		
		public IncrementInfo(IncrementType type, int amount) {
			this.type = type;
			this.amount = amount;
		}
		
		public IncrementType getIncrement() {
			return this.type;
		}
		
		public int getAmount() {
			return this.amount;
		}
		
		@Override
		public String toString() {
			return "ScrollablePanel[" + this.type + ", " + this.amount + "]";
		}
		
	}
	
}
