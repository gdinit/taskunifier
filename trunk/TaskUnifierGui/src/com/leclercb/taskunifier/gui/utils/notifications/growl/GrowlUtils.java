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
package com.leclercb.taskunifier.gui.utils.notifications.growl;

import java.util.logging.Level;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.notifications.NotificationList;

public final class GrowlUtils {
	
	private GrowlUtils() {
		
	}
	
	private static Growl GROWL;
	
	static {
		initialize();
	}
	
	private static void initialize() {
		try {
			if (!Main.getSettings().getBooleanProperty("general.growl.enabled")) {
				GROWL = null;
				return;
			}
			
			if (SystemUtils.IS_OS_MAC) {
				GROWL = new GrowlForMac(
						Constants.TITLE,
						NotificationList.getAllNotificationsList(),
						NotificationList.getEnabledNotificationsList());
				
			}
			
			if (SystemUtils.IS_OS_WINDOWS) {
				GROWL = new GrowlForWindows();
			}
			
			GROWL.registerApplication();
			GuiLogger.getLogger().info("Growl support enabled");
		} catch (Throwable t) {
			GROWL = null;
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot initialize Growl",
					t);
		}
	}
	
	public static void notify(NotificationList list, String title) {
		notify(list, title, "");
	}
	
	public static void notify(
			NotificationList list,
			String title,
			String description) {
		if (GROWL == null)
			return;
		
		try {
			GROWL.notify(list.getNotificationList(), title, description);
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot send message to Growl",
					t);
		}
	}
	
}
