package com.googletink.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.googletink.service.TinkService;

@RestController
public class TinkController {

	@Autowired
	private TinkService tinkService;

	@GetMapping("/generateKeysetHandle")
	public String generateKeysetHandle() throws GeneralSecurityException, IOException {
		tinkService.generateKeysetHandle();
		return "Keyset handle has been Generated successfully";
	}

	@GetMapping("/loadKeysetHandle")
	public String loadKeysetHandle() throws GeneralSecurityException, IOException {
		tinkService.loadKeysetHandle();
		return "Keyset handle has been loaded successfully";
	}

	@GetMapping("/encrypt")
	public String encrypt(@RequestParam String plainText) throws GeneralSecurityException, IOException {
		return tinkService.encrypt(plainText);
	}
	
	@GetMapping("/decrypt")
	public String decrypt(@RequestParam String text) throws GeneralSecurityException, IOException {
		return tinkService.decrypt(text);
	}
}
