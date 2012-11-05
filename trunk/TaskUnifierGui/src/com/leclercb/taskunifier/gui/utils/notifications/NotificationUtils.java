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
package com.leclercb.taskunifier.gui.utils.notifications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.notifications.exceptions.NotifierOSException;
import com.leclercb.taskunifier.gui.utils.notifications.growl.GrowlForMac;
import com.leclercb.taskunifier.gui.utils.notifications.growl.GrowlForWindows;
import com.leclercb.taskunifier.gui.utils.notifications.snarl.SnarlForWindows;

public final class NotificationUtils {
	
	private static List<Notifier> NOTIFIERS = new ArrayList<Notifier>();
	
	private NotificationUtils() {
		
	}
	
	static {
		loadNotifier("general.growl.enabled", GrowlForMac.class);
		loadNotifier("general.growl.enabled", GrowlForWindows.class);
		loadNotifier("general.snarl.enabled", SnarlForWindows.class);
		
		Main.getActionSupport().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!event.getActionCommand().equals("BEFORE_EXIT"))
					return;
				
				for (Notifier notifier : NOTIFIERS) {
					try {
						notifier.close();
						GuiLogger.getLogger().log(
								Level.INFO,
								"Notifier closed: " + notifier.getName());
					} catch (Exception e) {
						GuiLogger.getLogger().log(
								Level.WARNING,
								"Cannot close notifier " + notifier.getName(),
								e);
					}
				}
			}
			
		});
	}
	
	public static <N extends Notifier> void loadNotifier(
			final String propertyName,
			final Class<N> cls) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				try {
					if (!Main.getSettings().getBooleanProperty(propertyName))
						return;
					
					Notifier notifier = cls.newInstance();
					notifier.open();
					
					synchronized (NOTIFIERS) {
						NOTIFIERS.add(notifier);
					}
					
					GuiLogger.getLogger().log(
							Level.INFO,
							"Notifier loaded: " + cls.getName());
				} catch (NotifierOSException e) {
					
				} catch (Exception e) {
					GuiLogger.getLogger().log(
							Level.WARNING,
							"Cannot load notifier: " + cls.getName(),
							e);
				}
			}
		};
		
		new Thread(runnable).start();
	}
	
	public static void notify(NotificationList list, String title) {
		notify(list, title, null);
	}
	
	public static void notify(
			final NotificationList list,
			final String title,
			final String description) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				List<Notifier> notifiers = null;
				
				synchronized (NOTIFIERS) {
					notifiers = new ArrayList<Notifier>(NOTIFIERS);
				}
				
				for (Notifier notifier : notifiers) {
					try {
						notifier.notify(list, title, description);
					} catch (Exception e) {
						GuiLogger.getLogger().log(
								Level.WARNING,
								"Cannot send message to " + notifier.getName(),
								e);
					}
				}
			}
			
		};
		
		new Thread(runnable).start();
	}
	
}
