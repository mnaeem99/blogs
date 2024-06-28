
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
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.naeem.blogs.commons.logging.LoggingHelper;

@Converter
public class ConvertEncryptAndDecrypt implements AttributeConverter<String, String> {

	public static String key = "qwertyuiopqwerty";
	public static String initVector = "asdfghjklasdfghj";

	@Autowired
	private LoggingHelper logHelper;

	@Override
	public String convertToDatabaseColumn(String attribute) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secret, iv);
			byte[] encrypted = cipher.doFinal(attribute.getBytes());
			return Base64.encodeBase64String(encrypted);

		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			logHelper.getLogger().error(String.format("error %s", e));
			return " exception >";
		}

	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secret, iv);
			byte[] originalString = cipher.doFinal(Base64.decodeBase64(dbData));
			return new String(originalString);

		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException e) {
			logHelper.getLogger().error(String.format("error %s", e));
			return " exception >";
		}

	}

}

