package org.neo4j.neo;

public class Tester {

	public static void main(String[] args) 
	{
		NeoDB db = new NeoDB();
		buildDB(db); 
		//doQueries(db);
		db.shutdown();
	}
	public static void buildDB(NeoDB db)
	{
		NeoParser parser = new NeoParser("smallData.txt");
		db.build(parser);
	}
	public static void doQueries(NeoDB db)
	{
		CrimeQuery query = new CrimeQuery(db);
		query.findCaseIDs();
	}
}
