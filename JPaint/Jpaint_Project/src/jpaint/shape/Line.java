package jpaint.shape;

import java.awt.*;

public class Line extends AbstractShape {

    public Line() {}

    @Override
    public void drawAbstractShape(Graphics2D g) {
        g.setColor(penColor);
        //g.setStroke(new BasicStroke(width));
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(x1, y1, x2, y2);
    }
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    public boolean contains(int x, int y) {
        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);
        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        if (y == y1 || y == y2) {
            if ((y == y1 && x == x1) || (y == y2 && x == x2)) {//防止除数等于0
                return true;
            } else {
                return false;
            }
        }
        if (((x1 - x) / (y1 - y)) == ((x2 - x) / (y2 - y))) {//+++这里判断的是小数有点危险
            //if( Math.abs((((x1-x)/(y1-y))-((x2-x)/(y2-y)))) < 0.00001 ){//加大命中率，这个度不好掌握，算了
            //首先得在线段这条直线上
            if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
