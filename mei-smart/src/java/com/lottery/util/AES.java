package com.lottery.util;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

//import sun.misc.BASE64Decoder;

/**
 * AES的加密和解密
 * @author libo
 */
public class AES {
    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "1234561234567890";  
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    
    /** 
     * aes解密 
     * @param encrypt   内容 
     * @return 
     * @throws Exception 
     */  
    public static String aesDecrypt(String encrypt) {  
        try {
            return aesDecrypt(encrypt, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }  
    }  
      
    /** 
     * aes加密 
     * @param content 
     * @return 
     * @throws Exception 
     */  
    public static String aesEncrypt(String content) {  
        try {
            return aesEncrypt(content, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }  
    }  
  
    /** 
     * 将byte[]转为各种进制的字符串 
     * @param bytes byte[] 
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制 
     * @return 转换后的字符串 
     */  
    public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    }  
  
    /** 
     * base 64 encode 
     * @param bytes 待编码的byte[] 
     * @return 编码后的base 64 code 
     */  
    public static String base64Encode(byte[] bytes){  
        return new String(Base64.encodeBase64(bytes));  
    }  
  
    /** 
     * base 64 decode 
     * @param base64Code 待解码的base 64 code 
     * @return 解码后的byte[] 
     * @throws Exception 
     */  
    public static byte[] base64Decode(String base64Code) throws Exception{  
        //return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);  
    	return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code.getBytes()); 
    }  
  
      
    /** 
     * AES加密 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的byte[] 
     * @throws Exception 
     */  
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));  
  
        return cipher.doFinal(content.getBytes("utf-8"));  
    }  
  
  
    /** 
     * AES加密为base 64 code 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的base 64 code 
     * @throws Exception 
     */  
    public static String aesEncrypt(String content, String encryptKey) throws Exception {  
        return base64Encode(aesEncryptToBytes(content, encryptKey));  
    }  
  
    /** 
     * AES解密 
     * @param encryptBytes 待解密的byte[] 
     * @param decryptKey 解密密钥 
     * @return 解密后的String 
     * @throws Exception 
     */  
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);  
        //return new String(decryptBytes);  
        return new String(decryptBytes,"utf-8"); 
    }  
  
  
    /** 
     * 将base 64 code AES解密 
     * @param encryptStr 待解密的base 64 code 
     * @param decryptKey 解密密钥 
     * @return 解密后的string 
     * @throws Exception 
     */  
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {  
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);  
    }  
    
    
    public static String complementKey(String keyStr,int len){
    	int keyLen= keyStr.length();
    	if(keyLen<len){
    		for(int i=0;i<len-keyLen;i++){
    			keyStr +=" ";
    		}
    	}
    	return keyStr;
    }
    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {  
        String content = "{\"1data\":[{\"username\":\"121212\",\"corp\":\"分别是两个springboot搭建的独立服务端\",\"contact\":\"男\",\"addr\":\"�?\",\"mobile\":\"\",\"telephone\":\"\",\"postcode\":\"\",\"account\":\"\",\"sex\":\"男\"}],\"timestamp\":1557308213848}";  
        System.out.println("加密前：" + content);  
        System.out.println("加密密钥和解密密钥：" + KEY);  
        String encrypt = aesEncrypt(content, KEY);  
        System.out.println("加密后：" + encrypt);  
        String decrypt = aesDecrypt("RIBk6ejJC/UBEc/5FuOcVd97Hl/RHkdnRQytKNkAhmD9QgotT5zUh2JK1L3vSjDqFloTSH/oo9sJyAUuSoF4BwYEXP55+LB/7RHUEkXLaw2x33tsMd7s5q0eXJzvrQxFGUss+Ske5nvXIH9SJ+YpQtqq4TNeoup+JBhTJmZGTLMKTXloQ8czqUBUT9SsoYoyGPBDe1wwYUFw6VWMD/ejOOtb1eHFe3z1NQ4JmsvC/RjEBKmjfbXk6DA8r3VPwVOgU/WUlnA6l73CfLe+aNkRAYesR/GZFD7kvxZ8GGd+2PU=", KEY);  
        System.out.println("解密后：" + decrypt);  
        
     // 使用String的有参构造方法
        String str = new String("hhhh ty智障%shfu摸淑芬十分uif内服NSF黑男");
        // 1.以GBK编码方式获取str的字节数组，再用String有参构造函数构造字符串
        System.out.println(new String(str.getBytes("GBK")));
        // 2.以UTF-8编码方式获取str的字节数组，再以默认编码构造字符串
        System.out.println(new String(str.getBytes("UTF-8")));
        // 2.以UTF-8编码方式获取str的字节数组，再以默认编码构造字符串
        System.out.println(new String(str.getBytes("UNICODE")));
        // 2.以UTF-8编码方式获取str的字节数组，再以默认编码构造字符串
        System.out.println(new String(str.getBytes("UNICODE"),"UTF-8"));
        System.out.println(new String(str.getBytes(),"UTF-8"));
    } 
}