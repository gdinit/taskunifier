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
package com.leclercb.taskunifier.gui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.leclercb.commons.api.progress.event.ProgressMessageAddedEvent;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedListener;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class TUWorkerDialog<T> extends TUDialog implements ProgressMessageAddedListener, ActionListener {
	
	private Worker<T> worker;
	
	private JPanel panel;
	private JProgressBar progressBar;
	private JTextPane progressStatus;
	
	public TUWorkerDialog(String title) {
		super(FrameUtils.getCurrentWindow());
		
		this.initialize(title);
	}
	
	@Override
	public void progressMessageAdded(ProgressMessageAddedEvent event) {
		this.appendToProgressStatus(null, event.getMessage() + "\n");
	}
	
	public void appendToProgressStatus(Icon icon, String text) {
		try {
			if (icon != null)
				this.progressStatus.insertIcon(icon);
			
			Document document = this.progressStatus.getDocument();
			document.insertString(document.getLength(), text, null);
		} catch (BadLocationException e) {
			GuiLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public T getResult() {
		try {
			return this.worker.get();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
	}
	
	public Worker<T> getWorker() {
		return this.worker;
	}
	
	public void setWorker(Worker<T> worker) {
		if (this.worker != null)
			this.worker.removeActionListener(this);
		
		this.worker = worker;
		
		if (this.worker != null)
			this.worker.addActionListener(this);
	}
	
	public void setSouthComponent(JComponent component) {
		this.panel.add(component, BorderLayout.SOUTH);
	}
	
	private void initialize(String title) {
		this.setModal(true);
		this.setTitle(title);
		this.setSize(600, 300);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		if (this.getOwner() != null)
			this.setLocationRelativeTo(this.getOwner());
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent event) {
				TUWorkerDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				TUWorkerDialog.this.worker.execute();
			}
			
		});
		
		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout(5, 5));
		this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
		this.progressBar.setIndeterminate(true);
		this.progressBar.setString("");
		
		this.progressStatus = new JTextPane();
		this.progressStatus.setEditable(false);
		
		JScrollPane scrollStatus = ComponentFactory.createJScrollPane(
				this.progressStatus,
				true);
		scrollStatus.setAutoscrolls(true);
		scrollStatus.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable().setValue(
								e.getAdjustable().getMaximum());
					}
					
				});
		
		this.panel.add(this.progressBar, BorderLayout.NORTH);
		this.panel.add(scrollStatus, BorderLayout.CENTER);
		
		this.add(this.panel, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(Worker.ACTION_FINISHED)) {
			this.setCursor(null);
			this.setVisible(false);
			this.dispose();
		}
	}
	
}
