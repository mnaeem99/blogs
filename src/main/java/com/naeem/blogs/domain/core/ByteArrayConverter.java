package com.naeem.blogs.domain.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class ByteArrayConverter implements AttributeConverter<Object, byte[]> {
	@Override
	public byte[] convertToDatabaseColumn(Object obj){
		try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
			try(ObjectOutputStream o = new ObjectOutputStream(b)){
				o.writeObject(obj);
			} catch (IOException e) {
				return new byte[]{};
			}
			return b.toByteArray();
		} catch (IOException e1) {
			return new byte[]{};
		}
	}

	@Override
	public Object convertToEntityAttribute(byte[] bytes){
		try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
			try(ObjectInputStream o = new ObjectInputStream(b)){
				return o.readObject();
			} catch (IOException | ClassNotFoundException e) {
				return new Object[]{};
				
			}
		} catch (IOException e1) {
			    return new Object[]{};

		}

	}
}
