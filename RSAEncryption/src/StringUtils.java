import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

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
	
	public static String formatPassword(String encryptedKey) throws NumberFormatException{
		return formatPassword(new BigInteger(encryptedKey));
	}
	
	public static String formatPassword(BigInteger encryptedKey) {
		ArrayList<Integer> asciiValues = new ArrayList<Integer>();
		while (encryptedKey.compareTo(BigInteger.ZERO) > 0) {
        	Integer asciiCharacter = encryptedKey.mod(BigInteger.valueOf(36)).intValue();
        	//System.out.println(asciiCharacter);
        	encryptedKey = encryptedKey.divide(BigInteger.valueOf(36));
        	asciiCharacter += 48;
        	//0-9
        	if (asciiCharacter > 57) {
        		asciiCharacter += 7;
        	}
        	//A-Z
        	asciiValues.add(asciiCharacter);
        }
		StringBuilder rv = new StringBuilder();
		int counter = 0;
		for (Integer item : asciiValues) {
			
			if (item == null) {
				break;
			}
			if (counter ==  4) {
				counter = 0;
				rv.insert(0, "-");
			} 
			counter ++;
			
			rv.insert(0, (char) item.intValue());
		}
		return rv.toString();
	}
	
	public static String formatPassword(String e, String n) {
		return formatPassword(e) + "-" + formatPassword(n); 
	}
	
	public static String formatPassword(BigInteger e, BigInteger n) {
		return formatPassword(e) + "-" + formatPassword(n);
	}
	
	public static BigInteger formatKey(String password) {
		password = password.replace("-", "");
		ArrayList<Integer> asciiValues = new ArrayList<Integer>();
		for (int i = 0; i < password.length(); i++) {
			int asciiValue = (int) password.charAt(i);
			asciiValue -= 48;
			if (asciiValue > 9) {
        		asciiValue -= 7;
        	}
        	asciiValues.add(asciiValue);
		}
		
		BigInteger encryptedKey = BigInteger.ZERO;
		for (Integer item : asciiValues) {
			encryptedKey = encryptedKey.multiply(BigInteger.valueOf(36));
			encryptedKey = encryptedKey.add(BigInteger.valueOf(item));
			
		}
		return encryptedKey;
	}
	
	public static void main(String[] args) {
		BigInteger d = BigInteger.probablePrime(23, new Random());
		System.out.println(d);
		String s = StringUtils.formatPassword(d);
		System.out.println(s);
		BigInteger e = StringUtils.formatKey(s);
		System.out.println(e);
	}
	
	
}
