package com.naeem.blogs.addons.encryption;
import java.lang.reflect.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 *         This class get the field data and call the encrypt method to get the
 *         encrypted data and then set it to the same field
 */
@Component
public class FieldEncrypter {
	@Autowired
	private Encrypter encrypter;

	public void encrypt(Object[] state, String[] propertyNames, Object entity) {
		ReflectionUtils.doWithFields(entity.getClass(), field -> encryptField(field, state, propertyNames),
				EncryptionUtils::isFieldEncrypted);
	}

	private void encryptField(Field field, Object[] state, String[] propertyNames) {
		int propertyIndex = EncryptionUtils.getPropertyIndex(field.getName(), propertyNames);
		Object currentValue = state[propertyIndex];
		if (!(currentValue instanceof String)) {
			throw new IllegalStateException("Encrypted annotation was used on a non-String field");
		}
		state[propertyIndex] = encrypter.encrypt(currentValue.toString());
	}
}
