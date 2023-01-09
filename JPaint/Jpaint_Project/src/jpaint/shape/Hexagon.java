package jpaint.shape;

import java.awt.*;
import java.awt.geom.Point2D;

public class Hexagon extends AbstractShape {
    int[] arrayX;
    int[] arrayY;
    public Hexagon() {
        super();
        updateArrays();
    }

    @Override
    public void drawAbstractShape(Graphics2D g2d) {
        updateArrays();
        if (fillFlag) {
            fill(g2d);
        }
        g2d.setPaint(penColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawPolygon(arrayX, arrayY, 6);

    }
    public boolean intersects(int xx,int yy,int ww, int hh){
        Polygon temp = new Polygon(arrayX, arrayY, 6);

        boolean flagg = temp.intersects(xx, yy, ww, hh);
        return flagg;
    }
    public boolean contains(int x, int y) {
        //RectangularShape temp=
        Polygon temp = new Polygon(arrayX, arrayY, 6);
        //temp=new
        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;
    }

    public void updateArrays() {
        arrayX = new int[] {Math.min(x1, x2) + Math.abs(x1 - x2) / 4, Math.min(x1, x2),
                Math.min(x1, x2) + Math.abs(x1 - x2) / 4, Math.max(x1, x2) - Math.abs(x2 - x1) / 4, Math.max(x1, x2),
                Math.max(x1, x2) - Math.abs(x2 - x1) / 4};
        arrayY = new int[] {Math.min(y1, y2), (y1 + y2) / 2, Math.max(y1, y2), Math.max(y1, y2), (y1 + y2) / 2,
                Math.min(y1, y2)};
    }

    public void fill(Graphics2D g2d) {
        g2d.setPaint(fillColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//        int[] tempArrayX = new int[] {Math.min(x1, x2) + Math.abs(x1 - x2) / 4, Math.min(x1, x2) +1,
//                Math.min(x1, x2) + Math.abs(x1 - x2) / 4, Math.max(x1, x2) - Math.abs(x2 - x1) / 4, Math.max(x1, x2) -1,
//                Math.max(x1, x2) - Math.abs(x2 - x1) / 4};
//        int[] tempArrayY = new int[] {Math.min(y1, y2) +1, (y1 + y2) / 2, Math.max(y1, y2) -1, Math.max(y1, y2) -1, (y1 + y2) / 2,
//                Math.min(y1, y2) +1};
//        g2d.fillPolygon(tempArrayX, tempArrayY, 6);
        g2d.fillPolygon(arrayX, arrayY, 6);
    }
}