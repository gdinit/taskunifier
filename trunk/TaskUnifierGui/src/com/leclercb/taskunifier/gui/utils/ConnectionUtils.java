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
package com.leclercb.taskunifier.gui.utils;

import java.net.URI;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.progress.DefaultProgressMessage;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.TUSwingUtilities;
import com.leclercb.taskunifier.gui.swing.TUWorker;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;

public final class ConnectionUtils {
	
	private ConnectionUtils() {
		
	}
	
	public static HttpResponse testConnection(
			final long wait,
			final boolean showSuccess,
			final boolean showFailure) {
		TUWorkerDialog<HttpResponse> dialog = new TUWorkerDialog<HttpResponse>(
				Translations.getString("configuration.proxy.test_connection"));
		
		ProgressMonitor monitor = new ProgressMonitor();
		monitor.addListChangeListener(dialog);
		
		dialog.setWorker(new TUWorker<HttpResponse>(monitor) {
			
			@Override
			protected HttpResponse longTask() throws Exception {
				this.publish(new DefaultProgressMessage(
						Translations.getString("configuration.proxy.test_connection")));
				
				final HttpResponse response = new HttpResponse();
				response.setCode(-1);
				
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							HttpResponse r = HttpUtils.getHttpGetResponse(new URI(
									Constants.TEST_CONNECTION));
							
							synchronized (response) {
								response.setCode(r.getCode());
								response.setMessage(r.getMessage());
								response.setBytes(r.getBytes());
							}
						} catch (Throwable t) {
							
						}
					}
					
				});
				
				thread.start();
				thread.join(10000);
				
				HttpResponse returnResponse = new HttpResponse();
				
				synchronized (response) {
					returnResponse.setCode(response.getCode());
					returnResponse.setMessage(response.getMessage());
					returnResponse.setBytes(response.getBytes());
				}
				
				if (showSuccess && response.isSuccessfull())
					showResult(true);
				
				if (showFailure && !response.isSuccessfull())
					showResult(false);
				
				return returnResponse;
			}
			
		});
		
		dialog.setVisible(true);
		
		return dialog.getResult();
	}
	
	private static void showResult(final boolean result) {
		TUSwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
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
			}
			
		});
	}
	
}
