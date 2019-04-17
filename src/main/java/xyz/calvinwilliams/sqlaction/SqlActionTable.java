/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	List<SqlActionIndex>	indexList ;

	String					javaSaoClassName ;
	String					javaSauClassName ;
	String					javaSaoFileName ;
	String					javaSauFileName ;
	String					javaObjectName ;
	
	public static SqlActionTable fetchTableMetadataInDatabase( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable tableCache, String tableName ) throws Exception {
		SqlActionTable		table ;
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		String				tableType ;
		int					nret = 0 ;
		
		if( tableCache != null ) {
			if( tableName.equalsIgnoreCase(tableCache.tableName) )
				return tableCache;
		}
		
		table = findTable( database.tableList, tableName ) ;
		if( table != null )
			return table;
		
		if( dbserverConf.dbms.equals(SqlActionDatabase.SQLACTION_DBMS_MYSQL) ) {
			prestmt = conn.prepareStatement("SELECT table_name,table_type FROM information_schema.TABLES WHERE table_schema=? AND table_name=?") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, tableName );
		}
		rs = prestmt.executeQuery() ;
		rs.next();
		
		table = new SqlActionTable() ;
		table.tableName = rs.getString(1) ;
		tableType = rs.getString(2) ;
		if( ! tableType.equals("BASE TABLE") )
			return null;
		database.tableList.add( table );
		
		rs.close();
		
		nret = SqlActionColumn.fetchAllColumnsMetadataInTable( dbserverConf, sqlactionConf, conn, database, table ) ;
		if( nret != 0 ) {
			System.out.println( "GetAllColumnsInTable failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"]" );
			return null;
		}
		
		nret = SqlActionIndex.fetchAllIndexesMetadataInTable( dbserverConf, sqlactionConf, conn, database, table ) ;
		if( nret != 0 ) {
			System.out.println( "GetAllIndexesInTable failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"]" );
			return null;
		}
		
		String[] sa = table.tableName.split( "_" ) ;
		StringBuilder sb = new StringBuilder() ;
		for( String s : sa ) {
			sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
		}
		table.javaSaoClassName = sb.toString() + "SAO" ;
		table.javaSauClassName = sb.toString() + "SAU" ;
		table.javaSaoFileName = table.javaSaoClassName + ".java" ;
		table.javaSauFileName = table.javaSauClassName + ".java" ;
		table.javaObjectName = sb.toString().substring(0,1).toLowerCase(Locale.getDefault()) + sb.toString().substring(1) ;
		
		return table;
	}
	
	public static int travelTable( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, String tableName, int depth ) {
		for( SqlActionTable t : database.tableList ) {
			if( ! t.tableName.equalsIgnoreCase(tableName) )
				continue;
			
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"]" );
			
			SqlActionColumn.travelAllColumnsMetadata( dbserverConf, sqlactionConf, t.columnList, depth+1 );
			
			SqlActionIndex.travelAllIndexesMetadata( dbserverConf, sqlactionConf, t.indexList, depth+1 );
		}
		
		return 0;
	}
	
	public static SqlActionTable findTable( List<SqlActionTable> sqlactionTableList, String tableName ) {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.tableName.equalsIgnoreCase(tableName) )
				return t;
		}
		
		return null;
	}
}
