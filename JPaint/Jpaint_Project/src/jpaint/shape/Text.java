package jpaint.shape;

import java.awt.*;

public class Text extends AbstractShape {
    @Override
    public void drawAbstractShape(Graphics2D g) {
        //Font(String name, int style, int size)
        g.setColor(penColor);
        g.setFont(new Font(fontName, italicFlag + boldFlag, fontSize));
        if (s != null) {
            g.drawString(s, x1, y1);
            System.out.println(fontName + "  " + s + " " + fontSize + " " + x1 + " " + y1);
        }
    }

    public boolean contains(int x, int y) {
        if (s != null && y <= y1 + 25 && y >= y1 - 25 && (x <= x1 + s.length() + 30 && x > x1)) return true;
        else return false;
    }
    @Override
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    @Override
    public void changeSize(int degree, int choice) {  //字体的放大缩小
        if(choice==1&&this.fontSize<30)this.fontSize++;
        else if (choice==0&&this.fontSize>5) {
            this.fontSize--;
        }
    }
}