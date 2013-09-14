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
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallAddFolder extends AbstractCallFolder {
	
	public String addFolder(
			ToodledoAccountInfo accountInfo,
			String key,
			Folder folder) throws SynchronizerException {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(folder);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("name", folder.getTitle()));
		params.add(new BasicNameValuePair(
				"archived",
				(folder.isArchived() ? "1" : "0")));
		params.add(new BasicNameValuePair("f", "xml"));
		
		String scheme = super.getScheme(accountInfo);
		String content = super.callGet(scheme, "/2/folders/add.php", params);
		
		return this.getResponseMessage(folder, accountInfo, content)[0].getModelReferenceIds().get(
				"toodledo");
	}
	
}
