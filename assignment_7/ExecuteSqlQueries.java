import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;

import java.io.File;
import java.nio.file.Files;

import java.util.Properties;
import java.util.Objects;

/* non-needed packages */
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.util.logging.Logger;
// import java.util.logging.Level;

/* do not import com.mysql.jdbc.* or you will have problems */

public class ExecuteSqlQueries {

	private static String sid = null;
	private static String stcode1 = null;
	private static String stcode2 = null;
	private static String distance = null;
	private static String database = null;
	private static String query = null;
	private static int probStat = 0;

	private static Connection conn = null;
	private static String url = null;

	private static byte[] bufferPass = new byte[16];
	private static byte[] bufferKey = new byte[16];

	private static File filePass = new File("password");
	private static File fileKey = new File("key");
	/* unused code */
	// FileInputStream fis = null;

	private static String password = null;
		
	private static Properties prop = new Properties();

	private static Statement stmt = null;
	private static ResultSet resultSet = null;
	private static ResultSetMetaData rsmd = null;

	private static void validateArgs(String[] args) {
		if (args.length < 1) {
			System.out.println("Invalid number of command line arguments provided");
			System.exit(0);
		}
		else {
			if (Objects.equals(args[0], "1")) {
				if (args.length != 2) {
					System.out.println("Invalid number of command line arguments provided");
					System.exit(0);
				}
				else {
					sid = args[1];
					if (sid.length() < 1 || sid.length() > 5) {
						System.out.println("Invalid format of student ID");
						System.exit(0);
					}
					try {
						int chk = Integer.parseInt(sid);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid format of student ID");
						System.exit(0);
					}
					probStat = 1;
				}
			}
			else if (Objects.equals(args[0], "2")) {
				if (args.length != 4) {
					System.out.println("Invalid number of command line arguments provided");
					System.exit(0);
				}
				else {
					stcode1 = args[1];
					stcode2 = args[2];
					distance = args[3];
					if ((stcode1.length() < 1 || stcode1.length() > 5) || (stcode2.length() < 1 || stcode2.length() > 5) 
							|| (distance.length() < 1 || distance.length() > 11)) {
						System.out.println("Invalid format of the data input provided");
						System.exit(0);
					}
					try {
						int chk = Integer.parseInt(distance);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid format of distance provided");
					}
					probStat = 2;
				}
			}
			else if (Objects.equals(args[0], "3")) {
				if (args.length != 3) {
					System.out.println("Invalid number of command line arguments provided");
					System.exit(0);
				}
				else {
					database = args[1];
					System.out.println(database);

					/*
					 * When entering string query in command line, enter all
					 * special characters ('?', '*', etc.) with a leading backlash '\'
					 */
					query = args[2].replace("?", " ");
					probStat = 3;
				}
			}
			else {
				System.out.println("Invalid option for problem statement provided");
				System.exit(0);
			}
		}
	}

	private static void setUrl() {
		if (probStat == 1) {
			url = "jdbc:mysql://localhost:3306/university";
		}
		else if (probStat == 2) {
			url = "jdbc:mysql://localhost:3306/railway";
		}
		else {
			url = "jdbc:mysql://localhost:3306/" + database;
		}
	}

	private static void readPassword() {
		try {
			/* unused code */
			// fis = new FileInputStream("password");
			// fis.read(buffer);
			bufferPass = Files.readAllBytes(filePass.toPath());
			bufferKey = Files.readAllBytes(fileKey.toPath());

		} catch (
				/* unused code */
				// FileNotFoundException fnfe
				Exception e) {
			System.out.println("File not found" + e);
		}
		/* unused code */
		/*
		finally {
			try {
				if (fis != null) {
					fis.close();
				}

			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
		*/
	}

