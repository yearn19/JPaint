package jpaint.start;

import jpaint.shape.*;
import jpaint.shape.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class MyFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    /**
     * 文件是否已保存标志位
     */
    static boolean saved = false;
    /**
     * 铅笔或橡皮擦图形的存储长度
     */
    static int lengthCount;
    /**
     * 当前选择的字体名
     */
    static String fontName = " 宋体 ";
    /**
     * 当前字号
     */
    static int fSize = 16;
    /**
     * 是否粗体，默认无粗体
     */
    static int boldFlag = Font.PLAIN;
    /**
     * 是否斜体，默认无斜体
     */
    static int italicFlag = Font.PLAIN;
    /**
     * 是否填充，默认无填充
     */
    static boolean fillFlag = false;
    /**
     * 填充颜色
     */
    static Color fillColor = Color.WHITE;
    /**
     * 多页管理器
     */
    public static MultiPageManager multiPageManager;
    /**
     * abstractShapeList的索引，指向栈顶的下一元素
     */
    public static Integer index = 0;
    /**
     * 图形存储单元  //+++栈
     */
    public static ArrayList<AbstractShape> abstractShapeList = new ArrayList<AbstractShape>();
    /**
     * +++选中图形，不是数组也可吧，以后如果多选倒是可以扩展
     */
    public static ArrayList<AbstractShape> select = new ArrayList<AbstractShape>();
    //public boolean ifSelect=false;
    /**
     * 储存要删除的对象，与select略有区别，如果鼠标还没释放，就拖动，可能有问题
     */
    public static ArrayList<AbstractShape> preSelect = new ArrayList<AbstractShape>(); //+++List确实比较好用，避免了clone
    /**
     * 画图区域 (modified to static)
     */
    static DrawPanel currentDrawPanel;
    /**
     * 鼠标状态 (modified to static)
     */
    private static JLabel statusBar;
    /**
     * 画笔粗细
     */
    static int stroke = 1;
    /**
     * 笔触颜色，默认为Color.BLACK
     */
    public static Color penColor = Color.BLACK;
    /**
     * 当前画笔（要绘制的图形类型），默认为铅笔
     */
    static int currentChoice = 3;
    /**
     * 菜单类 (modified to static)
     */
    static MyMenu myMenu;
    /**
     * 工具条 (modified to static)
     */
    private static MyToolBar myToolBar;
    /**
     * 调色板 (modified to static)
     */
    static ColorPanel colorPanel;

    MyFrame(String s) {
        init(s);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //this.setVisible(true); //调到init()中执行
    }

    public MyFrame() {}

    private void init(String s) {
        this.setTitle(s); //设置标题
        this.setSize(1200, 800); //设置窗口大小
        this.setLocationRelativeTo(null); //居中显示
        try { //设置窗体图标
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/themeicon.png"));
            Image image = imageIcon.getImage();
            this.setIconImage(image);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "窗体图标异常");
        }

        //初始化颜色面板
        colorPanel = new ColorPanel(this);
        colorPanel.addColorPanel();
        //初始化顶部菜单栏
        myMenu = new MyMenu(this);
        myMenu.addMenu(); //创建一个JMenuBar对象，配置好其中的按钮并设置为这个MyFrame的JMenuBar
        //初始化按钮工具条
        myToolBar = new MyToolBar(this);
        myToolBar.addToolBar(); //创建一个JToolBar对象，配置好其中的按钮并添加到这个MyFrame中

        //初始化绘图区域
        currentDrawPanel = new DrawPanel();
        this.add(currentDrawPanel, BorderLayout.CENTER);

        //初始化底部
        statusBar = new JLabel();
        this.add(statusBar, BorderLayout.SOUTH);
        statusBar.setText("坐标");
        //由于JLabel是透明的，当把JLabel控件添加到JPanel控件之上时，JLabel的背景色总是会和JPanel的背景色保持一致
        statusBar.setOpaque(true); //设置statusBar为透明
        statusBar.setBackground(new Color(195, 195, 195));

        //初始化多页管理器
        multiPageManager = new MultiPageManager(this);
        this.add(multiPageManager, BorderLayout.WEST);

        //MyFrame.createNewShape(); //就绪后进入createNewShape
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!saved) {
                    int n = JOptionPane.showConfirmDialog(null, "您还没保存，确定要退出？", "提示", JOptionPane.OK_CANCEL_OPTION);
                    if (n == 0) {
                        System.exit(0);
                    }
                } else { //saved
                    System.exit(0);
                }
            }
        });

        this.setVisible(true);

        currentDrawPanel.addKeyListener(new MyKeyAdapter());
        currentDrawPanel.requestFocus();
    }
    public void myframeVisible(MultiPageManager obj){
        //+++现在发现这样写是真好用，混乱会有一点，但这也是因为现有结构臃肿难以改变的下策
        System.out.println("这里是myframeVisible函数");
        System.out.println("多页个数"+obj.multiPageList.size());
        for(int i=0 ;i< obj.multiPageList.size(); i++) {
            System.out.println("index" + obj.multiPageList.get(i).index);
            System.out.println("第" + i + "页图形个数" + obj.multiPageList.get(i).abstractShapeList.size());
            if(obj.multiPageList.get(i).abstractShapeList.size() >0) System.out.println("第" + i + "页第0个图形x1坐标" + obj.multiPageList.get(i).abstractShapeList.get(0).x1);
        }
        this.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);//让之前选择的页不再显示红色框？？？

        this.multiPageManager.multiPageList = obj.multiPageList;//(ArrayList<Page>) (obj.multiPageList).clone();
        for (Page p : multiPageManager.multiPageList) {
            p.addMouseListener(new PageMouseAdapter(p));
            p.setBorder(MyToolBar.alternateToolBorder); //全设白
        }

        this.multiPageManager.playingFlag = false;
        this.multiPageManager.playSpeed = 1000;

        //this.index = this.multiPageManager.multiPageList.get(0).index;

        this.multiPageManager.currentPage = this.multiPageManager.multiPageList.get(0);//new Page(parent.index, parent.abstractShapeList, parent.select, parent.preSelect);
        this.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);

        this.multiPageManager.currentPage.showPage(this);//得赋值


        this.multiPageManager.resetMultiPagePanel();
        //this.multiPageManager.init(this);
        //this.multiPageManager = new MultiPageManager(this);
        //this.add(this.multiPageManager, BorderLayout.WEST);
        //this.setVisible(true);


        //multiPageList.add(currentPage);
    }

    /**
     * 撤销操作的实现
     */
    static void undo() {
        System.out.println("撤销前");
        System.out.print("index");
        System.out.println(index);
        System.out.print("itemList");
        System.out.println(abstractShapeList.size());
        //+++撤销却不删除响应对象，就是一个大大的隐患
        if (index > 0) {
            //if (currentChoice == 3 || currentChoice == 16 || currentChoice == 17) {
            if (abstractShapeList.get(index - 1).length != 0) { //+++判断这个显然不合适，应该判断取出来的类型，或者取出来的东西length不为
                int len = abstractShapeList.get(index - 1).length;
                for (int i = index - 1; i >= index - len; i--) {
                    abstractShapeList.remove(i);
                }
                index -= len;
            } else {
                abstractShapeList.remove(index - 1);
                index--;
            }
            currentDrawPanel.repaint();
        }
        System.out.println("撤销后");
        System.out.print("index");
        System.out.println(index);
        System.out.print("itemList");
        System.out.println(abstractShapeList.size());
        //currentChoice = 1; //将操作定为选择
    }

    /**
     * 新建一个画图基本单元对象（AbstractShape）的程序段
     */
    static void createNewShape() {
        System.out.print("当前选择");
        System.out.println(currentChoice);
        /*
         * MOVE_CURSOR: 移动光标 CROSSHAIR_CURSOR: 十字光标
         * CUSTOM_CURSOR: 定制光标 WAIT_CURSOR: 等待光标
         */
        //定义鼠标进入DrawPanel时的光标样式
        if (currentChoice == 16) { //若为橡皮擦，光标设置为方块形状（rubber.png）
            try {
                String url = "/image/rubber.png"; //储存鼠标图片的位置
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image image = new ImageIcon(MyFrame.class.getResource(url)).getImage();
                Cursor cursor = tk.createCustomCursor(image, new Point(10, 10), "norm");
                currentDrawPanel.setCursor(cursor);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "自定义光标异常");
            }
        } else if (currentChoice == 18) {
            currentDrawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            currentDrawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        //根据当前的画笔类型，创建相应类型的图形
        switch (currentChoice) {
            case 0:
                abstractShapeList.add(new Images());
                break;
            //case 1:break;
            case 3:
                abstractShapeList.add(new Pencil());
                break;
            case 4:
                abstractShapeList.add(new Line());
                break;
            case 5:
                abstractShapeList.add(new Rectangle());
                break;
            case 6:
                abstractShapeList.add(new FillRect());
                break;
            case 7:
                abstractShapeList.add(new Oval());
                break;
            case 8:
                abstractShapeList.add(new FillOval());
                break;
            case 9:
                abstractShapeList.add(new Circle());
                break;
            case 10:
                abstractShapeList.add(new FillCircle());
                break;
            case 11:
                abstractShapeList.add(new RoundRect());
                break;
            case 12:
                abstractShapeList.add(new FillRoundRect());
                break;
            case 13:
                abstractShapeList.add(new Triangle());
                break;
            case 14:
                abstractShapeList.add(new Pentagon());
                break;
            case 15:
                abstractShapeList.add(new Hexagon());
                break;
            case 16:
                abstractShapeList.add(new Rubber());
                break;
            case 17:
                abstractShapeList.add(new Brush());
                break;
            case 18:
                abstractShapeList.add(new Text());
                String input;
                input = JOptionPane.showInputDialog("请输入文字");
                //                if (input == null) {//当啥都没输入时，这个对象不要       修改
//                    abstractShapeList.remove(--index);修改
//                } else {修改
                    abstractShapeList.get(index - 1).s = input;
                    abstractShapeList.get(index - 1).fontSize = fSize;
                    abstractShapeList.get(index - 1).fontName = fontName;
                    abstractShapeList.get(index - 1).italicFlag = italicFlag;
                    abstractShapeList.get(index - 1).boldFlag = boldFlag;
//                }修改
                break;
            default: //19选择图形, 20填充图形 - 在鼠标监听器中实现
        }
        System.out.printf("当前的对象:%d", abstractShapeList.size());
        //if(index>0){ //当选择的按钮不是移动按钮时，说明   //这个应该放在创建对象的时候
        abstractShapeList.get(index - 1).penColor = penColor;
        abstractShapeList.get(index - 1).fillFlag = fillFlag;
        abstractShapeList.get(index - 1).fillColor = fillColor;
        abstractShapeList.get(index - 1).width = stroke;
        // this.repaint();
        // }
    }

    /**
     * +++暴力获取焦点
     */
    public static void foc() {
        currentDrawPanel.requestFocus();
    }

    /**
     * +++图形填充
     */
    static void shapeFill(int x, int y) {
        for (int i = index - 1; i >= 0; i--) {
            //if(itemList.get(i) instanceof Rectangle || itemList.get(i) instanceof RoundRect || itemList.get(i) instanceof Circle
            // || itemList.get(i) instanceof Oval || itemList.get(i) instanceof Triangle){//+++是空心矩形、空心圆角矩形，空心圆，空心椭圆、三角形才可以填充
            //if(itemList[i].getClass().equals(Rectangle.class)){
            //+++已经没必要判断了，因为不能填充的你改了fill参数也没什么影响
            System.out.println("ok1");
            if ((abstractShapeList.get(i)).contains(x, y)) {
                abstractShapeList.get(i).fillFlag = true;
                System.out.println(penColor);
                abstractShapeList.get(i).fillColor = penColor; //将图形的填充颜色改为填充工具的笔触颜色
                System.out.println("ok2");
                break;
            }
            //}
        }
    }
    /**
     * +++橡皮檫删除覆盖到的图形
     */
    static void rubber_delete(int x, int y) {
        System.out.println("rubber_delete函数执行了");
        //if(currentDrawPanel.g2d !=null) { // 应该不用担心空问题
        //java.awt.Rectangle temp = new java.awt.Rectangle(x - 10, y - 10, 20, 20);
        for (int i = index - 1; i >= 0; i--) {
            //if(itemList.get(i) instanceof Rectangle || itemList.get(i) instanceof RoundRect || itemList.get(i) instanceof Circle
            // || itemList.get(i) instanceof Oval || itemList.get(i) instanceof Triangle){//+++是空心矩形、空心圆角矩形，空心圆，空心椭圆、三角形才可以填充
            //if(itemList[i].getClass().equals(Rectangle.class)){
            //+++已经没必要判断了，因为不能填充的你改了fill参数也没什么影响
            System.out.println("橡皮檫删除ing");

            if(abstractShapeList.get(i).length != 0){ //毛笔、刷子、橡皮檫就别判断了
                i -=abstractShapeList.get(i).length;
                i++;//因为此次循环结束i还会-1
            }
//                if (abstractShapeList.get(i) instanceof Rectangle || abstractShapeList.get(i) instanceof RoundRect || abstractShapeList.get(i) instanceof Circle
//                        || abstractShapeList.get(i) instanceof Oval || abstractShapeList.get(i) instanceof Triangle) {//+++是空心矩形、空心圆角矩形，空心圆，空心椭圆、三角形才可以填充
            //else if (abstractShapeList.get(i) instanceof Rectangle){
            else {
                //if (currentDrawPanel.g2d.hit(temp, ((Rectangle)abstractShapeList.get(i)).shapeObj(), false)) {
                if((abstractShapeList.get(i)).intersects(x - 10, y - 10, 20, 20)){//代价太大
                    abstractShapeList.remove(i);
                    System.out.println("橡皮檫删除了一个形状");
                    index--;
                    currentDrawPanel.repaint();
                }
            }
        }
        //}
    }
    /**
     * +++键盘Delete监听器
     */
    class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("currentDrawPanel键盘按压");
            if (e.getKeyCode() == KeyEvent.VK_DELETE && preSelect.size() != 0) {
                System.out.println("currentDrawPanel删除键盘按压");
//            System.out.println(select.size());
//            System.out.println(shapeList.size());
                for (int i = 0; i < abstractShapeList.size(); i++) {
                    if (preSelect.get(0).hashCode() == abstractShapeList.get(i).hashCode()) {
                        abstractShapeList.remove(i);
                        break;
                    }
                }
                index--;
                preSelect.clear();
                //currentDrawPanel.repaint();
            }//修改
            else if (e.getKeyCode() == KeyEvent.VK_0 && preSelect.size() != 0){
                System.out.println("放大");
                for (int i = 0; i < abstractShapeList.size(); i++) {
                    if (preSelect.get(0).hashCode() == abstractShapeList.get(i).hashCode()) {
                        int temp = (int)Math.ceil(MyToolBar.zoomCoef);//+++实例是myToolBar
                        if(temp>10)
                        {
                            temp=10;
                        }
                        System.out.println(temp);
                        abstractShapeList.get(i).changeSize(temp,1);
                        break;
                    }
                }
            }//修改
            else if (e.getKeyCode() == KeyEvent.VK_9 && preSelect.size() != 0){
                System.out.println("缩小");
                for (int i = 0; i < abstractShapeList.size(); i++) {
                    if (preSelect.get(0).hashCode() == abstractShapeList.get(i).hashCode()) {
                        int temp = (int)Math.ceil(MyToolBar.zoomCoef);//+++实例是myToolBar
                        if(temp>10)
                        {
                            temp=10;
                        }
                        //temp *= 10;
                        System.out.println(temp);
                        abstractShapeList.get(i).changeSize(temp,0);
                        break;
                    }
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_S ) {
                //+++得先用选择按钮点击一下画板获取焦点
                System.out.println("S键");
                //得有这个，不然保存不全，index小于图形列表一个，造成显示、按钮出问题
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                //序列化多页
                //File file = new File("file"+File.separator+"out.txt");
                File file = new File("out.txt");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(fos);
                        //User user = new User("tom", 22);
                        System.out.println(multiPageManager);
                        oos.writeObject(multiPageManager);			//写入对象
                        oos.flush();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }finally{
                        try {
                            oos.close();
                        } catch (IOException ee) {
                            System.out.println("oos关闭失败："+ee.getMessage());
                        }
                    }
                } catch (FileNotFoundException ee) {
                    System.out.println("找不到文件："+ee.getMessage());
                } finally{
                    try {
                        fos.close();
                    } catch (IOException ee) {
                        System.out.println("fos关闭失败："+ee.getMessage());
                    }
                }



            }
            else if (e.getKeyCode() == KeyEvent.VK_F ){
                System.out.println("F键");
                //反序列化，恢复多页
                MultiPageManager obj = null;
                File file = new File("out.txt");
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(fis);
                        try {
                            obj = (MultiPageManager)ois.readObject();	//读出对象
                            System.out.println(obj);
                        } catch (ClassNotFoundException ee) {
                            ee.printStackTrace();
                        }
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }finally{
                        try {
                            ois.close();
                        } catch (IOException ee) {
                            System.out.println("ois关闭失败："+ee.getMessage());
                        }
                    }
                } catch (FileNotFoundException ee) {
                    System.out.println("找不到文件："+ee.getMessage());
                } finally{
                    try {
                        fis.close();
                    } catch (IOException ee) {
                        System.out.println("fis关闭失败："+ee.getMessage());
                    }
                }

                //接下来就是如何把恢复的对象显示出来


                if(obj != null){
                    //先给抽象类borad赋值
                    System.out.println("多页个数"+obj.multiPageList.size());
                    for(int i=0 ;i< obj.multiPageList.size(); i++){
                        System.out.println("第"+i+"页图形个数"+obj.multiPageList.get(i).abstractShapeList.size());
                        if(obj.multiPageList.get(i).abstractShapeList.size() >0) {System.out.println("第"+i+"页第0个图形x1坐标"+obj.multiPageList.get(i).abstractShapeList.get(0).x1);}
                        for(int j=0; j<obj.multiPageList.get(i).abstractShapeList.size(); j++){
                            if(obj.multiPageList.get(i).abstractShapeList.get(j)  instanceof Images){
                                obj.multiPageList.get(i).abstractShapeList.get(j).board = MyFrame.currentDrawPanel;
                            }
                        }
                    }

                    //MultiPageManager temp = new MultiPageManager();





                    //显示？
                    myframeVisible(obj);//装在这个多页
                    //currentDrawPanel.paint(colorPanel.getGraphics());
                    //currentDrawPanel.repaint();
                }

            }
            currentDrawPanel.repaint();
        }
    }

    public void Project_open(File file){
        System.out.println("工程打开");
        //反序列化，恢复多页
        MultiPageManager obj = null;
        //File file = new File("out.txt");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(fis);
                try {
                    obj = (MultiPageManager)ois.readObject();	//读出对象
                    System.out.println(obj);
                } catch (ClassNotFoundException ee) {
                    ee.printStackTrace();
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }finally{
                try {
                    ois.close();
                } catch (IOException ee) {
                    System.out.println("ois关闭失败："+ee.getMessage());
                }
            }
        } catch (FileNotFoundException ee) {
            System.out.println("找不到文件："+ee.getMessage());
        } finally{
            try {
                fis.close();
            } catch (IOException ee) {
                System.out.println("fis关闭失败："+ee.getMessage());
            }
        }

        //接下来就是如何把恢复的对象显示出来


        if(obj != null){
            //先给抽象类borad赋值
            System.out.println("多页个数"+obj.multiPageList.size());
            for(int i=0 ;i< obj.multiPageList.size(); i++){
                System.out.println("第"+i+"页图形个数"+obj.multiPageList.get(i).abstractShapeList.size());
                if(obj.multiPageList.get(i).abstractShapeList.size() >0) {System.out.println("第"+i+"页第0个图形x1坐标"+obj.multiPageList.get(i).abstractShapeList.get(0).x1);}
                for(int j=0; j<obj.multiPageList.get(i).abstractShapeList.size(); j++){
                    if(obj.multiPageList.get(i).abstractShapeList.get(j)  instanceof Images){
                        obj.multiPageList.get(i).abstractShapeList.get(j).board = MyFrame.currentDrawPanel;
                    }
                }
            }

            //MultiPageManager temp = new MultiPageManager();





            //显示？
            myframeVisible(obj);//装在这个多页
            //currentDrawPanel.paint(colorPanel.getGraphics());
            //currentDrawPanel.repaint();
        }
    }

    //工程保存
    void Project_save(File file) {
        System.out.println("工程保存");
        //得有这个，不然保存不全，index小于图形列表一个，造成显示、按钮出问题
        MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
        //序列化多页
        //File file = new File("file"+File.separator+"out.txt");
        //File file = new File("out.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(fos);
                //User user = new User("tom", 22);
                System.out.println(multiPageManager);
                oos.writeObject(multiPageManager);			//写入对象
                oos.flush();
            } catch (IOException ee) {
                ee.printStackTrace();
            }finally{
                try {
                    oos.close();
                } catch (IOException ee) {
                    System.out.println("oos关闭失败："+ee.getMessage());
                }
            }
        } catch (FileNotFoundException ee) {
            System.out.println("找不到文件："+ee.getMessage());
        } finally{
            try {
                fos.close();
            } catch (IOException ee) {
                System.out.println("fos关闭失败："+ee.getMessage());
            }
        }
    }

    /**
     * 鼠标按下或松开事件监听器，继承于MouseAdapter
     */
    static class MyMouseAdapter extends MouseAdapter {
        public static int position_x; //用于记录选中目标时按压的点的横坐标//修改
        public static int position_y;//用于记录选中目标时按压的点的纵坐标
        @Override
        public void mousePressed(MouseEvent e) {
            //恢复选中的画笔边框
            if (!myToolBar.toolButtonList[currentChoice].getBorder().equals(MyToolBar.chosenToolBorder)) {
                for (int j = 0; j < myToolBar.toolButtonList.length; j++) {
                    if (j == currentChoice) {
                        myToolBar.toolButtonList[j].setBorder(MyToolBar.chosenToolBorder);
                    } else if (myToolBar.toolButtonList[j] != null) {
                        myToolBar.toolButtonList[j].setBorder(MyToolBar.alternateToolBorder);
                    }
                }
            }
            //设置状态提示
            System.out.println("按压了");
            statusBar.setText("坐标:[" + e.getX() + "," + e.getY() + "]像素");
            preSelect.clear();//+++删除对象清除

            //如果当前选择的图形是画笔、刷子或者橡皮檫，则进行下面的操作
            if (currentChoice == 3 || currentChoice == 16 || currentChoice == 17) {
                if (currentChoice == 16){
                    rubber_delete(e.getX(),e.getY());//橡皮檫需要删除覆盖到的图形
                }
                index++;
                MyFrame.createNewShape();
                lengthCount = 0;
                abstractShapeList.get(index - 1).x1 = abstractShapeList.get(index - 1).x2 = e.getX();
                abstractShapeList.get(index - 1).y1 = abstractShapeList.get(index - 1).y2 = e.getY();
//                    System.out.println(itemList.get(index-1).x1);
//                    System.out.println(itemList.get(index-1).x2);
//                    System.out.println(itemList.get(index-1).y1);
//                    System.out.println(itemList.get(index-1).y2);
                lengthCount++;
            } else if ((currentChoice >= 4 && currentChoice <= 15)) {//当执行的是绘制图形时
                index++; //创建新图形
                MyFrame.createNewShape();
                abstractShapeList.get(index - 1).x1 = abstractShapeList.get(index - 1).x2 = e.getX();
                abstractShapeList.get(index - 1).y1 = abstractShapeList.get(index - 1).y2 = e.getY();
                System.out.println("生成了一个新图形");
                //System.out.println(itemList[index-1].x1);
            } else if(currentChoice==18){//当执行绘制文字///////////////////////修改
                index++;//创建新图形
                MyFrame.createNewShape();
                abstractShapeList.get(index-1).x1 = abstractShapeList.get(index-1).x2 = e.getX();
                abstractShapeList.get(index-1).y1 = abstractShapeList.get(index-1).y2 = e.getY();
                if(abstractShapeList.get(index-1).s==null){//如果新建的文字为空
                    abstractShapeList.remove(index-1);//删除当前对象   //修改
                    index--;
                    System.out.printf("因为新建的文字为空，当前的对象:%d",abstractShapeList.size());
                }
                else{
                    abstractShapeList.get(index-1).y2=abstractShapeList.get(index-1).y1+abstractShapeList.get(index-1).fontSize;
                }

            }else if (currentChoice == 20) { //+++填充操作
                shapeFill(e.getX(), e.getY()); //没有栈操作
                //repaint();//重绘
            } else if (currentChoice == 19) { //当执行的是选择图形时
                //this.requestFocus(); //让他获取焦点，一瞬间的，马上失效
                MyFrame.foc();
                int flag = 0; //等于0说明点的地方没有图形
                for (int i = index - 1; i >= 0; i--) { //如果鼠标点击时是在图形边框上
                    AbstractShape s = abstractShapeList.get(i);
                    //Triangle s = (Triangle) s1;
                    //当前点到中心的距离
                    if (s.contains(e.getX(), e.getY())) {
                        //System.out.println("圆内");
                        flag = 1; //说明点击的这个区域有图形
                        select.add(s); //将数组中的第一个当作首选对象
                        this.position_x=e.getX();//记录下当前位置的坐标  //修改
                        this.position_y=e.getY();
                        break;
                        //this.repaint();
                    }
                }
                if (flag == 0) { //如果本次点击没有选中图形,就清空clear
                    select.clear();
                }
            }
            currentDrawPanel.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            statusBar.setText("坐标:[" + e.getX() + "," + e.getY() + "]像素");
            //+++对于图形绘制，单击一下并不生效，文字应该不算在内，但添加上目前也没影响
            //if(index>0&&currentChoice!= 3 && currentChoice != 16 && currentChoice != 17&&currentChoice!=1){
            //if((currentChoice>3&&currentChoice<=13)||currentChoice==18){//对象容器里面有图形
            if ((currentChoice >= 4 && currentChoice <= 15)) { //对象容器里面有图形
                if (abstractShapeList.get(index - 1).x1 == abstractShapeList.get(index - 1).x2 && abstractShapeList.get(index - 1).y1 == abstractShapeList.get(index - 1).y2) {
                    //起始点和终止点重合，则这个图形的创建没有意义
                    abstractShapeList.remove(index - 1);
                    index--;
                } else if(currentChoice==9||currentChoice==10){// 修改  当图形创建成功但是创建的图形是圆形
                    int absSubX=Math.abs(abstractShapeList.get(index - 1).x1 - abstractShapeList.get(index - 1).x2);
                    int absSubY=Math.abs(abstractShapeList.get(index - 1).y1 - abstractShapeList.get(index - 1).y2);
                    if(absSubX>absSubY){//x相差大，故调整y来画圆
                        if(abstractShapeList.get(index - 1).y1>abstractShapeList.get(index - 1).y2){//下面那个点往下移，在上面那个点的基础上加上距离
                            abstractShapeList.get(index - 1).y1=abstractShapeList.get(index - 1).y2+absSubX;
                        }else abstractShapeList.get(index - 1).y2=abstractShapeList.get(index - 1).y1+absSubX;
                    }else{//y相差大，故调整x来画圆
                        if(abstractShapeList.get(index - 1).x1>abstractShapeList.get(index - 1).x2){//右面那个点往右移，在左面那个点的基础上加上距离
                            abstractShapeList.get(index - 1).x1=abstractShapeList.get(index - 1).x2+absSubY;
                        }else abstractShapeList.get(index - 1).x2=abstractShapeList.get(index - 1).x1+absSubY;
                    }
                }
            } else if (currentChoice == 3 || currentChoice == 16 || currentChoice == 17) { //+++对于可连续绘制操作，释放鼠标时再入栈一个（不入也行），把length复制，不然lengthCount就失去了意义
                //没压入新的
                //lengthCount = 0;
                abstractShapeList.get(index - 1).length = lengthCount;
            } else if (currentChoice == 19) { //如果现在执行的是选择操作，那么我释放后就要清空选择数组，以便于下次的选择
                //System.out.println("释放前："+itemList.size());
                if (select.size() != 0) {
                    preSelect.add(select.get(0)); //+++点击一下，鼠标释放之后，获得要删除的对象
                }
                System.out.println(preSelect.size());
                select.clear();
                //System.out.println("释放后："+itemList.size());
            }
//                if (currentChoice == 3 || currentChoice == 16 || currentChoice == 17) {
//                    itemList.get(index-1).x1 = e.getX();
//                    itemList.get(index-1).y1 = e.getY();
//                    lengthCount++;
//                    itemList.get(index-1).length = lengthCount;
//                }
//                else if(currentChoice!=1){ //当不是选择时且不是上述情况,即是绘制图形时
//                    index++;
//                    createNewGraphics();
//                    itemList.get(index-1).x2 = e.getX();
//                    itemList.get(index-1).y2 = e.getY();
//                    System.out.println("////////////////");
//                   //System.out.println(itemList[index-1].x1);
//                }
            currentDrawPanel.repaint();//为移动高亮显示重绘，停下来的时候不再高亮
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            statusBar.setText("坐标:[" + e.getX() + "," + e.getY() + "]像素");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            statusBar.setText("坐标：");
        }
    }

    /**
     * 鼠标拖拽和移动事件监听器，继承于MouseMotionAdapter
     */
    static class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            //System.out.println("拖动");
            statusBar.setText("坐标:[" + e.getX() + "," + e.getY() + "]像素");
            // System.out.println("拖拽了");
            if (currentChoice == 3 || currentChoice == 16 || currentChoice == 17) {
                if (currentChoice == 16){
                    rubber_delete(e.getX(),e.getY());//橡皮檫需要删除覆盖到的图形
                }
                index++;
                MyFrame.createNewShape();
                abstractShapeList.get(index - 1).x2 = abstractShapeList.get(index - 1).x1 = e.getX();
                abstractShapeList.get(index - 1).y2 = abstractShapeList.get(index - 1).y1 = e.getY();
                //if(index>=2){//按压鼠标的时候创建了一个对象，拖拽后又生成了一个对象
                //+++按压鼠标的时候创建了一个对象，拖拽后又生成了一个对象，所以应该没有必要判断大于2
                abstractShapeList.get(index - 2).x1 = e.getX();
                abstractShapeList.get(index - 2).y1 = e.getY();
                //}
                lengthCount++;
            } else if ((currentChoice >= 4 && currentChoice <= 15) || currentChoice == 18) {
                //当执行的是绘制图形时，拖拽过程中不生成新对象
                abstractShapeList.get(index - 1).x2 = e.getX();
                abstractShapeList.get(index - 1).y2 = e.getY();
            }
            if (select.size() != 0) { //修改
                //System.out.println("select.get(0).s");
//                int centre_x = (select.get(0).x1 + select.get(0).x2) / 2;
//                int centre_y = (select.get(0).y1 + select.get(0).y2) / 2;
                int temp_x=e.getX();
                int temp_y=e.getY();
                int offset_x = temp_x - MyMouseAdapter.position_x;//centre_x;
                int offset_y = temp_y -MyMouseAdapter.position_y; //centre_y;
                MyMouseAdapter.position_x=temp_x;
                MyMouseAdapter.position_y=temp_y;
                select.get(0).x1 = select.get(0).x1 + offset_x;
                select.get(0).y1 = select.get(0).y1 + offset_y;
                select.get(0).x2 = select.get(0).x2 + offset_x;
                select.get(0).y2 = select.get(0).y2 + offset_y;
//                if (select.get(0).s != null) { //如果是文字
//                    select.get(0).x1 = e.getX();
//                    select.get(0).y1 = e.getY();
//                }
            }
            currentDrawPanel.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            statusBar.setText("坐标:[" + e.getX() + "," + e.getY() + "]像素");
        }
    }
}

