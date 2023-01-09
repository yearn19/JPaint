package jpaint.shape;

import java.awt.*;
import java.awt.geom.Point2D;

public class Pentagon extends AbstractShape {
    int[] arrayX;
    int[] arrayY;
    public Pentagon() {
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

        g2d.drawPolygon(arrayX, arrayY, 5);

    }
    public boolean intersects(int xx,int yy,int ww, int hh){
        Polygon temp = new Polygon(arrayX, arrayY, 5);

        boolean flagg = temp.intersects(xx, yy, ww, hh);
        return flagg;
    }

    public boolean contains(int x, int y) {
        //RectangularShape temp=
        Polygon temp = new Polygon(arrayX, arrayY, 5);
        //temp=new
        Point2D p = new Point(x, y);
        boolean res = temp.contains(p);
        return res;
    }

    public void updateArrays() {
        arrayX = new int[] {(x1 + x2) / 2, Math.min(x1, x2), Math.min(x1, x2) + Math.abs(x1 - x2) / 4,
                Math.max(x1, x2) - Math.abs(x1 - x2) / 4, Math.max(x1, x2)};
        arrayY = new int[] {Math.min(y1, y2), (int) (Math.min(y1, y2) + Math.abs(y1 - y2) / 2.5), Math.max(y1, y2),
                Math.max(y1, y2), (int) (Math.min(y1, y2) + Math.abs(y1 - y2) / 2.5)};
    }

    public void fill(Graphics2D g2d) {
        g2d.setPaint(fillColor);
        g2d.setStroke(new BasicStroke(width));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        int[] tempArrayX = arrayX.clone();
//        for (int i = 0; i < tempArrayX.length; i++) {
//            tempArrayX[i] -= 1;
//        }
//        int[] tempArrayY = arrayY.clone();
//        for (int i = 0; i < tempArrayY.length; i++) {
//            tempArrayY[i] -= 1;
//        }
        //这种是硬写方法
//        int[] tempArrayX = new int[] {(x1 + x2) / 2, Math.min(x1, x2)+1, Math.min(x1, x2) + Math.abs(x1 - x2) / 4 +1,
//                Math.max(x1, x2) - Math.abs(x1 - x2) / 4 -1, Math.max(x1, x2)-1};
//        int[] tempArrayY = new int[] {Math.min(y1, y2) +1, (int) (Math.min(y1, y2) + Math.abs(y1 - y2) / 2.5), Math.max(y1, y2) -1,
//                Math.max(y1, y2)-1, (int) (Math.min(y1, y2) + Math.abs(y1 - y2) / 2.5)};
//        g2d.fillPolygon(tempArrayX, tempArrayY, 5);
        g2d.fillPolygon(arrayX, arrayY, 5);
    }
}