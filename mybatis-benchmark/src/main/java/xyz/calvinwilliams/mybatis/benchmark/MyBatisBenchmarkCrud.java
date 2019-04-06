/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.mybatis.benchmark;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO;
import xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAOMapper;

public class MyBatisBenchmarkCrud {

	public static void main(String[] args) {
		SqlSession					session = null ;
		SqlactionBenchmarkSAOMapper	mapper ;
		List<SqlactionBenchmarkSAO>	sqlactionBenchmarkList ;
		long						beginMillisSecondstamp ;
		long						endMillisSecondstamp ;
		double						elpaseSecond ;
		long						i , j , k ;
		long						count , count2 , count3 ;
		
		try {
			FileInputStream in = new FileInputStream("src/main/java/mybatis-config.xml");
			session = new SqlSessionFactoryBuilder().build(in).openSession();
			
			SqlactionBenchmarkSAO	sqlactionBenchmark = new SqlactionBenchmarkSAO() ;
			sqlactionBenchmark.id = 1 ;
			sqlactionBenchmark.name = "Calvin" ;
			sqlactionBenchmark.name_cn = "卡尔文" ;
			sqlactionBenchmark.salary = new BigDecimal(0) ;
			long time = System.currentTimeMillis() ;
			sqlactionBenchmark.birthday = new java.sql.Date(time) ;
			count = 500 ;
			count2 = 5 ;
			count3 = 1000 ;
			
			mapper = session.getMapper(SqlactionBenchmarkSAOMapper.class) ;
			
			mapper.deleteAll();
			session.commit();
			
			// benchmark for INSERT
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				sqlactionBenchmark.name = "Calvin"+i ;
				sqlactionBenchmark.name_cn = "卡尔文"+i ;
				mapper.insertOne(sqlactionBenchmark);
				if( i % 10 == 0 ) {
					session.commit();
				}
			}
			session.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All mybatis INSERT done , count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for UPDATE
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				sqlactionBenchmark.name = "Calvin"+i ;
				sqlactionBenchmark.salary = new BigDecimal(i) ;
				mapper.updateOneByName(sqlactionBenchmark);
				if( i % 10 == 0 ) {
					session.commit();
				}
			}
			session.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All mybatis UPDATE WHERE done , count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for SELECT ... WHERE ...
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( j = 0 ; j < count2 ; j++ ) {
				for( i = 0 ; i < count ; i++ ) {
					sqlactionBenchmark = mapper.selectOneByName(sqlactionBenchmark.name) ;
					if( sqlactionBenchmark == null ) {
						System.out.println( "mapper.selectOneByName failed" );
						return;
					}
				}
			}
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All mybatis SELECT WHERE done , count2["+count2+"] count["+count+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for SELECT
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( k = 0 ; k < count3 ; k++ ) {
				sqlactionBenchmarkList = mapper.selectAll() ;
				if( sqlactionBenchmarkList == null ) {
					System.out.println( "mapper.selectAll failed" );
					return;
				}
			}
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All mybatis SELECT to List done , count3["+count3+"] elapse["+elpaseSecond+"]s" );
			
			// benchmark for DELETE
			beginMillisSecondstamp = System.currentTimeMillis() ;
			for( i = 0 ; i < count ; i++ ) {
				sqlactionBenchmark.name = "Calvin"+i ;
				mapper.deleteOneByName(sqlactionBenchmark.name);
				if( i % 10 == 0 ) {
					session.commit();
				}
			}
			session.commit();
			endMillisSecondstamp = System.currentTimeMillis() ;
			elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "All mybatis DELETE WHERE done , count["+count+"] elapse["+elpaseSecond+"]s" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
