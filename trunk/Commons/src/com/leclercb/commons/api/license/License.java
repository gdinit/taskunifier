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
package com.leclercb.commons.api.license;

import java.io.StringWriter;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;

import com.leclercb.commons.api.license.exceptions.LicenseException;
import com.leclercb.commons.api.license.exceptions.LicenseExpiredException;
import com.leclercb.commons.api.license.exceptions.LicenseVersionExpiredException;
import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.utils.CheckUtils;

public class License {
	
	private String name;
	private String email;
	
	private LicenseType licenseType;
	private String version;
	private Calendar expiration;
	
	private String reference;
	
	public License() {
		this(LicenseType.TRIAL);
	}
	
	public License(LicenseType licenseType) {
		this.setLicenseType(licenseType);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public LicenseType getLicenseType() {
		return this.licenseType;
	}
	
	public void setLicenseType(LicenseType licenseType) {
		CheckUtils.isNotNull(licenseType);
		this.licenseType = licenseType;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Calendar getExpiration() {
		return this.expiration;
	}
	
	public void setExpiration(Calendar expiration) {
		this.expiration = expiration;
	}
	
	public String getReference() {
		return this.reference;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public void validate(Calendar currentDate, String currentVersion)
			throws LicenseException {
		this.validateExpiration(currentDate);
		this.validateVersion(currentVersion);
	}
	
	protected void validateExpiration(Calendar currentDate)
			throws LicenseExpiredException {
		CheckUtils.isNotNull(currentDate);
		
		if (this.getLicenseType().equals(LicenseType.TRIAL)) {
			if (this.getExpiration() == null
					|| currentDate.after(this.getExpiration())) {
				throw new LicenseExpiredException();
			}
		}
	}
	
	protected void validateVersion(String currentVersion)
			throws LicenseVersionExpiredException {
		CheckUtils.isNotNull(currentVersion);
		
		if (this.getLicenseType().equals(LicenseType.SINGLE_VERSION)) {
			if (this.getVersion() == null) {
				throw new LicenseVersionExpiredException();
			}
			
			if (!currentVersion.startsWith(this.getVersion())) {
				throw new LicenseVersionExpiredException();
			}
		}
	}
	
	public String licenseToString() throws Exception {
		PropertyMap p = new PropertyMap();
		p.setStringProperty("name", this.name);
		p.setStringProperty("email", this.email);
		p.setEnumProperty("licenseType", LicenseType.class, this.licenseType);
		p.setStringProperty("version", this.version);
		p.setCalendarProperty("expiration", this.expiration);
		p.setStringProperty("reference", this.reference);
		
		StringWriter writer = new StringWriter();
		p.store(writer, null);
		
		return writer.toString();
	}
	
	public static License parseLicense(String license) throws Exception {
		PropertyMap p = new PropertyMap();
		p.load(IOUtils.toInputStream(license));
		
		License l = new License();
		l.setName(p.getStringProperty("name"));
		l.setEmail(p.getStringProperty("email"));
		l.setLicenseType(p.getEnumProperty("licenseType", LicenseType.class));
		l.setVersion(p.getStringProperty("version"));
		l.setExpiration(p.getCalendarProperty("expiration"));
		l.setReference(p.getStringProperty("reference"));
		
		return l;
	}
	
}
