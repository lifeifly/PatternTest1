package com.yele.bluetoothlib.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class JavaAES128Encryption {

    private static final String DEFAULT_KEY = "D=onsas,r$#%^&*i";

    private static byte[] getPwd() {
//        byte[] keysBUf = DEFAULT_KEY.getBytes();
        byte[] keysBUf = new byte[]{0x11, 0x3c, (byte) 0xef, (byte) 0x9b, (byte) 0xa5, 0x15, 0x36, (byte) 0x85, (byte) 0xaa, 0x5a, 0x08, 0x1c, 0x33, 0x62, 0x14, (byte) 0x88};
        keysBUf[keysBUf.length - 1] = 0x13;
        return keysBUf;
    }

    /**
     * 通过AES128加密
     * @param content 需要加密的内容
     * @return 返回的数组
     */
    public static byte[] encryptAES128(String content) {
        byte[] result = null;
        byte[] pwd = getPwd();
        try {
            byte[] enCodeFormat = pwd;
            // 创建密匙的加密算法AES,DES,DES3
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            result = cipher.doFinal(byteContent);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将数据根据AES128进行解密
     * @param content 需要解密的数据内容
     * @return 返回解密之后的内容
     */
    public static byte[] decryptAES128(byte[] content) {
        byte[] pwd = getPwd();
        try {
            byte[] enCodeFormat = pwd;
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
//        catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
