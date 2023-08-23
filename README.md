# Distributed-Shared-White-Board
基于Java的分布式共享白板软件

* 技术：Java Socket, RMI, GUI, JSON编程；多线程并发控制；加密信息传输(AES对称加密)
* 功能：可实现多个用户在白板画布上同时作画，拖动鼠标绘制基本图形(线条，圆形，矩形，字符)，调节颜色、粗细、字体等，实现聊天室功能，可查看当前用户列表；管理员可以允许/拒绝用户加入申请，可以踢出特定用户，可操控文件菜单下的新建/保存/另存为/退出白板。


CreateWhiteBoard.jar和JoinWhiteBoard.jar作为打包导出的可执行jar包，可以直接启动白板程序。

启动方式如下：

> '''java CreateWhiteBoard \<serverIPAddress\> \<serverPort\> username'''

> '''java JoinWhiteBoard \<serverIPAddress\> \<serverPort\> username'''

例如：
'''
java CreateWhiteBoard localhost 8123 jiamingXIAN_Server
'''

> '''java JoinWhiteBoard localhost 8123 jiamingXIAN_Client'''
