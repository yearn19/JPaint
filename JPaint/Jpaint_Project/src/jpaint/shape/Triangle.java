package jpaint.shape;

import java.awt.*;
import java.awt.geom.Point2D;

public class Triangle extends AbstractShape {
    int[] arrayX;
    int[] arrayY;

    public Triangle() {
        super();
        updateArrays();
    }

    public void updateArrays() {
        arrayX = new int[]{(x1 + x2) / 2, x1, x2};
        arrayY = new int[]{y1, y2, y2};
    }
    @Override
    public boolean contains(int x, int y) {
        //RectangularShape temp=
        Polygon temp = new Polygon(arrayX, arrayY, 3);
        //temp=new
        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;
    }

    @Override
    public void drawAbstractShape(Graphics2D g2d) {
        updateArrays();
        if (fillFlag) {
            fill(g2d);
        }
        g2d.setPaint(penColor);
        g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawPolygon(arrayX, arrayY, 3);
//        if (fillFlag) {
//            fill(g2d);
//        }
    }
    @Override
    public boolean intersects(int xx,int yy,int ww, int hh){
        Polygon temp = new Polygon(arrayX, arrayY, 3);

        boolean flagg = temp.intersects(xx, yy, ww, hh);
        return flagg;
    }

    public void fill(Graphics2D g2d) {
        g2d.setPaint(fillColor);
        g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillPolygon(arrayX, arrayY, 3);
    }
}