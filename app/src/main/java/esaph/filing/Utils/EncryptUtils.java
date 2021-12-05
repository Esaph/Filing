package esaph.filing.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils
{
    public SecretKey generateKey(String password)
    {
        return new SecretKeySpec(password.getBytes(), "AES");
    }

    public byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/GCM/NOPADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(secret.getEncoded()));
        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return cipherText;
    }

    public String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/GCM/NOPADDING");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(secret.getEncoded()));
        String decryptString = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        return decryptString;
    }

}