/**
 * 画图面板类，用来画图
 */
class DrawPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    DrawPanel() {
        //设置光标类型，为十字形
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        //设置背景颜色
        this.setBackground(Color.white);
        //设置鼠标监听
        this.addMouseListener(new MyFrame.MyMouseAdapter());
        this.addMouseMotionListener(new MyFrame.MyMouseMotionAdapter());
    }

    /**
     * 重写paintComponent方法，使得画板每次刷新时可将之前的所有图形重新画出来。
     * 由上层组件的paint()调用
     */
    @Override
    //public void paintComponent(Graphics g) {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; //定义画板
        int j = 0;
        while (j < MyFrame.index) {
            drawShape(g2d, MyFrame.abstractShapeList.get(j)); //重绘时逐个画出所有栈内存在的abstractShape
            j++;
        }
        if(MyFrame.preSelect.size()!=0){//当有选中图形时，画出该图形的选框
            //设置虚线选框
            g2d.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[]{9},0));
            g2d.drawRect(Math.min(MyFrame.preSelect.get(0).x1, MyFrame.preSelect.get(0).x2), Math.min(MyFrame.preSelect.get(0).y1, MyFrame.preSelect.get(0).y2), Math.abs(MyFrame.preSelect.get(0).x1 - MyFrame.preSelect.get(0).x2), Math.abs(MyFrame.preSelect.get(0).y1 - MyFrame.preSelect.get(0).y2));
        }
    }

    /**
     * 将Graphics2D g2d传入到栈中各个AbstractShape中，用来完成各自的绘图
     */
    void drawShape(Graphics2D g2d, AbstractShape abstractShape) {
        if (abstractShape != null) {
            abstractShape.drawAbstractShape(g2d);
        }
    }
}

