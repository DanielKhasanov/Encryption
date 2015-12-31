import java.math.BigInteger;
import java.util.ArrayList;

public class Decryptor {
	private BigInteger decryptionKey;
	private BigInteger nModulus;
	
	public Decryptor( BigInteger d, BigInteger n) {
		decryptionKey = d;
		nModulus = n;
	}
	
	public void setParams( BigInteger d, BigInteger n) {
		decryptionKey = d;
		nModulus = n;
	}
	//use if BigInteger not working
//	public String decryptMessage(ArrayList<Long> encryptedMessage) {
//		StringBuilder sb = new StringBuilder();
//		for (Long packet : encryptedMessage) {
//			sb.append(decryptPacket(packet));
//		}
//		return sb.toString();
//	}
	
	//use if BigInteger library not working
//	public String decryptPacket(Long packet) {
//		Long decryptedMessage = (long) (Math.pow(packet, decryptionKey) % nModulus);
//		String message = StringUtils.toMessage(decryptedMessage);
//		return message;
//	}
	
	public String decryptMessage(BigInteger packet) {
		BigInteger decryptedMessage = packet.modPow(decryptionKey, nModulus) ;
		String message = StringUtils.toMessage(decryptedMessage);
		return message;
	}
}
