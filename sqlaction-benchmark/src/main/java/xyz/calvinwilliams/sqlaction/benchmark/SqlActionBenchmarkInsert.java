package xyz.calvinwilliams.sqlaction.benchmark;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SqlActionBenchmarkInsert {

	public static void main(String[] args) {
		Connection				conn = null ;
		SqlactionBenchmarkSAO	sqlactionBenchmark ;
		long					i , count ;
		int						nret = 0 ;
		
		// connect to database
		try {
			Class.forName( "com.mysql.jdbc.Driver" );
			conn = DriverManager.getConnection( "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT", "calvin", "calvin" ) ;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			conn.setAutoCommit(false);
			
			// benchmark for INSERT
			sqlactionBenchmark = new SqlactionBenchmarkSAO() ;
			sqlactionBenchmark.name = "Calvin" ;
			sqlactionBenchmark.nameCn = "卡尔文" ;
			sqlactionBenchmark.salary = new BigDecimal(0) ;
			long time = System.currentTimeMillis() ;
			sqlactionBenchmark.birthday = new java.sql.Date(time) ;
			
			count = 5000 ;
			
			long beginMillisSecondstamp = System.currentTimeMillis() ;
			
			for( i = 0 ; i < count ; i++ ) {
				nret = SqlactionBenchmarkSAO.SqlAction_INSERT_INTO_sqlaction_benchmark( conn, sqlactionBenchmark ) ;
			}
			conn.commit();
			System.out.println( "All sqlaction insert done!!!" );
			
			long endMillisSecondstamp = System.currentTimeMillis() ;
			double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		} catch(Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				return;
			}
		}
		
		return;
	}
}
