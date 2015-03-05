import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class Parser {
	
	private static String parseCaseNumber(String s, int i) {
		String[] line = s.split("\"");
		if (i < line.length)
			return line[i];
		return null;
	}
	
	private static BufferedReader getBufferedReader(String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return br;
	}
	
	private static String readLine(BufferedReader br) {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void skipLines(BufferedReader br, int i) {
		try {
			for (int j = 0; j < i; j++)
				System.out.println("skipped : " + br.readLine()); 
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void closeBufferedReader(BufferedReader br) {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static PrintWriter getWriter(String file) {
		try {
			return new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
//		for (int i = 0; i < 2; i++) {
//			String oldFile =  "query" + (i + 1) + "Neo.txt"; 
//			BufferedReader br = getBufferedReader(oldFile);
//			skipLines(br, 3);
//			
//			String newFile = "query" + (i + 1) + "NeoParsed.txt";
//			PrintWriter wr = getWriter(newFile);
//			
//			for (String line; (line = readLine(br)) != null;) {
//				String caseNumber = parseCaseNumber(line, 1);
//				if (caseNumber != null)
//					wr.println(caseNumber);
//			}
//			closeBufferedReader(br);
//			wr.close();
//		}
	}

}
