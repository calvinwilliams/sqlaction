package xyz.calvinwilliams.sqlaction;

public class SqlActionLexicalParser {
	
	char[]			sql ;
	int				sqlLength ;
	int				parserOffset ;
	
	public void SetSqlString( String sql ) {
		this.sql = sql.toCharArray() ;
		this.sqlLength = sql.length() ;
		this.parserOffset = 0 ;
		return;
	}
	
	public String GetSqlToken() {
		int		beginOffset ;
		
		while( parserOffset < sqlLength ) {
			if( ! ( sql[parserOffset] == ' ' || sql[parserOffset] == '\t' || sql[parserOffset] == '\r' || sql[parserOffset] == '\n' ) )
				break;
			parserOffset++;
		}
		if( parserOffset >= sqlLength )
			return null;
		
		beginOffset = parserOffset ;
		
		while( parserOffset < sqlLength ) {
			if( sql[parserOffset] == '.' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				return new String(sql,beginOffset,1);
			} else if( sql[parserOffset] == ',' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				return new String(sql,beginOffset,1);
			} else if( sql[parserOffset] == '?' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				return new String(sql,beginOffset,1);
			} else if( sql[parserOffset] == '"' || sql[parserOffset] == '\'' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				while( parserOffset < sqlLength ) {
					if( sql[parserOffset] == sql[beginOffset] )
						break;
					parserOffset++;
				}
				if( parserOffset >= sqlLength )
					return null;
				
				parserOffset++;
				return new String(sql,beginOffset,parserOffset-beginOffset);
			} else if( sql[parserOffset] == '=' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				return new String(sql,beginOffset,1);
			} else if( sql[parserOffset] == '<' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				if( sql[parserOffset] == '>' ) {
					parserOffset++;
					return new String(sql,beginOffset,2);
				} else if( sql[parserOffset] == '=' ) {
					parserOffset++;
					return new String(sql,beginOffset,2);
				} else {
					return new String(sql,beginOffset,1);
				}
			} else if( sql[parserOffset] == '>' ) {
				if( parserOffset != beginOffset ) {
					return new String(sql,beginOffset,parserOffset-beginOffset);
				}
				
				parserOffset++;
				if( sql[parserOffset] == '=' ) {
					parserOffset++;
					return new String(sql,beginOffset,2);
				} else {
					return new String(sql,beginOffset,1);
				}
			} else if( sql[parserOffset] == ' ' || sql[parserOffset] == '\t' || sql[parserOffset] == '\r' || sql[parserOffset] == '\n' ) {
				return new String(sql,beginOffset,parserOffset-beginOffset);
			}
			
			parserOffset++;
		}
		if( parserOffset != beginOffset ) {
			return new String(sql,beginOffset,parserOffset-beginOffset);
		} else {
			return null;
		}
	}
	
	public String GetRemainSqlToken() {
		return new String( sql, parserOffset, sqlLength-parserOffset );
	}
}
