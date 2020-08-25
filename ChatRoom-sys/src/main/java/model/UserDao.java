package model;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;
import util.ChatroomException;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 通过 UserDao 这个类来完成针对 User 表的基本操作.
public class UserDao {
    // 1. 新增一个用户(注册功能)
    public void add(User user) {
        // 1. 获取数据库连接.
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL 语句
        String sql = "insert into user values(null, ?, ?, ?, now())";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getNickName());
            // 3. 执行 SQL 语句
            // 插入数据, 删除数据, 修改数据, 都叫 "executeUpdate"
            // 查找数据, 就是 "executeQuery"
            // 返回结果表示 "影响到的行数"
            int ret = statement.executeUpdate();
            if (ret != 1) {
                // 关于出现问题的处理方式, 有很多种.
                // 此处我们可以主动抛出一个自定义的异常.
                throw new ChatroomException("插入用户失败");
            }
            System.out.println("插入用户成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChatroomException("插入用户失败");
        } finally {
            // 4. 释放连接.
            DBUtil.close(connection, statement, null);
        }
    }

    // 2. 按用户名查找用户信息(登陆功能)
    public User selectByName(String name) {
        // 1. 获取到连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "select * from user where name = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            // 3. 执行 SQL
            resultSet = statement.executeQuery();
            // 4. 遍历结果集合. (执行查找操作, 必须要有这一步)
            //    由于查找结果预期最多只有一条记录, 直接使用 if 判定即可.
            //    如果查找结果有多条的话, 就需要使用 while 循环的方式来获取了.
            //    如果 resultSet.next 直接为 false, 说明该用户名不存在.
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setNickName(resultSet.getString("nickName"));
                user.setLastLogout(resultSet.getTimestamp("lastLogout"));
                return user;
            }
            // throw new ChatroomException("按用户名查找用户失败");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChatroomException("按用户名查找用户失败");
        } finally {
            // 5. 释放连接.
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    // 3. 按用户 id 查找用户信息(把 userId 转换成 昵称 的时候)
    public User selectById(int userId) {
        // 1. 获取连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "select * from user where userId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            // 3. 执行 SQL
            resultSet = statement.executeQuery();
            // 4. 遍历结果集
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setNickName(resultSet.getString("nickName"));
                user.setLastLogout(resultSet.getTimestamp("lastLogout"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChatroomException("按 id 查找用户失败");
        } finally {
            // 5. 释放连接.
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    // 4. 更新用户的 lastLogout 时间. (用户下线时更新, 为了实现历史记录功能)
    public void updateLogout(int userId) {
        // 哪个用户下线了, 就更新哪个.
        // 1. 获取连接.
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL
        String sql = "update user set lastLogout = now() where userId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            // 3. 执行 SQL
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new ChatroomException("更新退出时间失败");
            }
            System.out.println("更新退出时间成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ChatroomException("更新退出时间失败");
        } finally {
            // 4. 释放连接.
            DBUtil.close(connection, statement, null);
        }
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        // 针对上面的代码进行简单验证.
        // 1. 验证 add 方法
//        User user = new User();
//        user.setName("tz2");
//        user.setPassword("123");
//        user.setNickName("汤老湿2");
//        userDao.add(user);
        // 2. 按名字查找用户信息
//        User user = userDao.selectByName("tz");
//        System.out.println(user);
        // 3. 按用户 id 查找用户信息
//        User user = userDao.selectById(1);
//        System.out.println(user);
        // 4. 更新用户的退出时间
        userDao.updateLogout(1);
    }
}
