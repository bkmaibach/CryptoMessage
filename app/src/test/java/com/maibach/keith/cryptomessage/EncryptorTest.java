package com.maibach.keith.cryptomessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
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
    //Key Fingerprint: ssh-rsa 2048 c9:2a:0e:c3:85:1a:6d:1b:5f:b8:59:3d:e7:da:a8:08

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

        toEncryptFile = new File("EncryptorTest_toEncrypt.txt");
        toDecryptFile = new File("EncryptorTest_toDecrypt.txt");
        resultFile = new File("EncryptorTest_result.txt");
        publicKeyFile = new File("public.der");
        privateKeyFile = new File("private.der");
        aesKeyEncryptedFile = new File("EncryptorTest_aesKeyEncrypted");

        try{
            PrintWriter writer = new PrintWriter(toEncryptFile, "UTF-8");
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
        String pk = readFile(publicKeyFile.getAbsolutePath());
        mEncryptor.saveKey(aesKeyEncryptedFile, publicKeyFile);
        String ake = readFile(aesKeyEncryptedFile.getAbsolutePath());

    }

    @Test
    public void file_encryptionCheck()
    {
        Assert.assertTrue(toEncryptFile.canRead());
        try {
            mEncryptor.encrypt(toEncryptFile, toDecryptFile);
        } catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertTrue(toDecryptFile.canRead());
        //TODO: Finish testing encryption
        try {
            mEncryptor.decrypt(toDecryptFile, resultFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String startedWith = readFile(toEncryptFile.getAbsolutePath());
            String endedWith = readFile(resultFile.getAbsolutePath());
            Assert.assertEquals("String corrupted by encryption/decryption", startedWith, endedWith);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            reader.close();
            return stringBuilder.toString();
        }
    }


}
