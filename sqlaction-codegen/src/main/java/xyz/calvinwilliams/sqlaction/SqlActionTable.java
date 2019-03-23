package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	List<SqlActionIndex>	indexList ;

	public static int GetAllTablesInDatabase( Connection conn, SqlActionDatabase database ) throws Exception {
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

	public static int TravelAllTables( List<SqlActionTable> sqlactionTableList ) throws Exception {
		for( SqlActionTable t : sqlactionTableList ) {
			System.out.println( "\ttableName["+t.tableName+"]" );
			
			SqlActionColumn.TravelAllColumns( t.columnList );
			
			SqlActionIndex.TravelAllIndexes( t.indexList );
			
			/*
			for( SqlActionColumn c : t.sqlactionColumnList ) {
				System.out.println( "\t\tcolumnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] columnComment["+c.columnComment+"]" );
			}

			for( SqlActionIndex i : t.sqlactionIndexList ) {
				System.out.println( "\t\tindexName["+i.indexName+"] isUnique["+i.isUnique+"]" );

				for( SqlActionColumn ic : i.sqlactionColumnList ) {
					System.out.println( "\t\t\tcolumn["+ic.columnName+"] columnDefault["+ic.columnDefault+"] isNullable["+ic.isNullable+"] DataType["+ic.dataType+"] columnLength["+ic.columnMaximumLength+"] numericPrecision["+ic.numericPrecision+"] numericScale["+ic.numericScale+"] isPrimaryKey["+ic.isPrimaryKey+"] columnComment["+ic.columnComment+"]" );
				}
			}
			*/
		}
		
		return 0;
	}
}
