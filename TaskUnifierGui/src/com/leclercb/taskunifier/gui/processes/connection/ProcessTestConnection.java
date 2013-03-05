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
package com.leclercb.taskunifier.gui.processes.connection;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.processes.Process;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import com.leclercb.taskunifier.gui.processes.Worker;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.HttpUtils;

public class ProcessTestConnection implements Process<HttpResponse> {
	
	private boolean showSuccess;
	private boolean showFailure;
	
	public ProcessTestConnection(boolean showSuccess, boolean showFailure) {
		this.setShowSuccess(showSuccess);
		this.setShowFailure(showFailure);
	}
	
	public boolean isShowSuccess() {
		return this.showSuccess;
	}
	
	public void setShowSuccess(boolean showSuccess) {
		this.showSuccess = showSuccess;
	}
	
	public boolean isShowFailure() {
		return this.showFailure;
	}
	
	public void setShowFailure(boolean showFailure) {
		this.showFailure = showFailure;
	}
	
	@Override
	public HttpResponse execute(final Worker<?> worker) throws Exception {
		final ProgressMonitor monitor = worker.getEDTMonitor();
		
		monitor.addMessage(new DefaultProgressMessage(
				Translations.getString("configuration.proxy.test_connection")));
		
		HttpResponse response = worker.executeInterruptibleAction(
				new Callable<HttpResponse>() {
					
					@Override
					public HttpResponse call() throws Exception {
						return HttpUtils.getHttpGetResponse(new URI(
								Constants.TEST_CONNECTION));
					}
					
				}, Constants.TIMEOUT_HTTP_CALL);
		
		if (worker.isCancelled())
			return null;
		
		if (this.showSuccess && response.isSuccessfull())
			this.showResult(true);
		
		if (this.showFailure && !response.isSuccessfull())
			this.showResult(false);
		
		return response;
	}
	
	@Override
	public void done(Worker<?> worker) {
		
	}
	
	private void showResult(final boolean result) throws Exception {
		ProcessUtils.executeOrInvokeAndWait(new Callable<Void>() {
			
			@Override
			public Void call() {
				if (result) {
					JOptionPane.showMessageDialog(
							FrameUtils.getCurrentWindow(),
							Translations.getString("configuration.proxy.test_connection.success"),
							Translations.getString("general.information"),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					ErrorInfo info = new ErrorInfo(
							Translations.getString("general.error"),
							Translations.getString("configuration.proxy.test_connection.failed"),
							null,
							"GUI",
							null,
							Level.INFO,
							null);
					
					JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
				}
				
				return null;
			}
			
		});
	}
	
}
