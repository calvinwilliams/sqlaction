package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	List<SqlActionIndex>	indexList ;

	public static int GetAllTablesInDatabase( SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database ) throws Exception {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionTable		table ;
		String				tableType ;
		int					nret = 0 ;
		
		database.tableList = new LinkedList<SqlActionTable>() ;
		
		prestmt = conn.prepareStatement("SELECT table_name,table_type FROM information_schema.TABLES WHERE table_schema=?") ;
		prestmt.setString( 1, database.databaseName );
		rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			table = new SqlActionTable() ;
			
			table.tableName = rs.getString(1) ;
			if( ! table.tableName.equals(sqlactionConf.table) )
				continue;
			tableType = rs.getString(2) ;
			if( ! tableType.equals("BASE TABLE") )
				continue;
			
			database.tableList.add( table );
		}
		rs.close();
		
		for( SqlActionTable t : database.tableList ) {
			nret = SqlActionColumn.GetAllColumnsInTable( conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllColumnsInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
			
			nret = SqlActionIndex.GetAllIndexesInTable( conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllIndexesInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
		}
		
		return 0;
	}

	public static int TravelAllTables( List<SqlActionTable> sqlactionTableList, int depth ) throws Exception {
		for( SqlActionTable t : sqlactionTableList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"]" );
			
			SqlActionColumn.TravelAllColumns( t.columnList, depth+1 );
			
			SqlActionIndex.TravelAllIndexes( t.indexList, depth+1 );
		}
		
		return 0;
	}
}
