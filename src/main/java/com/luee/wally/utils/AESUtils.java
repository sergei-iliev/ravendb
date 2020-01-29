package com.luee.wally.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.luee.wally.constants.Constants;
import com.luee.wally.exception.AESSecurityException;

public class AESUtils {
	
	public static String encrypt(String toEncrypt) throws AESSecurityException {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeySpec secretKey = getKey();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

			return Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes("UTF-8")));

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
				| InvalidKeyException | UnsupportedEncodingException | BadPaddingException
				| IllegalBlockSizeException e) {
			throw new AESSecurityException(e);
		}
	}

	public static String decrypt(String toDecrypt) throws AESSecurityException {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeySpec secretKey = getKey();

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(toDecrypt)));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException e) {
			throw new AESSecurityException(e);
		}
	}

	private static SecretKeySpec getKey() throws UnsupportedEncodingException, NoSuchAlgorithmException {

		byte[] pass = Constants.SECRET_AES_KEY.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = sha.digest(pass);
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, "AES");
	}

}
