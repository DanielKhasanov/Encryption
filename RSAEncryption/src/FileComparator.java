import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileComparator {
	public static String Compare(String file1, String file2) {
		try {
			FileInputStream a = new FileInputStream(file1);
			FileInputStream b = new FileInputStream(file2);
			
			byte[] aBytes = new byte[1];
			byte[] bBytes = new byte[1];
			
			int x = a.read(aBytes);
			int y = b.read(bBytes);
			int i = 0;
			int flag = 0;
			
			if (new File(file1).length() != new File(file2).length()) {
				flag = 1;
			}
			
			while (x > 0 && flag < 1) {
				if (aBytes[0] != bBytes[0]) {
//					System.out.println("Difference in files found at byte " + i + ": ");
//					System.out.println("    file1: " + ((char) aBytes[0]) );
//					System.out.println("    file2: " + ((char) bBytes[0]) );
					flag = 1;
				}
				i++;
				x = a.read(aBytes);
				y = b.read(bBytes);
			}
//			System.out.println("done!");
			
			a.close();
			b.close();
			
			String rv = "neq";
			if (flag == 0) {
				rv = "eq";
			}
			return rv;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}
