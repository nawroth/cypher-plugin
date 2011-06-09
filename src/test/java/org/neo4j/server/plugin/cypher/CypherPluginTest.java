/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.plugin.cypher;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.cypher.CypherParser;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.SyntaxError;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.rest.repr.Representation;
import org.neo4j.server.rest.repr.formats.JsonFormat;
import org.neo4j.test.GraphDescription;
import org.neo4j.test.GraphDescription.Graph;
import org.neo4j.test.GraphHolder;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.TestData;

public class CypherPluginTest implements GraphHolder
{
    
    private CypherParser parser;
    private static ImpermanentGraphDatabase db;
    private ExecutionEngine engine;
    public @Rule
    TestData<Map<String, Node>> data = TestData.producedThrough( GraphDescription.createGraphFor(
            this, true ) );
    private CypherPlugin plugin;
    private OutputFormat json;
    
    @Before
    public void setUp() throws Exception
    {
        parser = new CypherParser();
        db = new ImpermanentGraphDatabase();
        engine = new ExecutionEngine( db );
        plugin = new CypherPlugin();
        json = new OutputFormat( new JsonFormat(),
                new URI( "http://localhost/" ), null );
    }
 
    @Test
    @Graph( value = { "I know you" } )
    public void runSimpleQuery() throws Exception
    {
        Node i = data.get().get( "I" );
        Representation result = testQuery( "start n=("+i.getId() + ") return n" );
        assertTrue( json.format( result).contains( "I" ));
    }
 
    private Representation testQuery( String query ) throws SyntaxError
    {
        
        return plugin.executeScript( db, query );
    }

    @Override
    public GraphDatabaseService graphdb()
    {
        return db;
    }
    
    @BeforeClass
    public static void startDatabase()
    {
        db = new ImpermanentGraphDatabase("target/db"+System.currentTimeMillis());
        
    }

   
}
