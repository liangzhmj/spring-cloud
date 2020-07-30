package com.liangzhmj.cat.tools.security;

import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

@Log4j2
public class AESUtil {
  public static final AESUtil instance = new AESUtil();

  public static boolean initialized = false;
 
  /**
   * AES解密
   * @param content 密文
   * @return
   * @throws InvalidAlgorithmParameterException
   * @throws NoSuchProviderException
   */
  public static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
	Security.addProvider(new BouncyCastleProvider());
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
      Key sKeySpec = new SecretKeySpec(keyByte, "AES");
      AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
      params.init(new IvParameterSpec(ivByte));
      cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);// 初始化
      byte[] result = cipher.doFinal(content);
      return result;
    } catch (Exception e) {
    	log.error(e.getMessage());
    }
    return null;
  }
  
}

