package com.maibach.keith.cryptomessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bmaib_000 on 2016-08-14.
 */
public class Encryptor {
    private String TAG = "Encryptor";

    /*To use the code, you need corresponding public and private RSA keys. RSA keys can be generated using the open source tool OpenSSL. However, you have to be careful to generate them in the format required by the Java encryption libraries. To generate a private key of length 2048 bits:

openssl genrsa -out private.pem 2048
To get it into the required (PKCS#8, DER) format:

openssl pkcs8 -topk8 -in private.pem -outform DER -out private.der -nocrypt
To generate a public key from the private key:

openssl rsa -in private.pem -pubout -outform DER -out public.der
An example of how to use the code:

FileEncryption secure = new FileEncryption();

// to encrypt a file
secure.makeKey();
secure.saveKey(encryptedKeyFile, publicKeyFile);
secure.encrypt(fileToEncrypt, encryptedFile);

// to decrypt it again
secure.loadKey(encryptedKeyFile, privateKeyFile);
secure.decrypt(encryptedFile, unencryptedFile);
*/

    private Cipher pkCipher;
    private Cipher aesCipher;
    private byte[] aesKey;
    private int AES_Key_Size = 128;
    private SecretKeySpec aeskeySpec;

    public Encryptor() throws GeneralSecurityException
    {//The encryption algorithms are specified in the constructor:
        // create RSA public key cipher
        pkCipher = Cipher.getInstance("RSA");
        // create AES shared key cipher
        aesCipher = Cipher.getInstance("AES");
    }

    public void makeKey() throws NoSuchAlgorithmException
    {// A random AES key is generated to encrypt files. A key size (AES_Key_Size) of 256 bits is standard for AES:
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    public boolean isInitialized()
    {
        return pkCipher != null && aesCipher != null && aesKey != null && aeskeySpec != null;
    }

    public void encrypt(File in, File out) throws IOException, InvalidKeyException
    {//The file encryption and decryption routines then use the AES cipher:
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    public void decrypt(File in, File out) throws IOException, InvalidKeyException
    {//The file encryption and decryption routines then use the AES cipher:
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    private void copy(InputStream is, OutputStream os) throws IOException
    {//
        int i;
        byte[] b = new byte[1024];
        while((i=is.read(b))!=-1) {
            os.write(b, 0, i);
        }
    }

    public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException
    {//So that the files can be decrypted later, the AES key is encrypted to a file using the RSA cipher. The RSA public key is assumed to be stored in a file.
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
        os.write(aesKey);
        os.close();
    }

    public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException
    {//Before decryption can take place, the encrypted AES key must be decrypted using the RSA private key:
        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size/8];
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
        is.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }
}
