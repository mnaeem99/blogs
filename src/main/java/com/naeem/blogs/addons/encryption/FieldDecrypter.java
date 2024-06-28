package com.naeem.blogs.addons.encryption;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 *
 * 
 *         This class get the field data and call the decrypt method to get the
 *         decrypted data and then set it to the same field
 *
 */
@Component
public class FieldDecrypter {
	@Autowired
	private Decrypter decrypter;

	public void decrypt(Object entity) {
		ReflectionUtils.doWithFields(entity.getClass(), field -> decryptField(field, entity),
				EncryptionUtils::isFieldEncrypted);
	}

	private void decryptField(Field field, Object entity) {
		field.setAccessible(true);
		Object value = ReflectionUtils.getField(field, entity);
		if (!(value instanceof String)) {
			throw new IllegalStateException("Encrypted annotation was used on a non-String field");
		}
		ReflectionUtils.setField(field, entity, decrypter.decrypt(value.toString()));
	}
}
