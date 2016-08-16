package com.maibach.keith.cryptomessage;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import com.maibach.keith.cryptomessage.Encryptor;
import android.util.Log;

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
    private static final String TAG = "EncryptorTest";
    private static Encryptor mEncryptor = null;
    private static File toEncrypt;
    private static File toDecrtpt;
    private static File result;

    @Before
    public void init()
    {
        try {
           mEncryptor = new Encryptor();
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Assert.assertNotNull("mEncryptor failed to initialize", mEncryptor);

        try {
            mEncryptor.makeKey();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        toEncrypt = new File("EncryptorTest_toEncrypt.txt");
        toDecrtpt = new File("EncryptorTest_toDecrtpt.txt");
        result = new File("EncryptorTest_result.txt");
        try{
            PrintWriter writer = new PrintWriter(toEncrypt, "UTF-8");
            writer.write("Test input for Encryptor");
            writer.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Test
    public void key_getsMade() throws Exception
    {
        mEncryptor.makeKey();
        Assert.assertTrue("key creation failed", mEncryptor.isInitialized());
    }

    @Test
    public void file_encryptionCheck()
    {
        Assert.assertTrue(toEncrypt.canRead());

        try {
            mEncryptor.encrypt(toEncrypt, toDecrtpt);
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        //TODO: FInish encryptionCheck
    }

    @Test public void file_decrtptionCheck()
    {
        try {
            mEncryptor.decrypt(toDecrtpt, result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        //TODO: FInish decryptionCheck
    }
}
