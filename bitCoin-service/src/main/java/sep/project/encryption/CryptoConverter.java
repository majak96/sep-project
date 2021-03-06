package sep.project.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang.RandomStringUtils;

import sep.project.BitCoinServiceApplication;
import sep.project.model.Crypto;
import sep.project.services.CryptoService;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // mora PKCS5 jer inace Cannot find any provider supporting AES/CBC/PKCS7Padding

	@Override
	public String convertToDatabaseColumn(String something) {
		
		String generatedString = RandomStringUtils.randomAlphanumeric(16);
		byte[] IvParameterVector=generatedString.getBytes();		

		CryptoService cryptoService = SpringContext.getBean(CryptoService.class);

		KeyStore jceks;
		InputStream ins = BitCoinServiceApplication.class.getResourceAsStream("/db.jceks");
		Key key = null;
		
		try {
			jceks = KeyStore.getInstance("JCEKS");
			jceks.load(ins, "password".toCharArray());
			key = jceks.getKey("key", "password".toCharArray());

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableKeyException e) {
			System.out.println("Error while reading key");
		}

		if (something == null) {
			return "";
		}

		// do some encryption
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IvParameterVector));
			Crypto crypto=new Crypto(Base64.getEncoder().encodeToString(c.doFinal(something.getBytes())), generatedString);
			cryptoService.save(crypto);
			return Base64.getEncoder().encodeToString(c.doFinal(something.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		
		CryptoService cryptoService = SpringContext.getBean(CryptoService.class);
		byte[] IvParameterVector=(cryptoService.findByText(dbData).getIv()).getBytes();	
		
		KeyStore jceks;
		InputStream ins = BitCoinServiceApplication.class.getResourceAsStream("/db.jceks");
		Key key = null;

		try {
			jceks = KeyStore.getInstance("JCEKS");
			jceks.load(ins, "password".toCharArray());
			key = jceks.getKey("key", "password".toCharArray());

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableKeyException e) {
			System.out.println("Error while reading key");
		}

		if (dbData.equals("")) {
			return "";
		}
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IvParameterVector));
			return new String(c.doFinal(Base64.getDecoder().decode(dbData)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}