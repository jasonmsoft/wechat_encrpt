package com.jasonmsoft.wechat_encrpt;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * Created by cdmaji1 on 2016/2/4.
 */
public class encrpto_utils {

        private static byte[] iv = {1,2,3,4,5,6,7,8};
        public static String encryptDES(String encryptString, String encryptKey) throws Exception {
//      IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            return base64.encode(encryptedData);
        }

        public static String decryptDES(String decryptString, String decryptKey) throws Exception {
            byte[] byteMi = new base64().decode(decryptString);
            byte[] decryptedData = new byte[0];
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
//      IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            try{
             decryptedData = cipher.doFinal(byteMi);
            }catch (Exception e)
            {

                e.printStackTrace();
                return "";
            }

            return new String(decryptedData);
        }

}
