package jpaint.shape;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class RoundRect extends AbstractShape {
    @Override
    public void drawAbstractShape(Graphics2D g2d) {
        if (fillFlag) {
            fill(g2d);
        }
        g2d.setPaint(penColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);

    }
    @Override
    public boolean intersects(int xx,int yy,int ww, int hh){
        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);
        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        System.out.println(xMin + " " + yMin + " " + Math.abs(this.x1 - this.x2) + " " + Math.abs(this.y1 - this.y2));
        RoundRectangle2D temp = new RoundRectangle2D.Double(xMin, yMin, Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);

        boolean flagg = temp.intersects(xx, yy, ww, hh);
        return flagg;
    }
    public boolean contains(int x, int y) {
        //RectangularShape temp=
        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);
        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        System.out.println(xMin + " " + yMin + " " + Math.abs(this.x1 - this.x2) + " " + Math.abs(this.y1 - this.y2));
        RoundRectangle2D temp = new RoundRectangle2D.Double(xMin, yMin, Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);

        //temp=new
        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;
    }

    public void fill(Graphics2D g2d) {
        g2d.setPaint(fillColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(Math.min(x1, x2) + 1, Math.min(y1, y2) + 1, Math.abs(x1 - x2) - 2, Math.abs(y1 - y2) - 2, 50, 35);
    }
}