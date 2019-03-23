package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionIndex {
	String					indexName ;
	boolean					isUnique ;
	List<SqlActionColumn>	columnList ;

	public static int GetAllIndexesInTable( Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception  {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionIndex		index ;
		SqlActionColumn		column ;
		int					nret = 0 ;
		
		table.indexList = new LinkedList<SqlActionIndex>() ;
		
		prestmt = conn.prepareStatement("SELECT non_unique,index_name FROM information_schema.STATISTICS WHERE table_schema=? AND table_name=? AND seq_in_index=1 ORDER BY index_name ASC") ;
		prestmt.setString( 1, database.databaseName );
		prestmt.setString( 2, table.tableName );
		rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			index = new SqlActionIndex() ;
			
			if( rs.getInt(1) == 1 )
				index.isUnique = false ;
			else
				index.isUnique = true ;
			index.indexName = rs.getString(2) ;
			
			table.indexList.add( index );
		}
		rs.close();
		
		for( SqlActionIndex i : table.indexList ) {
			i.columnList = new LinkedList<SqlActionColumn>() ;
			
			prestmt = conn.prepareStatement("SELECT column_name FROM information_schema.STATISTICS WHERE table_schema=? AND table_name=? AND index_name=? ORDER BY seq_in_index ASC") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, table.tableName );
			prestmt.setString( 3, i.indexName );
			rs = prestmt.executeQuery() ;
			while( rs.next() ) {
				column = new SqlActionColumn() ;
				
				column.columnName = rs.getString(1) ;
				
				i.columnList.add( column );
			}
			rs.close();
			
			for( SqlActionColumn c : i.columnList ) {
				prestmt = conn.prepareStatement("SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,numeric_precision,numeric_scale,column_key,column_comment FROM information_schema.COLUMNS WHERE table_schema=? AND table_name=? AND column_name=?") ;
				prestmt.setString( 1, database.databaseName );
				prestmt.setString( 2, table.tableName );
				prestmt.setString( 3, c.columnName );
				rs = prestmt.executeQuery() ;
				while( rs.next() ) {
					nret = SqlActionColumn.GetColumnFromResultSet( database, table, c, rs );
					if( nret != 0 ) {
						System.out.println( "GetColumnFromResultSet failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"] column["+c.columnName+"]" );
						return nret;
					}
				}
				rs.close();
			}
		}
		
		return 0;
	}

	public static int TravelAllIndexes( List<SqlActionIndex> sqlactionIndexList, int depth ) throws Exception {
		for( SqlActionIndex i : sqlactionIndexList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "indexName["+i.indexName+"] isUnique["+i.isUnique+"]" );

			SqlActionColumn.TravelAllColumns( i.columnList, depth+1 );
		}
		
		return 0;
	}
}
