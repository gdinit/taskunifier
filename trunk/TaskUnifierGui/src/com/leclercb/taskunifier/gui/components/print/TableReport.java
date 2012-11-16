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
package com.leclercb.taskunifier.gui.components.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.MessageFormat;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class TableReport implements PrintableReport, Printable {
	
	private JTable table;
	private JTableHeader header;
	private TableColumnModel colModel;
	private int totalColWidth;
	private MessageFormat headerFormat;
	private MessageFormat footerFormat;
	private int last = -1;
	private int row = 0;
	private int col = 0;
	private final Rectangle clip = new Rectangle(0, 0, 0, 0);
	private final Rectangle hclip = new Rectangle(0, 0, 0, 0);
	private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);
	private static final int H_F_SPACE = 8;
	private static final float HEADER_FONT_SIZE = 12.0f;
	private static final float FOOTER_FONT_SIZE = 8.0f;
	private Font headerFont;
	private Font footerFont;
	private JTable.PrintMode printMode;
	private double scalingFactor = 1.0D;
	
	public TableReport(
			JTable table,
			JTable.PrintMode printMode,
			double scalingFactor,
			MessageFormat headerFormat,
			MessageFormat footerFormat) {
		this.table = table;
		this.printMode = printMode;
		this.scalingFactor = scalingFactor;
		this.headerFormat = headerFormat;
		this.footerFormat = footerFormat;
	}
	
	@Override
	public JTable getComponent() {
		return this.table;
	}
	
	@Override
	public JTable.PrintMode getPrintMode() {
		return this.printMode;
	}
	
	@Override
	public void setPrintMode(JTable.PrintMode printMode) {
		this.printMode = printMode;
	}
	
	@Override
	public double getScalingFactor() {
		return this.scalingFactor;
	}
	
	@Override
	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}
	
	@Override
	public MessageFormat getHeaderFormat() {
		return this.headerFormat;
	}
	
	@Override
	public void setHeaderFormat(MessageFormat headerFormat) {
		this.headerFormat = headerFormat;
	}
	
	@Override
	public MessageFormat getFooterFormat() {
		return this.footerFormat;
	}
	
	@Override
	public void setFooterFormat(MessageFormat footerFormat) {
		this.footerFormat = footerFormat;
	}
	
	private void initialize() {
		this.header = this.table.getTableHeader();
		this.colModel = this.table.getColumnModel();
		this.totalColWidth = this.colModel.getTotalColumnWidth();
		if (this.header != null) {
			this.hclip.height = this.header.getHeight();
		}
		
		this.headerFont = this.table.getFont().deriveFont(
				Font.BOLD,
				HEADER_FONT_SIZE);
		this.footerFont = this.table.getFont().deriveFont(
				Font.PLAIN,
				FOOTER_FONT_SIZE);
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		this.initialize();
		
		final int imgWidth = (int) pageFormat.getImageableWidth();
		final int imgHeight = (int) pageFormat.getImageableHeight();
		if (imgWidth <= 0) {
			throw new PrinterException("Width of printable area is too small.");
		}
		Object[] pageNumber = new Object[] { new Integer(pageIndex + 1) };
		String headerText = null;
		if (this.headerFormat != null) {
			headerText = this.headerFormat.format(pageNumber);
		}
		String footerText = null;
		if (this.footerFormat != null) {
			footerText = this.footerFormat.format(pageNumber);
		}
		Rectangle2D hRect = null;
		Rectangle2D fRect = null;
		int headerTextSpace = 0;
		int footerTextSpace = 0;
		int availableSpace = imgHeight;
		if (headerText != null) {
			graphics.setFont(this.headerFont);
			hRect = graphics.getFontMetrics().getStringBounds(
					headerText,
					graphics);
			headerTextSpace = (int) Math.ceil(hRect.getHeight());
			availableSpace -= headerTextSpace + H_F_SPACE;
		}
		if (footerText != null) {
			graphics.setFont(this.footerFont);
			fRect = graphics.getFontMetrics().getStringBounds(
					footerText,
					graphics);
			footerTextSpace = (int) Math.ceil(fRect.getHeight());
			availableSpace -= footerTextSpace + H_F_SPACE;
		}
		if (availableSpace <= 0) {
			throw new PrinterException("Height of printable area is too small.");
		}
		double sf = this.scalingFactor;
		if (this.printMode == JTable.PrintMode.FIT_WIDTH
				&& this.totalColWidth > imgWidth) {
			sf = (double) imgWidth / (double) this.totalColWidth;
		}
		while (this.last < pageIndex) {
			if (this.row >= this.table.getRowCount() && this.col == 0) {
				return NO_SUCH_PAGE;
			}
			int scaledWidth = (int) (imgWidth / sf);
			int scaledHeight = (int) ((availableSpace - this.hclip.height) / sf);
			this.findNextClip(scaledWidth, scaledHeight);
			this.last++;
		}
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		AffineTransform oldTrans;
		if (footerText != null) {
			oldTrans = g2d.getTransform();
			g2d.translate(0, imgHeight - footerTextSpace);
			this.printText(g2d, footerText, fRect, this.footerFont, imgWidth);
			g2d.setTransform(oldTrans);
		}
		if (headerText != null) {
			this.printText(g2d, headerText, hRect, this.headerFont, imgWidth);
			g2d.translate(0, headerTextSpace + H_F_SPACE);
		}
		this.tempRect.x = 0;
		this.tempRect.y = 0;
		this.tempRect.width = imgWidth;
		this.tempRect.height = availableSpace;
		g2d.clip(this.tempRect);
		if (sf != 1.0D) {
			g2d.scale(sf, sf);
		} else {
			int diff = (imgWidth - this.clip.width) / 2;
			g2d.translate(diff, 0);
		}
		oldTrans = g2d.getTransform();
		Shape oldClip = g2d.getClip();
		if (this.header != null) {
			this.hclip.x = this.clip.x;
			this.hclip.width = this.clip.width;
			g2d.translate(-this.hclip.x, 0);
			g2d.clip(this.hclip);
			this.header.print(g2d);
			g2d.setTransform(oldTrans);
			g2d.setClip(oldClip);
			g2d.translate(0, this.hclip.height);
		}
		g2d.translate(-this.clip.x, -this.clip.y);
		g2d.clip(this.clip);
		this.table.print(g2d);
		g2d.setTransform(oldTrans);
		g2d.setClip(oldClip);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, this.clip.width, this.hclip.height
				+ this.clip.height);
		return PAGE_EXISTS;
	}
	
	private void printText(
			Graphics2D g2d,
			String text,
			Rectangle2D rect,
			Font font,
			int imgWidth) {
		int tx;
		if (rect.getWidth() < imgWidth) {
			tx = (int) ((imgWidth - rect.getWidth()) / 2);
		} else if (this.table.getComponentOrientation().isLeftToRight()) {
			tx = 0;
		} else {
			tx = -(int) (Math.ceil(rect.getWidth()) - imgWidth);
		}
		int ty = (int) Math.ceil(Math.abs(rect.getY()));
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.drawString(text, tx, ty);
	}
	
	private void findNextClip(int pw, int ph) {
		final boolean ltr = this.table.getComponentOrientation().isLeftToRight();
		if (this.col == 0) {
			if (ltr) {
				this.clip.x = 0;
			} else {
				this.clip.x = this.totalColWidth;
			}
			this.clip.y += this.clip.height;
			this.clip.width = 0;
			this.clip.height = 0;
			int rowCount = this.table.getRowCount();
			int rowHeight = this.table.getRowHeight(this.row);
			do {
				this.clip.height += rowHeight;
				if (++this.row >= rowCount) {
					break;
				}
				rowHeight = this.table.getRowHeight(this.row);
			} while (this.clip.height + rowHeight <= ph);
		}
		if (this.printMode == JTable.PrintMode.FIT_WIDTH) {
			this.clip.x = 0;
			this.clip.width = this.totalColWidth;
			return;
		}
		if (ltr) {
			this.clip.x += this.clip.width;
		}
		this.clip.width = 0;
		int colCount = this.table.getColumnCount();
		int colWidth = this.colModel.getColumn(this.col).getWidth();
		do {
			this.clip.width += colWidth;
			if (!ltr) {
				this.clip.x -= colWidth;
			}
			if (++this.col >= colCount) {
				this.col = 0;
				break;
			}
			colWidth = this.colModel.getColumn(this.col).getWidth();
		} while (this.clip.width + colWidth <= pw);
	}
	
}
