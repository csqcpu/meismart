package com.lottery.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;


public class TOKEN{

    
    public static String makeToken()
    { // checkException
        // 7346734837483 834u938493493849384 43434384
        String token = UUID.randomUUID().toString().replaceAll("-", "") + "";
        // 数据指纹 128位长 16个字节 md5
        try
        {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte md5[] = md.digest(token.getBytes());
            return new String(Base64.encodeBase64(md5));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}