package com.maibach.keith.cryptomessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Created by bmaib_000 on 2016-08-15. Test Encryptor.java.
 */
public class EncryptorTest {
    private static Encryptor mEncryptor = null;
    private static File toEncryptFile;
    private static File toDecryptFile;
    private static File resultFile;
    private static File publicKeyFile;
    private static File privateKeyFile;
    private static File aesKeyEncryptedFile;
    private static File secondResultFile;


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

        toEncryptFile = new File("test/EncryptorTest/toEncrypt.txt");
        toDecryptFile = new File("test/EncryptorTest/toDecrypt.txt");
        resultFile = new File("test/EncryptorTest/result.txt");
        publicKeyFile = new File("test/EncryptorTest/public.der");
        privateKeyFile = new File("test/EncryptorTest/private.der");
        aesKeyEncryptedFile = new File("test/EncryptorTest/aesKeyEncrypted.txt");
        secondResultFile = new File("test/EncryptorTest/secondResult.txt");

        Assert.assertTrue(publicKeyFile.exists());
        Assert.assertTrue(privateKeyFile.exists());

        try{
            PrintWriter writer = new PrintWriter(toEncryptFile);//, "ASCII");
            writer.write("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            writer.close();
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void EncryptDecryptReloadRecheck()
    {
        Assert.assertTrue(toEncryptFile.canRead());
        try {
            mEncryptor.makeKey();
            Assert.assertTrue("key creation failed", mEncryptor.isInitialized());

            mEncryptor.encrypt(toEncryptFile, toDecryptFile);
            String startedWith = new Scanner(toEncryptFile).toString();
            mEncryptor.decrypt(toDecryptFile, resultFile);
            String endedWith = new Scanner(resultFile).toString();
            Assert.assertEquals("String corrupted by encryption/decryption", startedWith, endedWith);

            mEncryptor.saveKey(aesKeyEncryptedFile, publicKeyFile);
            Encryptor newEncryptor = new Encryptor();
            newEncryptor.loadKey(aesKeyEncryptedFile, privateKeyFile);

            mEncryptor.decrypt(toDecryptFile, secondResultFile);
            String firstResult = new Scanner(resultFile).toString();
            String secondResult = new Scanner(secondResultFile).toString();
        Assert.assertEquals("Re-loaded aes key yields different decryption result", firstResult, secondResult);
        } catch (IOException | GeneralSecurityException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

    }
}
