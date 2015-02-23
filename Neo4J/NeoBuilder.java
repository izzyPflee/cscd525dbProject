package org.neo4j.neo;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.neo4j.graphdb.GraphDatabaseService;

public class NeoBuilder 
{
	
	private final static Pattern COMMA_PATTERN = Pattern.compile(",");
	
	private GraphDatabaseService DB = null;
	
	public NeoBuilder(File file)
	{
		
	}
	
	
	public void build()
	{
		
	}
	
}
