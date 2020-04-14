import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
// import java.io.PrintWriter;

public class EncryptPassword {
	public static void main(String[] args) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
			SecretKey myDesKey = keygen.generateKey();

			Cipher desCipher = Cipher.getInstance("DES");

			byte[] password = "<password>".getBytes("UTF-8");

			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
			byte[] textEncrypted = desCipher.doFinal(password);
			byte[] bkey = myDesKey.getEncoded();
			FileOutputStream fos_pass = null;
			FileOutputStream fos_key = null;

			try {
				// File f = new File("password");
				// f.createNewFile();
				// FileOutputStream fos = new FileOutputStream(f, false);
				fos_pass = new FileOutputStream("password");
				fos_pass.write(textEncrypted, 0, textEncrypted.length);
				fos_key = new FileOutputStream("key");
				fos_key.write(bkey, 0, bkey.length);
			} catch (FileNotFoundException e) {
				System.out.println("File not found" + e);
			} finally {
				try {
					if (fos_pass != null) {
						fos_pass.close();
					}
					if (fos_key != null) {
						fos_key.close();
					}
				} catch (IOException ioe) {
					System.out.println("Error while closing stream: " + ioe);
				}
			}

			/*
			String s = new String(textEncrypted);
			System.out.println(s);
			PrintWriter out = new PrintWriter("password.txt");
			out.println(s);
			*/
		} catch (Exception e) {
			System.out.println("Exception hit while encrypting: " + e);
		}
	}
}
