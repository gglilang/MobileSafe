package com.lilang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Demo {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        //得到一个信息摘要器
        MessageDigest digest = MessageDigest.getInstance("md5");
        String password = "f4cc399f0effd13c888e310ea2cf5399";
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
        System.out.println(buffer);
    }
}