/**
 * 工具栏初始化部分
 */
class MyToolBar {
    /**
     * 这个MyToolbar所创建的JToolBar要放入的MyFrame
     */
    private MyFrame parent;
    /**
     * 定义各种绘图的按钮
     */
    JButton[] toolButtonList;
    private JComboBox<String> comboBoxFont;
    private JComboBox<String> comboBoxFontSize;
    /**
     * 将图片资源的相对路径存放于数组中，方便使用
     */
    private String[] images = {"/image/save.png", "/image/refresh.png", "/image/undo.png", "/image/pencil.png",
            "/image/line.png", "/image/rectangle.png", "/image/fillrectangle.png", "/image/oval.png",
            "/image/filloval.png", "/image/circle.png", "/image/fillcircle.png", "/image/roundrectangle.png",
            "/image/fillroundrectangle.png", "/image/triangle.png", "/image/pentagon.png", "/image/hexagon.png",
            "/image/eraser.png", "/image/brush.png", "/image/text.png", "/image/select.png", "/image/fill.png"};
    private String[] tipText = {"保存", "清空", "撤销", "铅笔", "直线", "矩形", "填充矩形", "椭圆形", "填充椭圆形", "圆形", "填充圆形",
            "圆角矩形", "填充圆角矩形", "三角形", "五边形", "六边形", "橡皮擦", "刷子", "文本", "选择图形", "填充图形"};
    private String[] font = {"宋体", "隶书", "华文彩云", "仿宋_GB2312", "华文行楷", "方正舒体"};
    private String[] fontSize = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36",
            "48", "72"};
    /**
     * 被选中的画笔的边框样式
     */
    static Border chosenToolBorder = new LineBorder(Color.RED, 2);
    /**
     * 未被选中的画笔的边框样式
     */
    static Border alternateToolBorder = new LineBorder(Color.WHITE, 2);
    /**
     * 搜索结果的画笔的边框样式
     */
    static Border electToolBorder = new LineBorder(Color.BLUE, 2);
    /**
     * 缩放时在多边形的点上乘以的系数
     */
    public static double zoomCoef = 1.0;

    MyToolBar(MyFrame parent) {
        this.parent = parent;
    } //原先的addToolbar()提到MyFrame的构造函数中执行

    /**
     * 创建一个JToolBar并加入到this.parent中
     */
    void addToolBar() {
        toolButtonList = new JButton[images.length]; //定义指定个数的按钮
        System.out.println(images.length);

        //--新布局定义的一些JPanel
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setBackground(new Color(195, 195, 195));
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JPanel toolBarPanelButtons = new JPanel();
        toolBarPanelButtons.setLayout(new GridLayout(2, 1));
        toolBarPanel.add(toolBarPanelButtons);

        //JPanel toolBarPanelColor = new JPanel(); //直接用MyFrame.colorPanel？
        //toolBarPanelColor.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBarPanel.add(MyFrame.colorPanel);

        JPanel toolBarPanelUpper = new JPanel();
        toolBarPanelUpper.setBackground(new Color(195, 195, 195));
        toolBarPanelUpper.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        //toolBarPanelUpper.setFloatable(false);
        JPanel toolBarPanelLower = new JPanel();
        toolBarPanelLower.setBackground(new Color(195, 195, 195));
        toolBarPanelLower.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        //toolBarPanelLower.setFloatable(false);

        //---定义按钮面板 - 实例化一个水平的工具标签（JToolBar）
//        JToolBar toolBar = new JToolBar("工具栏");
//        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
//        toolBar.setBackground(new Color(195, 195, 195));
        //中文会乱码
        //配置各个按钮图标以及图片
        ImageIcon[] icon = new ImageIcon[images.length];
        for (int i = 0; i < images.length; i++) {
            //System.out.println(images[i]); //测试
            if (i == 6 || i == 8 || i == 10 || i == 12) { //---跳过原来的填充图形
                continue;
            }
            toolButtonList[i] = new JButton();
            icon[i] = new ImageIcon(getClass().getResource(images[i]));
            toolButtonList[i].setIcon(icon[i]);
            toolButtonList[i].setToolTipText(tipText[i]);
            toolButtonList[i].setPreferredSize(new Dimension(28, 28)); //设置按钮大小
            //btnPaint[i].setBorderPainted(false); //去边框
            //btnPaint[i].setContentAreaFilled(false);
            toolButtonList[i].setBorder(i == 3 ? chosenToolBorder : alternateToolBorder); //---初始化铅笔（默认画笔）的边框
            toolButtonList[i].setBackground(Color.WHITE);
            //---toolBar.add(toolButtonList[i]);
        }
        //为各个按钮添加监听器
        toolButtonList[0].addActionListener(e -> {
            MyFrame.myMenu.saveFile();
            MyFrame.saved = true;
        }); //保存按钮
        toolButtonList[1].addActionListener(e -> MyFrame.myMenu.newFile()); //清空按钮
        toolButtonList[2].addActionListener(e -> MyFrame.undo()); //撤销按钮
        for (int i = 3; i < images.length; i++) { //各画笔选择按钮
            if (i == 6 || i == 8 || i == 10 || i == 12) { //---跳过原来的填充图形
                continue;
            }
            toolButtonList[i].addActionListener(e -> {
                for (int j = 0; j < images.length; j++) {
                    //如果按钮被点击，则设置相应的画笔
                    if (e.getSource() == toolButtonList[j]) {
                        MyFrame.currentChoice = j;
                        toolButtonList[j].setBorder(chosenToolBorder);
                        //System.out.println(images[j]);
                        //System.out.println(j); //测试监听设置
                        //MyFrame.createNewShape();
                    } else if (toolButtonList[j] != null) {
                        toolButtonList[j].setBorder(alternateToolBorder);
                    }
                }
                parent.repaint();
            });
        }
        //---将按钮分组添加到MyToolBar

        //配置填充复选框，设置监听器并添加到MyToolBar
        JCheckBox checkBoxFill = new JCheckBox("fill");
        checkBoxFill.setBackground(new Color(195, 195, 195));
        checkBoxFill.setPreferredSize(new Dimension(60, 30));
        checkBoxFill.addItemListener(e -> { //itemStateChanged()
            JCheckBox src = (JCheckBox) e.getSource();
            MyFrame.fillFlag = src.isSelected();
        });
        //---toolBar.add(checkBoxFill);
        //配置粗体复选框，设置监听器并添加到MyToolBar
        JCheckBox checkBoxBold = new JCheckBox("bold"); //原类型java.awt.Checkbox
        checkBoxBold.setBackground(new Color(195, 195, 195));
        checkBoxBold.setPreferredSize(new Dimension(55, 30));
        checkBoxBold.addItemListener(e -> { //itemStateChanged()
            JCheckBox src = (JCheckBox) e.getSource();
            MyFrame.boldFlag = src.isSelected() ? Font.BOLD : Font.PLAIN;
        });
        //---toolBar.add(checkBoxBold);
        //配置斜体复选框，设置监听器并添加到MyToolBar
        JCheckBox checkBoxItalic = new JCheckBox("italic");
        checkBoxItalic.setBackground(new Color(195, 195, 195));
        checkBoxItalic.setPreferredSize(new Dimension(60, 30));
        checkBoxItalic.addItemListener(e -> { //itemStateChanged()
            JCheckBox src = (JCheckBox) e.getSource();
            MyFrame.boldFlag = src.isSelected() ? Font.ITALIC : Font.PLAIN;
        });
        //---toolBar.add(checkBoxItalic);
        //配置字号下拉框，设置监听器并添加到MyToolBar
        comboBoxFontSize = new JComboBox<>(fontSize);
        comboBoxFontSize.setPreferredSize(new Dimension(50, 30));
        comboBoxFontSize.addItemListener(e -> MyFrame.fSize = Integer.parseInt(fontSize[comboBoxFontSize.getSelectedIndex()]));
        //---toolBar.add(comboBoxFontSize);
        //配置字体下拉框，设置监听器并添加到MyToolBar
        comboBoxFont = new JComboBox<>(font);
        comboBoxFont.setPreferredSize(new Dimension(100, 30));
        comboBoxFont.addItemListener(e -> MyFrame.fontName = font[comboBoxFont.getSelectedIndex()]);
        //---toolBar.add(comboBoxFont);

        //---配置搜索文本框，设置监听器并添加到MyToolBar
        JTextField searchTextField = new JTextField("搜索");
        searchTextField.setToolTipText("输入要搜索的图形名称");
        searchTextField.setBackground(Color.WHITE);
        searchTextField.setPreferredSize(new Dimension(120, 30));
        searchTextField.addActionListener(e -> {
            String y = e.getActionCommand(); //获得文本输入框里的字符串
            //检查工具栏上每个按钮，如果搜索内容和按钮的名称字符串匹配，则在该按钮上设置蓝色边框
            for (int j = 0; j < tipText.length; j++) {
                if (toolButtonList[j] == null) {
                    continue;
                }
                if ((j >= 3 && j <= 18) && !y.equals("") && tipText[j].contains(y)) {
                    toolButtonList[j].setBorder(electToolBorder);
                } else {
                    toolButtonList[j].setBorder(alternateToolBorder);
                }
            }
            parent.repaint();
        });
        toolBarPanel.add(searchTextField);

        //---配置放大、缩小按钮和缩放百分比输入框，并设置监听器
        //放大按钮
        JButton zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("放大");
        zoomInButton.setFont(new Font(null, Font.PLAIN, 25));
        zoomInButton.setPreferredSize(new Dimension(25, 25));
        zoomInButton.setBorder(alternateToolBorder);
        zoomInButton.addActionListener(e -> {
            //处理多边形;
            if (MyFrame.preSelect.size() != 0){//修改
                //System.out.println("放大");
                for (int i = 0; i < MyFrame.abstractShapeList.size(); i++) {
                    if (MyFrame.preSelect.get(0).hashCode() == MyFrame.abstractShapeList.get(i).hashCode()) {
                        int temp = (int)Math.ceil(MyToolBar.zoomCoef);//+++实例是myToolBar
                        if(temp>10)
                        {
                            temp=10;
                        }
                        MyFrame.abstractShapeList.get(i).changeSize(temp,1);
                        break;
                    }
                }
            }
            parent.repaint();
        });
        //缩小按钮
        JButton zoomOutButton = new JButton("－");
        zoomOutButton.setToolTipText("缩小");
        zoomOutButton.setFont(new Font(null, Font.BOLD, 15));
        zoomOutButton.setPreferredSize(new Dimension(25, 25));
        zoomOutButton.setBorder(alternateToolBorder);
        zoomOutButton.addActionListener(e -> {
            //处理多边形;
            if (MyFrame.preSelect.size() != 0){//修改
                //System.out.println("缩小");
                for (int i = 0; i < MyFrame.abstractShapeList.size(); i++) {
                    if (MyFrame.preSelect.get(0).hashCode() == MyFrame.abstractShapeList.get(i).hashCode()) {
                        int temp = (int)Math.ceil(MyToolBar.zoomCoef);//+++实例是myToolBar
                        if(temp>10)
                        {
                            temp=10;
                        }
                        MyFrame.abstractShapeList.get(i).changeSize(temp,0);
                        break;
                    }
                }
            }
            parent.repaint();
        });
        //缩放百分比输入框
        JTextField zoomScaleTextField = new JTextField("10");
        zoomScaleTextField.setToolTipText("输入缩放百分比后按Enter");
        zoomScaleTextField.setPreferredSize(new Dimension(40, 25));
        JLabel percentLabel = new JLabel("%");
        zoomScaleTextField.addActionListener(e -> {
            JTextField src = (JTextField) e.getSource();
            try {
                Integer zoomCoefInt = Integer.parseInt(e.getActionCommand(), 10);
                src.setText(zoomCoefInt.toString());
                //zoomCoef = zoomCoefInt * 0.01;
                zoomCoef = zoomCoefInt*0.1;//+++0.1
                System.out.println("缩放参数");
                System.out.println(zoomCoef);
                if(zoomCoef>100){//+++缩放，默认显示10，zoomCoef = 1，20%是2，大于100是11，向上取整，最大11
                    zoomCoef = 11.0;
                }
                else{
                    //+++
                }
            } catch (NumberFormatException nfExcept) {
                System.out.println("缩放参数");
                System.out.println(zoomCoef);
                src.setText("10");
                zoomCoef = 1.0;
            }
        });

        //---以下定义新布局
        JToolBar toolBarUpper0 = new JToolBar();
        toolBarUpper0.setBackground(new Color(195, 195, 195));
        toolBarUpper0.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarUpper0.setFloatable(false);
        toolBarUpper0.add(toolButtonList[0]);
        toolBarUpper0.add(toolButtonList[1]);
        toolBarUpper0.add(toolButtonList[2]);
        toolBarPanelUpper.add(toolBarUpper0);

        JToolBar toolBarUpper1 = new JToolBar();
        toolBarUpper1.setBackground(new Color(195, 195, 195));
        toolBarUpper1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarUpper1.setFloatable(false);
        toolBarUpper1.add(toolButtonList[3]);
        toolBarUpper1.add(toolButtonList[4]);
        toolBarUpper1.add(toolButtonList[17]);
        toolBarPanelUpper.add(toolBarUpper1);

        JToolBar toolBarUpper2 = new JToolBar();
        toolBarUpper2.setBackground(new Color(195, 195, 195));
        toolBarUpper2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarUpper2.setFloatable(false);
        toolBarUpper2.add(toolButtonList[16]);
        toolBarPanelUpper.add(toolBarUpper2);

        JToolBar toolBarUpper3 = new JToolBar();
        toolBarUpper3.setBackground(new Color(195, 195, 195));
        toolBarUpper3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarUpper3.setFloatable(false);
        toolBarUpper3.add(toolButtonList[18]);
        toolBarUpper3.add(checkBoxBold);
        toolBarUpper3.add(checkBoxItalic);
        toolBarUpper3.add(comboBoxFont);
        toolBarUpper3.add(comboBoxFontSize);
        toolBarPanelUpper.add(toolBarUpper3);

        toolBarPanelButtons.add(toolBarPanelUpper);

        JToolBar toolBarLower0 = new JToolBar();
        toolBarLower0.setBackground(new Color(195, 195, 195));
        toolBarLower0.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarLower0.setFloatable(false);
        toolBarLower0.add(toolButtonList[5]);
        toolBarLower0.add(toolButtonList[11]);
        toolBarLower0.add(toolButtonList[7]);
        toolBarLower0.add(toolButtonList[9]);
        toolBarLower0.add(toolButtonList[13]);
        toolBarLower0.add(toolButtonList[14]);
        toolBarLower0.add(toolButtonList[15]);
        toolBarLower0.add(checkBoxFill);
        toolBarPanelLower.add(toolBarLower0);

        JToolBar toolBarLower1 = new JToolBar();
        toolBarLower1.setBackground(new Color(195, 195, 195));
        toolBarLower1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarLower1.setFloatable(false);
        toolBarLower1.add(toolButtonList[19]);
        toolBarLower1.add(toolButtonList[20]);
        toolBarPanelLower.add(toolBarLower1);

        JToolBar toolBarLower2 = new JToolBar();
        toolBarLower2.setBackground(new Color(195, 195, 195));
        toolBarLower2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBarLower2.setFloatable(false);
        toolBarLower2.add(zoomInButton);
        toolBarLower2.add(zoomOutButton);
        JPanel zoomScalePanel = new JPanel();
        zoomScalePanel.setBackground(new Color(195, 195, 195));
        zoomScalePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
        zoomScalePanel.add(zoomScaleTextField);
        zoomScalePanel.add(percentLabel);
        toolBarLower2.add(zoomScalePanel);
        toolBarPanelLower.add(toolBarLower2);

        toolBarPanelButtons.add(toolBarPanelLower);

        //设置JToolBar可以浮动
        //toolBar.setFloatable(true);
        //toolBar.addSeparator();

        //JToolBar就绪后，添加到MyFrame中
        //MyFrame.this.add(toolBar, BorderLayout.NORTH); //改为在MyFrame的构造函数中添加
        //---this.parent.add(toolBar, BorderLayout.NORTH);
        this.parent.add(toolBarPanel, BorderLayout.NORTH);
    }
}

