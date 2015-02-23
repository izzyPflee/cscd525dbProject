package org.neo4j.neo;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Tester 
{

	public static void main(String[] args) 
	{
		String path = "C:\\Users\\Matthew\\Desktop\\MyNeoDB";
		//NeoDB db = new NeoDB();
		//buildDB(db); 
		//doQueries(db);
		//db.shutdown();
		GraphDatabaseService DB = new GraphDatabaseFactory().newEmbeddedDatabase(path);
		System.out.println("Data Base created at: " + path);
		buildDB(DB);
		System.out.println("Done");
	
	}
	public static void buildDB(GraphDatabaseService DB)
	{
		NeoParser parser = new NeoParser("crimes.csv", DB); //smallData.txt
	}
	public static void doQueries(GraphDatabaseService DB)
	{
		CrimeQuery query = new CrimeQuery(DB);
		query.findCaseIDs();
	}
}
