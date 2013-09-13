/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.threads.checkversion;

import java.net.URI;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.DesktopUtils;
import com.leclercb.taskunifier.gui.utils.HttpUtils;

public class CheckVersionRunnable implements Runnable {
	
	private boolean silent;
	
	public CheckVersionRunnable(boolean silent) {
		this.silent = silent;
	}
	
	@Override
	public void run() {
		try {
			HttpResponse response = HttpUtils.getHttpGetResponse(new URI(
					Constants.VERSION_FILE));
			
			if (!response.isSuccessfull())
				throw new Exception();
			
			String version = response.getContent().trim();
			
			if (version.length() > 10)
				throw new Exception();
			
			if (Constants.getVersion().compareTo(version) < 0) {
				GuiLogger.getLogger().info("New version available : " + version);
				
				String showed = Main.getSettings().getStringProperty(
						"new_version.showed");
				
				if (!this.silent || !EqualsUtils.equals(version, showed)) {
					Main.getSettings().setStringProperty(
							"new_version.showed",
							version);
					
					String[] options = new String[] {
							Translations.getString("general.download"),
							Translations.getString("general.cancel") };
					
					int result = JOptionPane.showOptionDialog(
							FrameUtils.getCurrentWindow(),
							Translations.getString(
									"action.check_version.new_version_available",
									version),
							Translations.getString("general.information"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							null,
							options,
							options[0]);
					
					if (result == 0) {
						DesktopUtils.browse(Constants.DOWNLOAD_URL);
					}
				}
			} else {
				GuiLogger.getLogger().info("No new version available");
				
				if (!this.silent) {
					JOptionPane.showMessageDialog(
							FrameUtils.getCurrentWindow(),
							Translations.getString(
									"action.check_version.no_new_version_available",
									Constants.getVersion()),
							Translations.getString("general.information"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (Exception e) {
			if (this.silent) {
				GuiLogger.getLogger().warning(
						"An error occured while checking for updates");
			} else {
				ErrorInfo info = new ErrorInfo(
						Translations.getString("general.error"),
						Translations.getString("error.check_version_error"),
						null,
						"GUI",
						e,
						Level.INFO,
						null);
				
				JXErrorPane.showDialog(FrameUtils.getCurrentWindow(), info);
			}
		}
	}
	
}
