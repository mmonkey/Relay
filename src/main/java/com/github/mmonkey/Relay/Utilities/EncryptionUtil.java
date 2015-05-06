package com.github.mmonkey.Relay.Utilities;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptionUtil {
	
	private String secretKey;
	
	public EncryptionUtil(String secretKey) {
		this.secretKey = secretKey;
	}

	public String encrypt(String plainText) throws GeneralSecurityException, UnsupportedEncodingException {
		
		byte[] text = plainText.getBytes();
		byte[] raw = secretKey.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		
		byte[] encryptedText = cipher.doFinal(text);
		return Base64.encodeBase64String(encryptedText);
		
	}

	public String decrypt(String encryptedText) throws GeneralSecurityException, UnsupportedEncodingException {

		byte[] encryptText = Base64.decodeBase64(encryptedText);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		
	    return new String(cipher.doFinal(encryptText));
	    
	}
    
	public static String generateSecretKey(int length) {
		
		Random rand = new Random();
		String characters = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
		char[] key = new char[length];
		
		for(int i = 0; i < length; i++) {
			key[i] = characters.charAt(rand.nextInt(characters.length()));
		}
		
		return new String(key);
	
	}
	
}
