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
package com.leclercb.taskunifier.gui.components.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.Calendar;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.leclercb.commons.api.license.License;
import com.leclercb.commons.api.license.LicenseManager;
import com.leclercb.commons.api.license.exceptions.LicenseException;
import com.leclercb.commons.api.license.exceptions.NoLicenseException;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.resources.Resources;

public final class LicenseUtils {
	
	private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCY"
			+ "kSwG7upH9Y1Mq155AAHIQy+rGMrLs614j4cs6A/m"
			+ "kBRYm7sdumhE16hwsaWF5RXPN/2tesM/bAlr3Y4z"
			+ "QwSD5TIFp5XwsEed9XKREDdiy6FhbkHgvv0OY7U1"
			+ "ueWj+kkbI7WvLj2z41u+/uH9lU4WIQOifBh0fP0+"
			+ "WpZqcw7QXwIDAQAB";
	
	private static License LICENSE;
	
	private LicenseUtils() {
		
	}
	
	public static License loadLicense() {
		if (LICENSE != null)
			return LICENSE;
		
		try {
			File file = new File(Main.getLicenseFile());
			String license = FileUtils.readFileToString(file, "UTF-8");
			
			InputStream publicKey = LicenseManager.keyDecoder(PUBLIC_KEY);
			LicenseManager lm = new LicenseManager(publicKey, null);
			License l = lm.readLicense(license);
			
			return l;
		} catch (FileNotFoundException e) {
			
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Cannot load license", e);
		}
		
		return null;
	}
	
	public static void saveLicense(String license) throws Exception {
		try {
			CheckUtils.isNotNull(license);
			
			license = license.trim();
			
			File file = new File(Main.getLicenseFile());
			FileUtils.writeStringToFile(file, license);
			
			LICENSE = null;
			
			Main.reloadProVersion();
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Cannot save license", e);
		}
	}
	
	public static void checkLicense() throws LicenseException {
		License license = loadLicense();
		
		checkLicense(license);
	}
	
	public static void checkLicense(License license) throws LicenseException {
		if (license == null)
			throw new NoLicenseException();
		
		try {
			license.validate(Calendar.getInstance(), Constants.VERSION);
		} catch (LicenseException e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"License validation failed",
					e);
			
			throw e;
		}
	}
	
	public static boolean isFirstTrialLicense() {
		// TODO: multiple trials not allowed
		// Sign message in settings ?
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		boolean generateKey = true;
		boolean encodeKey = true;
		
		if (generateKey) {
			KeyPair keyPair = LicenseManager.generateKeys("RSA", 2048);
			
			OutputStream os = null;
			
			os = new FileOutputStream(
					"src\\com\\leclercb\\taskunifier\\gui\\resources\\public_key");
			IOUtils.write(keyPair.getPublic().getEncoded(), os);
			
			os = new FileOutputStream("private_key");
			IOUtils.write(keyPair.getPrivate().getEncoded(), os);
		}
		
		if (encodeKey) {
			InputStream publicKey = Resources.class.getResourceAsStream("public_key");
			System.out.println(LicenseManager.keyEncoder(publicKey));
		}
	}
	
}
