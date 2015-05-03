package com.github.mmonkey.Relay.Utilities;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
	
	private String encryptionKey;
	
	public EncryptionUtil(String key) {
		this.encryptionKey = key;
	}

	public String encrypt(String plainText) throws GeneralSecurityException, UnsupportedEncodingException {
		
		byte[] raw = encryptionKey.getBytes(Charset.forName("UTF-8"));

		if (raw.length != 16) {
			throw new IllegalArgumentException("Invalid key size.");
		}

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
		
		byte[] encrypted = cipher.doFinal(plainText.getBytes(Charset.forName("UTF-8")));
		return new String(encrypted, Charset.forName("UTF-8"));
		
	}

	public String decrypt(String encryptedText) throws GeneralSecurityException {
		
		byte[] encrypted = encryptedText.getBytes(Charset.forName("UTF-8"));
		byte[] raw = encryptionKey.getBytes(Charset.forName("UTF-8"));

		if (raw.length != 16) {
			throw new IllegalArgumentException("Invalid key size.");
		}
		
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
		
	    byte[] decrypted = cipher.doFinal(encrypted);
	    return new String(decrypted, Charset.forName("UTF-8"));
	    
	}
    
	public static String generateSecretKey() {
		
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < 16; i++) {
			char c = (char) ((int)r.nextInt(128));
			sb.append(c);
		}
		
		return sb.toString();
	
	}
	
}
