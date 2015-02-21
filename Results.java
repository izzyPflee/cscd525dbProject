import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/*Takes two text file arguments and compares their contents*/

public class Results {
	private static HashMap<String, ?> map;
	
	private Results() {} //public xtor unnecessary
	
	private static Scanner getScanner(String fileName) {
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void buildMap(String file) {
		map = new HashMap();
		Scanner sc = getScanner(file);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			map.put(line, null);
		}
		sc.close();		
	}
	
	private static void reduceMap(String file) {
		Scanner sc = getScanner(file);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			map.remove(line);
		}
		sc.close();
	}
	
	public static boolean match(String file1, String file2) {
		buildMap(file1);
		reduceMap(file2);
		if (map.size() == 0) 
			return true;
		else 
			return false;
	}
	
	/*testing*/
	public static void main(String[] args) {
		match(args[0], args[1]);
	}
	

}
