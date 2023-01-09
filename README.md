# 画图软件
# java大作业画图软件，模仿微软Paint、PowerPoint（含报告、只因你太美动画）
##### 含报告、附送java文档、只因你太美动画（donghua49.txt）
![](https://files.mdnice.com/user/35615/a6128ae7-ab2d-4d3f-b587-8e731ec04d3e.png)
##### 开源不易，有用的话点个star

## 实现功能
##### 一、基本功能：
1.	新建一个空白的文件；
2.	打开或保存整个文件；
3.	绘制基本图形（直线、矩形、圆、椭圆）；
4.	绘制任意直线、曲线（铅笔工具）、刷子；
5.	添加文字；
6.	设定颜色与文字风格（种类数≥3）；
7.	对页面上已有的基本图形、线形、文字进行选取和移动；
8.	通过鼠标拖动完成上述绘制和添加文字等操作；
9.	实现对基本图形命名，支持按照名称搜索特定图形，并定位;
10.	支持按照图形类别浏览图形名称列表的功能。
##### 二.、选做功能：
1.	添加多页图形内容；
2.	实现幻灯片的全屏播放、翻页；
3.	设定画笔（粗细）、插入图像、修改图像（使用橡皮擦）；
4.	图形填充、线型设置；
5.	对页面上已有的基本图形、线形、文字进行框选、缩放、移动；
6.	操作的撤销（无重做）；
7.	橡皮擦 – 整个图形删除；
8.	菜单；
9.	按照名称进行模糊搜索；
##### 三、总结：
（1）在画布上绘制直线、矩形、椭圆等图形
（2）绘制填充图形、对已有图形进行填充
（3）绘制任意曲线
（4）设置画笔的颜色和粗细
（5）橡皮擦擦除任意曲线和基本图形
（6）选取、移动、删除基本图形
（7）添加字体
（8）设定文字的颜色与风格
（9）通过鼠标拖动完成上述绘制和添加文字等操作
（10）支持按照名称搜索特定图形并定位
（11）实现幻灯片的全屏播放、翻页
（12）插入并修改图像
（13）对选中图形进行框选
（14）操作的撤销
（15）菜单
（16）按照名称进行模糊搜索
（18）实现了序列化存储，生成了可编辑工程文件。
（19）对选中的基本图形、文字、图像进行框选和放缩
（20）添加多个可编辑的页面

## 总体设计
##### 2.1总设计
1）从前端布局上看，分为如下五大类：
1、菜单（MyMenu）、
2、工具类（MyToolBar）、
3、调色板（ColorPanel）、
4、画布（DrawPanel）
5、多页栏（MultiPageManager多页管理器、Page单页、PageMouseAdapter页面按钮监听）

2）整个窗体的父类是MyFrame，以上各类的实例化对象添加在myframe对象中。
3）图形类包含Rectangle（直角矩形）、RoundRect（圆角矩形）、Brush（刷子）等等......所有细化图形类均为抽象类父类AbstractShape的子类。

##### 2.2使用流程图
![](https://files.mdnice.com/user/35615/7d0ca42e-6d9a-47a6-9d2e-430edcba9d77.png)

## 软件展示
#### 主页面
![](https://files.mdnice.com/user/35615/f9173d3f-57c0-4264-a92f-0109ee04e4ba.png)

![](https://files.mdnice.com/user/35615/805b9524-2847-40fd-a2a2-fa48331bd12c.png)

#### 动画截图（只因你太美）
![](https://files.mdnice.com/user/35615/4ad2739a-0141-4989-b9ee-b92be70be474.png)


开发软件IDEA
JDK版本：17

程序非从零开发，基于Github开源程序二次开发
源程序CSDN文章链接: [https://blog.csdn.net/WEDUEST/article/details/81038362](https://blog.csdn.net/WEDUEST/article/details/81038362)

源程序Github链接链接: [https://blog.csdn.net/WEDUEST/article/details/81038362](https://blog.csdn.net/WEDUEST/article/details/81038362)

## 如何加载只因你太美动画
点击文件->打开->
![](https://files.mdnice.com/user/35615/f7651f02-dd50-4af4-a594-244bb2bf92d6.png)


找到donghua49.txt文件
![](https://files.mdnice.com/user/35615/24ad57c8-2f01-4eeb-8316-7d4e1930aa74.png)

文件类型选txt，打开（txt格式一定要选，不然打不开）
![](https://files.mdnice.com/user/35615/0f5df4c9-7263-426f-a361-dedb851e1b3b.png)

导入后，左侧播放速度填0.06，输入完一定要按回车，然后点击播放按钮即可，没有音乐
![](https://files.mdnice.com/user/35615/35babde2-31db-4a9f-aca1-a5a60ab1cb1d.png)

