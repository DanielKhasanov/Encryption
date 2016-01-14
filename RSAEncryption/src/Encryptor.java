import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Encryptor {
	private static final int PACKETSIZE = 13;
	private static final String TAGLINE = "D.K.K.D Certified ";
	private BigInteger encryptionKey;
	private BigInteger decryptionKey;
	private BigInteger nModulus;
	
	//creates a new Key-Value pair, returns as "%e %d %n"
	public static String newKeyValue(){
		BigInteger p = BigInteger.probablePrime(PACKETSIZE * 8, new Random());
		BigInteger q = BigInteger.probablePrime(PACKETSIZE * 8, new Random());
		BigInteger n = p.multiply(q);
		BigInteger keyGen = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		BigInteger d = BigInteger.probablePrime(PACKETSIZE * 3, new Random());
		BigInteger e = d.modInverse(keyGen);
		
		StringBuilder sb = new StringBuilder();
		sb.append(e);
		sb.append(" ");
		sb.append(d);
		sb.append(" ");
		sb.append(n);
		return sb.toString();
	}	
	
	public Encryptor(BigInteger e, BigInteger d, BigInteger p, BigInteger q) {
		BigInteger modulus = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		if (((e.multiply(d)).mod(modulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
			decryptionKey = d;
			nModulus = p.multiply(q);
		} else {
			System.out.println("The key value pair is not legal");
		}
	}
	
	public Encryptor(String e, String d, String nMod) {
		encryptionKey = new BigInteger(e);
		nModulus = new BigInteger(nMod);
		decryptionKey = new BigInteger(d);
	}
	
	public Encryptor(String e, String nMod) {
		encryptionKey = new BigInteger(e);
		nModulus = new BigInteger(nMod);
	}
	
	public void setParams(String e,  String n) {
		encryptionKey = new BigInteger(e);
		nModulus = new BigInteger(n);	
	}
	
	public void setParams(BigInteger e, BigInteger d, BigInteger n) {
		if ((e.multiply(d).mod(nModulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
			decryptionKey = d;
			nModulus = n;
		} else {
			System.out.print("The key value pair is not legal");
		}
	}
	
	public void setKeys(BigInteger e, BigInteger d) {
		if ((e.multiply(d).mod(nModulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
			decryptionKey = d;
		} else {
			System.out.print("The key value pair is not legal");
		}
	}
	
	public BigInteger getEncryptionKey(){
		return encryptionKey;
	}
	
	public BigInteger getDecryptionKey(){
		return decryptionKey;
	}
	
	public BigInteger getNModulus(){
		return nModulus;
	}
	
	
	private String mostRecentError;
	
	public File encryptFile(String fileInPathName, String fileOutPathName) {
		ArrayList<Integer> metadata = new ArrayList<Integer>();
		Path fileInPath;
		Path fileOutPath;
		try { 
			fileInPath = Paths.get(fileInPathName);
			fileOutPath = Paths.get(fileOutPathName);
		} catch (InvalidPathException e) {
			mostRecentError = e.getMessage();
			return null;
		}
		FileInputStream fileStreamIn;
		FileOutputStream fileStreamOut;
		
		try {
			File output = fileOutPath.toFile();
			boolean flag = output.createNewFile();
			if (flag == false) {
				mostRecentError = "Error while creating destination file";
				return null;
			}
			byte[] bufferPacket = new byte[PACKETSIZE];
			fileStreamIn = new FileInputStream(fileInPath.toFile());
			fileStreamOut = new FileOutputStream(output);
			int x = fileStreamIn.read(bufferPacket, 0, PACKETSIZE);
			while ( x  >= 1) {
				byte[] packetOut = encryptPacket(bufferPacket, x);
				if (((new BigInteger(bufferPacket)).compareTo(BigInteger.ZERO)) < 0) {
					metadata.add(-packetOut.length);
				} else {
					metadata.add(packetOut.length);
				}
				
				fileStreamOut.write(packetOut);
				x = fileStreamIn.read(bufferPacket);
			}
			String metaString = metadata.toString().replace("[", "").replace("]", "").replace(" ", "");
			byte[] metaBytes = metaString.getBytes();
			Integer metaByteSize = metaBytes.length;
			fileStreamOut.write(metaBytes);
			StringBuilder sb = new StringBuilder();
			sb.append(TAGLINE);
			sb.append(" ");
			for (int i = 0; i < "2000000000".length() - metaByteSize.toString().length(); i++) {
				sb.append("0");
			}
			sb.append(metaByteSize.toString());
			fileStreamOut.write(sb.toString().getBytes());			
			fileStreamIn.close();
			fileStreamOut.close();
			return output;
		} catch (FileNotFoundException e) {
			mostRecentError = e.getMessage();
			return null;
		} catch (IOException e) {
			mostRecentError = e.getMessage();
			return null;
		} catch (SecurityException e) {
			mostRecentError = e.getMessage();
			return null;
		}
	}
	
	public String encryptFileWrapper(String fileInPathName, String fileOutPathName) {
		
		File test = encryptFile(fileInPathName, fileOutPathName);
		if (test == null) {
			return "Encryption failed with error message: \n" + mostRecentError;
		}
		
		File a;
		a = new File(fileInPathName);

		File b;
		b = new File("C:/Users/Daniel/Desktop/RSA/RSAEncryption/FIXEDFILE.pdf");
		//System.out.println(b.length());
		
		
		FileComparator.Compare("C:/Users/Daniel/Desktop/RSA/RSAEncryption/CoverLetterGSI.pdf","C:/Users/Daniel/Desktop/RSA/RSAEncryption/FIXEDFILE.pdf" );
		
		return "Encryption completed successfully";
	}
	
	public String getMostRecentError() {
		return mostRecentError;
	}
	
	public ArrayList<BigInteger> encryptMessage(String message) {
		ArrayList<String> packetMessage = StringUtils.tokenizeString(message, PACKETSIZE);
		ArrayList<BigInteger> encryptedPackets = new ArrayList<BigInteger>();
		for (String packet : packetMessage) {
			encryptedPackets.add(encryptPacket(packet));
	}
		return encryptedPackets;
	}	
	
	public BigInteger encryptPacket(String message) {
		BigInteger rv = null;
		BigInteger asciiMessage = StringUtils.toAscii(message);
		rv = asciiMessage.modPow(encryptionKey, nModulus);
		return rv;
	}
	
	public byte[] encryptPacket(byte[] packet, int x) {
		packet = Arrays.copyOf(packet, x);
		BigInteger rvB = new BigInteger(packet);
		//System.out.print("Encrypting size of " + x + " to BigInt " + rvB );
		//System.out.print(" " + new String(packet));
		rvB = rvB.modPow(encryptionKey, nModulus);
		byte[] rv = rvB.toByteArray();
		//System.out.println(", resulted in Big Int "  + rvB + " of size " + rv.length);
		return rv;
	}
	
	
	public static void main(String[] args) {
		
		BigInteger p = BigInteger.probablePrime(PACKETSIZE * 8, new Random());
		BigInteger q = BigInteger.probablePrime(PACKETSIZE * 8, new Random());
		BigInteger n = p.multiply(q);
		BigInteger keyGen = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		BigInteger d = BigInteger.probablePrime(PACKETSIZE * 3, new Random());
		BigInteger e = d.modInverse(keyGen);
		System.out.println(System.getProperty("sun.arch.data.model") );
		System.out.print("p: ");
		System.out.println(p);
		System.out.print("q: ");
		System.out.println(q);
		System.out.print("e: ");
		System.out.println(e);
		System.out.print("d: ");
		System.out.println(d);
		
		//System.out.println(p);
		//System.out.println(q);
		System.out.println(n);
//		//System.out.println(keyGen);
//		System.out.println(e);
//		System.out.println(d);
		
		Encryptor encryptor = new Encryptor(e, d, p, q);
		Decryptor decryptor = new Decryptor(d, n);
		
//		System.out.println("Current Directory: " + System.getProperty("user.dir"));
//		
		encryptor.encryptFileWrapper("C:/Users/Daniel/Desktop/RSA/RSAEncryption/CoverLetterGSI.pdf", 
				"C:/Users/Daniel/Desktop/RSA/RSAEncryption/ENCRYPTEDFILE.enc");
		
		decryptor.decryptFileWrapper("C:/Users/Daniel/Desktop/RSA/RSAEncryption/ENCRYPTEDFILE.enc", 
				"C:/Users/Daniel/Desktop/RSA/RSAEncryption/FIXEDFILE.pdf");
		
		File a;
		a = new File("C:/Users/Daniel/Desktop/RSA/RSAEncryption/CoverLetterGSI.pdf");
		System.out.println(a.length());
		File b;
		b = new File("C:/Users/Daniel/Desktop/RSA/RSAEncryption/FIXEDFILE.pdf");
		System.out.println(b.length());
		
		
		System.out.println(FileComparator.Compare("C:/Users/Daniel/Desktop/RSA/RSAEncryption/CoverLetterGSI.pdf","C:/Users/Daniel/Desktop/RSA/RSAEncryption/FIXEDFILE.pdf" ));
		
	}
}