	private static void decryptPassword() {
		/*
		 * Using DES (Data Encryption Standard) to decrypt the encrypted
		 * password stored in a password file using a unique key generated
		 * stored in the key file
		 * The encrypted password and key files are generated using DES itself 
		 * by the running the EncryptPassword class file
		 * This password is used to access the MySQL databases using a valid
		 * user
		 */
		try {
			SecretKey myDesKey = new SecretKeySpec(bufferKey, 0, bufferKey.length, "DES");

			Cipher desCipher = Cipher.getInstance("DES");
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
			byte[] textDecrypted = desCipher.doFinal(bufferPass);
			password = new String(textDecrypted);
		} catch (BadPaddingException bpe) {
			System.out.println("Wrong key: " + bpe);
		} catch (Exception e) {
			System.out.println("Exception hit while decrypting: " + e);
		}
	}

	private static void setProperties() {
		prop.setProperty("user", "niramay");
		prop.setProperty("password", password);

		/*
		 * establishing SSL connection without server's identity verification
		 * is not recommended warning occurs
		 * Explicitly disable SSL by setting useSSL=false to suppress warning
		 */
		prop.setProperty("useSSL", "false");

		/* unused code */
		// prop.setProperty("autoReconnect", "true");
	}

	private static void executeQueries() {
		try {
			conn = DriverManager.getConnection(url, prop);
			
			/* Do something with the connection */
			
			/* unused code */
			/*
			if (conn != null) {
				System.out.println("Succesfully connected to the MySQL database university");
			}
			*/

			stmt = conn.createStatement();
			if (probStat == 1) {
				resultSet = stmt.executeQuery("select title from course inner join takes on course.course_id = takes.course_id where ID = " + sid);
			}
			else if (probStat == 2) {
				resultSet = stmt.executeQuery("select * from track where stcode1 = \"" + stcode1 + "\" and stcode2 = \"" + stcode2 + "\"");
			}
			else {
				resultSet = stmt.executeQuery(query);
			}

			rsmd = resultSet.getMetaData();
			/* unused code */
			// int row_count = rs.getRow();
			int colCount = rsmd.getColumnCount();
			boolean empty = true;

			if (resultSet.next()) {
				empty = false;
				if (probStat == 2) {
					stmt.executeUpdate("update track set distance = " + distance + " where stcode1 = \"" + stcode1 + "\" and stcode2 = \"" + stcode2 + "\"");
				}
				else {
					int i;
					System.out.print("| ");
					for (i = 1; i <= colCount; i++) {
						System.out.print(rsmd.getColumnName(i).toUpperCase() + " | ");
					}
					do {
						System.out.println("");
						System.out.print("| ");
						for (i = 1; i <= colCount; i++) {
							String columnValue = resultSet.getString(i);
							System.out.print(columnValue + " | ");
						}
					} while (resultSet.next());
					System.out.println("");
				}
			}

			if (empty) {
				if (probStat == 2) {
					stmt.executeUpdate("insert into track values (\"" + stcode1 + "\", \"" + stcode2 + "\", " + distance + ")");
				}
				else {
					System.out.println("The database contains no records for the specified Student ID");
				}
			}

		} catch (SQLException sqle) {
			/* handle any errors */

			/* unused code */
			/*
			System.out.println("An error occurred while connecting to the MySQL database university");
			ex.printStackTrace();
			*/

			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("SQLState: " + sqle.getSQLState());
			System.out.println("VendorError: " + sqle.getErrorCode());
		} finally {
			try {
				conn.close();
			} catch (Throwable te) {
				/* unused code */
				// Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
				// logger.warn("Could not close JDBC connection", e);
				// logger.info("Could not close JDBC connection");

				System.out.println("Could not close JDBC connection");
			}
		}
	}

	public static void main(String[] args) {

		/*
		 * JDBC 4.0 has autoloading of JDBC driver class
		 * if mysql java connector 5.1.36 is in classpath, below code is not a
		 * requirement, since this connector is JDBC 4.0 compliant
		 * mysql java connector 5.0.8 is not JDBC 4.0 compliant, in this case
		 * below code is a requirement
		 * view .bashrc for classpath env var
		 */

		/* The newInstance() call is a work around for some broken Java implementations */

		/*
		try {
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
			// handle the error
		}
		*/

		validateArgs(args);

		setUrl();

		readPassword();

		decryptPassword();

		setProperties();

		executeQueries();
    }
}
