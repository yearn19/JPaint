package jpaint.shape;

import java.awt.*;
import java.awt.geom.Point2D;

public class FillRect extends AbstractShape {
    @Override
    public void drawAbstractShape(Graphics2D g2d) {
        g2d.setPaint(penColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    public boolean contains(int x, int y) {
        //RectangularShape temp=
        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);
        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        System.out.println(xMin + " " + yMin + " " + Math.abs(this.x1 - this.x2) + " " + Math.abs(this.y1 - this.y2));
        java.awt.Rectangle temp = new java.awt.Rectangle(xMin, yMin, Math.abs(x1 - x2), Math.abs(y1 - y2));

        //temp=new
        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;
    }
}