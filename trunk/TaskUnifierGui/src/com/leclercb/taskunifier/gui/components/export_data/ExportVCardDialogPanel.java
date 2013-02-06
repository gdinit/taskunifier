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
package com.leclercb.taskunifier.gui.components.export_data;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;

import com.leclercb.taskunifier.api.models.Contact;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ExportVCardDialogPanel extends AbstractExportDialogPanel {
	
	private static ExportVCardDialogPanel INSTANCE;
	
	public static ExportVCardDialogPanel getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ExportVCardDialogPanel();
		
		return INSTANCE;
	}
	
	private ExportVCardDialogPanel() {
		super(
				Translations.getString("action.export_vcard"),
				"vcf",
				Translations.getString("general.vcard_files"),
				"export.vcard.file_name");
	}
	
	@Override
	protected void exportToFile(String file) throws Exception {
		VCardComposer composer = new VCardComposer();
		
		StringBuffer buffer = new StringBuffer();
		List<Contact> contacts = ContactFactory.getInstance().getList();
		for (Contact contact : contacts) {
			ContactStruct c = new ContactStruct();
			c.name = contact.getLastName() + " " + contact.getFirstName();
			c.addContactmethod(
					Contacts.KIND_EMAIL,
					-1,
					contact.getEmail(),
					null,
					true);
			
			String vcard = composer.createVCard(
					c,
					VCardComposer.VERSION_VCARD21_INT);
			
			buffer.append(vcard);
			buffer.append("\n");
		}
		
		FileUtils.writeStringToFile(new File(file), buffer.toString());
	}
	
}
