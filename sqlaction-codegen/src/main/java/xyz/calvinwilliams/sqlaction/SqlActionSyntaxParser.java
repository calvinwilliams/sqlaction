package xyz.calvinwilliams.sqlaction;

import java.util.*;

public class SqlActionSyntaxParser {

	List<SqlActionSelectColumnToken>	selectColumnTokenList = null ;
	boolean								selectAllColumn = false ;
	List<SqlActionFromTableToken>		fromTableTokenList = null ;
	
	String								insertTableName = null ;
	
	String								updateTableName = null ;
	List<SqlActionSetColumnToken>		setColumnTokenList = null ;
	
	String								deleteTableName = null ;
	
	List<SqlActionWhereColumnToken>		whereColumnTokenList = null ;
	
	public int ParseSyntax( String sql ) {
		SqlActionSelectColumnToken	selectColumnToken = null ;
		SqlActionFromTableToken		fromTableToken = null ;
		SqlActionSetColumnToken		setColumnToken = null ;
		SqlActionWhereColumnToken	whereColumnToken = null ;
		
		SqlActionLexicalParser lexicalParser = new SqlActionLexicalParser() ;
		
		lexicalParser.SetSqlString(sql);
		
		String	token = lexicalParser.GetSqlToken() ;
		if( token == null )
			return 0;
		while( token != null ) {
			if( token.equalsIgnoreCase("SELECT") ) {
				while(true) {
					String token1 = lexicalParser.GetSqlToken() ;
					if( token1 == null ) {
						return -11;
					} else if( token1.equals("*") ) {
						// SELECT *
						String token2 = lexicalParser.GetSqlToken() ;
						if( token2 == null ) {
							// SELECT *\0
							selectAllColumn = true ;
							token = token2 ;
							break;
						} else if( token2.equalsIgnoreCase("FROM") ) {
							// SELECT * FROM
							token = token2 ;
							break;
						} else {
							return -12;
						}
					}
					String token2 = lexicalParser.GetSqlToken() ;
					if( token2 == null ) {
						// SELECT column1\0
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
						token = null ;
						break;
					} else if( token2.equalsIgnoreCase("FROM") ) {
						// SELECT column FROM
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
						token = token2 ;
						break;
					} else if( token2.equals(".") ) {
						// SELECT table1.column1
						String token3 = lexicalParser.GetSqlToken() ;
						
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableName = token1 ;
						selectColumnToken.columnName = token3 ;
						selectColumnTokenList.add(selectColumnToken);
						
						String token4 = lexicalParser.GetSqlToken() ;
						if( token4 == null ) {
							// SELECT table1.column1\0
							token = null ;
							break;
						}
						if( token4.equalsIgnoreCase("FROM") ) {
							// SELECT table1.column1 FROM
							token = token4 ;
							break;
						} else if( token4.equals(",") ) {
							// SELECT table1.column1,
						} else {
							return -13;
						}
					} else if( token2.equals(",") ) {
						// SELECT column1,column2
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
					}
				}
			} else if( token.equalsIgnoreCase("INSERT") ) {
				String token1 = lexicalParser.GetSqlToken() ;
				if( token1 == null ) {
					return -21;
				}
				if( ! token1.equalsIgnoreCase("TO") ) {
					return -22;
				} else {
					token = token1 ;
				}
				
				insertTableName = token1 ;
				
				String token2 = lexicalParser.GetSqlToken() ;
				if( token2 == null ) {
					token = null ;
				} else {
					return -23;
				}
			} else if( token.equalsIgnoreCase("DELETE") ) {
				String token1 = lexicalParser.GetSqlToken() ;
				if( token1 == null ) {
					return -21;
				}
				if( ! token1.equalsIgnoreCase("FROM") ) {
					return -22;
				}
				
				String token2 = lexicalParser.GetSqlToken() ;
				if( token2 == null ) {
					return -23;
				}
				
				insertTableName = token2 ;
				
				String token3 = lexicalParser.GetSqlToken() ;
				token = token3 ;
			} else if( token.equalsIgnoreCase("FROM") ) {
				while(true) {
					String token1 = lexicalParser.GetSqlToken() ;
					if( token1 == null ) {
						return -31;
					}
					String token2 = lexicalParser.GetSqlToken() ;
					if( token2 == null ) {
						// FROM table\0
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = null ;
						break;
					}
					if( token2.equalsIgnoreCase("WHERE") ) {
						// FROM table WHERE
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = token2 ;
						break;
					} else if( token2.equals(",") ) {
						// FROM table1,
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
					} else {
						// FROM table1 t1
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableToken.tableAliasName = token2 ;
						fromTableTokenList.add(fromTableToken);
						
						String token3 = lexicalParser.GetSqlToken() ;
						if( token3 == null ) {
							// FROM table1 t1\0
							token = null ;
							break;
						}
						if(  token3.equalsIgnoreCase("WHERE") ) {
							// FROM table1 t1 WHERE
							token = token3 ;
							break;
						} else if( token3.equals(",") ) {
							// FROM table1 t1,
						} else {
							return -32;
						}
					}
				}
			} else if( token.equalsIgnoreCase("UPDATE") ) {
				while(true) {
					String token1 = lexicalParser.GetSqlToken() ;
					if( token1 == null ) {
						return -41;
					}
					String token2 = lexicalParser.GetSqlToken() ;
					if( token2 == null ) {
						// UPDATE table\0
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = null ;
						break;
					}
					if( token2.equalsIgnoreCase("SET") ) {
						// UPDATE table SET
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = token2 ;
						break;
					} else {
						return -42;
					}
				}
			} else if( token.equalsIgnoreCase("SET") ) {
				while(true) {
					String token1 = lexicalParser.GetSqlToken() ;
					if( token1 == null ) {
						return -51;
					}
					
					String token2 = lexicalParser.GetSqlToken() ;
					if( token2 == null ) {
						return -52;
					}
					
					String token3 = lexicalParser.GetSqlToken() ;
					if( token3 == null ) {
						return -53;
					}
					
					if( token2.equals(".") ) {
						String token4 = lexicalParser.GetSqlToken() ;
						if( token4 == null ) {
							return -541;
						}
						
						String token5 = lexicalParser.GetSqlToken() ;
						if( token5 == null ) {
							return -542;
						}
						
						// SET table.column = ?
						setColumnToken = new SqlActionSetColumnToken() ;
						setColumnToken.tableName = token1 ;
						setColumnToken.columnName = token3 ;
						if( ! token4.equals("=") ) {
							return -543;
						}
						setColumnToken.columnValue = token5 ;
						setColumnTokenList.add(setColumnToken);
					} else {
						// SET column = ?
						setColumnToken = new SqlActionSetColumnToken() ;
						setColumnToken.columnName = token1 ;
						if( ! token2.equals("=") ) {
							return -543;
						}
						setColumnToken.columnValue = token3 ;
						setColumnTokenList.add(setColumnToken);
					}
					
					String token6 = lexicalParser.GetSqlToken() ;
					if( token6 == null ) {
						// SET column = ?\0
						token = null ;
						break;
					}
					if( token6.equals(",") ) {
						;
					} else if( token6.equalsIgnoreCase("WHERE") ) {
						// SET column WHERE
						token = token6 ;
						break;
					} else {
						return -55;
					}
				}
			} else if( token.equalsIgnoreCase("WHERE") ) {
				while(true) {
					String token1 = lexicalParser.GetSqlToken() ;
					if( token1 == null ) {
						return -61;
					}
					
					String token2 = lexicalParser.GetSqlToken() ;
					if( token2 == null ) {
						return -62;
					}
					
					String token3 = lexicalParser.GetSqlToken() ;
					if( token3 == null ) {
						return -63;
					}
					
					if( token2.equals(".") ) {
						// table.column = ?
						String token4 = lexicalParser.GetSqlToken() ;
						if( token4 == null ) {
							return -641;
						}
						String token5 = lexicalParser.GetSqlToken() ;
						if( token5 == null ) {
							return -642;
						}
						
						whereColumnToken = new SqlActionWhereColumnToken() ;
						whereColumnToken.tableName = token1 ;
						whereColumnToken.columnName = token3 ;
						whereColumnToken.operator = token4 ;
						whereColumnToken.columnValue = token5 ;
						whereColumnTokenList.add(whereColumnToken);
					} else {
						// column = ?
						whereColumnToken = new SqlActionWhereColumnToken() ;
						whereColumnToken.columnName = token1 ;
						whereColumnToken.operator = token2 ;
						whereColumnToken.columnValue = token3 ;
						whereColumnTokenList.add(whereColumnToken);
					}
					
					String token6 = lexicalParser.GetSqlToken() ;
					if( token6 == null ) {
						// WHERE column = ?\0
						token = token6 ;
						break;
					}
					if( token6.equalsIgnoreCase("AND") ) {
						;
					} else {
						// WHERE ... others
						token = null ;
						break;
					}
				}
			} else {
				return -9;
			}
		}
		
		return 0;
	}
	
}
