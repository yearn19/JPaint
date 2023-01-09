package jpaint.shape;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
//import static sun.swing.MenuItemLayoutHelper.max;

public class Images extends AbstractShape implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     *存放待打开图片的高度，为缩放准备
     */
    int Height =0;
    /**
     *存放待打开图片的宽度
     */
    int Width =0;
    int flag=0;//等于0说明没有初始化
    //byte[] IbyteArray = new byte[1024];
    //用于数组储存
    byte[] IbyteArray;
    //修改
    public void size(){//如果图片当前大小大于画板尺寸，必需做出调整 1200 800，实测目前1125，625左右
        Height = this.image.getHeight();
        Width = this.image.getWidth();
        double H_size = (double)Height / 600 ;
        double W_size = (double)Width / 1100;
        double n = Math.max(H_size,W_size);
        if(n>1){
            Height = (int)((double)Height/n);
            Width = (int)((double)Width/n);
        }
        flag=1;//初始化结束
    }
    @Override
    public void drawAbstractShape(Graphics2D g) {
        if(image == null){//+++就写在这里了
            image = byte2image(IbyteArray);
        }
        else{
            IbyteArray = image2byte(image);
        }
        if(flag==0)size();
        //System.out.println("绘图所用"+this.Width+" "+this.Height);
        x2=x1+Width;
        y2=y1+Height;
        g.drawImage(image, x1, y1, this.Width, this.Height, board);
    }
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    public boolean contains(int x,int y){

        java.awt.Rectangle temp = new java.awt.Rectangle(x1, y1, x1 +Width, y1 + Height);

        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;

    }
    @Override
    public void changeSize(int degree, int choice) {//修改
        System.out.println(this.Width+" "+this.Height);
        int temp_h,temp_w;
        int maxSize=1000;
        int minSize=80;
        if(choice==1){//放大
            temp_h=(int) (this.Height*(1+degree/10.0));
            temp_w=(int) (this.Width*(1+degree/10.0));
            if(temp_h<maxSize&&temp_w<maxSize){
                this.Height=temp_h;
                this.Width=temp_w;
            }
        }
        if(choice==0){//缩小
            temp_h=(int) (this.Height*(1-degree/10.0));
            temp_w=(int) (this.Width*(1-degree/10.0));
            if(temp_h>minSize&&temp_w>minSize){
                this.Height=temp_h;
                this.Width=temp_w;
            }
        }
        //System.out.println(this.Width+" "+this.Height);
    }


    //byte to image
    public BufferedImage byte2image(byte[] byteArray ){
        //byte[] byteArray = new byte[1024];
// 文档：ByteArrayInputStream(byte[] buf)
// 文档：ByteArrayInputStream(byte[] buf, int offset, int length)
        try {
            InputStream buffIn = new ByteArrayInputStream(byteArray, 0, byteArray.length);
            BufferedImage bufferedImage = ImageIO.read(buffIn);
            //Image image = (Image) bufferedImage;
            //System.out.println(image.getGraphics());
            return bufferedImage;
        }
        catch (IOException ex1) {
            System.out.println("byte2image error");
            ex1.printStackTrace();
            return null;
        }
    }

    public byte[] image2byte(BufferedImage temp ){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(temp, "png", bos);
            return bos.toByteArray();
        }
        catch (IOException ex1) {
            System.out.println("image2byte error");
            ex1.printStackTrace();
            return null;
        }
    }
}