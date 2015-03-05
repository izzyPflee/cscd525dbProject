package org.neo4j.neo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	
	private HashMap<String, ?> map = new HashMap(); //for duplicate case_number matching

	
	
	
    private enum RelTypes implements RelationshipType
    {
        ARRESTED, COMMITTED, YEAR_WHEN, MONTH_WHEN, 
        LOCATION_WHERE, COMMUNITY_WHERE, BEAT_WHERE, 
        DISTRICT_WHERE, HOME_WHERE, TIME_WHEN
    }
    
	public NeoParser(String fileName, GraphDatabaseService DB)
	{
		buildDB(fileName, DB);
	}
	

private boolean isDuplicate(String case_number) {
	if (map.containsKey(case_number)) return true;
	map.put(case_number, null); return false;
}
	
	/**
	 * @param filename
	 * @return int
	 * 
	 * Parses the rows of the crimes file
	 * Each row has 13 columns after the parsing:
	 * id(0), date(1), time(2), AM/PM(3), primary_type(4), 
	 * description(5), location_description(6), arrest(7), 
	 * domestic(8), beat(9), district(10), ward(11), 
	 * community(12), latitude(13), longitude(14)
	 */
	private void buildDB(String fileName, GraphDatabaseService DB)
	{
		final int batchSize = 50000;//flush the heap every 50,000 records entered
		int heapCount =0;//keep track of unflushed nodes on the heap

		try{
			Scanner sc = new Scanner(new File(fileName));
		
			//The indexes for removing redundant nodes
			UniqueFactory.UniqueNodeFactory ampm= getIndex(DB, "AMPM");
			UniqueFactory.UniqueNodeFactory months = getIndex(DB, "MONTH");
			UniqueFactory.UniqueNodeFactory years = getIndex(DB, "YEAR");
			UniqueFactory.UniqueNodeFactory beats = getIndex(DB, "BEAT");
			UniqueFactory.UniqueNodeFactory communities = getIndex(DB, "COMMUNITY");
			UniqueFactory.UniqueNodeFactory loc_descriptions = getIndex(DB, "LOCATION_DESCRIPTION");
			UniqueFactory.UniqueNodeFactory crimes = getIndex(DB, "CRIME");
			UniqueFactory.UniqueNodeFactory districts = getIndex(DB, "DISTRICT");
			UniqueFactory.UniqueNodeFactory domestic = getIndex(DB, "DOMESTIC");
			UniqueFactory.UniqueNodeFactory arrest = getIndex(DB, "ARREST");
			
			//remove the column headers 'keys' from the file first
			String firstLine = sc.nextLine();
			String[] keys = COMMA_PATTERN.split(firstLine);
		
			Transaction tx = DB.beginTx();//initial transaction
		
			while (sc.hasNextLine()) /*read and parse data*/
			{
					String line = sc.nextLine();
					String[] vals = COMMA_PATTERN.split(line);
					
					
					
	    			if (! isDuplicate(vals[0]))
	    			{
	    				//Create criminal case node
						Label case_label =  DynamicLabel.label("CASE");
		    			Node CASE = DB.createNode(case_label);
		    			CASE.setProperty("CASE_ID", vals[0]);//set case id 
	    				
	    				
		    			//Connect the Criminal CASE to all other nodes in graph
		    			loadDomestic(DB, domestic, CASE, vals);
		    			loadArrest(DB, arrest, CASE, vals);
		    			loadAMPM(DB, ampm, CASE, vals);
		    			loadMonth(DB, months, CASE, vals);
		    			loadYear(DB, years, CASE, vals);
						loadCrime(DB, crimes, CASE, vals);	
						loadDistrict(DB,districts, CASE, vals);
						loadBeat(DB, beats, CASE, vals);
						loadCommunity(DB, communities, CASE, vals);
						loadLocDescription(DB, loc_descriptions, CASE, vals);
						
						//Flush the heap of the transaction and start a new transaction
						if(++heapCount % batchSize == 0)
						{
							tx.success();
							tx.close();
							tx = DB.beginTx();
							System.out.println(heapCount + " flushed");
						}
	    			}
				
			}//end while
			tx.success();
			tx.close();
		}//end scanner try block
		catch(Exception e){e.printStackTrace();}
	}//end buildDB
	
	//Create an index for a group of nodes, with the same name for label and internal property
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
	
	//All methods below either create or get an indexed node and connect it to the CASE node for a crime
	//All relationships are double-edged for versatile query paths
	//There are differences in properties on the edges and internally on some indexes
	
	private void loadArrest(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory arrest, Node CASE, String [] vals)
	{
		Node arrested;
		Relationship rel1, rel2;
		
		if(Boolean.parseBoolean(vals[7]))
			arrested = arrest.getOrCreate("ARREST", true);
		else
			arrested = arrest.getOrCreate("ARREST", false);
		
		rel1 = CASE.createRelationshipTo(arrested, RelTypes.ARRESTED);
		rel2 = arrested.createRelationshipTo(CASE, RelTypes.ARRESTED);
	}
	
	
	private void loadDomestic(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory domestic, Node CASE, String [] vals)
	{
		Node d;
		Relationship rel1, rel2;
		
		if(Boolean.parseBoolean(vals[8]))
			d = domestic.getOrCreate("DOMESTIC", true);
		else
			d = domestic.getOrCreate("DOMESTIC", false);
		
		rel1 = CASE.createRelationshipTo(d, RelTypes.HOME_WHERE);
		rel2 = d.createRelationshipTo(CASE, RelTypes.HOME_WHERE);
		
	}
	//Two nodes (AM or PM) in entire graph are indexed as AM/PM, their relationships store the HOUR and MINUTE of incident
	private void loadAMPM(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory ampm, Node CASE, String [] vals)
	{
		Relationship rel1, rel2;
		int hour = Integer.parseInt(vals[2].split(":")[0]);
		int minute = Integer.parseInt(vals[2].split(":")[1]);
		Node time = ampm.getOrCreate("AMPM", vals[3]);
		rel1 = CASE.createRelationshipTo(time, RelTypes.TIME_WHEN);
		rel2 = time.createRelationshipTo(CASE, RelTypes.TIME_WHEN);
		rel1.setProperty("HOUR", hour);
		rel2.setProperty("HOUR", hour);
		rel1.setProperty("MINUTE", minute);
		rel2.setProperty("MINUTE", minute);
	}
	
	private void loadMonth(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory months, Node CASE, String [] vals)
	{
		Relationship rel1, rel2;
		//Create or Grab Month Node of current case
		Node month = months.getOrCreate("MONTH", Integer.parseInt(vals[1].split("/")[0]));
		
		rel1 = CASE.createRelationshipTo(month, RelTypes.MONTH_WHEN);
		rel2 = month.createRelationshipTo(CASE, RelTypes.MONTH_WHEN);
		
		rel1.setProperty("DAY", Integer.parseInt(vals[1].split("/")[1]));
		rel2.setProperty("DAY", Integer.parseInt(vals[1].split("/")[1]));
	}
	private void loadYear(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory years, Node CASE, String [] vals)
	{	
		Relationship rel1, rel2;
		//Create or Grab Year Node of current case
		Node year = years.getOrCreate("YEAR", Integer.parseInt(vals[1].split("/")[2]));
		
		rel1 = CASE.createRelationshipTo(year, RelTypes.YEAR_WHEN);
		rel2 = year.createRelationshipTo(CASE, RelTypes.YEAR_WHEN);
		
		rel1.setProperty("MONTH", Integer.parseInt(vals[1].split("/")[0]));
		rel2.setProperty("MONTH", Integer.parseInt(vals[1].split("/")[0]));
	}
	private void loadCrime(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory crimes, Node CASE, String [] vals)
	{
		Relationship rel1, rel2;
		Node crime = crimes.getOrCreate("CRIME", vals[4]);
		
		rel1 = CASE.createRelationshipTo(crime, RelTypes.COMMITTED);
		rel2 = crime.createRelationshipTo(CASE, RelTypes.COMMITTED);
		
		if(Boolean.parseBoolean(vals[7]))
		{
			rel1.setProperty("ARRESTED", true);
			rel2.setProperty("ARRESTED", true);
		}
		else
		{
			rel1.setProperty("ARRESTED", false);
			rel2.setProperty("ARRESTED", false);
		}
		
	}
	private void loadDistrict(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory districts, Node CASE, String [] vals)
	{
		Relationship rel1, rel2;
		Node district = districts.getOrCreate("DISTRICT", vals[10]);
		rel1= CASE.createRelationshipTo(district, RelTypes.DISTRICT_WHERE);
		rel1.setProperty("LATITUDE", vals[13]);
		rel1.setProperty("LONGITUDE", vals[14]);
		rel2 = district.createRelationshipTo(CASE, RelTypes.DISTRICT_WHERE);
		rel2.setProperty("LATITUDE", vals[13]);
		rel2.setProperty("LONGITUDE", vals[14]);
	}
	private void loadBeat(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory beats, Node CASE, String [] vals)
	{
		Node beat = beats.getOrCreate("BEAT", vals[9]);
		CASE.createRelationshipTo(beat, RelTypes.BEAT_WHERE);
		beat.createRelationshipTo(CASE, RelTypes.BEAT_WHERE);
	}
	private void loadCommunity(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory communities, Node CASE, String [] vals)
	{
		Relationship rel1, rel2;
		Node community = communities.getOrCreate("COMMUNITY", vals[12]);
		rel1 = CASE.createRelationshipTo(community, RelTypes.COMMUNITY_WHERE);
		rel2 = community.createRelationshipTo(CASE, RelTypes.COMMUNITY_WHERE);
		rel1.setProperty("LATITUDE", vals[13]);
		rel1.setProperty("LONGITUDE", vals[14]);
		rel2.setProperty("LATITUDE", vals[13]);
		rel2.setProperty("LONGITUDE", vals[14]);
	}
	private void loadLocDescription(GraphDatabaseService DB, UniqueFactory.UniqueNodeFactory loc_descriptions, Node CASE, String [] vals)
	{
		Node ld = loc_descriptions.getOrCreate("LOCATION_DESCRIPTION", vals[6]);
		CASE.createRelationshipTo(ld, RelTypes.LOCATION_WHERE);
		ld.createRelationshipTo(CASE, RelTypes.LOCATION_WHERE);
	}
}
