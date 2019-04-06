/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.benchmark;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SqlActionBenchmarkCrud {

	public static void main(String[] args) {
		Connection					conn = null ;
		SqlactionBenchmarkSAO		sqlactionBenchmark ;
		List<SqlactionBenchmarkSAO>	sqlactionBenchmarkList ;
		long						beginMillisSecondstamp ;
		long						endMillisSecondstamp ;
		double						elpaseSecond ;
		long						i , j , k ;
		long						count , count2 , count3 ;
		int							rows = 0 ;
		
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
			
			sqlactionBenchmark = new SqlactionBenchmarkSAO() ;
			sqlactionBenchmark.name = "Calvin" ;
			sqlactionBenchmark.nameCn = "卡尔文" ;
			sqlactionBenchmark.salary = new BigDecimal(0) ;
			long time = System.currentTimeMillis() ;
			sqlactionBenchmark.birthday = new java.sql.Date(time) ;
			count = 500 ;
			count2 = 5 ;
			count3 = 1000 ;
			
			rows = SqlactionBenchmarkSAO.DELETE_FROM_sqlaction_benchmark( conn ) ;
			conn.commit();
			
			// benchmark for INSERT
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				sqlactionBenchmark.name = "Calvin"+i ;
				sqlactionBenchmark.nameCn = "卡尔文"+i ;
				rows = SqlactionBenchmarkSAO.INSERT_INTO_sqlaction_benchmark( conn, sqlactionBenchmark ) ;
				if( rows != 1 ) {
					System.out.println( "SqlactionBenchmarkSAO.INSERT_INTO_sqlaction_benchmark failed["+rows+"]" );
					return;
				}
				if( i % 10 == 0 ) {
					conn.commit();
				}
			}
			conn.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All sqlaction INSERT done , count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for UPDATE
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				rows = SqlactionBenchmarkSAO.UPDATE_sqlaction_benchmark_SET_salary_E_WHERE_name_E_( conn, new BigDecimal(i), "Calvin"+i ) ;
				if( rows != 1 ) {
					System.out.println( "SqlactionBenchmarkSAO.UPDATE_sqlaction_benchmark_SET_salary_E_WHERE_name_E_ failed["+rows+"]" );
					return;
				}
				if( i % 10 == 0 ) {
					conn.commit();
				}
			}
			conn.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All sqlaction UPDATE WHERE done , count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for SELECT ... WHERE ...
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( j = 0 ; j < count2 ; j++ ) {
				for( i = 0 ; i < count ; i++ ) {
					sqlactionBenchmarkList = new LinkedList<SqlactionBenchmarkSAO>() ;
					rows = SqlactionBenchmarkSAO.SELECT_ALL_FROM_sqlaction_benchmark_WHERE_name_E_( conn, sqlactionBenchmarkList, "Calvin"+i ) ;
					if( rows != 1 ) {
						System.out.println( "SqlactionBenchmarkSAO.SELECT_ALL_FROM_sqlaction_benchmark_WHERE_name_E_ failed["+rows+"]" );
						return;
					}
				}
			}
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All sqlaction SELECT WHERE done , count2["+count2+"] count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for SELECT
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( k = 0 ; k < count3 ; k++ ) {
				sqlactionBenchmarkList = new LinkedList<SqlactionBenchmarkSAO>() ;
				rows = SqlactionBenchmarkSAO.SELECT_ALL_FROM_sqlaction_benchmark( conn, sqlactionBenchmarkList ) ;
				if( rows != count ) {
					System.out.println( "SqlactionBenchmarkSAO.SELECT_ALL_FROM_sqlaction_benchmark failed["+rows+"]" );
					return;
				}
			}
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All sqlaction SELECT to LIST done , count3["+count3+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for DELETE
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				rows = SqlactionBenchmarkSAO.DELETE_FROM_sqlaction_benchmark_WHERE_name_E_( conn, "Calvin"+i ) ;
				if( rows != 1 ) {
					System.out.println( "SqlactionBenchmarkSAO.DELETE_FROM_sqlaction_benchmark_WHERE_name_E_ failed["+rows+"]" );
					return;
				}
				if( i % 10 == 0 ) {
					conn.commit();
				}
			}
			conn.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All sqlaction DELETE WHERE done , count["+count+"] elapse["+elpaseSecond+"]s" );
		} catch(Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				e.printStackTrace();
				return;
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
		}
		
		return;
	}
}
