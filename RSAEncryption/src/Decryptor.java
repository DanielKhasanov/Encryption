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
	
	public String decryptMessage(ArrayList<BigInteger> encryptedMessage) {
		StringBuilder sb = new StringBuilder();
		for (BigInteger packet : encryptedMessage) {
			sb.append(decryptPacket(packet));
		}
		return sb.toString();
	}
	

	public String decryptPacket(BigInteger packet) {
		BigInteger decryptedMessage = packet.modPow(decryptionKey, nModulus) ;
		String message = StringUtils.toMessage(decryptedMessage);
		return message;
	}
}
