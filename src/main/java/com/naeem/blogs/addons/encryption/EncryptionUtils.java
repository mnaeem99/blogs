package com.naeem.blogs.addons.encryption;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;


/**
 *
 * 
 * This class is used to detect the created annotation 
 *
 */
public abstract class EncryptionUtils {
    public static boolean isFieldEncrypted(Field field) {
        return AnnotationUtils.findAnnotation(field, encryptMe.class) != null;
    }
 
    public static int getPropertyIndex(String name, String[] properties) {
        for (int i = 0; i < properties.length; i++) {
            if (name.equals(properties[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("No property was found for name " + name);
    }
}
