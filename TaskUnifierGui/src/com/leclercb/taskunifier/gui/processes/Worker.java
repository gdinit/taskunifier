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
package com.leclercb.taskunifier.gui.processes;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.event.action.ActionSupport;
import com.leclercb.commons.api.event.action.ActionSupported;
import com.leclercb.commons.api.progress.ProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedEvent;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedListener;
import com.leclercb.commons.api.progress.event.WeakProgressMessageAddedListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.translations.Translations;

public abstract class Worker<T> extends SwingWorker<T, ProgressMessage> implements ActionSupported, ProgressMessageAddedListener {
	
	public static final String ACTION_STARTED = "ACTION_STARTED";
	public static final String ACTION_FINISHED = "ACTION_FINISHED";
	
	private ActionSupport actionSupport;
	
	private ProgressMonitor edtMonitor;
	private ProgressMonitor monitor;
	
	public Worker(ProgressMonitor monitor) {
		this.actionSupport = new ActionSupport(this);
		this.edtMonitor = new ProgressMonitor();
		
		this.edtMonitor.addProgressMessageAddedListener(new WeakProgressMessageAddedListener(
				this.edtMonitor,
				this));
		
		this.setMonitor(monitor);
	}
	
	public ProgressMonitor getEDTMonitor() {
		return this.edtMonitor;
	}
	
	public ProgressMonitor getMonitor() {
		return this.monitor;
	}
	
	public void setMonitor(ProgressMonitor monitor) {
		CheckUtils.isNotNull(monitor);
		this.monitor = monitor;
	}
	
	@Override
	protected void process(List<ProgressMessage> messages) {
		for (ProgressMessage message : messages) {
			this.monitor.addMessage(message);
		}
	}
	
	protected abstract T longTask() throws Exception;
	
	protected void handleException(final Throwable t) {
		TUSwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				ErrorInfo info = new ErrorInfo(
						Translations.getString("general.error"),
						t.getMessage(),
						null,
						"GUI",
						t,
						Level.WARNING,
						null);
				
				JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
			}
			
		});
	}
	
	@Override
	protected final T doInBackground() throws Exception {
		this.actionSupport.fireActionPerformed(0, ACTION_STARTED);
		
		try {
			return this.longTask();
		} catch (final Throwable t) {
			this.handleException(t);
			return null;
		} finally {
			Thread.sleep(1000);
		}
	}
	
	@Override
	protected void done() {
		super.done();
		this.actionSupport.fireActionPerformed(0, ACTION_FINISHED);
	}
	
	@Override
	public void progressMessageAdded(final ProgressMessageAddedEvent event) {
		TUSwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Worker.this.monitor.addMessage(event.getMessage());
			}
			
		});
	}
	
	@Override
	public void addActionListener(ActionListener listener) {
		this.actionSupport.addActionListener(listener);
	}
	
	@Override
	public void removeActionListener(ActionListener listener) {
		this.actionSupport.removeActionListener(listener);
	}
	
	public final <O> O executeAtomicAction(Callable<O> callable, int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<O> future = executor.submit(callable);
		return future.get(timeout, TimeUnit.MILLISECONDS);
	}
	
}
