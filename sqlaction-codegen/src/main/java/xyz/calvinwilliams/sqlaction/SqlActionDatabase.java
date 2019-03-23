package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionDatabase {
	String					databaseName ;
	List<SqlActionTable>	tableList ;
	
	public static int GetAllDatabases( SqlActionConf sqlactionConf, Connection conn, List<SqlActionDatabase> sqlactionDatabaseList ) throws Exception {
		Statement			stmt = null ;
		ResultSet			rs ;
		SqlActionDatabase	database ;
		int					nret = 0 ;
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT schema_name FROM information_schema.SCHEMATA") ;
		while( rs.next() ) {
			database = new SqlActionDatabase() ;

			database.databaseName = rs.getString(1) ;
			if( ! database.databaseName.equals(sqlactionConf.database) )
				continue;

			sqlactionDatabaseList.add( database );
		}
		rs.close();
		
		for( SqlActionDatabase d : sqlactionDatabaseList ) {
			nret = SqlActionTable.GetAllTablesInDatabase( sqlactionConf, conn, d ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllTablesInDatabase failed["+nret+"] , schema["+d.databaseName+"]" );
				return nret;
			}
		}
		
		return 0;
	}
	
	public static int TravelAllDatabases( List<SqlActionDatabase> sqlactionDatabaseList, int depth ) throws Exception {
		for( SqlActionDatabase d : sqlactionDatabaseList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "databaseName["+d.databaseName+"]" );
			
			SqlActionTable.TravelAllTables( d.tableList, depth+1 );
		}
		
		return 0;
	}
}
