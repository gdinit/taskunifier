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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

public class LicenseManager {
	
	private EncryptionManager encryptionManager;
	
	public LicenseManager(File publicKey, File privateKey) throws Exception {
		this(new FileInputStream(publicKey), new FileInputStream(privateKey));
	}
	
	public LicenseManager(InputStream publicKey, InputStream privateKey)
			throws Exception {
		byte[] pubdata = IOUtils.toByteArray(publicKey);
		byte[] privdata = null;
		
		if (privateKey != null) {
			privdata = IOUtils.toByteArray(privateKey);
		}
		
		this.encryptionManager = new EncryptionManager(pubdata, privdata);
	}
	
	public License readLicense(String input) throws Exception {
		input = input.trim();
		
		Base64 base64 = new Base64(40);
		byte[] data = base64.decode(input);
		
		byte[] signature = ArrayUtils.subarray(data, 0, 128);
		byte[] message = ArrayUtils.subarray(data, 128, data.length);
		
		if (!this.encryptionManager.verify(message, signature)) {
			return null;
		}
		
		return License.parseLicense(new String(message, "UTF-8"));
	}
	
	public void writeLicense(License license, File file) throws Exception {
		byte[] message = license.licenseToString().getBytes("UTF-8");
		byte[] signature = this.encryptionManager.sign(message);
		byte[] data = ArrayUtils.addAll(signature, message);
		
		Base64 base64 = new Base64(40);
		FileUtils.writeByteArrayToFile(file, base64.encode(data));
	}
	
	public static String keyEncoder(InputStream publicKey)
			throws Exception {
		byte[] input = IOUtils.toByteArray(publicKey);
		
		Base64 base64 = new Base64(40);
		byte[] data = base64.encode(input);
		
		return new String(data, "UTF-8");
	}
	
	public static InputStream keyDecoder(String publicKey)
			throws Exception {
		Base64 base64 = new Base64(40);
		byte[] data = base64.decode(publicKey);
		
		return new ByteArrayInputStream(data);
	}
	
}
