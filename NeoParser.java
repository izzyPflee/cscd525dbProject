package org.neo4j.neo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NeoParser 
{
	private final static Pattern COMMA_PATTERN = Pattern.compile(",");
	private final static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy H:m:s a");
	private String[][] PARSED_ROWS = null;
	private int ROW_COUNT = 0;
	private final int COL_COUNT = 15;
	public NeoParser(String fileName)
	{
		System.out.println(fileName);
		ROW_COUNT = calcRowCount(fileName);
		PARSED_ROWS = new String[ROW_COUNT][COL_COUNT];
		parseFile(fileName);
	}
	//find number of rows, NOT including the header row
	private int calcRowCount(String fileName)
	{
		int rows = -1;//no header in count
		Scanner sc = connectScanner(fileName);
		while (sc.hasNextLine()){
			rows++;
			sc.nextLine();
		}
		return rows;
	}
	public int getROW_COUNT()
	{
		return ROW_COUNT;
	}
	public int getCOL_COUNT()
	{
		return COL_COUNT;
	}
	private static Scanner connectScanner(String fileName) 
	{
		try {
			return new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param filename
	 * @return int
	 * 
	 * Parses the rows of the crimes file and recombines the dates columns into one date object.
	 * Each row has 13 columns after the parsing:
	 * id(0), date(1), time(2), AM/PM(3), primary_type(4), 
	 * description(5), location_description(6), arrest(7), 
	 * domestic(8), beat(9), district(10), ward(11), 
	 * community_area(12), latitude(13), longitude(14)
	 */
	private void parseFile(String filename)
	{
		int row = 0;
		/*scan & parse firstLine for key names*/
		Scanner sc = connectScanner(filename);
		String firstLine = sc.nextLine();
		String[] keys = COMMA_PATTERN.split(firstLine);
		
		/*read and parse data*/
		while (sc.hasNextLine()) 
		{
			String line = sc.nextLine();
			String[] vals = COMMA_PATTERN.split(line);

			for(int i=0 ;i < COL_COUNT;i++)
			{
				PARSED_ROWS[row][i] = vals[i];
				//System.out.print(PARSED_ROWS[row][i] + " & ");
			}
			//System.out.println("");
			
			row++;
		}//end while
	}//end parseFile
	
	public String[] getRow(int row)
	{
		return PARSED_ROWS[row];
	}
	public String getID(int row)
	{
		return PARSED_ROWS[row][0];
	}
	public int getMonth(int row)
	{
		String date = PARSED_ROWS[row][1];
		String[] month = date.split("/");
		return Integer.parseInt(month[0]);
	}
	public String getDate(int row)
	{
		return PARSED_ROWS[row][1];
	}
	public String getTime(int row)
	{
		return PARSED_ROWS[row][2];
	}
	public String getAMPM(int row)
	{
		return PARSED_ROWS[row][3];
	}
	public String getPrimaryType(int row)
	{
		return PARSED_ROWS[row][4];
	}
	public String getDescription(int row)
	{
		return PARSED_ROWS[row][5];
	}
	public String getLocationDescription(int row)
	{
		return PARSED_ROWS[row][6];
	}
	public boolean getArrest(int row)
	{
		return (PARSED_ROWS[row][7].compareTo("true") == 0);
	}
	public String getDomestic(int row)
	{
		return PARSED_ROWS[row][8];
	}
	public String getBeat(int row)
	{
		return PARSED_ROWS[row][9];
	}
	public String getDistrict(int row)
	{
		return PARSED_ROWS[row][10];
	}
	public String getWard(int row)
	{
		return PARSED_ROWS[row][11];
	}
	public String getCommunityArea(int row)
	{
		return PARSED_ROWS[row][12];
	}
	public String getLatitude(int row)
	{
		return PARSED_ROWS[row][13];
	}
	public String getLongitude(int row)
	{
		return PARSED_ROWS[row][14];
	}
}
