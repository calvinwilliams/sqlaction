/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

public class SqlActionUtil {

	private static int wildcardMatchMultiChar( char[] wildcard, int wildcardOffset, int wildcardLength, char[] str, int strOffset, int strLength ) {
		int		matchDestOffset = strOffset ;
		int		nret = 0 ;
		
		while( matchDestOffset < strLength ) {
			if( str[matchDestOffset] == wildcard[wildcardOffset] ) {
				nret = wildcardMatchChar( wildcard, wildcardOffset, wildcardLength, str, matchDestOffset, strLength ) ;
				if( nret == 0 )
					return nret;
			}
			
			matchDestOffset++;
		}
		
		return 1;
	}
	
	private static int wildcardMatchChar( char[] wildcard, int wildcardOffset, int wildcardLength, char[] str, int strOffset, int strLength ) {
		while(true) {
			if( wildcardOffset == wildcardLength && strOffset == strLength )
				return 0;
			if( wildcardOffset == wildcardLength )
				return 1;
			
			if( wildcard[wildcardOffset] == '*' ) {
				wildcardOffset++;
				while( wildcardOffset < wildcardLength && ( wildcard[wildcardOffset] == '*' || wildcard[wildcardOffset] == '?' ) && strOffset < strLength ) {
					wildcardOffset++;
				}
				
				if( wildcardOffset == wildcardLength )
					return 0;
				
				return wildcardMatchMultiChar( wildcard, wildcardOffset, wildcardLength, str, strOffset, strLength );
			} else if( wildcard[wildcardOffset] == '?' ) {
				wildcardOffset++;
				strOffset++;
				while( wildcardOffset < wildcardLength && wildcard[wildcardOffset] == '?' && strOffset < strLength ) {
					wildcardOffset++;
					strOffset++;
				}
			} else {
				if( wildcard[wildcardOffset] != str[strOffset] )
					return 1;
				wildcardOffset++;
				strOffset++;
			}
		}
	}
	
	public static int wildcardMatch( String wildcard, String str ) {
		return wildcardMatchChar( wildcard.toCharArray(), 0, wildcard.length(), str.toCharArray(), 0, str.length() );
	}
	
	public static String convertToUnderscoreExceptForLetterAndDigit( String str ) {
		char[] charArray = str.toCharArray() ;
		int	strLength = str.length() ;
		for( int i = 0 ; i < strLength ; i++ ) {
			if( ! Character.isLetterOrDigit(charArray[i]) ) {
				charArray[i] = '_' ;
			}
		}
		return new String( charArray );
	}
}
