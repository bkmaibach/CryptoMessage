package com.maibach.keith.cryptomessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
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
    private static File toEncryptFile;
    private static File toDecryptFile;
    private static File resultFile;
    private static File publicKeyFile;
    private static File privateKeyFile;
    private static File aesKeyEncryptedFile;
    private static File resavedKeyFile;
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
        aesKeyEncryptedFile = new File("test/EncryptorTest/aesKeyEncrypted.txt");
        resavedKeyFile = new File("test/EncryptorTest/resavedKey.txt");
        secondResultFile = new File("test/EncryptorTest/secondResult.txt");

        Assert.assertTrue(toEncryptFile.exists());
        Assert.assertTrue(toEncryptFile.canWrite());

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
    public void key_getsMade() throws Exception
    {
        mEncryptor.makeKey();
        Assert.assertTrue("key creation failed", mEncryptor.isInitialized());
    }

    @Test
    public void encryptionDecryptionCheck()
    {
        Assert.assertTrue(toEncryptFile.canRead());
        try {
            mEncryptor.encrypt(toEncryptFile, toDecryptFile);
            mEncryptor.decrypt(toDecryptFile, resultFile);
        } catch (IOException| InvalidKeyException e){
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        byte[] startedWith = readFile(toEncryptFile);
        byte[] endedWith = readFile(resultFile);
        Assert.assertArrayEquals("String corrupted by encryption/decryption", startedWith, endedWith);
    }

    @Test
    public void reloadKeyCheck() throws Exception
    {
        //TODO: Fix encryption test of aes key - Consider generating keypair using different method??
        mEncryptor.saveKey(aesKeyEncryptedFile, publicKeyFile);
        Encryptor newEncryptor = new Encryptor();
        newEncryptor.loadKey(aesKeyEncryptedFile, privateKeyFile);

        //int origKeySig = mEncryptor.aesKeyHash();
        //int newKeySig = newEncryptor.aesKeyHash();
        //Assert.assertEquals("Reloaded key's hash differs from original's", origKeySig, newKeySig);

        byte[] origKey = mEncryptor.aesKey;
        byte[] newKey = newEncryptor.aesKey;
        Assert.assertArrayEquals("Reloaded key's hash differs from original's", origKey, newKey);

        mEncryptor.decrypt(toDecryptFile, secondResultFile);
        byte[] firstResult = readFile(resultFile);
        byte[] secondResult = readFile(secondResultFile);
        Assert.assertArrayEquals("Re-loaded aes key yields different decryption result", firstResult, secondResult);
    }

    private byte[] readFile(File file) {
        byte[] fileContents = new byte[(int) file.length()];
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            fis.read(fileContents);
            fis.close();
        }catch (IOException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
        return fileContents;
    }
}
