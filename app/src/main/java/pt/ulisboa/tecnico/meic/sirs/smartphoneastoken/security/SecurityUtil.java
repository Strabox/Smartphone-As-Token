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

    public static int NONCE_BYTES_SIZE = 6;

    public static String AES = "AES";

    public static String SHA256 = "SHA-256";

    public static String UTF8 = "UTF-8";

    public static byte[] generateRandomAESKey() throws GeneralSecurityException, IOException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(128);
        Key key = keyGen.generateKey();
        byte[] encoded = key.getEncoded();
        return encoded;
    }

    public static byte[] encrypt(byte[] plain,SecretKey key) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] encrypted = cipher.doFinal(plain);
        return encrypted;
    }

    public static byte[] decrypt(byte[] encrypted,Key aesKey) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
        Cipher cipher = Cipher.getInstance(AES);
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

    public static SecretKey getAesKeyFromBytes(byte [] key) throws GeneralSecurityException,
            IOException {
        return new SecretKeySpec(key, 0, 16, AES);
    }

    public static byte[] Hash(String input) throws NoSuchAlgorithmException,
            UnsupportedEncodingException{
        MessageDigest digest = MessageDigest.getInstance(SHA256);
        return digest.digest(input.getBytes(UTF8));
    }

    public static byte[] nonceTransformation(byte[] bytes){
        byte[] res = new byte[bytes.length];
        for(int i = 0; i < bytes.length;i++){
            res[i] = (byte) (bytes[i] +  1);
        }
        return res;
    }

    public static byte[] base64ToByte(String base64){
        return Base64.decode(base64,Base64.DEFAULT);
    }

    public static String byteToBase64(byte[] bytes){
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }



}
