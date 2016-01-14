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
import java.util.Random;

public class Decryptor {
	private BigInteger decryptionKey;
	private BigInteger nModulus;
	private final int PACKETSIZE = 13;
	private static final String TAGLINE = "D.K.K.D Certified ";
	private static final int ENDBYTES = (TAGLINE + " 2000000000").length(); 
	
	public Decryptor( BigInteger d, BigInteger n) {
		decryptionKey = d;
		nModulus = n;
	}
	
	public void setParams( BigInteger d, BigInteger n) {
		decryptionKey = d;
		nModulus = n;
	}
	
	public Decryptor(String d, String n) {
		decryptionKey = new BigInteger(d);
		nModulus = new BigInteger(n);
	}
	
	public void setParams( String d, String n) {
		decryptionKey = new BigInteger(d);
		nModulus = new BigInteger(n);
	}
	
	public String decryptMessage(ArrayList<BigInteger> encryptedMessage) {
		StringBuilder sb = new StringBuilder();
		for (BigInteger packet : encryptedMessage) {
			sb.append(decryptPacket(packet));
		}
		return sb.toString();
	}
	
	private String mostRecentError;
	
	public File decryptFile(String fileInPathName, String fileOutPathName) {
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
			
			File encryptedFile = fileInPath.toFile();
			
			//determine validity of decryption key and also determine size of metadata
			FileInputStream fileStreamMeta = new FileInputStream(encryptedFile);
			byte[] certificationBytes = new byte[ENDBYTES];
			fileStreamMeta.skip(encryptedFile.length() - ENDBYTES);
			fileStreamMeta.read(certificationBytes);
			fileStreamMeta.close();
			String certificationString = new String(certificationBytes);
			if (!certificationString.startsWith(TAGLINE)) {
				mostRecentError = "Error, the decryption failed. The file was not properly encrypted for this decryption key.";
				return null;
			}
			
			//Read the packet sizes into an array
			Integer metaSize = new Integer(certificationString.substring(TAGLINE.length() + 1));
			//System.out.println(metaSize);
			FileInputStream fileStreamBeta = new FileInputStream(encryptedFile);
			fileStreamBeta.skip(encryptedFile.length() - metaSize - ENDBYTES);
			byte[] metaBytes = new byte[metaSize];
			fileStreamBeta.read(metaBytes);
			fileStreamBeta.close();
			
			String[] packetSizes = new String(metaBytes).split(",");
			
			fileStreamIn = new FileInputStream(encryptedFile);
			
			
			fileStreamOut = new FileOutputStream(output);
			for (int i = 0; i < packetSizes.length; i++) {

				String packetSize = packetSizes[i];
				Integer magnitude = new Integer(packetSize);
				byte[] bufferPacket = new byte[Math.abs(magnitude)];
				fileStreamIn.read(bufferPacket);
				//System.out.println(new String(bufferPacket));
				byte[] packetOut = decryptPacket(bufferPacket, magnitude);
				if (packetOut.length != PACKETSIZE) {
					if (packetOut.length > PACKETSIZE) {
						System.out.println(new BigInteger(bufferPacket));
						System.out.println(packetOut.length);
						System.out.println(packetSize);
						mostRecentError = "Error, packet fault during decryption";
						return null;
					} else if (i != packetSizes.length - 1) {
						byte[] nullArray = new byte[PACKETSIZE ];
						if (magnitude < 0) {
							for (int i1 = 0; i1 < PACKETSIZE; i1++) {
								nullArray[i1] = (byte) -1;
							}
						}
						System.arraycopy(packetOut, 0, nullArray, PACKETSIZE - packetOut.length, packetOut.length);
						packetOut = nullArray;
					} 	
				}
				//System.out.println(new String(packetOut));
				fileStreamOut.write(packetOut);
			}
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
	
	public void decryptFileWrapper(String fileInPathName, String fileOutPathName) {
		File test = decryptFile(fileInPathName, fileOutPathName);
		if (test == null) {
			System.out.println(mostRecentError);
		}
	}
	

	public String decryptPacket(BigInteger packet) {
		BigInteger decryptedMessage = packet.modPow(decryptionKey, nModulus) ;
		String message = StringUtils.toMessage(decryptedMessage);
		return message;
	}
	
	public byte[] decryptPacket(byte[] packet, int x) {
		BigInteger packetEnum = new BigInteger(packet);
		//System.out.print("Decrypting size of " + x + " which is " + packetEnum);
		BigInteger decryptedPacket = packetEnum.modPow(decryptionKey, nModulus);
		if (x < 0) {
			decryptedPacket = decryptedPacket.subtract(nModulus);
		}
		//System.out.print("Decrypting: " + decryptedPacket);
		//System.out.println(", resulted in Big Int "  + decryptedPacket +" " + new String(decryptedPacket.toByteArray()) +  " of size " + decryptedPacket.toByteArray().length);
		return decryptedPacket.toByteArray();
		
	}
public static void main(String[] args) {
		BigInteger p = new BigInteger( "2400577747320161");
		BigInteger q = new BigInteger("3345356237657191");
		BigInteger modulus = new BigInteger( "2400577747320161").multiply(new BigInteger("3345356237657191"));
		BigInteger totient = new BigInteger( "2400577747320160").multiply(new BigInteger("3345356237657190"));
		BigInteger e = new BigInteger("62201533");
		BigInteger d = new BigInteger("256228362579188903273865011797");
		
		//p: 2400577747320161
		//q: 3345356237657191
		//e: 62201533
		//d: 256228362579188903273865011797
		
		BigInteger message = new BigInteger("8458592908715048584259042728010");
		BigInteger encrypt = new BigInteger("6543251105291977581563920543797");
		BigInteger decrypt = new BigInteger("427805167736499856725801800259");
		
		
		System.out.println(p.isProbablePrime(100));
		System.out.println(q.isProbablePrime(100));
		
		System.out.println( (e.multiply(d)).mod(modulus));
		System.out.println( (e.multiply(d)).mod(totient));
		System.out.println(message.toByteArray().length);
		//System.out.println( (e.multiply(d)).compareTo(modulus));
		
		System.out.println(message.modPow(e, modulus) );
		System.out.println(message.modPow(d, modulus).modPow(e, modulus));
		System.out.println(message.mod(modulus));
		System.out.println(encrypt.modPow(d, modulus));
		
		
	}
	
}
