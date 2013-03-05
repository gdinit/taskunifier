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
package com.leclercb.taskunifier.gui.components.synchronize;

import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.JButton;

import com.leclercb.commons.api.progress.ProgressMessageTransformer;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedEvent;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.gui.actions.ActionGetSerial;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.synchronize.progress.SynchronizerProgressMessageTransformer;
import com.leclercb.taskunifier.gui.processes.synchronization.ProcessSynchronize;
import com.leclercb.taskunifier.gui.swing.TUWorkerDialog;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class SynchronizerDialog extends TUWorkerDialog<Void> {
	
	private boolean serialNeeded;
	
	public SynchronizerDialog() {
		super(Translations.getString("general.synchronization"));
		
		this.serialNeeded = false;
		
		final SynchronizerWorker worker = new SynchronizerWorker(false);
		this.setWorker(worker);
	}
	
	public void add(SynchronizerGuiPlugin plugin, ProcessSynchronize.Type type) {
		SynchronizerWorker worker = (SynchronizerWorker) this.getWorker();
		worker.add(plugin, type);
		
		try {
			if (!this.serialNeeded
					&& plugin.needsLicense()
					&& plugin.getLicenseUrl() != null
					&& !plugin.checkLicense()) {
				this.serialNeeded = true;
				this.setSouthComponent(new JButton(new ActionGetSerial(
						22,
						22,
						plugin.getLicenseUrl())));
			}
		} catch (SynchronizerException e) {
			GuiLogger.getLogger().log(Level.WARNING, "Cannot check license", e);
		}
	}
	
	@Override
	public void progressMessageAdded(ProgressMessageAddedEvent event) {
		ProgressMessageTransformer t = SynchronizerProgressMessageTransformer.getInstance();
		
		if (t.acceptsEvent(event)) {
			Icon icon = (Icon) t.getEventValue(event, "icon");
			
			if (icon == null)
				icon = ImageUtils.getResourceImage("transparent.png", 16, 16);
			
			SynchronizerDialog.this.appendToProgressStatus(
					icon,
					"   " + t.getEventValue(event, "message") + "\n");
		}
	}
	
}
