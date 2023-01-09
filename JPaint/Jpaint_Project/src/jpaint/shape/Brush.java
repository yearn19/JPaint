package jpaint.shape;

import java.awt.*;
import java.util.Random;

public class Brush extends AbstractShape {

    private int[] fx = new int[100];
    private int[] fy = new int[100];

    public Brush() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            fx[i] = random.nextInt(16) - 16;
            fy[i] = random.nextInt(16) - 16;
            //System.out.println("("+fx[i]+","+fy[i]+")");
        }
    }

    @Override
    public void drawAbstractShape(Graphics2D g) {
        g.setPaint(penColor);
        g.setStroke(new BasicStroke(0, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        for (int i = 0; i < 100; i++) {
            double d = (double) fx[i];
            double c = (double) fy[i];
            g.drawLine((int) (x1 + d * Math.sin(d)), (int) (y1 + c * Math.sin(c)), (int) (x2 + d * Math.sin(d)),
                    (int) (y2 + c * Math.sin(c)));
        }
    }
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    public boolean contains(int x,int y){
        return  false;
    }
    @Override
    public void changeSize(int degree, int choice) {//刷子工具不必改变大小

    }
}