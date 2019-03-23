package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionCodegen {

	public static void main(String[] args) {
		String					driver = "com.mysql.jdbc.Driver" ;
		Connection				conn = null ;
		List<SqlActionDatabase>	databaseList = null ;
		int						nret = 0 ;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?serverTimezone=GMT", "root", "root") ;
			
			databaseList = new LinkedList<SqlActionDatabase>() ;
			
			nret = SqlActionDatabase.GetAllDatabases( conn, databaseList ) ;
			if( nret != 0 ) {
				System.out.println("GetAllSchemas failed["+nret+"]");
				conn.close();
				return;
			} else {
				System.out.println("GetAllSchemas ok");
			}
			
			conn.close();
			
			SqlActionDatabase.TravelAllDatabases( databaseList );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
