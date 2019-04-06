sqlaction - JDBC代码自动生成工具
=============================================

<!-- TOC -->

- [1. 概述](#1-概述)
- [2. 一个DEMO](#2-一个demo)
    - [2.1. 建表DDL](#21-建表ddl)
    - [2.2. 新建JAVA项目](#22-新建java项目)
    - [2.3. 在包目录中执行`sqlaction`工具](#23-在包目录中执行sqlaction工具)
    - [2.4. 到目前为止，一行JAVA代码都没写，现在开始写应用代码](#24-到目前为止一行java代码都没写现在开始写应用代码)
    - [2.5. 执行](#25-执行)
- [3. 配置文件`dbserver.conf.json`](#3-配置文件dbserverconfjson)
- [4. 配置文件`sqlaction.conf.json`](#4-配置文件sqlactionconfjson)
- [5. 性能压测](#5-性能压测)
- [6. 关于作者](#6-关于作者)

<!-- /TOC -->

# 1. 概述

厌烦了MyBatis和JPA(Hibernate)的冗余配置和繁琐使用，以及XML拷来拷去，写那么多Mapper要是直接用JDBC早就写完了，如果使用一个框架/工具比不使用而带来更多的心智负担，那还不如不用。其实，直接使用JDBC还是蛮干净和高性能的，只要解决其三个痛点：

1. 手工编写数据库表实体类。
1. 手工编写大量setString和getString代码，尤其还要人工保证字段序号递增。
1. 因为直接写SQL，涉及不同DBMS的SQL方言时，移植性不好，比如分页查询。

能否造一个更好的轮子？

于是，我花了十多个晚上，结合之前在C技术栈中的设计和经验，结合JAVA特点，写了sqlaction。

sqlaction是JDBC代码自动生成工具，它为应用提供了类似MyBatis和Hibernate操作数据库能力，但更轻量级和尽量减少冗余手工工作，提高开发效率，也易于与其它框架搭配使用。sqlaction读取数据库中的表结构元信息和少量配置文件信息（SQL），自动生成数据库表实体类，自动生成基于JDBC的SQL动作方法代码，应用调用其自动生成的代码就能极其快捷的操作数据库，同时还拥有JDBC的高性能，更重要的是开发者能直接看到底层操作代码，增加自主可控，没有低效的反射，没有复杂的热修改字节码，没有庞大笨重的隐藏核心。

# 2. 一个DEMO

放一个DEMO感受一下：（全套源码详见`sqlaction/sqlaction-demo`）

## 2.1. 建表DDL

`ddl.sql`

```
CREATE TABLE `sqlaction_demo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '名字',
  `address` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`),
  KEY `sqlaction_demo` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

## 2.2. 新建JAVA项目

建立包目录，在包目录或上级某一级目录中创建数据库连接配置文件`dbserver.conf.json`

```
{
	"driver" : "com.mysql.jdbc.Driver" ,
	"url" : "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT" ,
	"user" : "calvin" ,
	"pwd" : "calvin"
}
```

在包目录或上级某一级目录中创建SQL配置文件`sqlaction.conf.json`

```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "sqlaction_demo" ,
			"sqlactions" : [
				"SELECT * FROM sqlaction_demo" ,
				"SELECT * FROM sqlaction_demo WHERE name=?" ,
				"INSERT INTO sqlaction_demo" ,
				"UPDATE sqlaction_demo SET address=? WHERE name=? @@METHOD(updateAddressByName)" ,
				"DELETE FROM sqlaction_demo WHERE name=?"
			]
		}
	] ,
	"javaPackage" : "xyz.calvinwilliams.sqlaction.demo"
}
```

## 2.3. 在包目录中执行`sqlaction`工具

这里把执行命令行包成批处理文件展示，欢迎懂`Eclipse`插件开发的同学帮我写个插件 :)

pp.bat

```
java -Dfile.encoding=UTF-8 -classpath "D:\Work\sqlaction\sqlaction.jar;D:\Work\mysql-connector-java-8.0.15\mysql-connector-java-8.0.15.jar" xyz.calvinwilliams.sqlaction.gencode.SqlActionGencode
pause
```

执行pp.bat

```
//////////////////////////////////////////////////////////////////////////////
/// sqlaction v0.0.8.0
/// Copyright by calvin<calvinwilliams@163.com,calvinwilliams@gmail.com>
//////////////////////////////////////////////////////////////////////////////
--- dbserverConf ---
  dbms[mysql]
driver[com.mysql.jdbc.Driver]
   url[jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT]
  user[calvin]
   pwd[calvin]
--- sqlactionConf ---
 database[calvindb]
        table[sqlaction_demo]
                sqlaction[SELECT * FROM sqlaction_demo]
                sqlaction[SELECT * FROM sqlaction_demo WHERE name=?]
                sqlaction[INSERT INTO sqlaction_demo]
                sqlaction[UPDATE sqlaction_demo SET address=? WHERE name=? @@METHOD(updateAddressByName)]
                sqlaction[DELETE FROM sqlaction_demo WHERE name=?]
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary
.
SqlActionTable.getTableInDatabase[sqlaction_demo] ...
SqlActionTable.getTableInDatabase[sqlaction_demo] ok
        tableName[sqlaction_demo]
                columnName[id] columnDefault[null] isNullable[false] DataType[SQLACTION_DATA_TYPE_INTEGER] columnLength[0] numericPrecision[10] numericScale[0] isPrimaryKey[true] isAutoIncrement[true] columnComment[编编号号]
                columnName[name] columnDefault[null] isNullable[false] DataType[SQLACTION_DATA_TYPE_VARCHAR] columnLength[32] numericPrecision[0] numericScale[0] isPrimaryKey[false] isAutoIncrement[false] columnComment[名名字字]
                columnName[address] columnDefault[null] isNullable[true] DataType[SQLACTION_DATA_TYPE_VARCHAR] columnLength[128] numericPrecision[0] numericScale[0] isPrimaryKey[false] isAutoIncrement[false] columnComment[地地
址址]
                indexName[PRIMARY] isUnique[true]
                        columnName[id] columnDefault[null] isNullable[false] DataType[SQLACTION_DATA_TYPE_INTEGER] columnLength[0] numericPrecision[10] numericScale[0] isPrimaryKey[true] isAutoIncrement[true] columnComment[编编
号号]
                indexName[sqlaction_demo] isUnique[false]
                        columnName[name] columnDefault[null] isNullable[false] DataType[SQLACTION_DATA_TYPE_VARCHAR] columnLength[32] numericPrecision[0] numericScale[0] isPrimaryKey[false] isAutoIncrement[false] columnCommen
t[名名字字]
SqlActionTable.travelTable[sqlaction_demo] ok
*** NOTICE : Prepare SqlactionDemoSAO.java output buffer ...
...
...
...
*** NOTICE : Write SqlactionDemoSAO.java completed!!!
```

如果没有出现`*** ERROR : ...`说明工具执行成功，在执行所在目录中自动生成了一个JAVA源代码文件`SqlactionDemoSAO.java`

```
// This file generated by sqlaction v0.0.8.0

package xyz.calvinwilliams.sqlaction.demo;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlactionDemoSAO {

	int				id ; // 编号
	String			name ; // 名字
	String			address ; // 地址

	int				_count_ ; // defining for 'SELECT COUNT(*)'

	// SELECT * FROM sqlaction_demo
	public static int SELECT_ALL_FROM_sqlaction_demo( Connection conn, List<SqlactionDemoSAO> sqlactionDemoListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery("SELECT * FROM sqlaction_demo") ;
		while( rs.next() ) {
			SqlactionDemoSAO sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.id = rs.getInt( 1 ) ;
			sqlactionDemo.name = rs.getString( 2 ) ;
			sqlactionDemo.address = rs.getString( 3 ) ;
			sqlactionDemoListForSelectOutput.add(sqlactionDemo) ;
		}
		return sqlactionDemoListForSelectOutput.size();
	}

	// SELECT * FROM sqlaction_demo WHERE name=?
	public static int SELECT_ALL_FROM_sqlaction_demo_WHERE_name_E_( Connection conn, List<SqlactionDemoSAO> sqlactionDemoListForSelectOutput, String _1_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM sqlaction_demo WHERE name=?") ;
		prestmt.setString( 1, _1_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			SqlactionDemoSAO sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.id = rs.getInt( 1 ) ;
			sqlactionDemo.name = rs.getString( 2 ) ;
			sqlactionDemo.address = rs.getString( 3 ) ;
			sqlactionDemoListForSelectOutput.add(sqlactionDemo) ;
		}
		return sqlactionDemoListForSelectOutput.size();
	}

	// INSERT INTO sqlaction_demo (name,address) VALUES (?,?)
	public static int INSERT_INTO_sqlaction_demo( Connection conn, SqlactionDemoSAO sqlactionDemo ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("INSERT INTO sqlaction_demo (name,address) VALUES (?,?)") ;
		prestmt.setString( 1, sqlactionDemo.name );
		prestmt.setString( 2, sqlactionDemo.address );
		return prestmt.executeUpdate() ;
	}

	// UPDATE sqlaction_demo SET address=? WHERE name=? 
	public static int updateAddressByName( Connection conn, String _1_address_ForSetInput, String _1_name_ForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE sqlaction_demo SET address=? WHERE name=? ") ;
		prestmt.setString( 1, _1_address_ForSetInput );
		prestmt.setString( 2, _1_name_ForWhereInput );
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM sqlaction_demo WHERE name=?
	public static int DELETE_FROM_sqlaction_demo_WHERE_name_E_( Connection conn, String _1_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM sqlaction_demo WHERE name=?") ;
		prestmt.setString( 1, _1_name );
		return prestmt.executeUpdate() ;
	}
}
```

工具`sqlaction`内部处理流程如下：

1. 首先查找执行目录中的数据库连接配置文件`dbserver.conf.json`，如果没有找到就迭代往上级目录继续找，从该配置文件中获得数据库连接配置信息。然后以相同的查找逻辑读取`sqlaction.conf.json`获得数据库名、表列表、以及每张表需要执行的SQL语句列表等信息。配置文件往上查找机制是为了灵活适应不同规模项目目录的规划。
1. 连接数据库，读取表结构元信息，每张表对应自动生成一个数据库表实体类JAVA源代码文件，里面还包含SQL动作方法，sqlaction配置文件中的每一条SQL自动解析生成一个基于JDBC的JAVA方法，从此再也不用手工写JDBC代码了！

## 2.4. 到目前为止，一行JAVA代码都没写，现在开始写应用代码

`Demo.java`

```
package xyz.calvinwilliams.sqlaction.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Demo {

	public static void main(String[] args) {
		Connection				conn = null ;
		List<SqlactionDemoSAO>	sqlactionDemoList = null ;
		SqlactionDemoSAO		sqlactionDemo = null ;
		SqlactionDemoSAO		sqlactionDemoForSetInput = null ;
		SqlactionDemoSAO		sqlactionDemoForWhereInput = null ;
		int						nret = 0 ;

		// Connect to database
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
			
			// Delete records with name
			nret = SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_( conn, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_ ok , rows["+nret+"] effected" );
			}
			
			// Insert record
			sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.name = "Calvin" ;
			sqlactionDemo.address = "My address" ;
			nret = SqlactionDemoSAO.INSERT_INTO_sqlaction_demo( conn, sqlactionDemo ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.INSERT_INTO_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.INSERT_INTO_sqlaction_demo ok" );
			}
			
			// Update record with name
			nret = SqlactionDemoSAO.updateAddressByName( conn, "My address 2", "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.updateAddressByName failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.updateAddressByName ok , rows["+nret+"] effected" );
			}
			
			// Query records
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			conn.commit();
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
```

## 2.5. 执行

```
SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_ ok , rows[1] effected
SqlactionDemoSAO.INSERT_INTO_sqlaction_demo ok
SqlactionDemoSAO.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ ok , rows[1] effected
SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo ok
    id[15] name[Calvin] address[My address 2]
```

总结：

对表的增删改查只需调用前面自动生成的数据库表实体类中的方法即可，而且底层执行代码可随时查看，没有什么秘密，没有什么高深的技术。

工具`sqlaction`只在开发阶段使用，与运行阶段无关，说到底只是在应用与JDBC之间自动生成了薄薄的一层代码而已，把大量手工冗余工作都自动做掉了，让开发者节省大量时间而关注业务，减少大量机械操作减轻心智负担。简单朴素，无需MyBatis或Hibernate那么复杂、炫耀技术之嫌。

# 3. 配置文件`dbserver.conf.json`

```
{
	"driver" : "com.mysql.jdbc.Driver" ,
	"url" : "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT" ,
	"user" : "root" ,
	"pwd" : "root" ,
	"userDefineDataTypes" : [
		{ "source":"decimal,*,12,2" , "redefine":"double,*,14,*" }
	]
}
```

# 4. 配置文件`sqlaction.conf.json`

```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user" ,
			"sqlactions" : [
				"SELECT * FROM user" ,
				"SELECT * FROM user WHERE name=?" ,
				"SELECT name,address FROM user WHERE age<=? AND gender=?" ,
				"SELECT * FROM user ORDER BY name DESC" ,
				"SELECT gender,count(*) FROM user GROUP BY gender" ,
				"INSERT INTO user" ,
				"UPDATE user SET level=?" ,
				"UPDATE user SET address='calvin address',level=10 WHERE name='Calvin'" ,
				"UPDATE user SET level=? WHERE age>? AND gender=?" ,
				"DELETE FROM user" ,
				"DELETE FROM user WHERE name='Calvin'" ,
				"DELETE FROM user WHERE age<>? AND gender<>?"
			]
		} ,
		{
			"table" : "user_order" ,
			"sqlactions" : [
				"SELECT /* blablabla~ */ * FROM user_order" ,
				"SELECT * FROM user_order WHERE user_id=?" ,
				"SELECT user.name,user.address,user_order.item_name,user_order.amount,user_order.total_price FROM user,user_order WHERE user.name=? AND user.id=user_order.user_id @@METHOD(queryUserAndOrderByName)" ,
				"SELECT u.name,u.address,o.item_name,o.amount,o.total_price FROM user u,user_order o WHERE u.name=? AND u.id=o.user_id" ,
				"INSERT INTO user_order" ,
				"UPDATE user_order SET total_price=? WHERE user_id=?" ,
				"DELETE FROM user_order"
			]
		}
	] ,
	"javaPackage" : "xyz.calvinwilliams.sqlaction.test"
}
```


# 5. 性能压测

# 6. 关于作者
