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

	int				id ; // 编号
	String			code ; // 编码
	String			name ; // 英文简称
	String			fnameCn ; // 中文全称
	BigDecimal		price ; // 价格
	java.sql.Date	createDate ; // 创建日期
	java.sql.Time	createTime ; // 创建时间
	Timestamp		createTimestamp ; // 创建时间戳

	// select name from my_demo_table where code =
	public static int SqlAction_SELECT_name_FROM_my_demo_table_WHERE_code_E( Connection conn, List<MyDemoTableSAO> selectOutputList, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name FROM my_demo_table WHERE code=?") ;
		prestmt.setString( 1, whereInput.code );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			MyDemoTableSAO selectOutput = new MyDemoTableSAO() ;
			selectOutput.name = rs.getString( 1 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// select fname_cn,price from my_demo_table where id < and code =
	public static int SqlAction_SELECT_fname_cn_price_FROM_my_demo_table_WHERE_id_LT_AND_code_E( Connection conn, List<MyDemoTableSAO> selectOutputList, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT fname_cn,price FROM my_demo_table WHERE id<? AND code=?") ;
		prestmt.setInt( 1, whereInput.id );
		prestmt.setString( 2, whereInput.code );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			MyDemoTableSAO selectOutput = new MyDemoTableSAO() ;
			selectOutput.fnameCn = rs.getString( 1 ) ;
			selectOutput.price = rs.getBigDecimal( 2 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// select * from my_demo_table
	public static int SqlAction_SELECT_ALL_FROM_my_demo_table( Connection conn, List<MyDemoTableSAO> selectOutputList ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery("SELECT * FROM my_demo_table") ;
		while( rs.next() ) {
			MyDemoTableSAO selectOutput = new MyDemoTableSAO() ;
			selectOutput.id = rs.getInt( 1 ) ;
			selectOutput.code = rs.getString( 2 ) ;
			selectOutput.name = rs.getString( 3 ) ;
			selectOutput.fnameCn = rs.getString( 4 ) ;
			selectOutput.price = rs.getBigDecimal( 5 ) ;
			selectOutput.createDate = rs.getDate( 6 ) ;
			selectOutput.createTime = rs.getTime( 7 ) ;
			selectOutput.createTimestamp = rs.getTimestamp( 8 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// insert into my_demo_table
	public static int SqlAction_INSERT_INTO_my_demo_table( Connection conn, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("INSERT INTO my_demo_table (code,name,fname_cn,price,create_date,create_time,create_timestamp) VALUES (?,?,?,?,?,?,?)") ;
		prestmt.setString( 1, whereInput.code );
		prestmt.setString( 2, whereInput.name );
		prestmt.setString( 3, whereInput.fnameCn );
		prestmt.setBigDecimal( 4, whereInput.price );
		prestmt.setDate( 5, whereInput.createDate );
		prestmt.setTime( 6, whereInput.createTime );
		prestmt.setTimestamp( 7, whereInput.createTimestamp );
		return prestmt.executeUpdate() ;
	}

	// update my_demo_table set fname_cn
	public static int SqlAction_UPDATE_my_demo_table_SET_fname_cn( Connection conn, MyDemoTableSAO setInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE my_demo_table SET fname_cn=?") ;
		prestmt.setString( 1, setInput.fnameCn );
		return prestmt.executeUpdate() ;
	}

	// update my_demo_table set fname_cn where code =
	public static int SqlAction_UPDATE_my_demo_table_SET_fname_cn_WHERE_code_E( Connection conn, MyDemoTableSAO setInput, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE my_demo_table SET fname_cn=? WHERE code=?") ;
		prestmt.setString( 1, setInput.fnameCn );
		prestmt.setString( 2, whereInput.code );
		return prestmt.executeUpdate() ;
	}

	// update my_demo_table set name,fname_cn where id >= and code =
	public static int SqlAction_UPDATE_my_demo_table_SET_name_fname_cn_WHERE_id_GE_AND_code_E( Connection conn, MyDemoTableSAO setInput, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE my_demo_table SET name=?,fname_cn=? WHERE id>=? AND code=?") ;
		prestmt.setString( 1, setInput.name );
		prestmt.setString( 2, setInput.fnameCn );
		prestmt.setInt( 3, whereInput.id );
		prestmt.setString( 4, whereInput.code );
		return prestmt.executeUpdate() ;
	}

	// delete from my_demo_table
	public static int SqlAction_DELETE_FROM_my_demo_table( Connection conn ) throws Exception {
		Statement stmt = conn.createStatement() ;
		return stmt.executeUpdate("DELETE FROM my_demo_table") ;
	}

	// delete from my_demo_table where code <>
	public static int SqlAction_DELETE_FROM_my_demo_table_WHERE_code_NE( Connection conn, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM my_demo_table WHERE code<>?") ;
		prestmt.setString( 1, whereInput.code );
		return prestmt.executeUpdate() ;
	}

	// delete from my_demo_table where id <> and code <>
	public static int SqlAction_DELETE_FROM_my_demo_table_WHERE_id_NE_AND_code_NE( Connection conn, MyDemoTableSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM my_demo_table WHERE id<>? AND code<>?") ;
		prestmt.setInt( 1, whereInput.id );
		prestmt.setString( 2, whereInput.code );
		return prestmt.executeUpdate() ;
	}

}
