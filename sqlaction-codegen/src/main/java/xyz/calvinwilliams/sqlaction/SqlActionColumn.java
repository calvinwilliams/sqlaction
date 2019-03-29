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
	
//	private static int ParseOtherStatementForGeneratingMethodCode( String[] sa, int index, StringBuilder sql, StringBuilder methodName ) {
//		String		str ;
//		while( index < sa.length ) {
//			sql.append( " " + sa[index] );
//			str = sa[index].replace(',','_') ;
//			str = sa[index].replace('=','E') ;
//			str = sa[index].replace("<>","NE") ;
//			str = sa[index].replace(">","GT") ;
//			str = sa[index].replace(">=","GE") ;
//			str = sa[index].replace("<","LT") ;
//			str = sa[index].replace("<=","LE") ;
//			methodName.append( "_" + str );
//			
//			index++;
//		}
//		
//		return index;
//	}
	
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
		
		// out.append( "\n" );
		for( SqlActionColumn c : sqlactionColumnList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "columnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] isAutoIncrement["+c.isAutoIncrement+"] columnComment["+c.columnComment+"]" );
			
			/*
			String[] sa = c.columnName.split( "_" ) ;
			StringBuilder javaPropertyNameBuilder = new StringBuilder() ;
			for( String s : sa ) {
				if( javaPropertyNameBuilder.length() == 0 )
					javaPropertyNameBuilder.append( s.substring(0,1).toLowerCase(Locale.getDefault()) + s.substring(1) );
				else
					javaPropertyNameBuilder.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
			}
			c.javaPropertyName = javaPropertyNameBuilder.toString() ;
			
			ParseDefinePropertyForGeneratingClassCode( c, out );
			*/
		}
		
		/*
		for( String s : sqlactionConf.sqlactions ) {
			SqlActionSyntaxParser parser = new SqlActionSyntaxParser() ;
			nret = parser.ParseSyntax(s) ;
			if( nret != 0 ) {
				System.out.println( "SqlActionSyntaxParser.ParseSyntax failed["+nret+"]" );
				return nret;
			}
			
			if( parser.selectAllColumn == true ) {
				for( SqlActionFromTableToken tt : parser.fromTableTokenList ) {
					SqlActionSelectColumnToken ct = new SqlActionSelectColumnToken() ;
					ct.tableName = tt.tableName ;
					ct.tableAliasName = tt.tableAliasName ;
					
				}
			}
			
			if( parser.fromTableTokenList.size() > 0 ) {
				
			} else if( parser.insertTableName != null ) {
				
			} else if( parser.updateTableName != null ) {
				
			} else if( parser.deleteTableName != null ) {
				
			} else {
				System.out.println( "sqlaction["+s+"] invalid" );
				return -1;
			}
			
			
			
			
			
			
			s = s.toLowerCase() ;
			String[] sa = s.split( " |\t|\r|\n" ) ;
			if( sa[0].equals("select") && sa[2].equals("from") && sa[3].equals(sqlactionConf.javaTableName) ) {
				nret = ParseSelectSqlForGeneratingMethodCode( dbserverConf, sqlactionConf, sqlactionColumnList, s, sa, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseSelectSqlForGeneratingMethodCode["+s+"] failed["+nret+"]" );
					return nret;
				}
			} else if( sa[0].equals("insert") && sa[1].equals("into") && sa[2].equals(sqlactionConf.javaTableName) ) {
				nret = ParseInsertSqlForGeneratingMethodCode( dbserverConf, sqlactionConf, sqlactionColumnList, s, sa, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseInsertSqlForGeneratingMethodCode["+s+"] failed["+nret+"]" );
					return nret;
				}
			} else if( sa[0].equals("update") && sa[1].equals(sqlactionConf.javaTableName) && sa[2].equals("set") ) {
				nret = ParseUpdateSqlForGeneratingMethodCode( dbserverConf, sqlactionConf, sqlactionColumnList, s, sa, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseUpdateSqlForGeneratingMethodCode["+s+"] failed["+nret+"]" );
					return nret;
				}
			} else if( sa[0].equals("delete") && sa[1].equals("from") && sa[2].equals(sqlactionConf.javaTableName) ) {
				nret = ParseDeleteSqlForGeneratingMethodCode( dbserverConf, sqlactionConf, sqlactionColumnList, s, sa, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseDeleteSqlForGeneratingMethodCode["+s+"] failed["+nret+"]" );
					return nret;
				}
			} else {
				System.out.println( "sqlaction["+sa[0]+"] invalid" );
				return -1;
			}
		}
		
		out.append( "\n" );
		*/
		
		return 0;
	}
	
	public static int DumpDefineProperty( SqlActionColumn c, StringBuilder out ) {
	
		switch( c.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t" + "boolean			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t" + "byte			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t" + "short			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t" + "int				"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t" + "int				"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t" + "long			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t" + "float			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t" + "double			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t" + "double			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t" + "BigDecimal		"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t" + "BigDecimal		"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t" + "String			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t" + "String			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t" + "java.sql.Date	"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t" + "java.sql.Time	"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t" + "java.sql.Date	"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t" + "Timestamp		"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t" + "java.sql.Date	"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t" + "byte[]			"+c.javaPropertyName+" ;" );
				break;
			default :
				out.append( "\t" + "String			"+c.javaPropertyName+" ;" );
				break;
		}
		
		if( c.columnComment != null && ! c.columnComment.isEmpty() ) {
			out.append( " // "+c.columnComment+"\n" );
		} else {
			out.append( "\n" );
		}
		
		return 0;
	}
	
	public static int DumpSelectOutputColumn( int columnIndex, SqlActionColumn column, StringBuilder out ) {
		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBoolean( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getByte( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getShort( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getLong( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getFloat( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDate( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTimestamp( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
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
				out.append( "\t\t" + "prestmt.setBoolean( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t" + "prestmt.setByte( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t" + "prestmt.setShort( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t" + "prestmt.setLong( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t" + "prestmt.setFloat( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t" + "prestmt.setTime( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t" + "prestmt.setTimestamp( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
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
				out.append( "\t\t" + "prestmt.setBoolean( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t" + "prestmt.setByte( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t" + "prestmt.setShort( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t" + "prestmt.setLong( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t" + "prestmt.setFloat( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t" + "prestmt.setTime( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t" + "prestmt.setTimestamp( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", "+columnValue+" );\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	
}
