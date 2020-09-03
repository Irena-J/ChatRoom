package model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 功能: 帮助我们管理连接. DBUtil 本质上是实现了 DataSource 类的单例版本.
// 对于一个应用程序来说, DataSource 只需要有一个实例就可以了.
// 单例是面试中最常考的设计模式 (没有之一)
// 饿汉模式比较简单, 但是懒汉模式更常考一些.
// 注意线程安全问题.
// 1. 合适的位置加锁
// 2. 双重 if 判定
// 3. volatile
public class DBUtil {
//    private static final String URL = "jdbc:mysql://127.0.0.1:3306/java13_chatroom?character=utf-8&useSSL=true";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/java13_chatroom?characterEncoding=utf-8&useSSL=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "2222";
//    private static final String PASSWORD = "";

    private static volatile DataSource dataSource = null;

    public static DataSource getDataSource() {
        // 获取到这个单例的 DataSource 实例.
        if (dataSource == null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    // 必须要告诉代码, 数据库是谁, 以啥样的方式登陆上去.
                    ((MysqlDataSource)dataSource).setURL(URL);
                    ((MysqlDataSource)dataSource).setUser(USERNAME);
                    ((MysqlDataSource)dataSource).setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection () {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection, PreparedStatement statement,
                             ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
