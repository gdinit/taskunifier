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
package com.leclercb.taskunifier.gui.components.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.leclercb.commons.api.license.License;
import com.leclercb.commons.api.license.LicenseManager;
import com.leclercb.commons.api.license.LicenseType;
import com.leclercb.commons.api.license.exceptions.LicenseException;
import com.leclercb.commons.api.license.exceptions.NoLicenseException;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;

public final class LicenseUtils {
	
	private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC"
			+ "AQEAhohNXK6blnWsh24Mc+RrehiTlIWLHZNV5sWx"
			+ "6h2TlPK6uufHJpx9SrQbln8CSJzQ8c1UayXTL8KH"
			+ "LwJ743BGTqsWXUXYiGYqjAQL/KE03UrebRifuVL3"
			+ "Fada+hmzF8NYMfKKDUqv++MA0zknzMywGk2bB0gx"
			+ "4l2oO7g5psBc1XjWPaE0LV3b0hbKTCUxHqLTqBwN"
			+ "i39hIfKZ+gTlzRriUZLWc/AUmbRTvFzyA9EYHOWP"
			+ "rTprRRcoOcvsnkSNODQFcpoiIV4EvK03JuIBGK+S"
			+ "sS/yZrLtaWrV5HUzzkdKWqjES3iU3xnsGGh+Z5XD"
			+ "dF2GXHHvbQKNLEF/C1TVs4RusQIDAQAB";
	
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
			
			if (l.getLicenseType() == LicenseType.TRIAL)
				Main.getSettings().setBooleanProperty(
						"license.trial.used",
						true);
			
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
			license.validate(Calendar.getInstance(), Constants.getVersion());
		} catch (LicenseException e) {
			GuiLogger.getLogger().log(
					Level.SEVERE,
					"License validation failed",
					e);
			
			throw e;
		}
	}
	
	public static boolean isFirstTrialLicense() {
		return !Main.getSettings().getBooleanProperty("license.trial.used");
	}
	
}
