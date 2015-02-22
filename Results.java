import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/*Methods for comparing query results on matching content and/or order of content*/

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
	
	public static boolean inOrder(String file1, String file2) {
		if (file1.equals(file2)) return true;
		
		Scanner sc1 = getScanner(file1);
		Scanner sc2 = getScanner(file2);

		while (sc1.hasNext()) {
			if (sc2.hasNext()) {
				String line1 = sc1.nextLine();
				String line2 = sc2.nextLine();
				if (!line1.equals(line2)) {
					sc1.close(); sc2.close();
					return false; 				//records out of order
				}
			} else { 
				sc1.close(); sc2.close();
				return false; 					//file1 longer than file2
			}
		}
		if (sc2.hasNext()) { 
			sc1.close(); sc2.close(); 				//file2 longer than file1
			return false;
		}
		sc1.close(); sc2.close();
		return true;							//files are in matching order
	}
	
	/*testing*/
	public static void main(String[] args) {
		//match(args[0], args[1]);
	}
	

}
