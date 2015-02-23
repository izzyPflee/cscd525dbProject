package org.neo4j.neo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;

public class NeoParser 
{
	private final static Pattern COMMA_PATTERN = Pattern.compile(",");
	private final static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy H:m:s a");
	private final int COLS= 15;
	
    private enum RelTypes implements RelationshipType
    {
        ARRESTED, COMMITTED, HAPPENED_IN, WHEN, WHERE, 
    }
    
	public NeoParser(String fileName, GraphDatabaseService DB)
	{
		buildDB(fileName, DB);
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
	private void buildDB(String fileName, GraphDatabaseService DB)
	{
		final int batchSize = 50000;//flush the heap every 50,000 records entered
		Relationship relationship;
		try{
			Scanner sc = new Scanner(new File(fileName));
		
			//The indexes for removing redundant nodes
			UniqueFactory.UniqueNodeFactory months = getIndex(DB, "Month");
			UniqueFactory.UniqueNodeFactory years = getIndex(DB, "Year");
			UniqueFactory.UniqueNodeFactory crimes = getIndex(DB, "Crime");
			UniqueFactory.UniqueNodeFactory districts = getIndex(DB, "District");
			UniqueFactory.UniqueNodeFactory beats = getIndex(DB, "Beat");
			UniqueFactory.UniqueNodeFactory communities = getIndex(DB, "Community");
			UniqueFactory.UniqueNodeFactory loc_descriptions = getIndex(DB, "LocationDescription");
			
	
		
			//remove the column headers 'keys' from the file first
			String firstLine = sc.nextLine();
			String[] keys = COMMA_PATTERN.split(firstLine);
		
			int heapCount =0;
			
			Transaction tx = DB.beginTx();//initial transaction
		
			while (sc.hasNextLine()) /*read and parse data*/
			{
					String line = sc.nextLine();
					String[] vals = COMMA_PATTERN.split(line);
					String[] date = vals[1].split("/");
					
					Label case_label =  DynamicLabel.label("CASE");
	    			Node CASE = DB.createNode(case_label);
	    			CASE.setProperty("Case_id", vals[0]);//set case id 
					
					//Create or Grab Month Node of current case
					Node month = months.getOrCreate("Month", date[0]);
					CASE.createRelationshipTo(month, RelTypes.HAPPENED_IN);
					
					//Create or Grab Year Node of current case
					Node year = years.getOrCreate("Year", date[2]);
					CASE.createRelationshipTo(year, RelTypes.HAPPENED_IN);
					
					Node crime = crimes.getOrCreate("Crime", vals[4]);
					CASE.createRelationshipTo(crime, RelTypes.COMMITTED);
					
					Node district = districts.getOrCreate("District", vals[10]);
					CASE.createRelationshipTo(district, RelTypes.WHERE);
					
					Node beat = beats.getOrCreate("Beat", vals[9]);
					CASE.createRelationshipTo(beat, RelTypes.WHERE);
					
					Node community = communities.getOrCreate("Community", vals[9]);
					CASE.createRelationshipTo(community, RelTypes.WHERE);
					
					Node ld = loc_descriptions.getOrCreate("LocationDescription", vals[6]);
					CASE.createRelationshipTo(community, RelTypes.WHERE);
					
					//Flush the heap of the transaction and start a new transaction
					if(++heapCount % batchSize == 0)
					{
						tx.success();
						tx.close();
						tx = DB.beginTx();
						System.out.println(heapCount);
					}
				
			}//end while
			tx.success();
			tx.close();
		}//end scanner try block
		catch(Exception e){e.printStackTrace();}
	}//end buildDB
	
	//Create an index for a group of nodes
	private UniqueFactory.UniqueNodeFactory getIndex(GraphDatabaseService DB, final String index)
	{
		try (Transaction tx = DB.beginTx())
	    {
			UniqueFactory.UniqueNodeFactory indexes = new UniqueFactory.UniqueNodeFactory(DB, index)
			{
				@Override
				protected void initialize(Node created, Map<String, Object> properties) 
				{
					created.addLabel(DynamicLabel.label(index));
					created.setProperty(index, properties.get(index));
				}
			};
			tx.success();
			return indexes;
	    }
	}
	
	
}
