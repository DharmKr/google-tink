package com.googletink.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TinkService {

	public KeysetHandle generateKeysetHandle() throws GeneralSecurityException, IOException {
		AeadConfig.register();
		KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));
		System.out.println("keysetHandle: "+keysetHandle);
		String keysetFilename = "my_keyset.json";
		CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(keysetFilename)));
		return keysetHandle;
	}

	public KeysetHandle loadKeysetHandle() throws GeneralSecurityException, IOException {
		String keysetFilename = "my_keyset.json";
		KeysetHandle keysetHandle = null;
		try {
			keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(keysetFilename)));
		} catch (GeneralSecurityException | IOException e) {
			log.error(String.format("Error occured: %s",e.getMessage()));
		}		
		if(keysetHandle==null) {
			keysetHandle = generateKeysetHandle();
		}
		return keysetHandle;
	}

	public String encrypt(String plaintext) throws GeneralSecurityException, IOException {
		Aead aead = getAead();
		String associatedData = "Tink";
		byte[] cipherText = aead.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), associatedData.getBytes());
		return Base64.getUrlEncoder().encodeToString(cipherText);
	}

	public String decrypt(String text) throws GeneralSecurityException, IOException{
		Aead aead = getAead();
		byte[] cipherText = Base64.getUrlDecoder().decode(text);
		String associatedData = "Tink";
		
		byte[] decipheredData = aead.decrypt(cipherText, associatedData.getBytes());
		return new String(decipheredData, StandardCharsets.UTF_8);
	}

	private Aead getAead() throws GeneralSecurityException, IOException {
		AeadConfig.register();
		KeysetHandle keysetHandle = loadKeysetHandle();
		return keysetHandle.getPrimitive(Aead.class);
	}
}
