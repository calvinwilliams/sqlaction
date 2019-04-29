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
	
	String					javaPropertyType ;
	int						javaDefineTabsBetweenTypeAndName ;
	String					javaPropertyName ;
	
	public static String getUserDefineDataType( DbServerConf dbserverConf, String sourceDataTypeAndLength ) {
		if( dbserverConf.userDefineDataTypes == null )
			return null;
		
		for( DbServerConfUserDefineDataTypes t : dbserverConf.userDefineDataTypes ) {
			if( SqlActionUtil.wildcardMatch( t.source, sourceDataTypeAndLength ) == 0 )
				return t.redefine;
		}
		
		return null;
	}
	
	public static int getColumnMetadataFromResultSet_for_MYSQL( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, SqlActionTable table, SqlActionColumn column, ResultSet rs ) throws Exception {
		String		sourceDataType ;
		String		sourceDataTypeAndLength ;
		String		userDefineDataTypeAndLength ;
		
		column.columnName = rs.getString(1) ;
		column.columnDefault = rs.getString(2) ;
		if( rs.getString(3).equals("NO") )
			column.isNullable = false ;
		else
			column.isNullable = true ;
		sourceDataType = rs.getString(4) ;
		column.columnMaximumLength = rs.getLong(5);
		column.numericPrecision = rs.getInt(6) ;
		column.numericScale = rs.getInt(7) ;
		sourceDataTypeAndLength = sourceDataType+","+column.columnMaximumLength+","+column.numericPrecision+","+column.numericScale ;
		userDefineDataTypeAndLength = getUserDefineDataType( dbserverConf, sourceDataTypeAndLength ) ;
		if( userDefineDataTypeAndLength != null ) {
			String[] sa = userDefineDataTypeAndLength.split(",") ;
			sourceDataType = sa[0] ;
			if( ! sa[1].equals("*") )
				column.columnMaximumLength = Long.parseLong(sa[1]) ;
			if( ! sa[2].equals("*") )
				column.numericPrecision = Integer.parseInt(sa[2]) ;
			if( ! sa[3].equals("*") )
				column.numericScale = Integer.parseInt(sa[3]) ;
		}
		if( sourceDataType.equalsIgnoreCase("bit") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIT ;
			column.javaPropertyType = "boolean" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("tinyint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TINYINT ;
			column.javaPropertyType = "byte" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("smallint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_SMALLINT ;
			column.javaPropertyType = "short" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("mediumint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_MEDIUMINT ;
			column.javaPropertyType = "int" ;
			column.javaDefineTabsBetweenTypeAndName = 4 ;
		} else if( sourceDataType.equalsIgnoreCase("int") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
			column.javaPropertyType = "int" ;
			column.javaDefineTabsBetweenTypeAndName = 4 ;
		} else if( sourceDataType.equalsIgnoreCase("bigint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIGINT ;
			column.javaPropertyType = "long" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("real") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_REAL ;
			column.javaPropertyType = "float" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("float") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_FLOAT ;
			column.javaPropertyType = "double" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("double") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DOUBLE ;
			column.javaPropertyType = "double" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("decimal") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DECIMAL ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("numeric") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_NUMBERIC ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("char") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_CHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("varchar") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("date") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATE ;
			column.javaPropertyType = "java.sql.Date" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("time") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIME ;
			column.javaPropertyType = "java.sql.Time" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("datetime") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATETIME ;
			column.javaPropertyType = "java.sql.Date" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("timestamp") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIMESTAMP ;
			column.javaPropertyType = "Timestamp" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("year") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_YEAR ;
			column.javaPropertyType = "java.sql.Date" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("binary") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BINARY ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("varbinary") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARBINARY ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("blob") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BLOB ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("tinyblob") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TINYBLOB ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("mediumblob") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_MEDIUMBLOB ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("longblob") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_LONGBLOB ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else {
			System.out.println( "*** ERROR : sourceDataType["+sourceDataType+"] not support" );
			return -1;
		}
		if( rs.getString(8).equals("PRI") ) {
			column.isPrimaryKey = true ;
		} else {
			column.isPrimaryKey = false ;
		}
		if( rs.getString(9).equals("auto_increment") )
			column.isAutoIncrement = true ;
		else
			column.isAutoIncrement = false ;
		column.columnComment = rs.getString(10) ;

		return 0;
	}

	public static int getColumnMetadataFromResultSet_for_POSTGRESQL( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, SqlActionTable table, SqlActionColumn column, ResultSet rs ) throws Exception {
		String		sourceDataType ;
		String		sourceDataTypeAndLength ;
		String		userDefineDataTypeAndLength ;

		column.columnName = rs.getString(1) ;
		column.columnDefault = rs.getString(2) ;
		if( rs.getString(3).equals("NO") )
			column.isNullable = false ;
		else
			column.isNullable = true ;
		sourceDataType = rs.getString(4) ;
		column.columnMaximumLength = rs.getLong(5);
		column.numericPrecision = rs.getInt(6) ;
		column.numericScale = rs.getInt(7) ;
		sourceDataTypeAndLength = sourceDataType+","+column.columnMaximumLength+","+column.numericPrecision+","+column.numericScale ;
		userDefineDataTypeAndLength = getUserDefineDataType( dbserverConf, sourceDataTypeAndLength ) ;
		if( userDefineDataTypeAndLength != null ) {
			String[] sa = userDefineDataTypeAndLength.split(",") ;
			sourceDataType = sa[0] ;
			if( ! sa[1].equals("*") )
				column.columnMaximumLength = Long.parseLong(sa[1]) ;
			if( ! sa[2].equals("*") )
				column.numericPrecision = Integer.parseInt(sa[2]) ;
			if( ! sa[3].equals("*") )
				column.numericScale = Integer.parseInt(sa[3]) ;
		}
		if( sourceDataType.equalsIgnoreCase("boolean") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIT ;
			column.javaPropertyType = "boolean" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("smallint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_SMALLINT ;
			column.javaPropertyType = "short" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("integer") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
			column.javaPropertyType = "int" ;
			column.javaDefineTabsBetweenTypeAndName = 4 ;
		} else if( sourceDataType.equalsIgnoreCase("bigint") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_BIGINT ;
			column.javaPropertyType = "long" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("real") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_REAL ;
			column.javaPropertyType = "float" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("double precision") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DOUBLE ;
			column.javaPropertyType = "double" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("decimal") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DECIMAL ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("numeric") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_NUMBERIC ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("character") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_CHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("character varying") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("text") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("date") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATE ;
			column.javaPropertyType = "java.sql.Date" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("time") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIME ;
			column.javaPropertyType = "java.sql.Time" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("timestamp") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIMESTAMP ;
			column.javaPropertyType = "Timestamp" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("bytea") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARBINARY ;
			column.javaPropertyType = "byte[]" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else {
			System.out.println( "*** ERROR : sourceDataType["+sourceDataType+"] not support" );
			return -1;
		}

		return 0;
	}
	
	public static int getColumnMetadataFromResultSet_for_ORACLE( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, SqlActionTable table, SqlActionColumn column, ResultSet rs ) throws Exception {
		String		sourceDataType ;
		String		sourceDataTypeAndLength ;
		String		userDefineDataTypeAndLength ;
		
		column.columnName = rs.getString(1) ;
		column.columnDefault = rs.getString(2) ;
		if( rs.getString(3).equals("NO") )
			column.isNullable = false ;
		else
			column.isNullable = true ;
		sourceDataType = rs.getString(4) ;
		column.columnMaximumLength = rs.getLong(5);
		column.numericPrecision = rs.getInt(6) ;
		column.numericScale = rs.getInt(7) ;
		sourceDataTypeAndLength = sourceDataType+","+column.columnMaximumLength+","+column.numericPrecision+","+column.numericScale ;
		userDefineDataTypeAndLength = getUserDefineDataType( dbserverConf, sourceDataTypeAndLength ) ;
		if( userDefineDataTypeAndLength != null ) {
			String[] sa = userDefineDataTypeAndLength.split(",") ;
			sourceDataType = sa[0] ;
			if( ! sa[1].equals("*") )
				column.columnMaximumLength = Long.parseLong(sa[1]) ;
			if( ! sa[2].equals("*") )
				column.numericPrecision = Integer.parseInt(sa[2]) ;
			if( ! sa[3].equals("*") )
				column.numericScale = Integer.parseInt(sa[3]) ;
		}
		if( sourceDataType.equalsIgnoreCase("NUMBER") ) {
			if( column.numericScale == 0 ) {
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
				column.javaPropertyType = "int" ;
				column.javaDefineTabsBetweenTypeAndName = 4 ;
			} else {
				column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DOUBLE ;
				column.javaPropertyType = "double" ;
				column.javaDefineTabsBetweenTypeAndName = 4 ;
			}
		} else if( sourceDataType.equalsIgnoreCase("INT") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
			column.javaPropertyType = "int" ;
			column.javaDefineTabsBetweenTypeAndName = 4 ;
		} else if( sourceDataType.equalsIgnoreCase("integer") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_INTEGER ;
			column.javaPropertyType = "int" ;
			column.javaDefineTabsBetweenTypeAndName = 4 ;
		} else if( sourceDataType.equalsIgnoreCase("real") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_REAL ;
			column.javaPropertyType = "float" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("double precision") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DOUBLE ;
			column.javaPropertyType = "double" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("decimal") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DECIMAL ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("numeric") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_NUMBERIC ;
			column.javaPropertyType = "BigDecimal" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else if( sourceDataType.equalsIgnoreCase("VARCHAR2") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_VARCHAR ;
			column.javaPropertyType = "String" ;
			column.javaDefineTabsBetweenTypeAndName = 3 ;
		} else if( sourceDataType.equalsIgnoreCase("DATE") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_DATE ;
			column.javaPropertyType = "java.sql.Date" ;
			column.javaDefineTabsBetweenTypeAndName = 1 ;
		} else if( sourceDataType.equalsIgnoreCase("TIMESTAMP") ) {
			column.dataType = SqlActionJdbcDataType.SQLACTION_DATA_TYPE_TIMESTAMP ;
			column.javaPropertyType = "Timestamp" ;
			column.javaDefineTabsBetweenTypeAndName = 2 ;
		} else {
			System.out.println( "*** ERROR : sourceDataType["+sourceDataType+"] not support" );
			return -1;
		}
		
		return 0;
	}
	
	public static int fetchAllColumnsMetadataInTable( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception  {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionColumn		column ;
		int					nret = 0 ;
		
		table.columnList = new LinkedList<SqlActionColumn>() ;
		
		if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
			prestmt = conn.prepareStatement("SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,numeric_precision,numeric_scale,column_key,extra,column_comment FROM information_schema.COLUMNS WHERE table_schema=? AND table_name=? ORDER BY ordinal_position ASC") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, table.tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
			prestmt = conn.prepareStatement("SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,numeric_precision,numeric_scale FROM information_schema.COLUMNS WHERE table_catalog=? AND table_name=? ORDER BY ordinal_position ASC") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, table.tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
			prestmt = conn.prepareStatement("SELECT column_name,data_default,nullable,data_type,data_length,data_precision,data_scale FROM user_tab_columns WHERE table_name=? ORDER BY column_id ASC") ;
			prestmt.setString( 1, table.tableName );
		}
		rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			column = new SqlActionColumn() ;
			
			if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
				nret = getColumnMetadataFromResultSet_for_MYSQL( dbserverConf, sqlactionConf, database, table, column, rs );
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
				nret = getColumnMetadataFromResultSet_for_POSTGRESQL( dbserverConf, sqlactionConf, database, table, column, rs );
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
				nret = getColumnMetadataFromResultSet_for_ORACLE( dbserverConf, sqlactionConf, database, table, column, rs );
			}
			if( nret != 0 ) {
				System.out.println( "*** ERROR : GetColumnFromResultSet_for__ failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"] column["+column.columnName+"]" );
				return nret;
			}
			
			column.javaPropertyName = SqlActionColumn.columnToJavaProperty( column.columnName ) ;
			
			if( column.isPrimaryKey )
				table.primaryKey = column ;
			
			table.columnList.add( column );
		}
		rs.close();
		
		return 0;
	}
	
	public static SqlActionColumn findColumn( List<SqlActionColumn> sqlactionColumnList, String columnName ) {
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.columnName.equalsIgnoreCase(columnName) )
				return c; 
		}
		
		return null;
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
	
	public static int travelAllColumnsMetadata( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, int depth ) {
		for( SqlActionColumn c : sqlactionColumnList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "columnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] isAutoIncrement["+c.isAutoIncrement+"] columnComment["+c.columnComment+"] javaPropertyType["+c.javaPropertyType+"] javaPropertyName["+c.javaPropertyName+"]" );
		}
		
		return 0;
	}
	
	public static int dumpDefineProperty( SqlActionColumn c, StringBuilder out ) {
		out.append("\t").append(c.javaPropertyType).append(SqlActionUtil._8tabsArray,0,c.javaDefineTabsBetweenTypeAndName).append(c.javaPropertyName).append(" ;");
		
		if( c.columnComment != null && ! c.columnComment.isEmpty() ) {
			out.append(" // ").append(c.columnComment);
		}
		
		if( c.isPrimaryKey ) {
			out.append(" // PRIMARY KEY");
		}
		
		out.append("\n");
		
		return 0;
	}
	
	public static int dumpSelectOutputColumn( String tabs, int columnIndex, SqlActionColumn column, String javaObjectNameAndPropertyName, StringBuilder out ) {
		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBoolean( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getByte( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getShort( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getLong( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getFloat( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getDate( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getTimestamp( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append(tabs).append(javaObjectNameAndPropertyName).append(" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int dumpSetInputColumn( int columnIndex, SqlActionColumn column, String columnValue, StringBuilder out ) {

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
	
	public static int dumpWhereInputColumn( int columnIndex, SqlActionColumn column, String columnValue, StringBuilder out ) {

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
