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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.attribute.standard.OrientationRequested;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.jdesktop.swingx.JXHeader;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUPrintButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class PrintDialogPanel extends TUDialogPanel {
	
	private static PrintDialogPanel INSTANCE;
	
	protected static PrintDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PrintDialogPanel();
		
		return INSTANCE;
	}
	
	private PrintableReport printableReport;
	private String propertyName;
	
	private JPanel printPanel;
	private boolean cancelled;
	
	private JComboBox orientation;
	private JSpinner scalingFactor;
	private JTextField reportTitle;
	
	private JButton printButton;
	private JButton cancelButton;
	
	private PrintDialogPanel() {
		this.initialize();
	}
	
	public String getReportTitle() {
		return this.reportTitle.getText();
	}
	
	public PrintableReport getPrintableReport() {
		return this.printableReport;
	}
	
	public void setPrintableReport(PrintableReport printableReport) {
		CheckUtils.isNotNull(printableReport);
		this.printableReport = printableReport;
		
		this.reportTitle.setText(this.printableReport.getHeaderFormat().toPattern());
		
		this.printPanel.removeAll();
		
		this.printPanel.add(
				ComponentFactory.createJScrollPane(
						printableReport.getComponent(),
						true),
				BorderLayout.CENTER);
		
		this.printPanel.validate();
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}
	
	public void setPropertyName(String propertyName) {
		CheckUtils.isNotNull(propertyName);
		this.propertyName = propertyName;
		
		int orientation = Main.getSettings().getIntegerProperty(
				propertyName + ".orientation");
		this.orientation.setSelectedItem(PrintUtils.getOrientationRequested(orientation));
		
		double scalingFactor = Main.getSettings().getDoubleProperty(
				propertyName + ".scaling_factor");
		this.scalingFactor.setValue(scalingFactor);
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.print"));
		header.setDescription(Translations.getString("header.description.print"));
		header.setIcon(ImageUtils.getResourceImage("print.png", 32, 32));
		this.add(header, BorderLayout.NORTH);
		
		JPanel printOptionsPanel = new JPanel();
		printOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.orientation = new JComboBox(new Object[] {
				OrientationRequested.LANDSCAPE,
				OrientationRequested.PORTRAIT,
				OrientationRequested.REVERSE_LANDSCAPE,
				OrientationRequested.REVERSE_PORTRAIT });
		
		this.scalingFactor = new JSpinner();
		this.scalingFactor.setModel(new SpinnerNumberModel(
				0.10,
				0.10,
				2.00,
				0.10));
		
		this.scalingFactor.setEditor(new JSpinner.NumberEditor(
				this.scalingFactor,
				"##0%"));
		
		this.reportTitle = new JTextField(25);
		
		printOptionsPanel.add(this.orientation);
		printOptionsPanel.add(this.scalingFactor);
		printOptionsPanel.add(this.reportTitle);
		
		this.printPanel = new JPanel();
		this.printPanel.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.add(printOptionsPanel, BorderLayout.NORTH);
		mainPanel.add(this.printPanel, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);
		
		this.initializeButtonsPanel(mainPanel);
	}
	
	private void initializeButtonsPanel(JPanel panel) {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("PRINT")) {
					if (PrintDialogPanel.this.propertyName != null) {
						Main.getSettings().setIntegerProperty(
								PrintDialogPanel.this.propertyName
										+ ".orientation",
								((OrientationRequested) PrintDialogPanel.this.orientation.getSelectedItem()).getValue());
						
						Main.getSettings().setDoubleProperty(
								PrintDialogPanel.this.propertyName
										+ ".scaling_factor",
								(Double) PrintDialogPanel.this.scalingFactor.getValue());
					}
					
					PrintDialogPanel.this.cancelled = false;
				} else {
					PrintDialogPanel.this.cancelled = true;
				}
				
				PrintDialogPanel.this.getDialog().setVisible(false);
			}
			
		};
		
		this.printButton = new TUPrintButton(listener);
		this.cancelButton = new TUCancelButton(listener);
		TUButtonsPanel buttonsPanel = new TUButtonsPanel(
				this.printButton,
				this.cancelButton);
		
		panel.add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().getRootPane().setDefaultButton(this.printButton);
	}
	
}
