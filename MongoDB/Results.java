import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/*Step 1: Print your query results (just the case number) to a text file*/
/*Step 2.1: Use Results.match(file1, file2) to compare unorder results. Assumption is no query returns duplicate records*/
/*Step 2.2: Use Results.inOrder(file1, file2) to compare sorted results*/

public class Results {
	private static HashMap<String, Boolean> map;
	
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
		map = new HashMap<String, Boolean>();
		Scanner sc = getScanner(file);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			map.put(line, true);
		}
		sc.close();		
	}
	
	private static void reduceMap(String file) {
		Scanner sc = getScanner(file);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			if (map.remove(line) == null) { //found extra record
				map.put(line, true); 
				break;
			}
		}
		sc.close();
	}
	
	public static boolean match(String file1, String file2) {
		if (file1.equals(file2)) return true;
		buildMap(file1);
		reduceMap(file2);
		return map.size() == 0;
	}
	
	public static boolean inOrder(String file1, String file2) {
		if (file1.equals(file2)) return true;
		
		Scanner sc1 = getScanner(file1);
		Scanner sc2 = getScanner(file2);
		
		boolean inOrder = true; 
		while (sc1.hasNext() && inOrder) {
			if (sc2.hasNext()) {
				String line1 = sc1.nextLine();
				String line2 = sc2.nextLine();
				if (!line1.equals(line2)) 
					inOrder = false;    //records out of order
			} else  inOrder = false;            //file1 has more lines than file2
		}
		if (sc2.hasNext() && inOrder)     
			inOrder = false;                    //file2 has more lines than file1	
		sc1.close(); sc2.close();
		return inOrder;							            
	}
	
	/*testing*/
	public static void main(String[] args) {
		//match(args[0], args[1]);
	}
	

}
