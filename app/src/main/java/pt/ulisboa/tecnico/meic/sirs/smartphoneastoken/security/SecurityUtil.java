package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.security;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by André on 26-11-2015.
 */
public abstract class SecurityUtil {


    public static SecretKey generateRandomAESKey(){
        try{
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            SecretKey key = keygen.generateKey();
            return key;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static Key read(byte [] key) throws GeneralSecurityException, IOException {

        return new SecretKeySpec(key, 0, 16, "AES");
    }

    public static byte[] Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes("UTF-8"));
    }

    public static byte[] encrypt(byte[] plain,Key aesKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,aesKey);
        byte[] encrypted = cipher.doFinal(plain);
        return encrypted;
    }

    public static byte[] decrypt(byte[] encrypted,Key aesKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,aesKey);
        byte[] plain = cipher.doFinal(encrypted);
        return plain;
    }

    public static byte[] generateSecureRandom(int numBytes){
        byte[] random = new byte[numBytes];
        SecureRandom randomGenerator = new SecureRandom();
        randomGenerator.nextBytes(random);
        return random;
    }

    public static byte[] base64ToByte(String base64){
        return Base64.decode(base64,Base64.DEFAULT);
    }

    public static String byteToBase64(byte[] bytes){
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

}
