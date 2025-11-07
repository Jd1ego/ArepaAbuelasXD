package com.example.ArepaAbuelas.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesUtil {
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error", e);
        }
    }

    public static String decrypt(String encryptedData, String key) {
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] original = cipher.doFinal(decoded);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }
}
