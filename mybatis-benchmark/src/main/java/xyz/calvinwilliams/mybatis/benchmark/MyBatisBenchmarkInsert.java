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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO;
import xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAOMapper;

public class MyBatisBenchmarkInsert {

	public static void main(String[] args) {
		SqlSession					session = null ;
		SqlactionBenchmarkSAOMapper	mapper ;
		long						i , count ;
		int							rows ;
		
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
			
			count = 10000 ;
			
			long beginMillisSecondstamp = System.currentTimeMillis() ;
			
			for( i = 0 ; i < count ; i++ ) {
				sqlactionBenchmark.name = "Calvin"+i ;
				sqlactionBenchmark.name_cn = "卡尔文"+i ;
				mapper = session.getMapper(SqlactionBenchmarkSAOMapper.class) ;
				rows = mapper.insertOne(sqlactionBenchmark) ;
				if( i % 10 == 0 ) {
					session.commit();
				}
			}
			session.commit();
			System.out.println( "All mybatis insert done!!!" );
			
			long endMillisSecondstamp = System.currentTimeMillis() ;
			double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
			System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

}
