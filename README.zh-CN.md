sqlaction - 自动生成JDBC代码的数据库持久层工具
=============================================

<!-- TOC -->

- [1. 概述](#1-概述)
- [2. 一个DEMO](#2-一个demo)
    - [2.1. 建表DDL](#21-建表ddl)
    - [2.2. 新建JAVA项目](#22-新建java项目)
    - [2.3. 在包目录中执行sqlaction工具](#23-在包目录中执行sqlaction工具)
    - [2.4. 到目前为止，一行JAVA代码都没写，现在开始写应用代码](#24-到目前为止一行java代码都没写现在开始写应用代码)
    - [2.5. 执行](#25-执行)
- [3. 使用参考](#3-使用参考)
    - [3.1. 开发流程](#31-开发流程)
    - [3.2. 配置文件dbserver.conf.json](#32-配置文件dbserverconfjson)
    - [3.3. 配置文件sqlaction.conf.json](#33-配置文件sqlactionconfjson)
    - [3.4. 自动生成JDBC代码的规则](#34-自动生成jdbc代码的规则)
    - [3.5. 配置元](#35-配置元)
        - [3.5.1. 自定义SQL动作方法名](#351-自定义sql动作方法名)
        - [3.5.2. 抽象统一了自增字段和序列两大数据库阵营对主键值的赋值](#352-抽象统一了自增字段和序列两大数据库阵营对主键值的赋值)
        - [3.5.3. 抽象统一了物理分页功能，原生自带通用分页能力](#353-抽象统一了物理分页功能原生自带通用分页能力)
        - [3.5.4. 拦截器](#354-拦截器)
            - [3.5.4.1. SQL拦截器](#3541-sql拦截器)
    - [3.6. 高级模式](#36-高级模式)
- [4. 为什么这样设计？](#4-为什么这样设计)
- [5. 与MyBatis的开发量比较](#5-与mybatis的开发量比较)
- [6. 与MyBatis的性能比较](#6-与mybatis的性能比较)
    - [6.1. 准备sqlaction](#61-准备sqlaction)
    - [6.2. 准备MyBatis](#62-准备mybatis)
    - [6.3. 测试案例](#63-测试案例)
    - [6.4. 测试结果](#64-测试结果)
- [7. 后续开发](#7-后续开发)
- [8. 关于本项目](#8-关于本项目)
- [9. 关于作者](#9-关于作者)

<!-- /TOC -->

# 1. 概述

厌烦了`MyBatis`和`JPA`(`Hibernate`)的冗余配置和繁琐使用，以及XML拷来拷去，写那么多Mapper要是直接用JDBC早就写完了，如果使用一个框架/工具比不使用而带来更多的心智负担，那还不如不用。其实，直接使用JDBC还是蛮干净和高性能的，只要解决其三个痛点：

1. 手工编写数据库表实体类。
1. 手工编写大量setString和getString代码，尤其还要人工保证字段序号递增。
1. 因为直接写SQL，涉及不同DBMS的SQL方言时，移植性不好，比如分页查询。

能否造一个更好的轮子？

于是，我结合之前在C技术栈中的产品经验，结合JAVA特点，写了sqlaction。

`sqlaction`是自动生成JDBC代码的数据库持久层工具，它为应用提供了类似`MyBatis`和`Hibernate`操作数据库能力，但更轻量级和几乎消除了所有的手工冗余工作（编码和配置），提高开发效率，也最大化运行效率。

`sqlaction`只负责把配置的SQL规则转换为完整的JDBC代码，不干涉数据库连接池管理、数据库事务控制等，也就易于与其它框架搭配使用。

`sqlaction`核心工作原理是读取数据库中的表结构元信息和少量配置文件信息（SQL语法），自动生成数据库表实体类，自动生成基于JDBC的SQL动作方法代码，自动生成拦截器框架等代码，应用调用其自动生成的代码就能极其快捷的操作数据库，同时还拥有JDBC的高性能，更重要的是开发者能直接看到底层操作代码，增加自主可控，没有低效的反射，没有复杂的热修改字节码，没有庞大笨重的隐藏核心要学习。

`sqlaction`核心功能：
1. 多数据库支持，目前支持有MySQL、PostgreSQL、Oracle、Sqlite、SqlServer。
1. 抽象统一了自增字段和序列两大数据库阵营对主键值的赋值，不同数据库用一样的语法配置。
1. 抽象统一了物理分页功能，原生自带通用分页能力，不同数据库用一样的语法配置。
1. 执行效率比MyBatis快约20%。
1. 高级模式支持任意复杂SQL。

`sqlaction`核心优势：
1. 使用`sqlaction`比`MyBatis`减少一半的开发工作量，大幅提升开发效能。
1. `sqlaction`运行性能比`MyBatis`快20%，明显降低服务迟延。
1. 抽象统一了不同数据库的SQL方言差异，真正做到开发与数据库无关。
1. 完整覆盖联机型应用（TP）和分析型应用（AP）两大场景。

# 2. 一个DEMO

放一个DEMO感受一下：

## 2.1. 建表DDL

以MySQL为例

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

`sqlaction`只依赖数据库连接库和作者另一个JSON解析器`okjson`，引入`mysql-connector-java-X.Y.Z.jar`、`okjson-0.0.9.0.jar`文件或Maven坐标。

建立包目录，在包目录或上级某一级目录中创建数据库连接配置文件`dbserver.conf.json`，工具会从执行目录开始往上查找，只要某一级目录中存在这个配置文件即可。

```
{
	"driver" : "com.mysql.jdbc.Driver" ,
	"url" : "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT" ,
	"user" : "calvin" ,
	"pwd" : "calvin"
}
```

在包目录或上级某一级目录中创建SQL动作配置文件`sqlaction.conf.json`，工具会从执行目录开始往上查找，只要某一级目录中存在这个配置文件即可。

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
				"UPDATE sqlaction_demo SET address=? WHERE name=?" ,
				"DELETE FROM sqlaction_demo WHERE name=?"
			]
		}
	] ,
	"javaPackage" : "xyz.calvinwilliams.sqlaction"
}
```

## 2.3. 在包目录中执行sqlaction工具

此示例中我把执行命令行包装成批处理文件执行，欢迎懂`Eclipse`插件开发的同学帮我写个插件 :)

`pp.bat`

```
java -Dfile.encoding=UTF-8 -classpath "D:\Work\mysql-connector-java-8.0.15\mysql-connector-java-8.0.15.jar;%USERPROFILE%\.m2\repository\xyz\calvinwilliams\okjson\0.0.9.0\okjson-0.0.9.0.jar;%USERPROFILE%\.m2\repository\xyz\calvinwilliams\sqlaction\0.2.9.0\sqlaction-0.2.9.0.jar" xyz.calvinwilliams.sqlaction.SqlActionGencode
pause
```

注意：使用`Maven`管理的项目，在`pom.xml`里添加`sqlaction`坐标后，`maven`会自动下载`sqlaction`以及其依赖`okjson`的jar到`C:\User\用户名\.m2\repository\xyz\calvinwilliams\`下，上面执行命令直接引用了Maven目录里的包。`sqlaction`坐标见最后面“关于本项目”章节。

执行`pp.bat`，工具`sqlaction`会从执行目录开始往上查找读入`dbserver.conf.json`和`sqlaction.conf.json`并自动生成所有代码。

```
//////////////////////////////////////////////////////////////////////////////
/// sqlaction v0.2.9.0
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
SqlActionTable.getTableInDatabase[sqlaction_demo] ...
...
*** NOTICE : Write SqlactionDemoSAO.java completed!!!
```

如果没有出现`*** ERROR : ...`说明工具执行成功，在执行所在目录中自动生成了两个JAVA源代码文件

`SqlactionDemoSAO.java`
```
// This file generated by sqlaction v0.2.9.0
// WARN : DON'T MODIFY THIS FILE

package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlactionDemoSAO {

	int				id ; // 编号 // PRIMARY KEY
	String			name ; // 名字
	String			address ; // 地址

	int				_count_ ; // defining for 'SELECT COUNT(*)'

	// SELECT * FROM sqlaction_demo
	public static int SELECT_ALL_FROM_sqlaction_demo( Connection conn, List<SqlactionDemoSAU> sqlactionDemoListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM sqlaction_demo" ) ;
		while( rs.next() ) {
			SqlactionDemoSAU sqlactionDemo = new SqlactionDemoSAU() ;
			sqlactionDemo.id = rs.getInt( 1 ) ;
			sqlactionDemo.name = rs.getString( 2 ) ;
			sqlactionDemo.address = rs.getString( 3 ) ;
			sqlactionDemoListForSelectOutput.add(sqlactionDemo) ;
		}
		rs.close();
		stmt.close();
		return sqlactionDemoListForSelectOutput.size();
	}

	// SELECT * FROM sqlaction_demo WHERE name=?
	public static int SELECT_ALL_FROM_sqlaction_demo_WHERE_name_E_( Connection conn, List<SqlactionDemoSAU> sqlactionDemoListForSelectOutput, String _1_SqlactionDemoSAU_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM sqlaction_demo WHERE name=?" ) ;
		prestmt.setString( 1, _1_SqlactionDemoSAU_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			SqlactionDemoSAU sqlactionDemo = new SqlactionDemoSAU() ;
			sqlactionDemo.id = rs.getInt( 1 ) ;
			sqlactionDemo.name = rs.getString( 2 ) ;
			sqlactionDemo.address = rs.getString( 3 ) ;
			sqlactionDemoListForSelectOutput.add(sqlactionDemo) ;
		}
		rs.close();
		prestmt.close();
		return sqlactionDemoListForSelectOutput.size();
	}

	// INSERT INTO sqlaction_demo
	public static int INSERT_INTO_sqlaction_demo( Connection conn, SqlactionDemoSAU sqlactionDemo ) throws Exception {
		PreparedStatement prestmt ;
		Statement stmt ;
		ResultSet rs ;
		prestmt = conn.prepareStatement( "INSERT INTO sqlaction_demo (name,address) VALUES (?,?)" ) ;
		prestmt.setString( 1, sqlactionDemo.name );
		prestmt.setString( 2, sqlactionDemo.address );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE sqlaction_demo SET address=? WHERE name=?
	public static int UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_( Connection conn, String _1_address_ForSetInput, String _1_name_ForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE sqlaction_demo SET address=? WHERE name=?" ) ;
		prestmt.setString( 1, _1_address_ForSetInput );
		prestmt.setString( 2, _1_name_ForWhereInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM sqlaction_demo WHERE name=?
	public static int DELETE_FROM_sqlaction_demo_WHERE_name_E_( Connection conn, String _1_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM sqlaction_demo WHERE name=?" ) ;
		prestmt.setString( 1, _1_name );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

}
```

`SqlactionDemoSAU.java`
```
// This file generated by sqlaction v0.2.9.0

package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlactionDemoSAU extends SqlactionDemoSAO {

}
```

## 2.4. 到目前为止，一行JAVA代码都没写，现在开始写应用代码

`Demo.java`

```
package xyz.calvinwilliams.sqlaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Demo {

	public static void main(String[] args) {
		Connection				conn = null ;
		List<SqlactionDemoSAU>	sqlactionDemoList = null ;
		SqlactionDemoSAU		sqlactionDemo = null ;
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
			nret = SqlactionDemoSAU.DELETE_FROM_sqlaction_demo_WHERE_name_E_( conn, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAU.DELETE_FROM_sqlaction_demo_WHERE_name_E_ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAU.DELETE_FROM_sqlaction_demo_WHERE_name_E_ ok , rows["+nret+"] effected" );
			}
			
			// Insert record
			sqlactionDemo = new SqlactionDemoSAU() ;
			sqlactionDemo.name = "Calvin" ;
			sqlactionDemo.address = "My address" ;
			nret = SqlactionDemoSAU.INSERT_INTO_sqlaction_demo( conn, sqlactionDemo ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAU.INSERT_INTO_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAU.INSERT_INTO_sqlaction_demo ok" );
			}
			
			// Update record with name
			nret = SqlactionDemoSAU.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_( conn, "My address 2", "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAU.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAU.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ ok , rows["+nret+"] effected" );
			}
			
			// Query records
			sqlactionDemoList = new LinkedList<SqlactionDemoSAU>() ;
			nret = SqlactionDemoSAU.SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAU r : sqlactionDemoList ) {
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
SqlactionDemoSAU.DELETE_FROM_sqlaction_demo_WHERE_name_E_ ok , rows[1] effected
SqlactionDemoSAU.INSERT_INTO_sqlaction_demo ok
SqlactionDemoSAU.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ ok , rows[1] effected
SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo ok
    id[20] name[Calvin] address[My address 2]
```

总结：

对表的增删改查只需调用前面自动生成的SAU类中的方法即可，底层执行代码完全基于JDBC，可随时查看，没有什么秘密，没有什么高深的技术。

工具`sqlaction`只在开发阶段使用，与运行阶段无关，说到底只是在应用与JDBC之间自动生成了薄薄的一层代码而已，把大量手工冗余代码都通过自动化生成了，让开发者节省大量时间而去关注业务逻辑，减少大量机械操作减轻心智负担，提高生产力，早点做完工作回家抱女盆友/老婆 :)

# 3. 使用参考

## 3.1. 开发流程

```
                                        sqlaction
dbserver.conf.json、sqlaction.conf.json ---------> XxxSao.java、XxxSau.java --\
                                                                               ---> App.jar
                                                                   App.java --/
```

`sqlaction`读取执行目录或上级目录中的数据库连接配置文件`dbserver.conf.json`和SQL动作配置文件`sqlaction.conf.json`，自动生成基于JDBC的SQL动作源代码的SAO类，以及继承SAO类的SAU类，应用直接调用SAU类就能操作数据库DML。

SAO类`XXXSao.java`在每次执行工具`sqlaction`时都会被覆盖刷新，所以不要手工修改。

SAU类`XXXSau.java`在首次执行工具`sqlaction`时创建，后续执行时不会被覆盖刷新，所以需要定制、扩展的方法和属性都加在这个类中，又因为SAU类继承自SAO类，如果存在相同的方法，应用调用的始终都是SAU类中的版本。

## 3.2. 配置文件dbserver.conf.json

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

数据库连接配置文件`dbserver.conf.json`配置了工具`sqlaction`执行所需数据库层面上的信息：

`driver` : DBMS驱动类。

`url` : DBMS连接配置串。

`user` : DBMS连接用户名。

`pwd` : DBMS连接密码。

`userDefineDataTypes` : 自定义字段类型转换，比如数据库中的类型`DECIMAL(12,2)`映射到JAVA变量类型是`BigDecimal`，但在某应用系统中希望是JAVA变量类型`double`，可以在这个配置集中在正式转换前把`DECIMAL(12,2)`强制转换成`DOUBLE`，那么正式转换时`DOUBLE`会映射成JAVA变量类型`double`。

数据库字段类型与sqlaction的JAVA变量类型映射表：

| MySQL字段类型 | sqlaction的JAVA变量类型 |
|---|---|
| bit | boolean |
| tinyint | byte |
| smallint | short |
| mediumint | int |
| int | int |
| bigint | long |
| real | float |
| float | double |
| double | double |
| decimal | BigDecimal |
| numeric | BigDecimal |
| char | String |
| varchar | String |
| date | java.sql.Date |
| time | java.sql.Time |
| datetime | java.sql.Date |
| timestamp | timestamp |
| year | java.sql.Date |
| binary | byte[] |
| varbinary | byte[] |
| blob | byte[] |
| tinyblob | byte[] |
| mediumblob | byte[] |
| longblob | byte[] |
| (other) | String |

注意：数据库连接配置文件`dbserver.conf.json`一般放在项目根目录里，以便于所有子模块都能使用到。

注意：读取JSON配置文件使用到了我的另一个开源项目：`okjson`，一个简洁易用的JSON解析器/生成器，只有一个类文件，可以很方便的融合到其它项目中。

`dbms` : 一般情况下`sqlaction`根据`driver`能猜对数据库类型，那么这个配置就不用写明，如果你用的`driver`实在太怪异导致猜不到，请显式配置。

## 3.3. 配置文件sqlaction.conf.json

```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"SELECT * FROM user_base" ,
				"SELECT * FROM user_base WHERE name=?" ,
				"SELECT name,address FROM user_base WHERE age<=? AND gender=?" ,
				"SELECT * FROM user_base ORDER BY name DESC" ,
				"SELECT gender,count(*) FROM user_base GROUP BY gender" ,
				"INSERT INTO user_base @@SELECTSEQ(user_base_seq_id) @@SELECTKEY(id)" ,
				"UPDATE user_base SET lvl=?" ,
				"UPDATE user_base SET address='calvin address',lvl=10 WHERE name='Calvin'" ,
				"UPDATE user_base SET lvl=? WHERE age>? AND gender=?" ,
				"DELETE FROM user_base" ,
				"DELETE FROM user_base WHERE name='Calvin'" ,
				"DELETE FROM user_base WHERE age<>? AND gender<>?"
			]
		} ,
		{
			"table" : "user_order" ,
			"sqlactions" : [
				"SELECT /* blablabla~ */ * FROM user_order @@STATEMENT_INTERCEPTOR()" ,
				"SELECT * FROM user_order WHERE user_id=?" ,
				"SELECT * FROM user_order @@PAGEKEY(id)" ,
				"SELECT * FROM user_order WHERE item_name<>'' @@PAGEKEY(id) @@PAGESORT(DESC)" ,
				"SELECT user_base.name,user_base.address,user_order.item_name,user_order.amount,user_order.total_price
					FROM user_base,user_order
					WHERE user_base.name=? AND user_base.id=user_order.user_id
					@@METHOD(queryUserAndOrderByName)" ,
				"SELECT u.name,u.address,o.* FROM user_base u,user_order o WHERE u.name=? AND u.id=o.user_id @@STATEMENT_INTERCEPTOR(statementInterceptorForQueryUserAndOrderByName)" ,
				"SELECT o.* #{UserOrderSAU.*} FROM user_order o #{user_order} @@ADVANCEDMODE" ,
				"SELECT MIN(total_price) #{SqlActionTest.minTotalPrice:double}, MAX(total_price) #{SqlActionTest.maxTotalPrice:double}, COUNT(*) #{UserOrderSAU._count_}
					FROM user_order #{user_order}
					@@ADVANCEDMODE" ,
				"SELECT user_base.name				#{UserBaseSAU.name}
					,user_order.item_name			#{UserOrderSAU.itemName}
					,SUM(user_order.amount)			#{UserOrderSAU.amount}
					,SUM(user_order.total_price)	#{UserOrderSAU.totalPrice}
					FROM user_base					#{user_base}
						,user_order					#{user_order}
					WHERE user_order.user_id IN (
												SELECT id
												FROM user_base
												WHERE id>=?		#{UserOrderSAU.id}
											)
						AND user_order.user_id=user_base.id
					GROUP BY user_base.name
					ORDER BY user_base.name
					@@ADVANCEDMODE @@METHOD(statUsersAmountAndTotalPrice)" ,
				"INSERT INTO user_order @@SELECTSEQ(user_order_seq_id) @@SELECTKEY(id)" ,
				"UPDATE user_order SET total_price=? WHERE user_id=?" ,
				"DELETE FROM user_order" ,
				"DELETE FROM user_order WHERE user_id=? #{UserOrderSAU.userId} @@ADVANCEDMODE @@METHOD(removeUserOrder)"
			]
		}
	] ,
	"javaPackage" : "xyz.calvinwilliams.test"
}
```

SQL动作配置文件`sqlaction.conf.json`主要配置了数据库SQL动作列表，用于后续的自动生成JDBC代码。

`database` : 数据库名。

`tables` : 表列表。

`table` : 表名。

`sqlactions` : SQL语句列表。注意最后一条SQL后不带','以符合JSON规范。

`javaPackage` : JAVA包名，自动生成JAVA类文件时放在最上面。

目前`sqlaction`支持的SQL标准：

**查询**

SQL动作语法：
```
SELECT [*|[table_name.|table_alias_name.][column_name|*][,...][,COUNT(*)]]
    [ /* hint */ ]
    FROM table_name [table_alias_name],...
    [ WHERE [table_name.|table_alias_name.]column_name [=|<>|>|>=|<|<=] [?|const|[table_name2.|table_alias_name2.]column_name2] [AND ...] ]
    [ GROUP BY [table_name.|table_alias_name.]column[,[table_name2.|table_alias_name.]column2][,...] ]
    [ HAVING ... ]
    [ ORDER BY column[,...] [ASC|DESC] ]
    ...
```

SQL动作语法示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"SELECT * FROM user_base" ,
				"SELECT * FROM user_base WHERE name=?" ,
				"SELECT name,address FROM user_base WHERE age<=? AND gender=?" ,
				"SELECT * FROM user_base ORDER BY name DESC" ,
				"SELECT gender,count(*) FROM user_base GROUP BY gender" ,
```

自动生成JDBC代码：
```
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
```

**插入**

SQL动作语法：
```
INSERT INTO table_name
```

SQL动作语法示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"INSERT INTO user_base @@SELECTSEQ(user_base_seq_id) @@SELECTKEY(id)" ,
```

自动生成JDBC代码：
```
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

```

**更新**

SQL动作语法：
```
UPDATE table_name
    SET column_name = [?|const|column_name2] [,...]
    [ WHERE column_name [=|<>|>|>=|<|<=] [const|column_name2] [AND ...] ]
```

SQL动作语法示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"UPDATE user_base SET lvl=?" ,
				"UPDATE user_base SET address='calvin address',lvl=10 WHERE name='Calvin'" ,
				"UPDATE user_base SET lvl=? WHERE age>? AND gender=?" ,
```

自动生成JDBC代码：
```
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
```

**删除**

SQL动作语法：
```
DELETE FROM table_name
    [ WHERE column_name [=|<>|>|>=|<|<=] [const|column_name2] [AND ...] ]
```

SQL动作语法示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"DELETE FROM user_base" ,
				"DELETE FROM user_base WHERE name='Calvin'" ,
				"DELETE FROM user_base WHERE age<>? AND gender<>?"
```

自动生成JDBC代码：
```
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
```

注意：数据库连接配置文件`sqlaction.conf.json`一般放在JAVA包目录里，以便于自动生成的类打包。

## 3.4. 自动生成JDBC代码的规则

工具`sqlaction`读取数据库中的表结构元信息和SQL动作配置文件`sqlaction.conf.json`，在执行目录里自动生成JAVA类源代码文件`XxxSao.java`和`XxxSau.java`。类源代码文件`XxxSao.java`包含数据库表实体信息（字段映射属性）和SQL动作对应方法，每次运行`sqlaction`都会刷新该类源代码文件，所以不要修改此文件。类源代码文件`XxxSau.java`包含用户自定义代码，首次运行`sqlaction`会生成该类源代码文件，所以用户定制代码可写到此文件中。

数据库表字段映射属性由数据库中的表结构元信息映射生成，转换规则见前面的数据库字段类型与sqlaction的JAVA变量类型映射表。如果DDL中有comment，则在表实体类的对应属性后面加注释。

SQL动作对应缺省方法名为SQL转换而来，具体算法为所有非字母数字字符都转换为'\_'，合并多个'\_'为一个。

方法前的注释是原SQL，以便于对照和定位。

方法的第一个参数是数据库连接对象，可以和连接池框架结合使用。

如果SQL动作涉及输出，自动生成的代码在SQL执行后，根据解析出来的输出项（SELECT）自动生成getString等代码，方法参数中也要求给予以便于输出，按表实体类列表对象排列，SQL JOIN多表对应多个表实体类列表对象。

如果SQL动作涉及输入，自动生成的代码将使用JDBC的prepareStatement，并根据解析出来的输入项（SET、WHERE）自动生成setString等代码，方法参数中也要求给予以便于输入，按字段名排列。如果没有输入则使用createStatement。

如果是查询SQL，JAVA方法返回表实体类列表大小。如果是插入、更新、删除SQL，JAVA方法返回受影响记录条数。

表实体类属性列表中额外有"int _count_ ;"，用于查询COUNT(*)时存储输出结果用。

插入方法中会自动识别忽略自增类型。

就这么简单！

## 3.5. 配置元

SQL动作的最后面可追加一些以"@@"开头的配置元以实现一些额外的功能。

### 3.5.1. 自定义SQL动作方法名

允许自定义SQL动作方法名，在SQL动作配置中追加元信息"@@METHOD(自定义方法名)"，如：
```
				"SELECT user_base.name,user_base.address,user_order.item_name,user_order.amount,user_order.total_price
					FROM user_base,user_order
					WHERE user_base.name=? AND user_base.id=user_order.user_id
					@@METHOD(queryUserAndOrderByName)" ,
```

自动生成代码如下：
```
	public static int queryUserAndOrderByName( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, List<UserOrderSAU> userOrderListForSelectOutput, String _1_UserBaseSAU_name ) throws Exception {
		...
		return userBaseListForSelectOutput.size();
	}
```

### 3.5.2. 抽象统一了自增字段和序列两大数据库阵营对主键值的赋值

在INSERT时对主键值的赋值，一些拥有自增类型的数据库如MySQL只要在DDL里指明`AUTO_INCREMENT`，插入时会自动取当前值自增一作为新记录该字段的插入值，插入成功后调用`SELECT LAST_INSERT_ID()`回取出来告知应用，另一些拥有序列（SEQUENCE）的数据库如Oracle在插入前获取序列的下一个值作为新记录该字段的插入值。`sqlaction`为了统一前面两种对主键值的赋值在使用层面的抽象，定义了两个配置元`@@SELECTKEY(字段名)`和`@@SELECTSEQ(序列名)`。

示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user" ,
			"sqlactions" : [
				...
				"INSERT INTO user_base @@SELECTKEY(id) @@SELECTSEQ(user_base_seq_id)" ,
```

当在MySQL等有自增功能的DBMS中，`sqlaction`处理`sqlaction.conf.json`自动生成的JDBC代码中，在INSERT操作完后，追加生成调用"SELECT LAST_INSERT_ID()"的代码，取得自增主键值，赋值到主键字段中：
```
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
```

当在PostgreSQL等没有自增功能但有序列的DBMS中，`sqlaction`处理`sqlaction.conf.json`自动生成的JDBC代码中，在INSERT操作前，调用"select user_id_seq.nextval from dual;"取得序列值，赋值到主键字段中，然后再INSERT：
```
	public static int INSERT_INTO_user_base( Connection conn, UserBaseSAU userBase ) throws Exception {
		PreparedStatement prestmt ;
		Statement stmt ;
		ResultSet rs ;
		stmt = conn.createStatement() ;
		rs = stmt.executeQuery( "SELECT NEXTVAL('user_base_seq_id')" ) ;
		rs.next();
		userBase.id = rs.getInt( 1 ) ;
		rs.close();
		stmt.close();
		
		prestmt = conn.prepareStatement( "INSERT INTO user_base (id,name,gender,age,address,lvl) VALUES (?,?,?,?,?,?)" ) ;
		prestmt.setInt( 1, userBase.id );
		prestmt.setString( 2, userBase.name );
		prestmt.setString( 3, userBase.gender );
		prestmt.setShort( 4, userBase.age );
		prestmt.setString( 5, userBase.address );
		prestmt.setInt( 6, userBase.lvl );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}
```

注意：没有拿Oracle作例子是因为`sqlaction`对Oracle支持还有问题，主要是Oracle表名的大写约束。

### 3.5.3. 抽象统一了物理分页功能，原生自带通用分页能力

不同数据库的分页SQL写法各异，`sqlaction`为了统一所有分页SQL，定义了两个配置元`@@PAGEKEY(分页字段)`和`@@PAGESORT(ASC|DESC)`。

在SQL动作追加分页键配置元"@@PAGEKEY(...)"以自动生成分页代码，还可以可选的加上排序配置元"@@PAGESORT(ASC|DESC)"，如：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user" ,
			"sqlactions" : [
				...
				"SELECT * FROM user_order @@PAGEKEY(id)" ,
				"SELECT * FROM user_order WHERE item_name<>'' @@PAGEKEY(id) @@PAGESORT(DESC)" ,
```

当数据库为MySQL时自动生成的JAVA代码如下：
```
	// SELECT * FROM user_order @@PAGEKEY(id)
	public static int SELECT_ALL_FROM_user_order_PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE id>=(SELECT id FROM user_order ORDER BY id LIMIT ?,1) ORDER BY id LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT * FROM user_order WHERE item_name<>'' @@PAGEKEY(id) @@PAGESORT(DESC)
	public static int SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE id<=(SELECT id FROM user_order ORDER BY id DESC LIMIT ?,1) AND item_name<>'' ORDER BY id DESC LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}
```

当数据库为PostgrelSQL时自动生成的JAVA代码如下：
```
	// SELECT * FROM user_order @@PAGEKEY(id)
	public static int SELECT_ALL_FROM_user_order_PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order OFFSET ? LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT * FROM user_order WHERE item_name<>'' @@PAGEKEY(id) @@PAGESORT(DESC)
	public static int SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE item_name<>'' OFFSET ? LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}
```

其它支持的数据库还有Oracle、Sqlite、SqlServer。

应用代码如下：
```
			for( int pageNum=1 ; ; pageNum++ ) {
				userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
				nret = UserOrderSAU.SELECT_ALL_FROM_user_order_PAGEKEY_id( conn, userOrderListForSelectOutput, 3, pageNum ) ;
				if( nret < 0 ) {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_PAGEKEY_id failed["+nret+"]" );
					return -23;
				} else {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_PAGEKEY_id ok , ["+userOrderListForSelectOutput.size()+"]records" );
					if( userOrderListForSelectOutput.size() == 0 )
						break;
					for( UserOrderSAU o : userOrderListForSelectOutput ) {
						System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
					}
				}
			}
			
			for( int pageNum=1 ; ; pageNum++ ) {
				userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
				nret = UserOrderSAU.SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id( conn, userOrderListForSelectOutput, 3, pageNum ) ;
				if( nret < 0 ) {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id failed["+nret+"]" );
					return -24;
				} else {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id ok , ["+userOrderListForSelectOutput.size()+"]records" );
					if( userOrderListForSelectOutput.size() == 0 )
						break;
					for( UserOrderSAU o : userOrderListForSelectOutput ) {
						System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
					}
				}
			}
```

注意：目前只支持单表单分页键分页，SQL动作语法如下：
```
SELECT [*|column_name[,...]]
    [ /* hint */ ]
    FROM table_name
    [ WHERE column_name [=|<>|>|>=|<|<=] [?|const|column_name2] [AND ...] ]
    @@PAGEKEY(column_name) [ @@PAGESORT(ASC|DESC) ]
```

### 3.5.4. 拦截器

#### 3.5.4.1. SQL拦截器

如果需要SQL真正执行前微调一下SQL（如分库分表修改hint），可加入拦截器配置元"@@STATEMENT_INTERCEPTOR(拦截器方法名，填空则自动生成一个)"，配置示例：
```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user" ,
			"sqlactions" : [
				...
				"SELECT /* blablabla~ */ * FROM user_order @@STATEMENT_INTERCEPTOR()" ,
```
或自定义拦截器方法名
```
				"SELECT u.name,u.address,o.* FROM user_base u,user_order o WHERE u.name=? AND u.id=o.user_id @@STATEMENT_INTERCEPTOR(statementInterceptorForQueryUserAndOrderByName)" ,
```

首次生成XxxSau.java中出现
```
public class UserOrderSAU extends UserOrderSAO {
	
	public static String STATEMENT_INTERCEPTOR_for_SELECT_HT_blablabla_TH_ALL_FROM_user_order( String statementSql ) {
		
		return statementSql;
	}
	
	public static String statementInterceptorForQueryUserAndOrderByName( String statementSql, String _1_UserBaseSAU_name ) {
		
		return statementSql;
	}
```

拦截器方法输入参数与SQL动作方法的一致，可根据每次调用SQL动作方法时，拦截器方法拿到相同的输入参数对SQL进行修改，如hint。

注意：由于`XxxSau.java`只有在首次执行`sqlaction`才会自动生成，后面增加的拦截器方法框架源代码虽然不会自动添加到`XxxSau.java`中，但会作为注释出现在`XxxSao.java`中，方便开发者复制粘贴过去。

## 3.6. 高级模式

之前介绍的普通模式支持SQL语法有限，主要用于联机服务（TP场景），如果要支持复杂SQL（AP场景）可使用高级模式。

在`sqlaction.conf.json`的SQL动作最后面加上配置元`@@ADVANCEDMODE`即可开启这条SQL动作的高级模式。高级模式对SQL书写几乎没有任何限制，但引入一定的配置复杂性，示例：

```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "user_base" ,
			"sqlactions" : [
				"SELECT * FROM user_base" ,
				"SELECT * FROM user_base WHERE name=?" ,
				"SELECT name,address FROM user_base WHERE age<=? AND gender=?" ,
				"SELECT * FROM user_base ORDER BY name DESC" ,
				"SELECT gender,count(*) FROM user_base GROUP BY gender" ,
				"INSERT INTO user_base @@SELECTSEQ(user_base_seq_id) @@SELECTKEY(id)" ,
				"UPDATE user_base SET lvl=?" ,
				"UPDATE user_base SET address='calvin address',lvl=10 WHERE name='Calvin'" ,
				"UPDATE user_base SET lvl=? WHERE age>? AND gender=?" ,
				"DELETE FROM user_base" ,
				"DELETE FROM user_base WHERE name='Calvin'" ,
				"DELETE FROM user_base WHERE age<>? AND gender<>?"
			]
		} ,
		{
			"table" : "user_order" ,
			"sqlactions" : [
				"SELECT MIN(total_price) #{SqlActionTest.minTotalPrice:double}, MAX(total_price) #{SqlActionTest.maxTotalPrice:double}, COUNT(*) #{UserOrderSAU._count_}
					FROM user_order #{user_order}
					@@ADVANCEDMODE" ,
				"SELECT user_base.name				#{UserBaseSAU.name}
					,user_order.item_name			#{UserOrderSAU.itemName}
					,SUM(user_order.amount)			#{UserOrderSAU.amount}
					,SUM(user_order.total_price)	#{UserOrderSAU.totalPrice}
					FROM user_base					#{user_base}
						,user_order					#{user_order}
					WHERE user_order.user_id IN (
												SELECT id
												FROM user_base
												WHERE id>=?		#{UserOrderSAU.id}
											)
						AND user_order.user_id=user_base.id
					GROUP BY user_base.name
					ORDER BY user_base.name
					@@ADVANCEDMODE @@METHOD(statUsersAmountAndTotalPrice)" ,
				"DELETE FROM user_order WHERE user_id=? #{UserOrderSAU.userId} @@ADVANCEDMODE @@METHOD(removeUserOrder)"
```

SQL的FROM后必须配置有`#{表名}`，在SELECT后的每一个字段后都要有`#{SAU类名.类属性名}`绑定以便查询结果输出，在WHERE或SET后的每一个字段后都要有`#{SAU类名.类属性名}`绑定以便查询请求输入，给足了这些信息就能让工具自动生成完整的JDBC源代码了。

之前示例SQL动作生成的JDBC代码如下：
```
	// SELECT MIN(total_price) #{SqlActionTest.minTotalPrice:double}, MAX(total_price) #{SqlActionTest.maxTotalPrice:double}, COUNT(*) #{UserOrderSAU._count_}
	// 					FROM user_order #{user_order}
	// 					@@ADVANCEDMODE
	public static int SELECT_MIN_total_price_j_MAX_total_price_j_COUNT_ALL_FROM_user_order( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, List<SqlActionTest> sqlActionTestListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT MIN(total_price) , MAX(total_price) , COUNT(*) FROM user_order" ) ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			SqlActionTest sqlActionTest = new SqlActionTest() ;
			sqlActionTest.minTotalPrice = rs.getDouble( 1 ) ;
			sqlActionTest.maxTotalPrice = rs.getDouble( 2 ) ;
			userOrder._count_ = rs.getInt( 3 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
			sqlActionTestListForSelectOutput.add(sqlActionTest) ;
		}
		rs.close();
		stmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT user_base.name				#{UserBaseSAU.name}
	// 					,user_order.item_name			#{UserOrderSAU.itemName}
	// 					,SUM(user_order.amount)			#{UserOrderSAU.amount}
	// 					,SUM(user_order.total_price)	#{UserOrderSAU.totalPrice}
	// 					FROM user_base					#{user_base}
	// 						,user_order					#{user_order}
	// 					WHERE user_order.user_id IN (
	// 												SELECT id
	// 												FROM user_base
	// 												WHERE id>=?		#{UserOrderSAU.id}
	// 											)
	// 						AND user_order.user_id=user_base.id
	// 					GROUP BY user_base.name
	// 					ORDER BY user_base.name
	// 					@@ADVANCEDMODE @@METHOD(statUsersAmountAndTotalPrice)
	public static int statUsersAmountAndTotalPrice( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, List<UserOrderSAU> userOrderListForSelectOutput, int _1_UserOrderSAU_id ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT user_base.name ,user_order.item_name ,SUM(user_order.amount) ,SUM(user_order.total_price) FROM user_base ,user_order WHERE user_order.user_id IN ( SELECT id FROM user_base WHERE id>=? ) AND user_order.user_id=user_base.id GROUP BY user_base.name ORDER BY user_base.name" ) ;
		prestmt.setInt( 1, _1_UserOrderSAU_id );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userBase.name = rs.getString( 1 ) ;
			userOrder.itemName = rs.getString( 2 ) ;
			userOrder.amount = rs.getInt( 3 ) ;
			userOrder.totalPrice = rs.getDouble( 4 ) ;
			userBaseListForSelectOutput.add(userBase) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}
	
	// DELETE FROM user_order WHERE user_id=? #{UserOrderSAU.userId} @@ADVANCEDMODE @@METHOD(removeUserOrder)
	public static int removeUserOrder( Connection conn, int _1_userId ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_order WHERE user_id=?" ) ;
		prestmt.setInt( 1, _1_userId );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}
```

# 4. 为什么这样设计？

数据库应用接口层框架/工具对于表结构的配置源的有两派思路，一派是定义在配置文件中，好处是可以做不同DBMS的统一规范，如同一种数据类型的统一表达，坏处是与数据库之间同步较麻烦，另一派是定义在数据库中，需要用时读数据库中的元信息，好处是可以利用数据库设计工具，图形化界面管理表结构，还能自动生成E-R图，坏处是不同DBMS存在标准差异。`sqlaction`采用后一派思路，在数据类型与JAVA属性类型之间建立多DBMS映射表来解决标准差异。

很多数据库持久化框架对于SQL动作都定义了一套自己的动作语法标准，`sqlaction`坚持采用原SQL来配置，减少开发人员学习负担。在自增字段或序列对主键值的赋值和分页查询等差异问题上，`sqlaction`定义一套兼容标准来统一SQL动作表达。

`sqlaction`读取数据库连接配置文件和SQL动作配置文件支持本目录或上级某一层目录，这样就可以通过存放位置灵活适应各种各样的项目代码结构，比如整个项目只对应一个数据库实例，或者项目中每个模块对应一个数据库实例。

为了兼顾修改刷新和定制代码留存两全问题上，`sqlaction`设计了两个JAVA类源代码文件`XxxSAO.java`和`XxxSAU.java`，并规定每次执行工具强制`XxxSAO.java`，但只有首次执行才创建`XxxSAU.java`，这样定制代码只要写在`XxxSAU.java`中就不怕被刷新覆盖，但又通过刷新`XxxSAO.java`来体现每次修改。由于`XxxSAU`类继承自`XxxSAO`类，所以一旦重载了方法，`XxxSAU`定制内容优先。

`sqlaction`的SQL集合都放在一个文件中，便于审计表操作，如很容易查出是否有操作游离于索引之外，但生产部署又不需要其SQL动作配置文件，防止被人篡改。

`sqlaction`坚持采用最小化配置原则，规避一切冗余配置，尽力减少开发人员工作量，推荐用缺省值工作，如果需要自定义再提供额外的配置，如SQL动作方法名默认情况下无需配置，按照缺省规则就能自动根据SQL生成一个含义清晰的名字，开发人员无须为每个SQL动作必须配置方法名，甚至无需繁复的XML替代JAVA语言定义方法的输入输出参数（MyBatis），而且配置错误时只有在运行期才告知。

`sqlaction`朴素，无需`MyBatis`或`Hibernate`那么复杂、炫耀技术之嫌；`sqlaction`简单，只是代替手工而自动生成JDBC代码段落，没有运行时框架，不做其它事情（如连接池、分布式事务控制等），保持代码架构简单、透明、可控和高效，便于和其它数据库框架/工具协同工作；`sqlaction`对开发友好，大部分错误都能在预处理期或编译期发现和提示，而不像某些“高端”框架只有到了运行期才警示开发问题。

简洁是优秀工具的特质，而不是为了解决一种复杂性而带来另一种复杂性。

# 5. 与MyBatis的开发量比较

<table>
	<tr>
		<td align="center">MyBatis</td>
		<td align="center">sqlaction</td>
	</tr>
	<tr>
		<td colspan="2" align="center">每个项目手工开发量<td>
	</tr>
	<tr>
		<td>编写数据库连接信息配置文件<img src="mybatis-config.xml.png" /></td>
		<td>编写数据库连接信息配置文件<img src="dbserver.conf.json.png" /></td>
	</tr>
	<tr>
		<td colspan="2" align="center">每个表手工开发量<td>
	</tr>
	<tr>
		<td>编写表操作配置文件<img src="mybatis-mapper.xml.png" /></td>
		<td>编写表操作配置文件<img src="sqlaction.conf.json.png" /></td>
	</tr>
	<tr>
		<td>编写表实体类文件<img src="SqlactionBenchmarkSAO.java.png" /></td>
		<td>sqlaction原生自动生成</td>
	</tr>
	<tr>
		<td>编写表操作接口类文件<img src="SqlactionBenchmarkSAOMapper.java.png" /></td>
		<td>sqlaction不需要</td>
	</tr>
	<tr>
		<td>MyBatis不需要</td>
		<td>sqlaction处理命令行：<br />java -Dfile.encoding=UTF-8 -classpath "D:\Work\sqlaction\sqlaction.jar;D:\Work\mysql-connector-java-8.0.15\mysql-connector-java-8.0.15.jar" xyz.calvinwilliams.sqlaction.gencode.SqlActionGencode</td>
	</tr>
</table>

以上表格可以看出，每个项目都要配置一遍的数据库连接信息配置文件，MyBatis比sqlaction大了一倍，日常表配置/源文件，MyBatis要手工编写三个文件（存在很多冗余工作），sqlaction只需手工编写一个文件即可（一点冗余都没有，最大化提升开发效能）。

# 6. 与MyBatis的性能比较

由于`sqlaction`自动生成的JDBC代码，与手工代码基本无异，没有低效的反射，没有多坑的热修改字节码，所以稳定性和运行性能都非常出色，下面是`sqlaction`与`MyBatis`的性能测试。

CPU : Intel Core i5-7500 3.4GHz 3.4GHz
内存 : 16GB
操作系统 : WINDOWS 10
JAVA开发工具 : Eclipse 2018-12
数据库 : MySQL 8.0.15 Community Server
数据库连接地址 : 127.0.0.1:3306

DDL

```
CREATE TABLE `sqlaction_benchmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '英文名',
  `name_cn` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '中文名',
  `salary` decimal(12,2) NOT NULL COMMENT '薪水',
  `birthday` date NOT NULL COMMENT '生日',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42332 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

## 6.1. 准备sqlaction

手工编写数据库连接配置文件`dbserver.conf.json`

```
{
	"driver" : "com.mysql.jdbc.Driver" ,
	"url" : "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT" ,
	"user" : "calvin" ,
	"pwd" : "calvin"
}
```

手工编写SQL动作配置文件`sqlaction.conf.json`

```
{
	"database" : "calvindb" ,
	"tables" : [
		{
			"table" : "sqlaction_benchmark" ,
			"sqlactions" : [
				"INSERT INTO sqlaction_benchmark" ,
				"UPDATE sqlaction_benchmark SET salary=? WHERE name=?" ,
				"SELECT * FROM sqlaction_benchmark WHERE name=?" ,
				"SELECT * FROM sqlaction_benchmark" ,
				"DELETE FROM sqlaction_benchmark WHERE name=?" ,
				"DELETE FROM sqlaction_benchmark"
			]
		}
	] ,
	"javaPackage" : "xyz.calvinwilliams.sqlaction.benchmark"
}
```

运行工具`sqlaction`，自动生成`SqlactionBenchmarkSAO.java`

手工编写性能测试应用类`SqlActionBenchmarkCrud.java`

```
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
```

## 6.2. 准备MyBatis

手工编写数据库连接配置文件`mybatis-config.xml`

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	<settings>
		<setting name="cacheEnabled" value="false" />
	</settings>
	<environments default="development">
		<environment id="development">
			<transactionManager type="JDBC"></transactionManager>
			<dataSource type="POOLED">
				<property name="driver" value="com.mysql.jdbc.Driver" />
				<property name="url" value="jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT" />
				<property name="username" value="calvin" />
				<property name="password" value="calvin" />
			</dataSource>
		</environment>
	</environments>
	<mappers>
		<mapper resource="mybatis-mapper.xml" />
	</mappers>
</configuration>
```

手工编写mapper配置文件`mybatis-mapper.xml`

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAOMapper">
	<insert id="insertOne" parameterType="xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO">
		INSERT INTO sqlaction_benchmark (name,name_cn,salary,birthday) VALUES( #{name}, #{name_cn}, #{salary}, #{birthday} )
	</insert>
	<update id="updateOneByName" parameterType="xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO">
		UPDATE sqlaction_benchmark SET salary=#{salary} WHERE name=#{name}
	</update>
	<select id="selectOneByName" parameterType="java.lang.String" resultType="xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO" flushCache="true" useCache="false">
		SELECT * FROM sqlaction_benchmark WHERE name=#{name}
	</select>
	<select id="selectAll" resultType="xyz.calvinwilliams.mybatis.benchmark.SqlactionBenchmarkSAO" flushCache="true" useCache="false">
		SELECT * FROM sqlaction_benchmark
	</select>
	<delete id="deleteOneByName" parameterType="java.lang.String">
		DELETE FROM sqlaction_benchmark WHERE name=#{name}
	</delete>
	<delete id="deleteAll">
		DELETE FROM sqlaction_benchmark
	</delete>
</mapper>
```

手工编写数据库表实体类`SqlactionBenchmarkSAO.java`

```
package xyz.calvinwilliams.mybatis.benchmark;

import java.math.*;

public class SqlactionBenchmarkSAO {

	int				id ; // 编号
	String			name ; // 英文名
	String			name_cn ; // 中文名
	BigDecimal		salary ; // 薪水
	java.sql.Date	birthday ; // 生日

	int				count___ ; // defining for 'SELECT COUNT(*)'

}
```

手工编写数据库表Mapper接口类`SqlactionBenchmarkSAOMapper.java`

```
package xyz.calvinwilliams.mybatis.benchmark;

import java.util.*;

public interface SqlactionBenchmarkSAOMapper {
    public void insertOne(SqlactionBenchmarkSAO sqlactionBenchmark);
    public void updateOneByName(SqlactionBenchmarkSAO sqlactionBenchmark);
    public SqlactionBenchmarkSAO selectOneByName(String name);
    public List<SqlactionBenchmarkSAO> selectAll();
    public void deleteOneByName(String name);
    public void deleteAll();
}
```

手工编写性能测试应用类`MyBatisBenchmarkCrud.java`

```
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
			System.out.println( "All mybatis UPDATE done , count["+count+"] elapse["+elpaseSecond+"]s" );
			
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
			System.out.println( "All mybatis DELETE done , count["+count+"] elapse["+elpaseSecond+"]s" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
```

## 6.3. 测试案例

INSERT表500条记录（每10条提交一次）
UPDATE表500条记录（每10条提交一次）
SELECT表单条记录500*5次
SELECT表所有记录1000次
DELETE表500条记录（每10条提交一次）

## 6.4. 测试结果

```
All sqlaction INSERT done , count[500] elapse[4.742]s
All sqlaction UPDATE WHERE done , count[500] elapse[5.912]s
All sqlaction SELECT WHERE done , count2[5] count[500] elapse[0.985]s
All sqlaction SELECT to LIST done , count3[1000] elapse[1.172]s
All sqlaction DELETE WHERE done , count[500] elapse[5.001]s

All mybatis INSERT done , count[500] elapse[5.869]s
All mybatis UPDATE WHERE done , count[500] elapse[6.921]s
All mybatis SELECT WHERE done , count2[5] count[500] elapse[1.239]s
All mybatis SELECT to List done , count3[1000] elapse[1.792]s
All mybatis DELETE WHERE done , count[500] elapse[5.382]s

All sqlaction INSERT done , count[500] elapse[5.392]s
All sqlaction UPDATE WHERE done , count[500] elapse[5.821]s
All sqlaction SELECT WHERE done , count2[5] count[500] elapse[0.952]s
All sqlaction SELECT to LIST done , count3[1000] elapse[1.15]s
All sqlaction DELETE WHERE done , count[500] elapse[5.509]s

All mybatis INSERT done , count[500] elapse[6.066]s
All mybatis UPDATE WHERE done , count[500] elapse[6.946]s
All mybatis SELECT WHERE done , count2[5] count[500] elapse[1.183]s
All mybatis SELECT to List done , count3[1000] elapse[1.804]s
All mybatis DELETE WHERE done , count[500] elapse[5.958]s

All sqlaction INSERT done , count[500] elapse[5.236]s
All sqlaction UPDATE WHERE done , count[500] elapse[5.84]s
All sqlaction SELECT WHERE done , count2[5] count[500] elapse[0.985]s
All sqlaction SELECT to LIST done , count3[1000] elapse[1.222]s
All sqlaction DELETE WHERE done , count[500] elapse[4.91]s

All mybatis INSERT done , count[500] elapse[5.448]s
All mybatis UPDATE WHERE done , count[500] elapse[7.287]s
All mybatis SELECT WHERE done , count2[5] count[500] elapse[1.149]s
All mybatis SELECT to List done , count3[1000] elapse[1.873]s
All mybatis DELETE WHERE done , count[500] elapse[6.035]s
```

![benchmark_INSERT.png](benchmark_INSERT.png)

![benchmark_UPDATE_WHERE.png](benchmark_UPDATE_WHERE.png)

![benchmark_SELECT_WHERE.png](benchmark_SELECT_WHERE.png)

![benchmark_SELECT_to_LIST.png](benchmark_SELECT_to_LIST.png)

![benchmark_DELETE_WHERE.png](benchmark_DELETE_WHERE.png)

**从以上性能测试图表中可以看出，`sqlaction`运行性能比`MyBatis`快大约20%，这意味着技术选型`sqlaction`的应用系统的交易延迟比`MyBatis`有明显优势。**

**综合开发效能和运行效率，总结：**

* 从测试前准备来看，无论配置文件、源代码文件数量还是大小，`sqlaction`的工作量只有`MyBatis`一半，能更快速的展开业务开发，减轻开发人员学习压力和心智负担，且采用的技术更简单更透明更易掌控。
* 从设计理念来看，`sqlaction`只是在应用和`JDBC`之间自动生成了以往需要手工编写的代码，真正运行时与`sqlaction`无关，性能等价于高效直接的`JDBC`，理论上不可能有比它更快的数据库持久层类库/工具了。`sqlaction`只自动生成了SQL处理对应的JAVA方法操作，可以和其它优秀数据库类库/工具（如连接池、分布式事务控制）更容易的集成使用。

# 7. 后续开发

1. Eclipse插件触发执行工具sqlaction
1. 增加缓存
1. 进一步优化支持Oracle。

# 8. 关于本项目

欢迎使用`sqlaction`，如果你在使用中碰到了问题请告诉我，谢谢 ^_^

源码托管地址 : [开源中国](https://gitee.com/calvinwilliams/sqlaction)、[github](https://github.com/calvinwilliams/sqlaction)

Apache Maven
```
<dependency>
  <groupId>xyz.calvinwilliams</groupId>
  <artifactId>sqlaction</artifactId>
  <version>0.2.9.0</version>
</dependency>
```

# 9. 关于作者

厉华，右手C，左手JAVA，写过小到性能卓越方便快捷的日志库、HTTP解析器、日志采集器等，大到交易平台/中间件等，分布式系统实践者，容器技术专研者，目前在某城商行负责基础架构。

通过邮箱联系我 : [网易](mailto:calvinwilliams@163.com)、[Gmail](mailto:calvinwilliams.c@gmail.com)
