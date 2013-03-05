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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import com.leclercb.commons.gui.logger.GuiLogger;

public final class ProcessUtils {
	
	private ProcessUtils() {
		
	}
	
	public static void invokeLater(final Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}
	
	public static void executeOrInvokeAndWait(final Runnable runnable) {
		try {
			executeOrInvokeAndWait(new Callable<Void>() {
				
				@Override
				public Void call() throws Exception {
					runnable.run();
					return null;
				}
				
			});
		} catch (Exception e) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"EDT invoke and wait exception",
					e);
		}
	}
	
	public static <T> T executeOrInvokeAndWait(final Callable<T> callable)
			throws InterruptedException, ExecutionException {
		if (SwingUtilities.isEventDispatchThread()) {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}
		
		FutureTask<T> task = new FutureTask<T>(callable);
		SwingUtilities.invokeLater(task);
		return task.get();
	}
	
}
