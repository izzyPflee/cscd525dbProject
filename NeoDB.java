package org.neo4j.neo;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NeoDB 
{  
    private GraphDatabaseService DB = null;
    
    //Month magic numbers for attaching crime case nodes to month nodes during db build
    final private int JAN = 1;
    final private int FEB = 2;
    final private int MAR = 3;
    final private int APR = 4;
    final private int MAY = 5;
    final private int JUNE = 6;
    final private int JULY = 7;
    final private int AUG = 8;
    final private int SEP = 9;
    final private int OCT = 10;
    final private int NOV = 11;
    final private int DEC = 12;
    
    private enum RelTypes implements RelationshipType
    {
        WHEN, ARRESTED, HAPPENED
    }
    
    public NeoDB()
    {
    	DB = new GraphDatabaseFactory().newEmbeddedDatabase("C:\\Users\\Matthew\\Desktop\\MyNeoDB");
    }
    public GraphDatabaseService getDB()
    {
    	return this.DB;
    }
    public void build(NeoParser parser)
    {
    	final int ROWS = parser.getROW_COUNT();
    	final int COLS = parser.getCOL_COUNT();
    	
    	try (Transaction tx = DB.beginTx())
    	{
    		/*These are all of the redundant fields that are pulled out as their own nodes*/
    		
    		Label AM_label = DynamicLabel.label("AM");
    		Node AM = DB.createNode(AM_label);
    		
    		Label PM_label = DynamicLabel.label("PM");
    		Node PM = DB.createNode(PM_label);
    		
    		Label arrest_label = DynamicLabel.label("ARREST");
    		Node ARREST = DB.createNode(arrest_label);
    		
    		//Nodes for months, all labeled month, individual month is a property for each node
    		Label month_label = DynamicLabel.label("MONTH");
    		Node januaryNode = DB.createNode(month_label);
    		januaryNode.setProperty("month", "January");
    		Node februaryNode = DB.createNode(month_label);
    		februaryNode.setProperty("month", "February");
    		Node marchNode = DB.createNode(month_label);
    		marchNode.setProperty("month", "March");
    		Node aprilNode = DB.createNode(month_label);
    		aprilNode.setProperty("month", "April");
    		Node mayNode = DB.createNode(month_label);
    		mayNode.setProperty("month", "May");
    		Node juneNode = DB.createNode(month_label);
    		juneNode.setProperty("month", "June");
    		Node julyNode = DB.createNode(month_label);
    		julyNode.setProperty("month", "July");
    		Node augustNode = DB.createNode(month_label);
    		augustNode.setProperty("month", "August");
    		Node septemberNode = DB.createNode(month_label);
    		septemberNode.setProperty("month", "September");
    		Node octoberNode = DB.createNode(month_label);
    		octoberNode.setProperty("month", "October");
    		Node novemberNode = DB.createNode(month_label);
    		novemberNode.setProperty("month", "November");
    		Node decemberNode = DB.createNode(month_label);
    		decemberNode.setProperty("month", "December");
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		int i = 0;
    		for(i=0;i <ROWS; i++)//Link the entire database
    		{
    			Label case_label =  DynamicLabel.label("CASE");
    			Node CASE = DB.createNode(case_label);
    			String case_id = parser.getID(i);
    			CASE.setProperty("CASE_ID", case_id);
    			Relationship relationship;
    			
    			//Connect cases to am or pm
				if(parser.getAMPM(i).compareTo("AM") == 0)
    				relationship = CASE.createRelationshipTo(AM, RelTypes.WHEN);
				else
					relationship = CASE.createRelationshipTo(PM, RelTypes.WHEN);
				
				//Connect cases to ARREST if arrested
				if(parser.getArrest(i))
					relationship = CASE.createRelationshipTo(ARREST, RelTypes.ARRESTED);
				
				switch (parser.getMonth(i))
				{
					case JAN:
						CASE.createRelationshipTo(januaryNode, RelTypes.HAPPENED);
						break;
					case FEB:
						CASE.createRelationshipTo(februaryNode, RelTypes.HAPPENED);
						break;
					case MAR:
						CASE.createRelationshipTo(marchNode, RelTypes.HAPPENED);
						break;
					case APR:
						CASE.createRelationshipTo(aprilNode, RelTypes.HAPPENED);
						break;
					case MAY:
						CASE.createRelationshipTo(mayNode, RelTypes.HAPPENED);
						break;
					case JUNE:
						CASE.createRelationshipTo(juneNode, RelTypes.HAPPENED);
						break;
					case JULY:
						CASE.createRelationshipTo(julyNode, RelTypes.HAPPENED);
						break;
					case AUG:
						CASE.createRelationshipTo(augustNode, RelTypes.HAPPENED);
						break;
					case SEP:
						CASE.createRelationshipTo(septemberNode, RelTypes.HAPPENED);
						break;
					case OCT:
						CASE.createRelationshipTo(octoberNode, RelTypes.HAPPENED);
						break;
					case NOV:
						CASE.createRelationshipTo(novemberNode, RelTypes.HAPPENED);
						break;
					case DEC:
						CASE.createRelationshipTo(decemberNode, RelTypes.HAPPENED);
						break;
				}
			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
	    		
    		}//end for loop
    		tx.success();
    		tx.close(); 
    	}
    }
    public void shutdown() 
    {
    
		DB.shutdown();
		System.out.println("NeoDB has shutdown...");
	}
	
    

	/*
    // Database operations go here
	firstNode = graphDb.createNode();
	firstNode.setProperty( "message", "Hello, " );
	secondNode = graphDb.createNode();
	secondNode.setProperty( "message", "World!" );

	relationship = firstNode.createRelationshipTo( secondNode, RelTypes.WHEN );
	relationship.setProperty( "message", "brave Neo4j " );
    tx.success();
    System.out.print( firstNode.getProperty( "message" ) );
    System.out.print( relationship.getProperty( "message" ) );
    System.out.println( secondNode.getProperty( "message" ) );
 
    firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
    firstNode.delete();
    secondNode.delete();
    
    	    tx.close();
    		 registerShutdownHook( graphDb );*/

}