/**
 * 菜单初始化部分
 */
class MyMenu {
    /**
     * 这个MyToolbar所创建的JToolBar要放入的MyFrame
     */
    private MyFrame parent;

    private String[] strokes = {"/image/stroke1.png", "/image/stroke2.png", "/image/stroke3.png",
            "/image/stroke4.png"};

    MyMenu(MyFrame parent) {
        this.parent = parent;
    }

    /**
     * 创建一个JMenuBar并加入到this.parent中
     */
    void addMenu() {
        //菜单条
        JMenuBar jMenuBar = new JMenuBar();
        JMenuItem[] strokeItems = new JMenuItem[strokes.length];
        //实例化菜单对象
        //定义文件、设置、帮助菜单
        JMenu fileMenu = new JMenu("文件");
        JMenu setMenu = new JMenu("设置");
        JMenu helpMenu = new JMenu("帮助");
        JMenu strokeMenu = new JMenu("画笔粗细");
        //实例化菜单项,并通过ImageIcon对象添加图片 定义文件菜单的菜单项
        JMenuItem fileItemNew = new JMenuItem("新建", new ImageIcon(getClass().getResource("/image/new.png")));
        JMenuItem fileItemOpen = new JMenuItem("打开", new ImageIcon(getClass().getResource("/image/open.png")));
        JMenuItem fileItemSave = new JMenuItem("保存", new ImageIcon(getClass().getResource("/image/save.png")));
        JMenuItem fileItemExit = new JMenuItem("退出", new ImageIcon(getClass().getResource("/image/exit.png")));
        //定设置菜单的菜单项
        JMenuItem setItemColor = new JMenuItem("笔触颜色", new ImageIcon(getClass().getResource("/image/color.png")));
        JMenuItem setItemUndo = new JMenuItem("撤销", new ImageIcon(getClass().getResource("/image/undo.png")));
        JMenuItem helpItemUse = new JMenuItem("使用手册");
        JMenuItem helpItemInfo = new JMenuItem("关于画图");
        for (int i = 0; i < 4; i++) {
            strokeItems[i] = new JMenuItem("", new ImageIcon(getClass().getResource(strokes[i])));
            strokeMenu.add(strokeItems[i]);
        }
        helpItemInfo.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "" + "关于Jpaint\n" + "****该软件由以下成员联合开发****\n" + "****班级：计算机2001、计算机2002*****\n"
                        + "****姓名：组员一*****\n" + "****学号：xxxxxxxx*****\n"
                        + "****姓名：组员二*****\n" + "****学号：xxxxxxxx*****\n"
                        + "****姓名：组员三*****\n" + "****学号：xxxxxxxx*****\n",
                "关于画图", JOptionPane.PLAIN_MESSAGE));
        helpItemUse.addActionListener(e -> JOptionPane.showMessageDialog(null, "" + "#画图软件使用说明书#\r\n"
                + "1.本软件可以实现以下功能：\r\n" + "（1）在画布上绘制直线、矩形、椭圆等图形\r\n"
                + "（2）绘制填充图形、对已有图形进行填充\r\n" + "（3）绘制任意曲线\r\n" + "（4）设置画笔的颜色和粗细\r\n" + "（5）橡皮擦擦除任意曲线和基本图形\r\n"
                + "（6）选取、移动、删除基本图形\r\n"+ "（7）添加字体\r\n"
                + "（8）设定文字的颜色与风格\r\n"+ "（9）通过鼠标拖动完成上述绘制和添加文字等操作\r\n"+ "（10）支持按照名称搜索特定图形并定位\r\n"
                + "（11）实现幻灯片的全屏播放、翻页\r\n"+ "（12）插入并修改图像\r\n"
                + "（13）对选中图形进行框选\r\n"+ "（14）操作的撤销\r\n"+ "（15）菜单\r\n"+ "（16）按照名称进行模糊搜索\r\n"
                + "2.本软件主要分为四个模块：菜单、工具栏、调色板、画布和多页栏\r\n" + "（1）菜单栏的文件子菜单包括打开、新建、保存图片以及退出程序，设置有快捷键，方便操作，\r\n"
                + "	菜单栏的设置子菜单包括设置画笔的粗细和颜色；\r\n" + "（2）工具栏主要包括保存文件、清空画板、撤回操作、图形绘制和文字的绘制以及放大与缩小；\r\n"
                + "（3）调色板位于界面的左侧，用于设置画笔的颜色，可以使用已设定的颜色，也可以自己选择系统提供的颜色；\r\n"
                + "（4）画布用于图形绘制，使用鼠标选中要绘制的图形即可进行绘制。\r\n"
                + "（5）多页栏用于创建、切换或者删除画布，也可用于播放当前画布。\r\n"+ "3.项目特色\r\n"
                + "（1）实现了序列化存储，生成了可编辑工程文件。\r\n" + "（2）对选中的基本图形、文字、图像进行框选和放缩\r\n" + "（3）添加多个可编辑的页面\r\n" , "使用说明", JOptionPane.PLAIN_MESSAGE));
        helpMenu.add(helpItemUse);
        helpMenu.add(helpItemInfo);
        //设置快捷键
        fileItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        setItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        //添加粗细子菜单

        //将所有JMenuItem添加到对应的JMenu
        fileMenu.add(fileItemNew);
        fileMenu.add(fileItemOpen);
        fileMenu.add(fileItemSave);
        fileMenu.add(fileItemExit);
        setMenu.add(setItemColor);
        setMenu.add(setItemUndo);
        setMenu.add(strokeMenu);

        //将所有JMenu添加到JMenuBar
        jMenuBar.add(fileMenu);
        jMenuBar.add(setMenu);
        jMenuBar.add(helpMenu);

        //将JMenuBar添加到MyFrame
        this.parent.setJMenuBar(jMenuBar);

        //给文件菜单设置监听
        fileItemNew.addActionListener(e -> MyFrame.myMenu.newFile());
        fileItemSave.addActionListener(e -> {
            //保存文件，并将标志符saved设置为true
            MyFrame.myMenu.saveFile();
            MyFrame.saved = true;
        });
        fileItemOpen.addActionListener(e -> {
            //打开文件，并将标志符saved设置为false
            MyFrame.myMenu.openFile();
            MyFrame.saved = false;
        });
        fileItemExit.addActionListener(e -> {
            //如果文件已经保存就直接退出，若果文件没有保存，提示用户选择是否退出
            if (MyFrame.saved) {
                System.exit(0);
            } else {
                int n = JOptionPane.showConfirmDialog(null, "您还没保存，确定要退出？", "提示", JOptionPane.OK_CANCEL_OPTION);
                if (n == 0) {
                    System.exit(0);
                }
            }
        });
        //给设置菜单添加监听
        setItemColor.addActionListener(e -> {
            MyFrame.colorPanel.choosePenColor(); //设置笔触颜色
        });
        setItemUndo.addActionListener(e -> {
            MyFrame.undo(); //撤销
        });
        strokeItems[0].addActionListener(e -> {
            MyFrame.stroke = 1;
            //MyFrame.abstractShapeList.get(MyFrame.index - 1).width = MyFrame.stroke;
        });
        strokeItems[1].addActionListener(e -> {
            MyFrame.stroke = 5;
            //MyFrame.abstractShapeList.get(MyFrame.index - 1).width = MyFrame.stroke;
        });
        strokeItems[2].addActionListener(e -> {
            MyFrame.stroke = 15;
            //MyFrame.abstractShapeList.get(MyFrame.index - 1).width = MyFrame.stroke;
        });
        strokeItems[3].addActionListener(e -> {
            MyFrame.stroke = 25;
            //MyFrame.abstractShapeList.get(MyFrame.index - 1).width = MyFrame.stroke;
        });
    }

    /**
     * 保存图形文件
     */
    void saveFile() {
        //文件选择器
        JFileChooser fileChooser = getJFileChooser();
        //弹出一个 "Save File" 文件选择器对话框
        int result = fileChooser.showSaveDialog(this.parent);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();

        if (!fileName.getName().endsWith(fileChooser.getFileFilter().getDescription())) {
            String t = fileName.getPath() + fileChooser.getFileFilter().getDescription();
            fileName = new File(t);
        }
        if(fileChooser.getFileFilter().getDescription() == ".txt" ){
            System.out.println(".txt格式工程文件");
            if ("".equals(fileName.getName())) {
                JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "无效的文件名", JOptionPane.ERROR_MESSAGE);
            }

            parent.Project_save(fileName);
        }
        else {
            fileName.canWrite();
            if ("".equals(fileName.getName())) {
                JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "无效的文件名", JOptionPane.ERROR_MESSAGE);
            }

            BufferedImage image = createImage(MyFrame.currentDrawPanel);
            try {
                ImageIO.write(image, "png", fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开文件
     */
    void openFile() {
        JFileChooser fileChooser = getJFileChooser();
        //弹出一个 "Open File" 文件选择器对话框
        int result = fileChooser.showOpenDialog(this.parent);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        //得到选择文件的名字
        File fileToOpen = fileChooser.getSelectedFile();
        if (!fileToOpen.getName().endsWith(fileChooser.getFileFilter().getDescription())) {
            JOptionPane.showMessageDialog(this.parent, "文件格式错误！");
            return;
        }
        if(fileChooser.getFileFilter().getDescription() == ".txt" ){
            System.out.println(".txt格式工程文件");
            if ("".equals(fileToOpen.getName())) {
                JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "无效的文件名", JOptionPane.ERROR_MESSAGE);
            }

            parent.Project_open(fileToOpen);
        }
        else {
            fileToOpen.canRead();


            if ("".equals(fileToOpen.getName())) {
                JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "无效的文件名", JOptionPane.ERROR_MESSAGE);
            }

            BufferedImage image;
            try {//+++这里可能会有问题
                //MyFrame.index = 0;
                MyFrame.index++;
                int oldChoice = MyFrame.currentChoice;
                MyFrame.currentChoice = 0; //新生成一个Images
                image = ImageIO.read(fileToOpen);
                MyFrame.createNewShape();
                MyFrame.abstractShapeList.get(MyFrame.index - 1).image = image;
                MyFrame.abstractShapeList.get(MyFrame.index - 1).board = MyFrame.currentDrawPanel;
                MyFrame.currentDrawPanel.repaint();
                MyFrame.currentChoice = oldChoice;
                //MyFrame.createNewShape();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JFileChooser getJFileChooser() {
        //文件选择器
        JFileChooser fileChooser = new JFileChooser();
        //设置文件显示类型为仅显示文件
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //文件过滤器
        MyMenu.JpgFilter jpg = new MyMenu.JpgFilter();
        MyMenu.BmpFilter bmp = new MyMenu.BmpFilter();
        MyMenu.PngFilter png = new MyMenu.PngFilter();
        MyMenu.GifFilter gif = new MyMenu.GifFilter();
        MyMenu.TxtFilter txt = new MyMenu.TxtFilter();
        //向用户可选择的文件过滤器列表添加一个过滤器。
        fileChooser.addChoosableFileFilter(jpg);
        fileChooser.addChoosableFileFilter(bmp);
        fileChooser.addChoosableFileFilter(png);
        fileChooser.addChoosableFileFilter(gif);
        fileChooser.addChoosableFileFilter(txt);
        //返回当前的文本过滤器，并设置成当前的选择
        fileChooser.setFileFilter(fileChooser.getFileFilter());
        return fileChooser;
    }

    /**
     * 新建文件（清空画板）
     */
    void newFile() {
        MyFrame.index = 0;
        MyFrame.abstractShapeList.clear(); //清空原来的栈
        MyFrame.multiPageManager.multiPageList.clear();
        MyFrame.multiPageManager.currentPage = new Page(parent.index, parent.abstractShapeList, parent.select, parent.preSelect);
        MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
        MyFrame.multiPageManager.multiPageList.add(MyFrame.multiPageManager.currentPage);
        MyFrame.multiPageManager.resetMultiPagePanel();

        //MyFrame.currentChoice = 3;
        //MyFrame.penColor = Color.BLACK;
        //MyFrame.stroke = 1;
        //MyFrame.createNewShape();
        MyFrame.currentDrawPanel.repaint();
    }

    /**
     * 创建image，由saveFile方法调用
     * 将画板内容画到panelImage上
     */
    BufferedImage createImage(DrawPanel panel) {
        int width = this.parent.getWidth();
        int height = this.parent.getHeight();
        BufferedImage panelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = panelImage.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.translate(0, 0);
        panel.paint(graphics);
        graphics.dispose();
        return panelImage;
    }

    //文件过滤
    class JpgFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".jpg");
        }
        @Override
        public String getDescription() {
            return ".jpg";
        }
    }

    class BmpFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".bmp");
        }
        @Override
        public String getDescription() {
            return ".bmp";
        }
    }

    class GifFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".gif");
        }
        @Override
        public String getDescription() {
            return ".gif";
        }
    }

    class PngFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".png");
        }
        @Override
        public String getDescription() {
            return ".png";
        }
    }

    class TxtFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".txt");
        }
        @Override
        public String getDescription() {
            return ".txt";
        }
    }
}

