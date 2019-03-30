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
	boolean					isAutoIncrement ;
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
		if( rs.getString(9).equals("auto_increment") )
			column.isAutoIncrement = true ;
		else
			column.isAutoIncrement = false ;
		column.columnComment = rs.getString(10) ;
		
		return 0;
	}
	
	public static int GetAllColumnsInTable( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception  {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionColumn		column ;
		int					nret = 0 ;
		
		table.columnList = new LinkedList<SqlActionColumn>() ;
		
		if( dbserverConf.dbms.equals(SqlActionDatabase.SQLACTION_DBMS_MYSQL) ) {
			prestmt = conn.prepareStatement("SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,numeric_precision,numeric_scale,column_key,extra,column_comment FROM information_schema.COLUMNS WHERE table_schema=? AND table_name=? ORDER BY ordinal_position ASC") ;
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
			column.javaPropertyName = SqlActionColumn.columnToJavaProperty( column.columnName ) ;
			
			table.columnList.add( column );
		}
		rs.close();
		
		return 0;
	}
	
	public static SqlActionColumn FindColumn( List<SqlActionColumn> sqlactionColumnList, String columnName ) {
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.columnName.equals(columnName) )
				return c; 
		}
		
		return null;
	}
	
	public static String operatorTo( String operator ) {
		
		switch( operator ) {
			case "=" :
				return "E";
			case "<>" :
				return "NE";
			case ">" :
				return "GT";
			case ">=" :
				return "GE";
			case "<" :
				return "LT";
			case "<=" :
				return "LE";
			default :
				return operator;
		}
	}
	
	public static String columnToJavaProperty( String columnName ) {
		String[] sa = columnName.split( "_" ) ;
		StringBuilder javaPropertyNameBuilder = new StringBuilder() ;
		for( String s : sa ) {
			if( javaPropertyNameBuilder.length() == 0 )
				javaPropertyNameBuilder.append( s.substring(0,1).toLowerCase(Locale.getDefault()) + s.substring(1) );
			else
				javaPropertyNameBuilder.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
		}
		return javaPropertyNameBuilder.toString() ;
	}
	
	public static int TravelAllColumns( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, int depth, StringBuilder out ) throws Exception {
		int		nret = 0 ;
		
		for( SqlActionColumn c : sqlactionColumnList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "columnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] isAutoIncrement["+c.isAutoIncrement+"] columnComment["+c.columnComment+"]" );
		}
		
		return 0;
	}
	
	public static int DumpDefineProperty( SqlActionColumn c, StringBuilder out ) {
	
		switch( c.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append("\t").append("boolean			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append("\t").append("byte			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append("\t").append("short			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append("\t").append("int				").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append("\t").append("int				").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append("\t").append("long			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append("\t").append("float			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append("\t").append("double			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append("\t").append("double			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append("\t").append("BigDecimal		").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append("\t").append("BigDecimal		").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append("\t").append("String			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append("\t").append("String			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append("\t").append("java.sql.Date	").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append("\t").append("java.sql.Time	").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append("\t").append("java.sql.Date	").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append("\t").append("Timestamp		").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append("\t").append("java.sql.Date	").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append("\t").append("byte[]			").append(c.javaPropertyName).append(" ;");
				break;
			default :
				out.append("\t").append("String			").append(c.javaPropertyName).append(" ;");
				break;
		}
		
		if( c.columnComment != null && ! c.columnComment.isEmpty() ) {
			out.append(" // ").append(c.columnComment).append("\n");
		} else {
			out.append("\n");
		}
		
		return 0;
	}
	
	public static int DumpSelectOutputColumn( int columnIndex, SqlActionColumn column, String columnValue, StringBuilder out ) {
		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBoolean( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getByte( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getShort( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append("\t\t\t").append(columnValue).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getLong( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append("\t\t\t").append(columnValue).append(" = rs.getFloat( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append("\t\t\t").append(columnValue).append(" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append("\t\t\t").append(columnValue).append(" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append("\t\t\t").append(columnValue).append(" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append("\t\t\t").append(columnValue).append(" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append("\t\t\t").append(columnValue).append(" = rs.getDate( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append("\t\t\t").append(columnValue).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append("\t\t\t").append(columnValue).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append("\t\t\t").append(columnValue).append(" = rs.getTimestamp( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append("\t\t\t").append(columnValue).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append("\t\t\t").append(columnValue).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int DumpSetInputColumn( int columnIndex, SqlActionColumn column, String columnValue, StringBuilder out ) {

		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append("\t\t").append("prestmt.setBoolean( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append("\t\t").append("prestmt.setByte( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append("\t\t").append("prestmt.setShort( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append("\t\t").append("prestmt.setLong( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append("\t\t").append("prestmt.setFloat( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append("\t\t").append("prestmt.setDouble( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append("\t\t").append("prestmt.setDouble( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append("\t\t").append("prestmt.setBigDecimal( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append("\t\t").append("prestmt.setBigDecimal( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append("\t\t").append("prestmt.setString( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append("\t\t").append("prestmt.setString( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append("\t\t").append("prestmt.setTime( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append("\t\t").append("prestmt.setTimestamp( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int DumpWhereInputColumn( int columnIndex, SqlActionColumn column, String columnValue, StringBuilder out ) {

		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append("\t\t").append("prestmt.setBoolean( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append("\t\t").append("prestmt.setByte( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append("\t\t").append("prestmt.setShort( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append("\t\t").append("prestmt.setLong( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append("\t\t").append("prestmt.setFloat( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append("\t\t").append("prestmt.setDouble( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append("\t\t").append("prestmt.setDouble( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append("\t\t").append("prestmt.setBigDecimal( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append("\t\t").append("prestmt.setBigDecimal( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append("\t\t").append("prestmt.setString( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append("\t\t").append("prestmt.setString( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append("\t\t").append("prestmt.setTime( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append("\t\t").append("prestmt.setTimestamp( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append("\t\t").append("prestmt.setDate( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append("\t\t").append("prestmt.setBytes( ").append(columnIndex).append(", ").append(columnValue).append(" );\n");
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	
}
