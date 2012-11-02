package com.leclercb.taskunifier.gui.utils.growl;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.code.jgntp.Gntp;
import com.google.code.jgntp.GntpApplicationInfo;
import com.google.code.jgntp.GntpClient;
import com.google.code.jgntp.GntpNotificationInfo;

public class GrowlForWindows implements Growl {
	
	private GntpClient client;
	private GntpApplicationInfo applicationInfo;
	
	public GrowlForWindows() {
		this.initialize();
	}
	
	private void initialize() {
		this.applicationInfo = Gntp.appInfo("TaskUnifier").build();
		this.client = Gntp.client(this.applicationInfo).forHost("localhost").build();
		this.client.register();
	}
	
	@Override
	public void registerApplication() throws Exception {
		this.client.register();
	}
	
	@Override
	public void notify(String notificationList, String title) throws Exception {
		this.notify(notificationList, title, null);
	}
	
	@Override
	public void notify(String notificationList, String title, String description)
			throws Exception {
		GntpNotificationInfo notificationInfo = Gntp.notificationInfo(
				this.applicationInfo,
				notificationList).build();
		
		this.client.notify(
				Gntp.notification(notificationInfo, title).text(description).build(),
				2,
				SECONDS);
	}
	
	@Override
	public void close() throws Exception {
		this.client.shutdown(2, SECONDS);
	}
	
}
