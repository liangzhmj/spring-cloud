package com.liangzhmj.cat.tools.serialize;

import com.liangzhmj.cat.tools.string.StringUtils;

import java.io.*;

public class SerializeUtil {
	
	public static byte[] serializeToByte(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			if (object != null) {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				byte[] bytes = baos.toByteArray();
				return bytes;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String serializeToString(Object object) {
		byte[] bytes = serializeToByte(object);
		String str = null;
		if (bytes != null) {
			try {
				str = new String(bytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static Object unserializeFromByte(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			if (bytes != null) {
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				Object obj = ois.readObject();
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bais != null){
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Object unserializeFromString(String str) throws Exception {
		Object object = null;
		if (!StringUtils.isEmpty(str)) {
			byte[] bytes = str.getBytes("utf-8");
			object = unserializeFromByte(bytes);
		}
		return object;
	}

}
