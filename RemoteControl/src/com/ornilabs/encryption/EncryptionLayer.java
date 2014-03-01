package com.ornilabs.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.util.Base64;

public class EncryptionLayer {
	private KeyPair kp;

	public EncryptionLayer() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			kp = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String getPublicKey() {
		if(kp==null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
			baos.write( kp.getPublic().getEncoded());
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
	}
	
	public String decodeString(String input) {
		try {
			return new String(decrypt(Base64.decode(input, Base64.DEFAULT), kp.getPrivate()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String encodeString(String input, String key) {
		byte[] keyBytes = Base64.decode(key.getBytes(), Base64.DEFAULT);
	    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
	    
	    RSAPublicKey pubKey = null;
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			pubKey = (RSAPublicKey)factory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    if(pubKey==null) return null;
	    
		try {
			return new String(Base64.encode(encrypt(input.getBytes(), pubKey), Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] encrypt(byte[] inpBytes, PublicKey key)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}

	private byte[] decrypt(byte[] inpBytes, PrivateKey key)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}
	
	
}
