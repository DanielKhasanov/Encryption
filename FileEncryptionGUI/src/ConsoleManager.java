import java.util.ArrayList;

public class ConsoleManager {
	private int logSize;
	private ArrayList<String> visiblePrintout;
	public ConsoleManager(int size) {
		logSize = size;
		visiblePrintout = new ArrayList<String>(size);
	}
	
	public String display(){
		StringBuilder sb = new StringBuilder();
		for (String s : visiblePrintout) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public void add(String s) {
		visiblePrintout.add(s);
		while (visiblePrintout.size() > logSize) {
			visiblePrintout.remove(0);
			System.out.println("removed one!");
		}
	}
	
	public static void main(String[] args) {
		//demonstration
		ConsoleManager cs = new ConsoleManager(3);
		cs.add("hallo");
		cs.add("I");
		cs.add("am");
		cs.add("doge");
		cs.add("plas halp!");
		System.out.println(cs.display());
	}
}
