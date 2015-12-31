import java.math.BigInteger;
import java.util.ArrayList;

public class StringUtils {
	
	
	static BigInteger toAscii(String s){
		 BigInteger asciiCode = BigInteger.ZERO;
	
        for (int i = 0; i < s.length(); i++){
        	asciiCode = asciiCode.multiply(BigInteger.valueOf((256)));
        	asciiCode = asciiCode.add(BigInteger.valueOf((int) s.charAt(i)));
        }
        
        return asciiCode;
	}

	static	String toMessage(BigInteger decryptedMessage) {
		StringBuilder messageBuilder = new StringBuilder();
        while (decryptedMessage.compareTo(BigInteger.ZERO) > 0) {
        	int asciiCharacter = decryptedMessage.mod(BigInteger.valueOf(256)).intValue();
        	decryptedMessage = decryptedMessage.divide(BigInteger.valueOf(256));
        	messageBuilder.insert(0, (char) asciiCharacter);
        }
        return messageBuilder.toString();
	}
	
	public static ArrayList<String> tokenizeString(String s, int size) {
		ArrayList<String> rv = new ArrayList<String>();
		String[] packeted = s.split("(?<=\\G.{" + Integer.toString(size) + "})");
		for (String item : packeted) {
			rv.add(item);
		}
		return rv;
	}
	
	public static String formatPassword(BigInteger encryptedKey) {
		ArrayList<Integer> asciiValues = new ArrayList<Integer>();
		while (encryptedKey.compareTo(BigInteger.ZERO) > 0) {
        	Integer asciiCharacter = encryptedKey.mod(BigInteger.valueOf(62)).intValue();
        	//System.out.println(asciiCharacter);
        	encryptedKey = encryptedKey.divide(BigInteger.valueOf(62));
        	asciiCharacter += 48;
        	if (asciiCharacter > 57) {
        		asciiCharacter += 7;
        	}
        	if (asciiCharacter > 90) {
        		asciiCharacter += 6;
        	}
        	asciiValues.add(asciiCharacter);
        }
		StringBuilder rv = new StringBuilder();
		for (Integer item : asciiValues) {
			if (item == null) {
				break;
			}
			rv.insert(0, (char) item.intValue());
		}
		return rv.toString();
	}
	
	public static BigInteger formatKey(String password) {
		ArrayList<Integer> asciiValues = new ArrayList<Integer>();
		for (int i = 0; i < password.length(); i++) {
			int asciiValue = (int) password.charAt(i);
			asciiValue -= 48;
			if (asciiValue > 10) {
        		asciiValue -= 7;
        	}
        	if (asciiValue > 36) {
        		asciiValue -= 6;
        	}
        	asciiValues.add(asciiValue);
		}
		
		BigInteger encryptedKey = BigInteger.ZERO;
		for (Integer item : asciiValues) {
			encryptedKey = encryptedKey.multiply(BigInteger.valueOf(62));
			encryptedKey = encryptedKey.add(BigInteger.valueOf(item));
			
		}
		return encryptedKey;
	}
	
	public static void main(String[] args) {
		String s =  "9999-12-31-24 D.K.K.D Certified";
		BigInteger l = StringUtils.toAscii(s);
		System.out.println(l);
		//System.out.println(StringUtils.tokenizeString(s, 5));
		String s2 = StringUtils.toMessage(l);
		System.out.println(s2);
	}
	
	
}
