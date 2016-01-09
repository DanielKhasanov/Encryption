import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Encryptor {
	private BigInteger encryptionKey;
	private BigInteger nModulus;
	private static final int PACKETSIZE = 13;
	
	public Encryptor(BigInteger e, BigInteger d, BigInteger p, BigInteger q) {
		BigInteger modulus = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		if (((e.multiply(d)).mod(modulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
			nModulus = p.multiply(q);
		} else {
			System.out.print("The key value pair is not legal");
		}
	}
	
	public void setParams(BigInteger e, BigInteger d, BigInteger n) {
		if ((e.multiply(d).mod(nModulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
			nModulus = n;
		} else {
			System.out.print("The key value pair is not legal");
		}
	}
	
	public void setKeys(BigInteger e, BigInteger d) {
		if ((e.multiply(d).mod(nModulus)).equals(BigInteger.ONE)) {
			encryptionKey = e;
		} else {
			System.out.print("The key value pair is not legal");
		}
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
	
	
	public static void main(String[] args) {
		BigInteger p = BigInteger.probablePrime(PACKETSIZE*4, new Random());
		BigInteger q = BigInteger.probablePrime(PACKETSIZE*4, new Random());
		BigInteger n = p.multiply(q);
		BigInteger keyGen = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		BigInteger e = BigInteger.probablePrime(PACKETSIZE*8, new Random());
		BigInteger d = e.modInverse(keyGen);
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
//		System.out.println(n);
//		//System.out.println(keyGen);
//		System.out.println(e);
//		System.out.println(d);
		
		Encryptor encryptor = new Encryptor(e, d, p, q);
		Decryptor decryptor = new Decryptor(d, n);
		
		ArrayList<BigInteger> encryptedMessage = encryptor.encryptMessage("2015-10-10-20 D.K.K.D Certified");
		
		System.out.println(StringUtils.formatPassword(encryptedMessage.get(0)));
		System.out.println(StringUtils.formatKey(StringUtils.formatPassword(encryptedMessage.get(0))));
//		System.out.println(StringUtils.formatPassword(StringUtils.formatKey(StringUtils.formatPassword(encryptedMessage))));
//		
		String decryptedMessage = decryptor.decryptMessage(encryptedMessage);
		System.out.println(decryptedMessage);
	}
}
