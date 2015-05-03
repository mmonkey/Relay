package com.github.mmonkey.Relay.Utilities;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptionUtil {

	private SecretKey encryptionKey;
	
	public static String generateSecretKey() throws NoSuchAlgorithmException {
		SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
		return Base64.encodeBase64String(secretKey.getEncoded());
	}
	
	public SecretKey parseSecretKey(String encodedKey) {
		byte[] decodedKey = Base64.decodeBase64(encodedKey);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
	}

	public EncryptionUtil(String encryptionKey) {
		this.encryptionKey = parseSecretKey(encryptionKey);
	}

	public String encrypt(String plainText) throws Exception {
		
		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

		return Base64.encodeBase64String(encryptedBytes);
		
	}

	public String decrypt(String encrypted) throws Exception {
        
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
		byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

		return new String(plainBytes);
        
    }

	private Cipher getCipher(int cipherMode) throws Exception {
       
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(cipherMode, encryptionKey);

		return cipher;
		
	}
    
}
