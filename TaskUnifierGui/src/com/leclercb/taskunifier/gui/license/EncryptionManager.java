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
package com.leclercb.taskunifier.gui.license;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.leclercb.commons.api.utils.CheckUtils;

public class EncryptionManager {
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public EncryptionManager(byte[] publicKey, byte[] privateKey)
			throws Exception {
		CheckUtils.isNotNull(publicKey);
		
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey);
		KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
		this.publicKey = publicKeyFactory.generatePublic(publicSpec);
		
		if (privateKey != null) {
			PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(
					privateKey);
			KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
			this.privateKey = privateKeyFactory.generatePrivate(privateSpec);
		}
	}
	
	public boolean verify(byte[] data, byte[] sig) throws Exception {
		Signature rsaSignature = Signature.getInstance("SHA1withRSA");
		rsaSignature.initVerify(this.publicKey);
		rsaSignature.update(data);
		
		return rsaSignature.verify(sig);
	}
	
	public byte[] sign(byte[] data) throws Exception {
		if (this.privateKey == null) {
			throw new UnsupportedOperationException(
					"Private key is not available.");
		}
		
		Signature rsaSignature = Signature.getInstance("SHA1withRSA");
		rsaSignature.initSign(this.privateKey);
		rsaSignature.update(data);
		
		return rsaSignature.sign();
	}
	
}
