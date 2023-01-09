package jpaint.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 * 抽象父类，所有图形类均要继承该类
 */
public abstract class AbstractShape implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * 绘制图形的坐标
     */
    public int x1, y1, x2, y2;
    /**
     * 笔触颜色
     */
    public Color penColor;
    /**
     *画笔粗细
     */
    public int width;
    /**
     *铅笔或橡皮擦的笔迹长度
     */
    public int length;
    /**
     *存放待打开图片
     */
    transient public BufferedImage image;
    /**
     *绘画的画板
     */
    public JPanel board;
    /**
     *字体大小
     */
    public int fontSize;
    /**
     *字体
     */
    public String fontName;
    /**
     *文本
     */
    public String s;
    /**
     *粗体
     */
    public int boldFlag;
    /**
     *斜体
     */
    public int italicFlag;
    /**
     *绘制时是否填充
     */
    public boolean fillFlag;//+++是true就填充
    /**
     *填充颜色
     */
    public Color fillColor;//+++填充颜色
    /**
     * 绘制
     * @param g 画笔
     */
    public abstract void drawAbstractShape(Graphics2D g);

    public abstract boolean intersects(int xx,int yy,int ww, int hh); //相交函数，是否包含当前橡皮檫

    public abstract boolean contains(int x, int y);
    public void changeSize(int degree, int choice) {//修改  改变图形的大小
        int temp_x,temp_y;//变化后的点
        int changedWidth,changedHeight;//变换后的边长
        int maxSize=700;
        int minSize=30;
        if(choice==1){//放大
            if(this.x1>this.x2){//找到位于右侧的一个点，如果该点是x1
                temp_x= (int) (this.x2+Math.round((this.x1-this.x2)*(1+degree/10.0)));
                if(this.y1>this.y2){//说明x1,y1位于右下角，则让他俩按照比例变化
                    temp_y=(int) (this.y2+Math.round((this.y1-this.y2)*(1+degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x2);
                    changedHeight=Math.abs(temp_y-y2);
                    if(changedWidth<maxSize&&changedHeight<maxSize){
                        this.x1=temp_x;
                        this.y1=temp_y;
                    }
                }else{//说明x1,y1位于右上角，此时要变化x1,y2
                    temp_y=(int) (this.y1+Math.round((this.y2-this.y1)*(1+degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x2);
                    changedHeight=Math.abs(temp_y-y1);
                    if(changedWidth<maxSize&&changedHeight<maxSize){
                        this.x1=temp_x;
                        this.y2=temp_y;
                    }
                }
            }
            else{//位于右侧的点是x2
                temp_x=(int) (this.x1+Math.round((this.x2-this.x1)*(1+degree/10.0)));
                if(this.y1>this.y2){//说明x2,y2位于右上角，x1,y1位于左下
                    temp_y= (int) (this.y2+Math.round((this.y1-this.y2)*(1+degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x1);
                    changedHeight=Math.abs(temp_y-y2);
                    if(changedWidth<maxSize&&changedHeight<maxSize){
                        this.x2=temp_x;
                        this.y1=temp_y;
                    }
                }else{//说明x2,y2位于右下
                    temp_y=(int) (this.y1+Math.round((this.y2-this.y1)*(1+degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x1);
                    changedHeight=Math.abs(temp_y-y1);
                    if(changedWidth<maxSize&&changedHeight<maxSize){
                        this.x2=temp_x;
                        this.y2=temp_y;
                    }
                }
            }
        }
        if(choice==0){//缩小
            if(this.x1>this.x2){//找到位于右侧的一个点，如果该点是x1
                temp_x=(int) (this.x2+Math.round((this.x1-this.x2)*(1-degree/10.0)));
                if(this.y1>this.y2){//说明x1,y1位于右下角，则让他俩按照比例变化
                    temp_y=(int) (this.y2+Math.round((this.y1-this.y2)*(1-degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x2);
                    changedHeight=Math.abs(temp_y-y2);
                    if(changedWidth>minSize&&changedHeight>minSize){
                        this.x1=temp_x;
                        this.y1=temp_y;
                    }
                }else{//说明x1,y1位于右上角，此时要变化x1,y2
                    temp_y=(int) (this.y1+Math.round((this.y2-this.y1)*(1-degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x2);
                    changedHeight=Math.abs(temp_y-y1);
                    if(changedWidth>minSize&&changedHeight>minSize){
                        this.x1=temp_x;
                        this.y2=temp_y;
                    }
                }
            }
            else{//位于右侧的点是x2
                temp_x= (int) (this.x1+Math.round((this.x2-this.x1)*(1-degree/10.0)));
                if(this.y1>this.y2){//说明x2,y2位于右上角，x1,y1位于左下
                    temp_y=(int) (this.y2+Math.round((this.y1-this.y2)*(1-degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x1);
                    changedHeight=Math.abs(temp_y-y2);
                    if(changedWidth>minSize&&changedHeight>minSize){
                        this.x2=temp_x;
                        this.y1=temp_y;
                    }
                }else{//说明x2,y2位于右下
                    temp_y=(int) (this.y1+Math.round((this.y2-this.y1)*(1-degree/10.0)));
                    changedWidth=Math.abs(temp_x-this.x1);
                    changedHeight=Math.abs(temp_y-y1);
                    if(changedWidth>minSize&&changedHeight>minSize){
                        this.x2=temp_x;
                        this.y2=temp_y;
                    }
                }
            }
        }
    }

}