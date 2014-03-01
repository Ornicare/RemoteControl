import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import javax.crypto.Cipher;

import com.ornilabs.encryption.EncryptionLayer;


public class Launch {
	public static void main(String[] args) {
		EncryptionLayer eL = new EncryptionLayer();
		String input = "Ceci est un tes de mons de 226 char ^^ @)+fdsfdsfdsds";
//		System.out.println(input.length()-
//				UUID.randomUUID().toString().length()+" "+("antoine@chronophage".length()));
		String publicKey = eL.getPublicKey();
		
		String encoded = eL.encodeString(input, publicKey);
		System.out.println(encoded);
		String decoded = eL.decodeString(encoded);
		
		System.out.println(decoded);
		
		System.out.println("[@]OrniAlivePacket[@]Asdfsdfs:dsfdsfsd".split("\\[@\\]")[2]);
	}
	
//	public static void main(String[] args) throws Exception {
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        kpg.initialize(1024);
//        KeyPair kp = kpg.generateKeyPair();
//        RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
//        RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();
//        String input = "Ceci est un tes de mons de 226 char ^^ @)+fdsfdsfdsds";
//
//        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
//        c.init(Cipher.ENCRYPT_MODE, pub);
//
//        byte [] plain = new byte[100]; // initialize to all zero bytes
//
//        for(int i = 0;i<input.getBytes().length;++i) {
//        	plain[i] = input.getBytes()[i];
//        }
//        // First encrypt: length of input (plain) is 100
//
//        byte [] cipher = c.doFinal(plain);
//
//        System.out.println("length of cipher is " + cipher.length);
//
//        // Now decrypt: length of input(cipher) is 128;
//
//        c.init(Cipher.DECRYPT_MODE, priv);
//
//        byte [] decrypted_cipher = c.doFinal(cipher);
//
//        System.out.println("length of decrypted cipher is " + new String(decrypted_cipher));
//    }
}
