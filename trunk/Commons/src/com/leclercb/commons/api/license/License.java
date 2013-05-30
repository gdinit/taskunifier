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
	
	private static LicenseValidator LICENSE_VALIDATOR;
	
	public static License parseLicense(String license) throws Exception {
		PropertyMap p = new PropertyMap();
		p.load(IOUtils.toInputStream(license));
		
		License l = new License();
		l.setProperties(p);
		
		return l;
	}
	
	public static void setLicenseValidator(LicenseValidator validator) {
		LICENSE_VALIDATOR = validator;
	}
	
	private PropertyMap properties;
	
	public License() {
		this(LicenseType.TRIAL);
	}
	
	public License(LicenseType licenseType) {
		this.setProperties(new PropertyMap());
		
		this.setLicenseType(licenseType);
	}
	
	public String getFirstName() {
		return this.properties.getStringProperty("first_name", null);
	}
	
	public void setFirstName(String firstName) {
		this.properties.setStringProperty("first_name", firstName);
	}
	
	public String getLastName() {
		return this.properties.getStringProperty("last_name", null);
	}
	
	public void setLastName(String lastName) {
		this.properties.setStringProperty("last_name", lastName);
	}
	
	public String getEmail() {
		return this.properties.getStringProperty("email", null);
	}
	
	public void setEmail(String email) {
		this.properties.setStringProperty("email", email);
	}
	
	public Calendar getPurchaseDate() {
		return this.properties.getCalendarProperty("purchase_date", null);
	}
	
	public void setPurchaseDate(Calendar purchaseDate) {
		this.properties.setCalendarProperty("purchase_date", purchaseDate);
	}
	
	public LicenseType getLicenseType() {
		return this.properties.getEnumProperty(
				"license_type",
				LicenseType.class,
				null);
	}
	
	public void setLicenseType(LicenseType licenseType) {
		CheckUtils.isNotNull(licenseType);
		this.properties.setEnumProperty(
				"license_type",
				LicenseType.class,
				licenseType);
	}
	
	public String getVersion() {
		return this.properties.getStringProperty("version", null);
	}
	
	public void setVersion(String version) {
		this.properties.setStringProperty("version", version);
	}
	
	public Calendar getExpiration() {
		return this.properties.getCalendarProperty("expiration", null);
	}
	
	public void setExpiration(Calendar expiration) {
		this.properties.setCalendarProperty("expiration", expiration);
	}
	
	public String getReference() {
		return this.properties.getStringProperty("reference", null);
	}
	
	public void setReference(String reference) {
		this.properties.setStringProperty("reference", reference);
	}
	
	public String getProperty(String key) {
		return this.properties.getStringProperty(key, null);
	}
	
	public void setProperty(String key, String value) {
		this.properties.setStringProperty(key, value);
	}
	
	private void setProperties(PropertyMap properties) {
		this.properties = properties;
	}
	
	public void validate(Calendar currentDate, String currentVersion)
			throws LicenseException {
		this.validateExpiration(currentDate);
		this.validateVersion(currentVersion);
		
		if (LICENSE_VALIDATOR != null)
			LICENSE_VALIDATOR.validate(this);
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
		StringWriter writer = new StringWriter();
		this.properties.store(writer, null);
		
		return writer.toString();
	}
	
}
