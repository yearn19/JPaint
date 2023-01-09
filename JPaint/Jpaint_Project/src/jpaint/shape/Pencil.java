package jpaint.shape;

import java.awt.*;

public class Pencil extends AbstractShape {
    public Pencil() {}

    @Override
    public void drawAbstractShape(Graphics2D g) {
        g.setPaint(penColor);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(x1, y1, x2, y2);
    }
    public boolean intersects(int xx,int yy,int ww, int hh){ return false; }
    public boolean contains(int x, int y) { return false; }
}