/**
 * 保存多页中每一页的图形对象信息
 */
class Page extends JButton implements Serializable {
    /**
     * 这一页的图形对象栈顶指示器
     */
    private static final long serialVersionUID=1L;

    Integer index;
    /**
     * 这一页的图形对象栈
     */
    ArrayList<AbstractShape> abstractShapeList;
    /**
     * 这一页的选中图形列表
     */
     ArrayList<AbstractShape> select;
    /**
     * 这一页的preSelect列表
     */
    ArrayList<AbstractShape> preSelect;

    /**
     * 新建一个空的Page对象
     */
    Page() {
        index = new Integer(0);
        abstractShapeList = new ArrayList<AbstractShape>();
        select = new ArrayList<AbstractShape>();
        preSelect = new ArrayList<AbstractShape>();
        this.addMouseListener(new PageMouseAdapter(this));
        this.setPreferredSize(new Dimension(63, 25));
    }

    /**
     * 根据已有页面信息创建Page对象
     */
    Page(Integer index,
         ArrayList<AbstractShape> abstractShapeList,
         ArrayList<AbstractShape> select,
         ArrayList<AbstractShape> preSelect) {
        this.index = index;
        this.abstractShapeList = abstractShapeList;
        this.select = select;
        this.preSelect = preSelect;
        this.addMouseListener(new PageMouseAdapter(this));
        this.setPreferredSize(new Dimension(63, 25));
    }

