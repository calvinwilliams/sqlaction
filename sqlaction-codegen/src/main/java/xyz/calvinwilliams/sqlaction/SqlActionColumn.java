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
			
			table.columnList.add( column );
		}
		rs.close();
		
		return 0;
	}
	
	private static SqlActionColumn FindColumn( List<SqlActionColumn> sqlactionColumnList, String columnName ) {
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.columnName.equals(columnName) )
				return c; 
		}
		
		return null;
	}
	
	private static int ParseSelectStatementForGeneratingMethodCode( List<SqlActionColumn> sqlactionColumnList, String selectOutputputColumnNames, List<SqlActionColumn> selectOutputputColumnList ) {
		SqlActionColumn column ;
		String[] sa = selectOutputputColumnNames.split( "," ) ;
		
		for( String columnName : sa ) {
			column = FindColumn( sqlactionColumnList , columnName ) ;
			if( column == null ) {
				System.out.println( "columnName["+columnName+"] not found" );
				return -1;
			}
			selectOutputputColumnList.add( column );
		}
		
		return 0;
	}
	
	private static int ParseWhereStatementForGeneratingMethodCode( List<SqlActionColumn> sqlactionColumnList, String[] sa, int index, List<SqlActionColumn> whereInputputColumnList, StringBuilder sql, StringBuilder methodName ) {
		SqlActionColumn column ;
		
		while( index < sa.length ) {
			sql.append( " " + sa[index] );
			methodName.append( "_" + sa[index] );
			
			column = FindColumn( sqlactionColumnList , sa[index] ) ;
			if( column == null ) {
				System.out.println( "columnName["+sa[index]+"] not found" );
				return -1;
			}
			whereInputputColumnList.add( column );
			
			index++;
			if( index >= sa.length ) {
				System.out.println( "sqlaction["+sa.toString()+"] invalid" );
				return -1;
			}
			sql.append( sa[index] + "?" );
			if( sa[index].equals("=") ) {
				methodName.append( "_E" );
			} else if( sa[index].equals("<>") ) {
				methodName.append( "_NE" );
			} else if( sa[index].equals(">") ) {
				methodName.append( "_GT" );
			} else if( sa[index].equals(">=") ) {
				methodName.append( "_GE" );
			} else if( sa[index].equals("<") ) {
				methodName.append( "_LT" );
			} else if( sa[index].equals("<=") ) {
				methodName.append( "_LE" );
			} else {
				System.out.println( "word["+sa[index]+"] unexpected in ["+sa.toString()+"]" );
				return -1;
			}
			
			index++;
			if( index >= sa.length )
				break;
			if( ! sa[index].equals("and") )
				break;
			sql.append( " AND" );
			methodName.append( "_AND" );
			
			index++;
		}
		
		return index;
	}
	
	private static int ParseOtherStatementForGeneratingMethodCode( String[] sa, int index, StringBuilder sql, StringBuilder methodName ) {
		String		str ;
		while( index < sa.length ) {
			sql.append( " " + sa[index] );
			str = sa[index].replace(',','_') ;
			str = sa[index].replace('=','E') ;
			str = sa[index].replace("<>","NE") ;
			str = sa[index].replace(">","GT") ;
			str = sa[index].replace(">=","GE") ;
			str = sa[index].replace("<","LT") ;
			str = sa[index].replace("<=","LE") ;
			methodName.append( "_" + str );
			
			index++;
		}
		
		return index;
	}
	
	public static int ParseWhereInputColumnForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, int columnIndex, SqlActionColumn column, StringBuilder out ) {

		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t\t" + "prestmt.setBoolean( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t" + "prestmt.setByte( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t" + "prestmt.setShort( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t" + "prestmt.setLong( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t" + "prestmt.setFloat( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t" + "prestmt.setTime( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t" + "prestmt.setTimestamp( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int ParseSelectOutputColumnsForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> selectOutputputColumnList, StringBuilder out ) {
		int		n = 0 ;
		
		for( SqlActionColumn c : selectOutputputColumnList ) {
			n++;
			out.append( "\t\t\t" );
			switch( c.dataType ) {
				case SQLACTION_DATA_TYPE_BIT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBoolean( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_TINYINT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getByte( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_SMALLINT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getShort( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_MEDIUMINT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getInt( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_INTEGER :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getInt( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_BIGINT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getLong( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_REAL :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getFloat( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_FLOAT :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getDouble( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_DOUBLE :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getDouble( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_DECIMAL :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBigDecimal( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_NUMBERIC :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBigDecimal( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_CHAR :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getString( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_VARCHAR :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getString( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_DATE :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getDate( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_TIME :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getTime( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_DATETIME :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getTime( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_TIMESTAMP :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getTimestamp( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_YEAR :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getTime( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_BINARY :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_VARBINARY :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_BLOB :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_TINYBLOB :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_MEDIUMBLOB :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				case SQLACTION_DATA_TYPE_LONGBLOB :
					out.append( "selectOutput."+c.javaPropertyName+" = rs.getBytes( "+n+" ) ;" );
					break;
				default :
					System.out.println( "dataType["+c.dataType+"] invalid" );
					return -1;
			}
			out.append( "\n" );
		}
		
		return 0;
	}
	
	public static int ParseSelectSqlForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, String sqlaction, String[] sa, StringBuilder out ) {
		
		StringBuilder			sql = new StringBuilder() ;
		StringBuilder			methodName = new StringBuilder() ;
		String					columnNames ;
		boolean					hasWHERE ;
		int						index ;
		List<SqlActionColumn>	selectOutputputColumnList = null ;
		List<SqlActionColumn>	whereInputputColumnList = null ;
		int						nret = 0 ;
		
		// sa[0]  sa[1] sa[2] sa[3]         sa[4]
		// SELECT name  FROM  my_demo_table WHERE id =
		// SELECT fname_cn,price FROM my_demo_table WHERE id < AND code <
		// SELECT * FROM my_demo_table WHERE code =
		sql.append( "SELECT " + sa[1].toString() + " FROM " + sa[3].toString() );
		columnNames = sa[1].toString().replace(',','_') ;
		if( columnNames.equals("*") ) {
			methodName.append( "SqlAction_SELECT_ALL_FROM_" + sa[3].toString() );
		} else {
			methodName.append( "SqlAction_SELECT_" + columnNames +"_FROM_" + sa[3].toString() );
		}
		
		if( sa[1].equals("*") ) {
			selectOutputputColumnList = null ;
		} else {
			selectOutputputColumnList = new LinkedList<SqlActionColumn>() ;
			
			nret = ParseSelectStatementForGeneratingMethodCode( sqlactionColumnList, sa[1], selectOutputputColumnList ) ;
			if( nret != 0 )
				return nret;
		}
		
		if( sa.length > 4 && sa[4].equals("where") ) {
			hasWHERE = true ;
			
			sql.append( " WHERE" );
			methodName.append( "_WHERE" );
			
			index = 5 ;
			
			whereInputputColumnList = new LinkedList<SqlActionColumn>() ;
			index = ParseWhereStatementForGeneratingMethodCode( sqlactionColumnList, sa, index, whereInputputColumnList, sql, methodName ) ;
			if( index < 0 ) {
				System.out.println( "ParseWhereSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
				return index;
			}
			
			index = ParseOtherStatementForGeneratingMethodCode( sa, index, sql, methodName ) ;
			if( index < 0 ) {
				System.out.println( "ParseOtherSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
				return index;
			}
		} else {
			hasWHERE = false ;
			
			index = 5 ;
			
			index = ParseOtherStatementForGeneratingMethodCode( sa, index, sql, methodName ) ;
			if( index < 0 ) {
				System.out.println( "ParseOtherSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
				return index;
			}
		}
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		out.append( "\t" + "public int " + methodName.toString() + "( Connection conn, List<" +sqlactionConf.javaClassName+ "> selectOutputList, " + sqlactionConf.javaClassName + " whereInput ) throws Exception {\n" );
		if( hasWHERE == true ) {
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionColumn c : whereInputputColumnList ) {
				columnIndex++;
				nret = ParseWhereInputColumnForGeneratingMethodCode( dbserverConf, sqlactionConf, columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseWhereInputColumnForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "ResultSet rs = prestmt.executeQuery() ;\n" );
		} else {
			out.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			out.append( "\t\t" + "ResultSet rs = stmt.executeQuery(\""+sql+"\") ;\n" );
		}
		out.append( "\t\t" + sqlactionConf.javaClassName+ " selectOutput ;\n" );
		out.append( "\t\t" + "while( rs.next() ) {\n" );
		out.append( "\t\t\t" + "selectOutput = new "+sqlactionConf.javaClassName+"() ;\n" );
		if( selectOutputputColumnList != null )
			nret = ParseSelectOutputColumnsForGeneratingMethodCode( dbserverConf, sqlactionConf, selectOutputputColumnList, out ) ;
		else
			nret = ParseSelectOutputColumnsForGeneratingMethodCode( dbserverConf, sqlactionConf, sqlactionColumnList, out ) ;
		if( nret != 0 ) {
			System.out.println( "ParseSelectOutputColumnsForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
			return nret;
		}
		out.append( "\t\t\t" + "selectOutputList.add(selectOutput);\n" );
		out.append( "\t\t" + "}\n" );
		out.append( "\t\t" + "return 0;\n" );
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int ParseInsertSqlForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, String sqlaction, String[] sa, StringBuilder out ) {
		
		StringBuilder		sql = new StringBuilder() ;
		StringBuilder		methodName = new StringBuilder() ;
		int					nret = 0 ;
		
		// sa[0]  sa[1] sa[2]
		// INSERT INTO  my_demo_table
		sql.append( "INSERT INTO " + sa[2].toString() + " (" );
		methodName.append( "SqlAction_INSERT_INTO_" + sa[2].toString() );
		
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.isAutoIncrement == false ) {
				if( c != sqlactionColumnList.get(1) )
					sql.append( "," );
				sql.append( c.columnName );
			}
		}
		
		sql.append( ") VALUES (" );
		
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.isAutoIncrement == false ) {
				if( c != sqlactionColumnList.get(1) )
					sql.append( "," );
				sql.append( "?" );
			}
		}
		
		sql.append( ")" );
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		out.append( "\t" + "public int " + methodName.toString() + "( Connection conn, List<" +sqlactionConf.javaClassName+ "> selectOutputList, " + sqlactionConf.javaClassName + " whereInput ) throws Exception {\n" );
		out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
		int	columnIndex = 0 ;
		for( SqlActionColumn c : sqlactionColumnList ) {
			if( c.isAutoIncrement == false ) {
				columnIndex++;
				nret = ParseWhereInputColumnForGeneratingMethodCode( dbserverConf, sqlactionConf, columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseWhereInputColumnForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
					return nret;
				}
			}
		}
		out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int ParseUpdateSqlForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, String sqlaction, String[] sa, StringBuilder out ) {
		
		StringBuilder			sql = new StringBuilder() ;
		StringBuilder			methodName = new StringBuilder() ;
		boolean					hasWHERE ;
		int						index ;
		List<SqlActionColumn>	setInputColumnList = null ;
		List<SqlActionColumn>	whereInputputColumnList = null ;
		int						nret = 0 ;
		
		// sa[0]  sa[1]         sa[2] sa[3] sa[4] sa[5] sa[6]
		// UPDATE my_demo_table SET   name  WHERE id    =
		sql.append( "UPDATE " + sa[1].toString() + " SET " );
		methodName.append( "SqlAction_UPDATE_" + sa[1].toString() + "_SET" );
		
		String[] sa2 = sa[3].split( "," ) ;
		setInputColumnList = new LinkedList<SqlActionColumn>() ;
		for( String s2 : sa2 ) {
			if( s2 != sa2[0] ) {
				sql.append( "," );
			}
			sql.append( s2 + "=?" );
			SqlActionColumn column = FindColumn( sqlactionColumnList, s2 ) ;
			if( column == null ) {
				System.out.println( "columnName["+s2+"] invalid in sqlaction["+sqlaction+"] invalid" );
				return -1;
			}
			setInputColumnList.add( column );
		}
		methodName.append( "_" + sa[3].toString().replace(',','_') );
		
		if( sa.length > 4 ) {
			if( sa[4].equals("where") ) {
				hasWHERE = true ;
				
				sql.append( " WHERE" );
				methodName.append( "_WHERE" );
				
				index = 5 ;
				
				whereInputputColumnList = new LinkedList<SqlActionColumn>() ;
				index = ParseWhereStatementForGeneratingMethodCode( sqlactionColumnList, sa, index, whereInputputColumnList, sql, methodName ) ;
				if( index < 0 ) {
					System.out.println( "ParseWhereSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
					return index;
				}
				
				index = ParseOtherStatementForGeneratingMethodCode( sa, index, sql, methodName ) ;
				if( index < 0 ) {
					System.out.println( "ParseOtherSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
					return index;
				}
			} else {
				System.out.println( "sqlaction["+sqlaction+"] invalid" );
				return -1;
			}
		} else {
			hasWHERE = false ;
		}
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		if( hasWHERE == true ) {
			out.append( "\t" + "public int " + methodName.toString() + "( Connection conn, " + sqlactionConf.javaClassName + " whereInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionColumn c : setInputColumnList ) {
				columnIndex++;
				nret = ParseWhereInputColumnForGeneratingMethodCode( dbserverConf, sqlactionConf, columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseWhereInputColumnForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
					return nret;
				}
			}
			for( SqlActionColumn c : whereInputputColumnList ) {
				columnIndex++;
				nret = ParseWhereInputColumnForGeneratingMethodCode( dbserverConf, sqlactionConf, columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseWhereInputColumnForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			out.append( "\t" + "public int " + methodName.toString() + "( Connection conn ) throws Exception {\n" );
			out.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			out.append( "\t\t" + "return stmt.executeUpdate(\""+sql+"\") ;\n" );
		}
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int ParseDeleteSqlForGeneratingMethodCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, String sqlaction, String[] sa, StringBuilder out ) {
		
		StringBuilder			sql = new StringBuilder() ;
		StringBuilder			methodName = new StringBuilder() ;
		boolean					hasWHERE ;
		int						index ;
		List<SqlActionColumn>	selectOutputputColumnList = null ;
		List<SqlActionColumn>	whereInputputColumnList = null ;
		int						nret = 0 ;
		
		// sa[0]  sa[1] sa[2]         sa[3] sa[4]
		// DELETE FROM  my_demo_table WHERE id =
		sql.append( "DELETE FROM " + sa[2].toString() );
		methodName.append( "SqlAction_DELETE_FROM_" + sa[2].toString() );
		
		if( sa.length > 3 ) {
			if( sa[3].equals("where") ) {
				hasWHERE = true ;
				
				sql.append( " WHERE" );
				methodName.append( "_WHERE" );
				
				index = 4 ;
				
				whereInputputColumnList = new LinkedList<SqlActionColumn>() ;
				index = ParseWhereStatementForGeneratingMethodCode( sqlactionColumnList, sa, index, whereInputputColumnList, sql, methodName ) ;
				if( index < 0 ) {
					System.out.println( "ParseWhereSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
					return index;
				}
				
				index = ParseOtherStatementForGeneratingMethodCode( sa, index, sql, methodName ) ;
				if( index < 0 ) {
					System.out.println( "ParseOtherSqlForGeneratingMethodCode["+sa.toString()+"] failed["+index+"]" );
					return index;
				}
			} else {
				System.out.println( "sqlaction["+sqlaction+"] invalid" );
				return -1;
			}
		} else {
			hasWHERE = false ;
		}
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		if( hasWHERE == true ) {
			out.append( "\t" + "public int " + methodName.toString() + "( Connection conn, " + sqlactionConf.javaClassName + " whereInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionColumn c : whereInputputColumnList ) {
				columnIndex++;
				nret = ParseWhereInputColumnForGeneratingMethodCode( dbserverConf, sqlactionConf, columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "ParseWhereInputColumnForGeneratingMethodCode["+sa.toString()+"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			out.append( "\t" + "public int " + methodName.toString() + "( Connection conn ) throws Exception {\n" );
			out.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			out.append( "\t\t" + "return stmt.executeUpdate(\""+sql+"\") ;\n" );
		}
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	private static int ParseDefinePropertyForGeneratingClassCode( SqlActionColumn c, StringBuilder out ) {
		
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
		
		out.append( " // "+c.columnComment+"\n" );
		
		return 0;
	}
	
	public static int TravelAllColumnsForGeneratingClassCode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionColumn> sqlactionColumnList, int depth, StringBuilder out ) throws Exception {
		
		int		nret = 0 ;
		
		out.append( "\n" );
		for( SqlActionColumn c : sqlactionColumnList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "columnName["+c.columnName+"] columnDefault["+c.columnDefault+"] isNullable["+c.isNullable+"] DataType["+c.dataType+"] columnLength["+c.columnMaximumLength+"] numericPrecision["+c.numericPrecision+"] numericScale["+c.numericScale+"] isPrimaryKey["+c.isPrimaryKey+"] isAutoIncrement["+c.isAutoIncrement+"] columnComment["+c.columnComment+"]" );
			
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
		}
		
		for( String s : sqlactionConf.sqlactions ) {
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
		
		return 0;
	}
}
