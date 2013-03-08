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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.OrientationRequested;

import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;

public final class PrintUtils {
	
	public static void printTable(
			String propertyName,
			TablePrintable tablePrintable) throws PrinterException {
		PrintDialog dialog = new PrintDialog();
		dialog.setPropertyName(propertyName);
		dialog.setPrintableReport(tablePrintable);
		dialog.setVisible(true);
		dialog.dispose();
		
		if (dialog.isCancelled())
			return;
		
		tablePrintable.getComponent().clearSelection();
		
		PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		attributes.add(new JobName(Constants.TITLE, null));
		
		int orientation = Main.getSettings().getIntegerProperty(
				propertyName + ".orientation");
		attributes.add(PrintUtils.getOrientationRequested(orientation));
		
		double scalingFactor = Main.getSettings().getDoubleProperty(
				propertyName + ".scaling_factor");
		tablePrintable.setScalingFactor(scalingFactor);
		
		tablePrintable.setHeaderFormat(new MessageFormat(
				dialog.getReportTitle()));
		
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		
		printerJob.setPrintable(tablePrintable);
		
		if (printerJob.printDialog(attributes)) {
			printerJob.print(attributes);
		}
	}
	
	public static OrientationRequested getOrientationRequested(int value) {
		if (OrientationRequested.LANDSCAPE.getValue() == value)
			return OrientationRequested.LANDSCAPE;
		
		if (OrientationRequested.PORTRAIT.getValue() == value)
			return OrientationRequested.PORTRAIT;
		
		if (OrientationRequested.REVERSE_LANDSCAPE.getValue() == value)
			return OrientationRequested.REVERSE_LANDSCAPE;
		
		if (OrientationRequested.REVERSE_PORTRAIT.getValue() == value)
			return OrientationRequested.REVERSE_PORTRAIT;
		
		return OrientationRequested.LANDSCAPE;
	}
	
}
