package org.neo4j.neo;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;
//Written by Matthew Perry
public class CrimeQuery 
{
	private ExecutionEngine QUERY = null;
	private GraphDatabaseService DB = null;
	public CrimeQuery(GraphDatabaseService DB)
	{
		if(DB != null)
		{
			QUERY = new ExecutionEngine(DB);
		}
		else
			throw new RuntimeException("Error!");
	}
	
	public void findCaseIDs()
	{
		ExecutionResult result;
		String rows = null;
		String nodeResult = null;
		try ( Transaction tx = DB.beginTx() )
		{
		    result = QUERY.execute( "match (n) return n.CASE_ID AS CASE " );
		 
		    
		    
		    /*Iterator<Node> n_column = result.columnAs( "n" );
		    for ( Node node : IteratorUtil.asIterable( n_column ) )
		    {
		        // note: we're grabbing the name property from the node,
		        // not from the n.name in this case.
		        nodeResult = node + ": " + node.getProperty( "CASE_ID" );
		    }*/
		   
		    for ( Map<String, Object> row : result )
		    {
		        for ( Entry<String, Object> column : row.entrySet() )
		        {
		            rows += column.getKey() + ": " + column.getValue() + "; ";
		        }
		        rows += "\n";
		    }
		    System.out.println(rows);
		    tx.success();
		}
		
	}
}
