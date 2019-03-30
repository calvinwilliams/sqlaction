package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserSAO {

	int				id ; // 编号
	String			name ; // 名字
	String			gender ; // 性别
	short			age ; // 年龄
	String			address ; // 地址
	int				level ; // 级别

	// SELECT * FROM user
	public static int SqlAction_SELECT_ALL_FROM_user( Connection conn, List<UserSAO> userListForSelectOutput, UserSAO userForWhereInput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery("SELECT * FROM user") ;
		while( rs.next() ) {
			UserSAO user = new UserSAO() ;
			user.id = rs.getInt( 1 ) ;
			user.name = rs.getString( 2 ) ;
			user.gender = rs.getString( 3 ) ;
			user.age = rs.getShort( 4 ) ;
			user.address = rs.getString( 5 ) ;
			user.level = rs.getInt( 6 ) ;
			userListForSelectOutput.add(user) ;
		}
		return userListForSelectOutput.size();
	}

	// SELECT * FROM user WHERE name=?
	public static int SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__( Connection conn, List<UserSAO> userListForSelectOutput, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM user WHERE name=?") ;
		prestmt.setString( 1, userForWhereInput.name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserSAO user = new UserSAO() ;
			user.id = rs.getInt( 1 ) ;
			user.name = rs.getString( 2 ) ;
			user.gender = rs.getString( 3 ) ;
			user.age = rs.getShort( 4 ) ;
			user.address = rs.getString( 5 ) ;
			user.level = rs.getInt( 6 ) ;
			userListForSelectOutput.add(user) ;
		}
		return userListForSelectOutput.size();
	}

	// SELECT name,address FROM user WHERE age<=? AND gender=?
	public static int SqlAction_SELECT_name_J_address_FROM_user_WHERE_age_LE___AND_gender_E__( Connection conn, List<UserSAO> userListForSelectOutput, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name,address FROM user WHERE age<=? AND gender=?") ;
		prestmt.setShort( 1, userForWhereInput.age );
		prestmt.setString( 2, userForWhereInput.gender );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserSAO user = new UserSAO() ;
			user.name = rs.getString( 1 ) ;
			user.address = rs.getString( 2 ) ;
			userListForSelectOutput.add(user) ;
		}
		return userListForSelectOutput.size();
	}

	// INSERT INTO user
	public static int SqlAction_INSERT_INTO_user( Connection conn, UserSAO user ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("INSERT INTO user (name,gender,age,address,level) VALUES (?,?,?,?,?)") ;
		prestmt.setString( 1, user.name );
		prestmt.setString( 2, user.gender );
		prestmt.setShort( 3, user.age );
		prestmt.setString( 4, user.address );
		prestmt.setInt( 5, user.level );
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET level=?
	public static int SqlAction_UPDATE_user_SET_level_E__( Connection conn, UserSAO userForSetInput  ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET level=?") ;
		prestmt.setInt( 1, userForSetInput.level );
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET address='calvin address',level=10 WHERE name='Calvin'
	public static int SqlAction_UPDATE_user_SET_address_E__calvin_address__j_level_E_10_WHERE_name_E__Calvin_( Connection conn, UserSAO userForSetInput, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET address='calvin address',level=10 WHERE name='Calvin'") ;
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET level=? WHERE age>? AND gender=?
	public static int SqlAction_UPDATE_user_SET_level_E___WHERE_age_GT___AND__gender_E__( Connection conn, UserSAO userForSetInput, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET level=? WHERE age>? AND gender=?") ;
		prestmt.setInt( 1, userForSetInput.level );
		prestmt.setShort( 2, userForWhereInput.age );
		prestmt.setString( 3, userForWhereInput.gender );
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user
	public static int SqlAction_DELETE_FROM_user( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user") ;
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user WHERE name='Calvin'
	public static int SqlAction_DELETE_FROM_user_WHERE_name_E__Calvin_( Connection conn, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user WHERE name='Calvin'") ;
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user WHERE age<>? AND gender<>?
	public static int SqlAction_DELETE_FROM_user_WHERE_age_NE___AND__gender_NE__( Connection conn, UserSAO userForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user WHERE age<>? AND  gender<>?") ;
		prestmt.setShort( 1, userForWhereInput.age );
		prestmt.setString( 2, userForWhereInput.gender );
		return prestmt.executeUpdate() ;
	}
}
