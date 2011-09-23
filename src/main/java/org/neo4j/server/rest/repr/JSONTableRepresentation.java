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
package org.neo4j.server.rest.repr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

public class JSONTableRepresentation extends ObjectRepresentation
{

    private final ExecutionResult queryResult;

    public JSONTableRepresentation( ExecutionResult result )
    {
        super( RepresentationType.STRING );
        this.queryResult = result;
    }

    @Mapping( "cols" )
    public Representation columns()
    {
        List<Representation> cols = new ArrayList<Representation>();
        for(String col : queryResult.columns()) {
            cols.add( ValueRepresentation.string(String.format( "{id: '%s', label:'%s', type:'string'}", col, col ) ));
        }
        return new ListRepresentation( "hej", cols );
    }

    @Mapping( "rows" )
    public Representation data()
    {
        // cells
        List<Representation> cells = new ArrayList<Representation>();
        for ( Map<String, Object> row : queryResult )
        {
            // columns
            for ( String column : queryResult.columns() )
            {
                cells.add( ValueRepresentation.string( String.format("{c: [{v:'%s'}]}", row.get( column )!=null?row.get(column).toString():null )) );

            }
        }
        return new ListRepresentation( "cells", cells );
    }

    // private void fillColumns() {
    // Collection<Node> nodeCollection = IteratorUtil.asCollection( asIterable(

    private Representation getRepresentation( Object r )
    {
        if(r == null ) {
            return ValueRepresentation.string( null );
        }
        if ( r instanceof Node )
        {
            return new NodeRepresentation( (Node) r );
        }
        if ( r instanceof Relationship )
        {
            return new RelationshipRepresentation( (Relationship) r );
        }
        else if ( r instanceof Double || r instanceof Float )
        {
            return ValueRepresentation.number( ( (Number) r ).doubleValue() );
        }
        else if ( r instanceof Long || r instanceof Integer )
        {
            return ValueRepresentation.number( ( (Number) r ).longValue() );
        }
        else if ( r instanceof Path )
        {
            return new PathRepresentation<Path>((Path) r );
        }
        else
        {
            return ValueRepresentation.string( r.toString() );
        }
    }

}
