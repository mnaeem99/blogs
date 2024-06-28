package com.naeem.blogs.addons.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naeem.blogs.commons.logging.LoggingHelper;


/**
 *
 * 
 *         This class is used to decrypt the data provided by the encryptMe
 *         annotation with using AES (Advanced Encryption Standard) This class
 *         has a method name decrypt which takes encrypted string and decrypt it
 *         with the aes key and return the data it uses javax crypto package and
 *         uses AES/CBC/PKCS5PADDING for encryption
 *
 * 
 */
@Component
public class Decrypter {

	public static String key="qwertyuiopqwerty" ;
	public static String initVector="asdfghjklasdfghj";

	@Autowired
	private LoggingHelper logHelper;
	
	/*
	 * it takes encoded string as a parameter 
	 * a secret key for decoding 
	 *
	 */

	public String decrypt(String encodedString) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secret, iv);
			byte[] originalString = cipher.doFinal(Base64.decodeBase64(encodedString));
			return new String(originalString);

		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			logHelper.getLogger().error("error"+e);
			return " exception >";
		}
	}
}
