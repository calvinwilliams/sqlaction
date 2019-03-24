package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyDemoTableSAO {

	// 编号
	int id ;
	// 编码
	String code ;
	// 英文简称
	String name ;
	// 中文全称
	String fnameCn ;
	// 价格
	BigDecimal price ;
	// 创建日期
	Date createDate ;
	// 创建时间
	Time createTime ;
	// 创建时间戳
	Timestamp createTimestamp ;

	// sqlaction for 'select name from my_demo_table where id ='
	public int SqlAction_SELECT_name_FROM_my_demo_table_WHERE_id_E( Connection conn, List<MyDemoTableSAO> selectOutList, MyDemoTableSAO whereIn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name FROM my_demo_table WHERE id=?") ;
		prestmt.setInt( 1, whereIn.id );
		ResultSet rs = prestmt.executeQuery() ;
		MyDemoTableSAO selectOut ;
		while( rs.next() ) {
			selectOut = new MyDemoTableSAO() ;
			selectOut.name = rs.getString( 1 ) ;
			selectOutList.add(selectOut);
		}
		return 0;
	}

	// sqlaction for 'select fname_cn,price from my_demo_table where id < and code <'
	public int SqlAction_SELECT_fname_cn_price_FROM_my_demo_table_WHERE_id_LT_AND_code_LT( Connection conn, List<MyDemoTableSAO> selectOutList, MyDemoTableSAO whereIn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT fname_cn,price FROM my_demo_table WHERE id<? AND code<?") ;
		prestmt.setInt( 1, whereIn.id );
		prestmt.setString( 2, whereIn.code );
		ResultSet rs = prestmt.executeQuery() ;
		MyDemoTableSAO selectOut ;
		while( rs.next() ) {
			selectOut = new MyDemoTableSAO() ;
			selectOut.fnameCn = rs.getString( 1 ) ;
			selectOut.price = rs.getBigDecimal( 2 ) ;
			selectOutList.add(selectOut);
		}
		return 0;
	}

	// sqlaction for 'select * from my_demo_table'
	public int SqlAction_SELECT_ALL_FROM_my_demo_table( Connection conn, List<MyDemoTableSAO> selectOutList, MyDemoTableSAO whereIn ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery("SELECT * FROM my_demo_table") ;
		MyDemoTableSAO selectOut ;
		while( rs.next() ) {
			selectOut = new MyDemoTableSAO() ;
			selectOut.id = rs.getInt( 1 ) ;
			selectOut.code = rs.getString( 2 ) ;
			selectOut.name = rs.getString( 3 ) ;
			selectOut.fnameCn = rs.getString( 4 ) ;
			selectOut.price = rs.getBigDecimal( 5 ) ;
			selectOut.createDate = rs.getDate( 6 ) ;
			selectOut.createTime = rs.getTime( 7 ) ;
			selectOut.createTimestamp = rs.getTimestamp( 8 ) ;
			selectOutList.add(selectOut);
		}
		return 0;
	}

}
