package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionColumn {
	String					columnName ;
	String					columnDefault ;
	boolean					isNullable ;
	SqlActionJdbcDataType	dataType ;
	long					columnMaximumLength ;
	int						numericPrecision ;
	int						numericScale ;
	boolean					isPrimaryKey ;
	String					columnComment ;
	
	String					javaPropertyName ;

	public static int GetColumnFromResultSet( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, SqlActionTable table, SqlActionColumn column, ResultSet rs ) throws Exception {
		column.columnName = rs.getString(1) ;
		column.columnDefault = rs.getString(2) ;
		if( rs.getString(3).equals("NO") )
			column.isNullable = false ;
		else
			column.isNullable = true ;
		switch( rs.getString(4) ) {
			case "bit" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIT ;
				break;
			case "tinyint" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TINYINT ;
				break;
			case "smallint" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_SMALLINT ;
				break;
			case "mediumint" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_MEDIUMINT ;
				break;
			case "int" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
				break;
			case "bigint" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIGINT ;
				break;
			case "real" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_REAL ;
				break;
			case "float" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_FLOAT ;
				break;
			case "double" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DOUBLE ;
				break;
			case "decimal" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DECIMAL ;
				break;
			case "numeric" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_NUMBERIC ;
				break;
			case "char" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_CHAR ;
				break;
			case "varchar" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
				break;
			case "date" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATE ;
				break;
			case "time" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIME ;
				break;
			case "datetime" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATETIME ;
				break;
			case "timestamp" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIMESTAMP ;
				break;
			case "year" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_YEAR ;
				break;
			case "binary" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BINARY ;
				break;
			case "varbinary" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARBINARY ;
				break;
			case "blob" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BLOB ;
				break;
			case "tinyblob" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TINYBLOB ;
				break;
			case "mediumblob" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_MEDIUMBLOB ;
				break;
			case "longblob" :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_LONGBLOB ;
				break;
			default :
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
				break;
		}
		column.columnMaximumLength = rs.getLong(5);
		column.numericPrecision = rs.getInt(6) ;
		column.numericScale = rs.getInt(7) ;
		if( rs.getString(8).equals("PRI") )
			column.isPrimaryKey = true ;
		else
			column.isPrimaryKey = false ;
		column.columnComment = rs.getString(9) ;
		
		return 0;
	}
	
	public static int GetAllColumnsInTable( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception  {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionColumn		column ;
		int					nret = 0 ;
		
		table.columnList = new LinkedList<SqlActionColumn>() ;
		
		if( dbserverConf.dbms.equals(SqlActionDatabase.SQLACTION_DBMS_MYSQL) ) {
			prestmt = conn.prepareStatement("SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,numeric_precision,numeric_scale,column_key,column_comment FROM information_schema.COLUMNS WHERE table_schema=? AND table_name=? ORDER BY ordinal_position ASC") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, table.tableName );
		}
		rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			column = new SqlActionColumn() ;
			
			nret = GetColumnFromResultSet( dbserverConf, sqlactionConf, database, table, column, rs );
			if( nret != 0 ) {
				System.out.println( "GetColumnFromResultSet failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"] column["+column.columnName+"]" );
				return nret;
			}
			
			table.columnList.add( column );
		}
		rs.close();
		
		return 0;
	}

	public static int TravelAllColumnsForGeneratingClassCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, int depth, StringBuilder out ) throws Exception {
		
		out.append( "\n" );
		for( SqlActionColumn c : sqlactionColumnList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "columnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] columnComment["+c.columnComment+"]" );
			
			String[] sa = c.columnName.split( "_" ) ;
			StringBuilder javaPropertyNameBuilder = new StringBuilder() ;
			for( String s : sa ) {
				if( javaPropertyNameBuilder.length() == 0 )
					javaPropertyNameBuilder.append( s.substring(0,1).toLowerCase(Locale.getDefault()) + s.substring(1) );
				else
					javaPropertyNameBuilder.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
			}
			c.javaPropertyName = javaPropertyNameBuilder.toString() ;
			
			out.append( "\t" );
			out.append( "// "+c.columnComment+"\n" );
			
			out.append( "\t" );
			switch( c.dataType ) {
				case SQLACTION_DATA_TYPE_BIT :
					out.append( "boolean "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_TINYINT :
					out.append( "byte "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_SMALLINT :
					out.append( "short "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_MEDIUMINT :
					out.append( "int "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_INTEGER :
					out.append( "int "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_BIGINT :
					out.append( "long "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_REAL :
					out.append( "float "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_FLOAT :
					out.append( "double "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_DOUBLE :
					out.append( "double "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_DECIMAL :
					out.append( "BigDecimal "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_NUMBERIC :
					out.append( "BigDecimal "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_CHAR :
					out.append( "String "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_VARCHAR :
					out.append( "String "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_DATE :
					out.append( "Date "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_TIME :
					out.append( "Time "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_DATETIME :
					out.append( "Date "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_TIMESTAMP :
					out.append( "Timestamp "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_YEAR :
					out.append( "Date "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_BINARY :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_VARBINARY :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_BLOB :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_TINYBLOB :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_MEDIUMBLOB :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				case SQLACTION_DATA_TYPE_LONGBLOB :
					out.append( "byte[] "+c.javaPropertyName+" ;" );
					break;
				default :
					out.append( "String "+c.javaPropertyName+" ;" );
					break;
			}
			out.append( "\n" );
		}
		
		out.append( "\n" );
		for( String s : sqlactionConf.sqlactions ) {
			String[] sa = s.split( " " ) ;
			int n = 0 ;
			
			
		}
		
		out.append( "\n" );
		
		return 0;
	}
}
