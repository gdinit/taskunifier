/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.beans.FolderBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditFolder extends AbstractCallFolder {
	
	public void editFolder(
			ToodledoAccountInfo accountInfo,
			String accessToken,
			Folder folder) throws SynchronizerException {
		CheckUtils.isNotNull(accessToken);
		CheckUtils.isNotNull(folder);
		
		if (folder.getModelReferenceId("toodledo") == null)
			throw new IllegalArgumentException("You cannot edit a new folder");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair(
				"id",
				folder.getModelReferenceId("toodledo")));
		params.add(new BasicNameValuePair("name", folder.getTitle()));
		params.add(new BasicNameValuePair(
				"archived",
				(folder.isArchived() ? "1" : "0")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/3/folders/edit.php", params);
		
		FolderBean[] folders = this.getResponseMessage(
				folder,
				accountInfo,
				content);
		
		if (folders.length != 1)
			throw new SynchronizerException(false, "Edition of folder "
					+ folder.getModelId()
					+ " has failed");
	}
	
}
