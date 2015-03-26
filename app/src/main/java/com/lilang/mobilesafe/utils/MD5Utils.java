package com.lilang.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 朗 on 2015/3/23.
 */
public class MD5Utils {

    /**
     * md5加密方法
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5Password(String password)  {
        //得到一个信息摘要器
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            //把每一个byte做一个与运算0xff
            for(byte b : result){
                //与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if(str.length() == 1){
                    buffer.append(0);
                }
                buffer.append(str);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
}