    /**
     * 复制构造Page对象
     */
    Page(Page p) {
        this.index = p.index;
        this.abstractShapeList = (ArrayList<AbstractShape>) p.abstractShapeList.clone();
        this.select = (ArrayList<AbstractShape>) p.select.clone();
        this.preSelect = (ArrayList<AbstractShape>) p.preSelect.clone();
        this.addMouseListener(new PageMouseAdapter(this));
        this.setPreferredSize(new Dimension(63, 25));
    }

    /**
     * 切换显示至此页面
     * @param dest 当前的MyFrame的引用
     */
    void showPage(MyFrame dest) {
        MyFrame.index = index;
        MyFrame.abstractShapeList = abstractShapeList;
        MyFrame.select = select;
        MyFrame.preSelect = preSelect;
        dest.repaint();
    }

    /**
     * 更新当前页面的图形对象栈顶指示器index的值
     * （index为基本类型，每次改动后需值传递更新）
     */
    void saveIndex(Integer index) {
        this.index = index;
    }
}

/**
 * 多页管理器类型
 */
class MultiPageManager extends JPanel implements Serializable{
    private static final long serialVersionUID=1L;

    transient MyFrame parent;
    /**
     * MultiPageManager的子面板，所有组件都放置于其中
     */
    transient JPanel managerPanel;
    /**
     * 页面列表
     */
    ArrayList<Page> multiPageList;
    /**
     * 容纳所有Page对象的Panel
     */
    transient JPanel multiPagePanel;
    /**
     * 当前页面的Page对象
     */
    transient Page currentPage;
    /**
     * 当前是否处于播放状态
     */
    boolean playingFlag = false;
    /**
     * 播放速度（ms/张）
     */
    int playSpeed = 1000;

