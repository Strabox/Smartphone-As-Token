package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.security;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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



}
