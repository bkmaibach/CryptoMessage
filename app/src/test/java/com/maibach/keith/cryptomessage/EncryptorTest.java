package com.maibach.keith.cryptomessage;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.maibach.keith.cryptomessage.Encryptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import java.io.File;

/**
 * Created by bmaib_000 on 2016-08-15.
 */
public class EncryptorTest {
    private static Encryptor mEncryptor = null;
    private static File toEncrypt;
    private static File toDecrypt;
    private static File result;
    private static File publicKey;
    private static File aesKeyEncrypted;

    @Before
    public void init()
    {
        try {
           mEncryptor = new Encryptor();
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull("mEncryptor failed to initialize", mEncryptor);

        try {
            mEncryptor.makeKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        toEncrypt = new File("EncryptorTest_toEncrypt.txt");
        toDecrypt = new File("EncryptorTest_toDecrypt.txt");
        result = new File("EncryptorTest_result.txt");
        publicKey = new File("EncryptorTest_publicKey");
        aesKeyEncrypted = new File("EncryptorTest_aesKeyEncrypted.txt");
        try{
            PrintWriter writer = new PrintWriter(toEncrypt, "UTF-8");
            writer.write("Test input for Encryptor");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void key_getsMade() throws Exception
    {
        mEncryptor.makeKey();
        Assert.assertTrue("key creation failed", mEncryptor.isInitialized());
    }

    @Test
    public void key_getsEncrypted() throws Exception
    {
        String pk = readFile(publicKey.getAbsolutePath());
        mEncryptor.saveKey(aesKeyEncrypted, publicKey);
        String ake = readFile(aesKeyEncrypted.getAbsolutePath());
        //TODO: Get key encryption to work
    }

    @Test
    public void file_encryptionCheck()
    {
        Assert.assertTrue(toEncrypt.canRead());
        try {
            mEncryptor.encrypt(toEncrypt, toDecrypt);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //@Test
    public void file_decryptionCheck()
    {
        try {
            mEncryptor.decrypt(toDecrypt, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: Finish decryptionCheck
    }

    @After
    public void cleanUp()
    {

    }

    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }


}
