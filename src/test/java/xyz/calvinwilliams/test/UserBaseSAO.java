// This file generated by sqlaction v0.2.9.0
// WARN : DON'T MODIFY THIS FILE

package xyz.calvinwilliams.test;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserBaseSAO {

	int				id ; // 编号 // PRIMARY KEY
	String			name ; // 名字
	String			gender ; // 性别
	short			age ; // 年龄
	String			address ; // 地址
	int				lvl ; // 级别

	int				_count_ ; // defining for 'SELECT COUNT(*)'

	// SELECT * FROM user_base
	public static int SELECT_ALL_FROM_user_base( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM user_base" ) ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getShort( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.lvl = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT * FROM user_base WHERE name=?
	public static int SELECT_ALL_FROM_user_base_WHERE_name_E_( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, String _1_UserBaseSAU_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_base WHERE name=?" ) ;
		prestmt.setString( 1, _1_UserBaseSAU_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getShort( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.lvl = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT name,address FROM user_base WHERE age<=? AND gender=?
	public static int SELECT_name_j_address_FROM_user_base_WHERE_age_LE_AND_gender_E_( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, short _1_UserBaseSAU_age, String _2_UserBaseSAU_gender ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT name,address FROM user_base WHERE age<=? AND gender=?" ) ;
		prestmt.setShort( 1, _1_UserBaseSAU_age );
		prestmt.setString( 2, _2_UserBaseSAU_gender );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			userBase.name = rs.getString( 1 ) ;
			userBase.address = rs.getString( 2 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT * FROM user_base ORDER BY name DESC
	public static int SELECT_ALL_FROM_user_base_ORDER_BY_name_DESC( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM user_base ORDER BY name DESC" ) ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getShort( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.lvl = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT gender,count(*) FROM user_base GROUP BY gender
	public static int SELECT_gender_j_count_ALL_FROM_user_base_GROUP_BY_gender( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT gender,count(*) FROM user_base GROUP BY gender" ) ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			userBase.gender = rs.getString( 1 ) ;
			userBase._count_ = rs.getInt( 2 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// INSERT INTO user_base @@SELECTSEQ(user_base_seq_id) @@SELECTKEY(id)
	public static int INSERT_INTO_user_base( Connection conn, UserBaseSAU userBase ) throws Exception {
		PreparedStatement prestmt ;
		Statement stmt ;
		ResultSet rs ;
		prestmt = conn.prepareStatement( "INSERT INTO user_base (name,gender,age,address,lvl) VALUES (?,?,?,?,?)" ) ;
		prestmt.setString( 1, userBase.name );
		prestmt.setString( 2, userBase.gender );
		prestmt.setShort( 3, userBase.age );
		prestmt.setString( 4, userBase.address );
		prestmt.setInt( 5, userBase.lvl );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		if( count != 1 )
			return count;
		
		stmt = conn.createStatement() ;
		rs = stmt.executeQuery( "SELECT LAST_INSERT_ID()" ) ;
		rs.next();
		userBase.id = rs.getInt( 1 ) ;
		rs.close();
		stmt.close();
		
		return count;
	}

	// UPDATE user_base SET lvl=?
	public static int UPDATE_user_base_SET_lvl_E_( Connection conn, int _1_lvl_ForSetInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET lvl=?" ) ;
		prestmt.setInt( 1, _1_lvl_ForSetInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE user_base SET address='calvin address',lvl=10 WHERE name='Calvin'
	public static int UPDATE_user_base_SET_address_E_calvin_address_j_lvl_E_10_WHERE_name_E_Calvin_( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET address='calvin address',lvl=10 WHERE name='Calvin'" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE user_base SET lvl=? WHERE age>? AND gender=?
	public static int UPDATE_user_base_SET_lvl_E_WHERE_age_GT_AND_gender_E_( Connection conn, int _1_lvl_ForSetInput, short _1_age_ForWhereInput, String _2_gender_ForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET lvl=? WHERE age>? AND gender=?" ) ;
		prestmt.setInt( 1, _1_lvl_ForSetInput );
		prestmt.setShort( 2, _1_age_ForWhereInput );
		prestmt.setString( 3, _2_gender_ForWhereInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base
	public static int DELETE_FROM_user_base( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base WHERE name='Calvin'
	public static int DELETE_FROM_user_base_WHERE_name_E_Calvin_( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base WHERE name='Calvin'" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base WHERE age<>? AND gender<>?
	public static int DELETE_FROM_user_base_WHERE_age_NE_AND_gender_NE_( Connection conn, short _1_age, String _2_gender ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base WHERE age<>? AND gender<>?" ) ;
		prestmt.setShort( 1, _1_age );
		prestmt.setString( 2, _2_gender );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

}
