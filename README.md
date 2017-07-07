# Personal-Project
Personal-Project是用来存放个人项目的`repository`
### 文件夹介绍
* Database      存放数据库相关文件
* Doc           存放流程图、需求相关文档
* Project       存放项目源码
* Prototype map 存放原型图文件


## AccountSecretary
AccountSecretary存放着`记账小秘`项目的相关文件，其中包含`AccountSecretary-Server`（服务器）和`AccountSecretary`（客户端）的相关源码以及开发时的相关文档。
### 项目介绍
`记账小秘`项目是用来记录用户收入/支出的账目，用户可以通过客户端对账目的类别进行操作，同时也可对账目记录进行操作，用户可以通过筛选条件对账目记录进行分类/分时查看。服务器主要用于操作于数据库，记录客户端的相关操作。
### 相关技术
* `数据库`采用`MySQL`数据库，使用`Workbench`操作
* `服务器`采用`MVC`框架，使用`MyEclipse`开发，搭建在`Tomcat`上运行
* `客户端`采用`Android Native`模式，使用`volley`与服务器进行通信、使用`Json`数据格式进行传输


## MorraGame
MorraGame存放着`人机划拳`项目的相关文件，其中包含`SocketServer`（服务器）和`MorraGame`（客户端）的相关源码以及开发时的相关文档。
### 项目介绍
`人机划拳`项目是一个小型的单机游戏，由小时候玩的划拳游戏而来，再借鉴于消消乐的模式进行整合而出。游戏通过用户选择石头/剪刀/布，然后发送至服务器，服务器随机产生一个手型，让其与之比较得出游戏结果，并记录本次游戏的相关数据，最后客户端呈现出游戏图形化结果；用户也可通过游戏进行积分，同时通过可以查看游戏积分前十排名。
### 相关技术
* `数据库`采用`MySQL`数据库，使用`Workbench`操作
* `服务器`为普通`socket`服务器，使用`Eclipse`开发
* `客户端`采用`Android Native`模式，使用`socket`与服务器进行通信、使用`Json`数据格式进行传输


## NamecardManager
NamecardManager存放着`名片管家`项目的相关文件，其中包含`NamecardManager-Server`(服务器)和`NamecardManager`（客户端）的相关源码以及开发时的相关文档。
### 项目介绍
`名片管家`项目是用来记录用户名片信息的工具，同时也可以通过其对用户名下所有名片进行管理。项目的名片识别采用[汉王云](http://developer.hanvon.com/)的云名片服务器进行识别，名片图片的来源通过自定义相机和系统裁剪进行采集。用户通过注册账号然后登陆APP，再录入相关名片信息之后，通过模糊查询即可查到想要的名片信息。
### 相关技术
* `数据库`采用`MySQL`数据库，使用`Workbench`操作
* `服务器`采用`MVC`框架，使用`MyEclipse`开发，搭建在`Tomcat`上运行
* `客户端`采用`Android Native`模式，使用`volley`与服务器进行通信、使用`Json`数据格式进行传输
	* 使用`pinyin4j`将汉字转换成拼音
	* 使用`自定义相机`和`系统裁剪`对名片图片进行采集
	* 使用[汉王云名片识别](http://developer.hanvon.com/card/toCard.do)将名片图片进行识别
