package com.example.prashantgajera.tallyerpsystem.CommenUtilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Prashant Gajera on 09-Feb-19.
 */

public class AESEncryption {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String email = firebaseUser.getEmail();


    public byte[] generateAesSecretKey(){

        String SALT2 = "mysaltvalue";
        String username = email;
        String password = "gujinfotech@12345";
        byte[] key = (SALT2 + username + password).getBytes();
        SecretKey secretKeySpec = null;

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public byte[] encodeFile(byte[] secretKey, byte[] fileData) {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(fileData);
            //System.out.println("encrypted "+encrypted);
            // Now write your logic to save encrypted data to sdcard here
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return encrypted;
    }

    public byte[] decodeFile(byte[] key, byte[] fileData) {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] decrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            decrypted = cipher.doFinal(fileData);
            System.out.println(decrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(Exception e){
            // for all other exception
            e.printStackTrace();
        }
        return decrypted;
    }


   /* public static void main(String args[])
    {
        String fileName = "C:/xampp/htdocs/gujinfotech/test.json";
        Path path = Paths.get(fileName);
        byte[] bytes=null;
        try{
            bytes = Files.readAllBytes(path);
        }
        catch(Exception e) {}

        byte[] encrypted = null;
        byte[] key = null;
        byte[] decrypted = null;
        //key = generateAesSecretKey();
        System.out.println(key);
        //encrypted=encodeFile(key,bytes);
        //decrypted = decodeFile(key,encrypted);
        System.out.println("dec "+decrypted);
        String str = new String(decrypted);
        System.out.println(str);

    }*/
}
