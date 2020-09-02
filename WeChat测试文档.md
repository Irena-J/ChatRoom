# WeChat 项目测试

## 分析需求

整个项目实现了用户的登录和注册；频道的添加、查看和删除；群聊功能。

在后端开发时已经完成了单元测试和接口测试详情可见项目设计文档，现阶段对项目的测试要实现：

基本功能需求：

+ 实现用户注册和用户登录，登录后可以看到频道列表
+ 用户点击频道列表进入消息发送界面
+ 在消息发送界面可以发送消息也可以看到其他人的消息
+ 历史消息，用户可以看到上次退出到这次登录之间的所有消息

界面需求：布局、排版美观

兼容性需求：操作系统：win10、win7；浏览器：chrome、火狐、edge

## 制定测试方案

测试的范围和测试点：

+ 界面测试需要按测试用例完成

+ 由于是网页版的，只需要测试浏览器端的所有功能模块
+ 本机是 win10，没有安装虚拟机，暂时只能测到 win10；浏览器这里主要完成 火狐和 edge
+ 另外由于不可能做到穷尽测试且本系统的受用群体只有室友，所以对于性能测试只是查看不做详细的分析和调优

测试方法：手工、自动化

测试所涉及的类型：

+ 界面测试：手工

+ 功能测试：根据需求分析先设计测试用例，并用自动化完成测试

+ 易用性测试：手工

+ 性能测试：依照性能测试用例，可以借助性能测试工具完成测试
+ 兼容性：依照兼容性测试用例，并用自动化完成测试
+ 容错性：手工

+ 安全测试：依照安全测试用例完成

测试管理工具：可以使用禅道，这里先简单记录



## 测试执行

### 测试用例：

![yuque_diagram](D:/typora/appdata/yuque_diagram.jpg)



### 自动化脚本：

火狐浏览器下基本的登录、注册和群聊功能测试

```
from selenium import webdriver
import time

from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.remote.webelement import WebElement

driver = webdriver.Firefox();
driver.get("http://localhost:8080/chatroom");
time.sleep(2);
driver.find_element_by_xpath("/html/body/div/nav/div[1]/div[1]/div[2]/div[2]/div").click();
driver.find_element_by_id("input-33").send_keys("the shy");
time.sleep(2);
driver.find_element_by_id("input-36").send_keys("the shy");
time.sleep(3);
driver.find_element_by_xpath("/html/body/div/div/div[3]/div/div/div[2]/div/div[3]/div[1]/button/span").click();
time.sleep(2);
driver.find_element_by_xpath("/html/body/div/div/div[1]/main/div/div[1]/div/div[2]/div[4]/div[2]/div").click();
driver.find_element_by_id("input-23").send_keys("你好，辅助宝蓝");
driver.find_element_by_xpath("/html/body/div/div/div[1]/main/div/div[2]/footer/div/div/div[2]/button/span").click();

# 退出登录
driver.find_element_by_xpath("/html/body/div/nav/div[1]/div[2]/div[2]/div[2]/div").click();

# 重新登录查看历史消息
driver.find_element_by_xpath("/html/body/div/nav/div[1]/div[1]/div[2]/div[2]/div").click();
#driver.find_element_by_xpath("//*[@id='input-33']").clear();

# 全选输入框内容
driver.find_element_by_id("input-33").send_keys(Keys.CONTROL,'a');
time.sleep(2);
# 剪切内容
driver.find_element_by_id("input-36").send_keys(Keys.CONTROL,'x');
time.sleep(2);
#driver.find_element_by_xpath("//*[@id='input-36']").clear();

# 重新输入登录
driver.find_element_by_id("input-33").send_keys("bao lan");
time.sleep(2);
# 全选原密码
driver.find_element_by_id("input-36").send_keys(Keys.CONTROL,'a');
time.sleep(2);
# 剪切原密码
driver.find_element_by_id("input-36").send_keys(Keys.CONTROL,'x');
time.sleep(2);
# 输入新密码
driver.find_element_by_id("input-36").send_keys("bao lan");
driver.find_element_by_xpath("/html/body/div/div/div[3]/div/div/div[2]/div/div[3]/div[1]/button/span").click();
time.sleep(2);
# 登录后查看消息并发送
#driver.find_element_by_xpath("/html/body/div/div/div[1]/main/div/div[1]/div/div[2]/div[4]/div[2]/div").click();
driver.find_element_by_xpath("//*[@id='input-23']").send_keys("你好，第一上单");
driver.find_element_by_xpath("/html/body/div/div/div[1]/main/div/div[2]/footer/div/div/div[2]/button").click();
driver.sleep(2);
driver.quit();
```

+ 界面界面友好简单，按钮正常跳转，文字没有乱码现象，页面可以显示频道数目
+ 如果重复注册会提示 ”用户名已存在“
+ 用户名或密码凡又一个不对都无法登录并提示 ”用户名或密码错误“
+ 已经登录用户可以再次在同一浏览器或不同浏览器登录
+ 当一个用户登录在频道发消息，另外一个用户在频道可以看到并可以继续发送，另外退出后重新登录后依然可以看到历史消息
+ 易用性方面是小窗口情况下无法找到登录和注册按钮

+ win10 下在 Chrome 浏览器和 Edge 浏览器下都是可以正常使用的，在这三个浏览器下不存在兼容性问题
+ 密码是明文显示不够安全
+ 在宿舍四人环境下，由于只支持文本消息的发送，没有出现卡顿或消息不同步情况

## 输出测试报告

### 缺陷记录

缺陷一：

缺陷标题：同一账户在同一时间出现在同一、不同浏览器重复登录
缺陷概述：
		同一浏览器下打开两个进程可以同时登录，并且其中一个登录的可以发送消息也能接收到推送消息；另一个可以发送消息但是不能看到自己的发送内容并且无法显示到推送消息

​		在不同浏览器，这里是 Chrome 和 Firefox 情况同上

缺陷影响：严重

环境配置：

​		操作系统：win10 教育版，基于 x64 的处理器

​		浏览器：Chrome 73.0.3683.86（正式版本） （64 位）、Firefox

缺陷重现：

​		在 Chrome 打开一个标签页登录后选择一个频道发送消息 ，在该下面再开一个进程后输入相同的 URL 就已经是登录状态分别在两个状态下发送消息其中后登录的可以发送并接收消息，先登录的只能发送。

​		一个 Chrome 浏览器另一个 Firefox 浏览器也执行同样的操作也出现同样的结果

期望结果：无论在相同或不同浏览器下均不可出现重复登录的情况

附件：

![image-20200902203215220](D:/typora/appdata/image-20200902203215220.png)

实际结果：不能出现重复登录	

根原因分析：可能是服务器发送来的 Cookie 没有设置好路径导致出现了重复的 set-Cookie；



缺陷二：小窗情况下找不到登录和注册按钮，易用性不好

### 测试结论

测试用例是没有穷尽的，本次测试用例只是针对需求完成了部分测试。采用手工测试完成了界面、易用性测试，并用自动化脚本完成了基本功能测试。当然一次测试并不能完全保证项目准确无误还需要探究更多的可能性，另外本次只是在本机上做了测试，没有在其他版本环境下测试是不具备足够说服力的，还需要改善。