    /**
     * 播放时用的定时器
     */
    transient Timer playTimer;
    MultiPageManager() {
        super(new FlowLayout(FlowLayout.LEFT, 0, 3));
    }
    MultiPageManager(MyFrame parent) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.setBackground(new Color(195, 195, 195));
        this.setPreferredSize(new Dimension(100, 280));
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        managerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));
        managerPanel.setBackground(new Color(195, 195, 195));
        managerPanel.setPreferredSize(new Dimension(90, 600));
        this.add(managerPanel);

        JLabel multiPageLabel = new JLabel("  页面列表");
        multiPageLabel.setPreferredSize(new Dimension(80, 25));
        multiPageLabel.setFont(new Font(null, Font.PLAIN, 15));
        managerPanel.add(multiPageLabel);
        JButton addPageButton = new JButton("新建页面");
        addPageButton.setFont(new Font(null, Font.PLAIN, 11));
        addPageButton.setPreferredSize(new Dimension(80, 25));
        addPageButton.addActionListener(e -> { //在当前页面后新建一个空页面
            if (multiPageList.size() < 50) { //当页面总数小于50时才允许添加
                //先保存当前页面图形对象栈顶指示器值，并取得原来位置的index
                currentPage.saveIndex(MyFrame.index);
                currentPage.setBorder(MyToolBar.alternateToolBorder);
                int oldIdx = multiPageList.indexOf(currentPage);
                //创建新的空页并添加到multiPageList中的指定位置
                currentPage = new Page();
                currentPage.setBorder(MyToolBar.chosenToolBorder);
                multiPageList.add(oldIdx + 1, currentPage);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                resetMultiPagePanel();
                currentPage.showPage(parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数上限为50");
            }
        });
        managerPanel.add(addPageButton);
        JButton deletePageButton = new JButton("删除页面");
        deletePageButton.setFont(new Font(null, Font.PLAIN, 11));
        deletePageButton.setPreferredSize(new Dimension(80, 25));
        deletePageButton.addActionListener(e -> { //删除当前选中的页面
            if (multiPageList.size() > 1) { //当页面总数大于1时才允许删除
                int srcIdx = multiPageList.indexOf(currentPage);
                int maxIdx = multiPageList.size() - 1;
                multiPageList.remove(currentPage);
                //若被删除的页面是最后一页，原页面删除后当前页面置为原页面的下一页
                //若被删除的页面不是最后一页，原页面删除后当前页面置为原页面的上一页
                currentPage = multiPageList.get(srcIdx < maxIdx ? srcIdx : srcIdx - 1);
                currentPage.setBorder(MyToolBar.chosenToolBorder);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                resetMultiPagePanel();
                currentPage.showPage(parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数下限为1");
            }
        });
        managerPanel.add(deletePageButton);

        JPanel divider1 = new JPanel();
        divider1.setPreferredSize(new Dimension(80, 2));
        divider1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        managerPanel.add(divider1);

        JButton playButton = new JButton("▶");
        playButton.setFont(new Font(null, Font.PLAIN, 7));
        playButton.setPreferredSize(new Dimension(40, 25));
        playButton.setToolTipText("开始播放");
        playButton.addActionListener(e -> {
            playingFlag = true;
            playTimer = new Timer(playSpeed, tsk -> { //每睡眠指定时间，切换一次页面
                int playIdx = multiPageList.indexOf(currentPage);
                if (playingFlag) {
                    currentPage.saveIndex(MyFrame.index);
                    currentPage.setBorder(MyToolBar.alternateToolBorder);
                    currentPage = multiPageList.get(playIdx + 1 == multiPageList.size() ? 0 : ++playIdx);
                    currentPage.setBorder(MyToolBar.chosenToolBorder);
                    currentPage.showPage(parent);
                }
                System.out.println("now displaying " + playIdx);
            });
            playTimer.start();
        });
        managerPanel.add(playButton);
        JButton stopButton = new JButton("◼");
        stopButton.setFont(new Font(null, Font.PLAIN, 7));
        stopButton.setPreferredSize(new Dimension(40, 25));
        stopButton.setToolTipText("停止播放");
        stopButton.addActionListener(e -> {
            playingFlag = false;
            playTimer.stop();
        });
        managerPanel.add(stopButton);
        JLabel playSpeedLabel = new JLabel("播放速度：   ");
        managerPanel.add(playSpeedLabel);
        JTextField playSpeedTextField = new JTextField("1");
        playSpeedTextField.setToolTipText("输入切换速度后按Enter");
        playSpeedTextField.setPreferredSize(new Dimension(50, 25));
        playSpeedTextField.addActionListener(e -> {
            JTextField src = (JTextField) e.getSource();
            try {
                Double playSpeedDouble = Double.parseDouble(e.getActionCommand());
                src.setText(playSpeedDouble.toString());
                playSpeed = (int) (playSpeedDouble * 1000);
            } catch (NumberFormatException nfExcept) {
                src.setText("1");
                playSpeed = 1000;
            }
        });
        managerPanel.add(playSpeedTextField);
        JLabel unitLabel = new JLabel("秒/张");
        unitLabel.setPreferredSize(new Dimension(30, 25));
        managerPanel.add(unitLabel);

        JPanel divider2 = new JPanel();
        divider2.setPreferredSize(new Dimension(80, 2));
        divider2.setBorder(new BevelBorder(BevelBorder.LOWERED));
        managerPanel.add(divider2);

        multiPagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        multiPagePanel.setBackground(new Color(195, 195, 195));
        multiPagePanel.setPreferredSize(new Dimension(80, 1500));
        JScrollPane multiPageScrollPane = new JScrollPane(multiPagePanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        multiPageScrollPane.setPreferredSize(new Dimension(80, 400));
        managerPanel.add(multiPageScrollPane);

        this.parent = parent;
        //初始化时根据MyFrame中已有的页面信息生成一个新页
        multiPageList = new ArrayList<>();
        currentPage = new Page(parent.index, parent.abstractShapeList, parent.select, parent.preSelect);
        currentPage.setBorder(MyToolBar.chosenToolBorder);
        multiPageList.add(currentPage);
        resetMultiPagePanel();
    }


    public void resetMultiPagePanel() {
        multiPagePanel.removeAll();
        Integer i = 1;
        for (Page p : multiPageList) {
            p.setText(i.toString());
            multiPagePanel.add(p);
            i++;
        }
        multiPagePanel.paintAll(multiPagePanel.getGraphics()); //若缺少此句则重绘不及时
    }
}

/**
 * 页面按钮上使用的鼠标监听器类型
 */
class PageMouseAdapter extends MouseAdapter {
    /**
     * 这个监听器所添加到的Page对象的引用
     */
    Page thisPage;
    /**
     * 右键弹出菜单
     */
    JPopupMenu rightClickMenu = new JPopupMenu();
    PageMouseAdapter(Page thisPage) {
        this.thisPage = thisPage;

        JMenuItem newBeforeThis = new JMenuItem("在前面添加新页");
        newBeforeThis.addActionListener(e -> {
            if (MyFrame.multiPageManager.multiPageList.size() < 50) { //当页面总数小于50时才允许添加
                //先保存当前页面图形对象栈顶指示器值，并取得原来位置的index
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
                int oldIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
                //创建新的空页并添加到multiPageList中的指定位置
                MyFrame.multiPageManager.currentPage = new Page();
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                MyFrame.multiPageManager.multiPageList.add(oldIdx, MyFrame.multiPageManager.currentPage); //在原来页的前面添加
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数上限为50");
            }
        });
        rightClickMenu.add(newBeforeThis);

        JMenuItem newAfterThis = new JMenuItem("在后面添加新页");
        newAfterThis.addActionListener(e -> {
            if (MyFrame.multiPageManager.multiPageList.size() < 50) { //当页面总数小于50时才允许添加
                //先保存当前页面图形对象栈顶指示器值，并取得原来位置的index
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
                int oldIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
                //创建新的空页并添加到multiPageList中的指定位置
                MyFrame.multiPageManager.currentPage = new Page();
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                MyFrame.multiPageManager.multiPageList.add(oldIdx + 1, MyFrame.multiPageManager.currentPage); //在原来页的后前面添加
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数上限为50");
            }
        });
        rightClickMenu.add(newAfterThis);

        JMenuItem switchWithPrev = new JMenuItem("与前一页交换位置");
        switchWithPrev.addActionListener(e -> {
            int thisIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
            if (thisIdx > 0 && MyFrame.multiPageManager.multiPageList.size() > 1) { //当前页不为第一页时才允许交换
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
                //当前页切换到被点击的页，并与multiPageList中前一个元素交换位置
                MyFrame.multiPageManager.currentPage = thisPage;
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                Page prev = MyFrame.multiPageManager.multiPageList.get(thisIdx - 1);
                MyFrame.multiPageManager.multiPageList.remove(prev);
                MyFrame.multiPageManager.multiPageList.add(thisIdx, prev);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            }
        });
        rightClickMenu.add(switchWithPrev);

        JMenuItem switchWithNext = new JMenuItem("与后一页交换位置");
        switchWithNext.addActionListener(e -> {
            int thisIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
            if (thisIdx < MyFrame.multiPageManager.multiPageList.size() - 1 && MyFrame.multiPageManager.multiPageList.size() > 1) { //当前页不为最后一页时才允许交换
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
                //当前页切换到被点击的页，并与multiPageList中后一个元素交换位置
                MyFrame.multiPageManager.currentPage = thisPage;
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                Page next = MyFrame.multiPageManager.multiPageList.get(thisIdx + 1);
                MyFrame.multiPageManager.multiPageList.remove(next);
                MyFrame.multiPageManager.multiPageList.add(thisIdx, next);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            }
        });
        rightClickMenu.add(switchWithNext);

        JMenuItem copyThis = new JMenuItem("复制此页");
        copyThis.addActionListener(e -> {
            if (MyFrame.multiPageManager.multiPageList.size() < 50) { //当页面总数小于50时才允许添加
                int thisIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
                MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
                //以被点击的页复制构造一个新页，加到multiPageList中
                MyFrame.multiPageManager.currentPage = new Page(thisPage);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                MyFrame.multiPageManager.multiPageList.add(thisIdx, MyFrame.multiPageManager.currentPage);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数上限为50");
            }
        });
        rightClickMenu.add(copyThis);

        JMenuItem deleteThis = new JMenuItem("删除此页");
        deleteThis.addActionListener(e -> {
            if (MyFrame.multiPageManager.multiPageList.size() > 1) { //当页面总数大于1时才允许删除
                int srcIdx = MyFrame.multiPageManager.multiPageList.indexOf(thisPage);
                int maxIdx = MyFrame.multiPageManager.multiPageList.size() - 1;
                MyFrame.multiPageManager.multiPageList.remove(thisPage);
                //若被删除的页面是最后一页，原页面删除后当前页面置为原页面的下一页
                //若被删除的页面不是最后一页，原页面删除后当前页面置为原页面的上一页
                MyFrame.multiPageManager.currentPage = MyFrame.multiPageManager.multiPageList.get(srcIdx < maxIdx ? srcIdx : srcIdx - 1);
                MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
                //清除multiPagePanel中的所有按钮，重新按照multiPageList的顺序添加
                MyFrame.multiPageManager.resetMultiPagePanel();
                MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
            } else {
                JOptionPane.showMessageDialog(null, "页面数下限为1");
            }
        });
        rightClickMenu.add(deleteThis);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int btnClicked = e.getButton();
        if (btnClicked == MouseEvent.BUTTON1) { //若以左键点击，则切换至该页显示
            MyFrame.multiPageManager.currentPage.saveIndex(MyFrame.index);
            MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.alternateToolBorder);
            MyFrame.multiPageManager.currentPage = thisPage;
            MyFrame.multiPageManager.currentPage.setBorder(MyToolBar.chosenToolBorder);
            MyFrame.multiPageManager.currentPage.showPage(MyFrame.multiPageManager.parent);
        } else if (btnClicked == MouseEvent.BUTTON3) { //若以右键点击，则显示右键菜单
            rightClickMenu.show(thisPage, 60, 10);
        }
    }
}