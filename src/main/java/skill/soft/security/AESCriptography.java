package skill.soft.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.codec.binary.Base64;

/***
 * Clase para encriptar y desencriptar informacion
 * @author vmacas
 *
 */
@ApplicationScoped
public class AESCriptography {

	private static String passphrase;
	private static int pswdIterations;
	private static String salt;
	private static String ALGORITHMKEY;
	private static String ALGORITHM;
	private static String MODE;
	private static String PADDING;
	private static String CIPHER_TRANSFORMATION;
	private static Charset PLAIN_TEXT_ENCODING;
	private static int KEY_SIZE_BITS;
	private static AESCriptography instance = null;
	private static Logger log = Logger.getLogger(AESCriptography.class.getName());

	/***
	 * Inicia la clase con las variables del propertie
	 */
	@PostConstruct
	public void init() {
		try {
			passphrase = getResource("PASSPHRASE");
			pswdIterations = Integer.valueOf(getResource("PSWDITERATIONS"));
			salt = getResource("SALT");
			ALGORITHMKEY = getResource("ALGORITHMKEY");
			ALGORITHM = getResource("ALGORITHM");
			MODE = getResource("MODE");
			PADDING = getResource("PADDING");
			PLAIN_TEXT_ENCODING = Charset.forName(getResource("ENCODING"));
			KEY_SIZE_BITS = Integer.valueOf(getResource("KEY_SIZE_BITS"));
			CIPHER_TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
		} catch (IOException eio) {
			log.info("[Error]: No se puede inicializar Encriptacion " + eio.getMessage() + ", " + eio.getCause());
		}
	}

	public static AESCriptography getInstance() throws IOException {
		synchronized (AESCriptography.class) {
			if (instance == null) {
				instance = new AESCriptography();
				passphrase = getResource("PASSPHRASE");
				pswdIterations = Integer.valueOf(getResource("PSWDITERATIONS"));
				salt = getResource("SALT");
				ALGORITHMKEY = getResource("ALGORITHMKEY");
				ALGORITHM = getResource("ALGORITHM");
				MODE = getResource("MODE");
				PADDING = getResource("PADDING");
				PLAIN_TEXT_ENCODING = Charset.forName(getResource("ENCODING"));
				KEY_SIZE_BITS = Integer.valueOf(getResource("KEY_SIZE_BITS"));
				CIPHER_TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
			}
		}
		return instance;
	}

	/***
	 * Encriptacion de la Informacion
	 * @param dataText
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encrypt(String dataText)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (dataText != null && !dataText.isEmpty()) {
			byte[] saltBytes = generateSalt().getBytes(PLAIN_TEXT_ENCODING);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHMKEY);
			PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), saltBytes, pswdIterations, KEY_SIZE_BITS);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(generateSalt().getBytes(PLAIN_TEXT_ENCODING));
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secret, ivParameterSpec);
			byte[] encryptedTextBytes = cipher.doFinal(dataText.getBytes(PLAIN_TEXT_ENCODING));
			return Base64.encodeBase64String(encryptedTextBytes);
		}
		return new String();
	}

	/***
	 * Encriptacion de la informacion
	 * @param dataText
	 * @param key
	 * @param salto
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encrypt(String dataText, String key, String salto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (dataText != null && !dataText.isEmpty()) {
			byte[] saltBytes = generateSalt(salto).getBytes(PLAIN_TEXT_ENCODING);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHMKEY);
			PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, KEY_SIZE_BITS);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(generateSalt(salto).getBytes(PLAIN_TEXT_ENCODING));
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secret, ivParameterSpec);
			byte[] encryptedTextBytes = cipher.doFinal(dataText.getBytes(PLAIN_TEXT_ENCODING));
			return Base64.encodeBase64String(encryptedTextBytes);
		}
		return new String();
	}

	/***
	 * Desencriptar Informacion
	 * @param encryptedText
	 * @param key
	 * @param salto
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decrypt(String encryptedText, String key, String salto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (encryptedText != null && !encryptedText.isEmpty()) {
			byte[] saltBytes = generateSalt(salto).getBytes(PLAIN_TEXT_ENCODING);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHMKEY);
			PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, KEY_SIZE_BITS);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(generateSalt(salto).getBytes(PLAIN_TEXT_ENCODING));
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secret, ivParameterSpec);
			byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);
			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
		}
		return new String();
	}

	/***
	 * Desencriptar Informacion
	 * @param encryptedText
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decrypt(String encryptedText)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (encryptedText != null && !encryptedText.isEmpty()) {
			byte[] saltBytes = generateSalt().getBytes(PLAIN_TEXT_ENCODING);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHMKEY);
			PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), saltBytes, pswdIterations, KEY_SIZE_BITS);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(generateSalt().getBytes(PLAIN_TEXT_ENCODING));
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secret, ivParameterSpec);
			byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);
			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
		}
		return new String();
	}

	/***
	 * Generacion de saltos 
	 * @return
	 */
	private static String generateSalt() {
		String s = new String(salt);
		return s;
	}

	private static String generateSalt(String salto) {
		String s = new String(salto);
		return s;
	}

	private static String getResource(String pValue) throws IOException {
		Properties properties = new Properties();
		InputStream archivo = AESCriptography.class.getClassLoader().getResourceAsStream("secure.properties");
		properties.load(archivo);
		String path = properties.getProperty(pValue);
		return path;
	}
}
