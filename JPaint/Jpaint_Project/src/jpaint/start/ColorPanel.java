package jpaint.start;

import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * 调色板
 */
public class ColorPanel extends JPanel {
    /**
     * 这个ColorPanel要放入的MyFrame
     */
    private MyFrame parent;
    private static final long serialVersionUID = 1L;
    JButton penColorButton;
    JButton fillColorButton;

    /**
     * 调色板颜色数组，用来设置按钮的背景颜色
     */
    private Color[] colors = {
        new Color(255, 255, 255), new Color(0, 0, 0), new Color(127, 127, 127), new Color(195, 195, 195),
        new Color(136, 0, 21), new Color(185, 122, 87), new Color(237, 28, 36), new Color(255, 174, 201),
        new Color(255, 127, 39), new Color(255, 242, 0), new Color(239, 228, 176), new Color(34, 117, 76),
        new Color(181, 230, 29), new Color(0, 162, 232), new Color(153, 217, 234), new Color(63, 72, 204),
        new Color(112, 146, 190), new Color(163, 73, 164), new Color(200, 191, 231), new Color(89, 173, 154),
        new Color(8, 193, 194), new Color(9, 253, 76), new Color(153, 217, 234), new Color(199, 73, 4)
    };

    public ColorPanel(MyFrame parent) {
        this.parent = parent;
    }

    public void addColorPanel() {
        //按钮特效，简单的双线斜面边框
        BevelBorder raisedBB = new BevelBorder(BevelBorder.RAISED, Color.gray, Color.white);
        BevelBorder LoweredBB = new BevelBorder(BevelBorder.LOWERED, Color.gray, Color.white);

        //配置主颜色面板
        this.setPreferredSize(new Dimension(340, 60));
        this.setLayout(null);
        this.setBackground(new Color(195, 195, 195));
        //配置主颜色面板的子面板（容纳所有颜色按钮），并添加到主颜色面板中
        JPanel panelDownChild = new JPanel();
        panelDownChild.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        GridBagLayout gridBagLayout = new GridBagLayout();
//        gridBagLayout.columnWidths = new int[] {40, 240, 40};
//        gridBagLayout.rowHeights = new int[] {40};
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridheight = 8;
//        panelDownChild.setLayout(gridBagLayout);
        //---调转了一下宽和高
        panelDownChild.setBounds(10, 10, 320, 40);
        this.add(panelDownChild);

        //当前选中颜色的JPanel
        JPanel selectedColors = new JPanel();
        selectedColors.setBackground(Color.LIGHT_GRAY);
        selectedColors.setLayout(null);
        selectedColors.setBorder(raisedBB);
        //selectedColors.setBounds(0, 0, 40, 40);
        selectedColors.setPreferredSize(new Dimension(40, 40));
        //配置当前笔触颜色的按钮并添加到当前选中颜色的JPanel
        penColorButton = new JButton();
        penColorButton.setBounds(5, 5, 20, 20);
        penColorButton.setBorder(raisedBB);
        penColorButton.setToolTipText("笔触颜色");
        penColorButton.setBackground(Color.BLACK);
        penColorButton.setSize(20, 20);
//        penColorButton.addActionListener(e -> {
//            //拿到被选中按钮的对象
//            JButton jbt = (JButton) e.getSource();
//            //将被选中按钮的背景颜色设为当前选中的笔触颜色
//            MyFrame.color = jbt.getBackground();
//            //MyFrame.abstractShapeList.get(MyFrame.index - 1).color = c;
//        });
        selectedColors.add(penColorButton);
        //配置当前填充颜色的按钮并添加到当前选中颜色的JPanel
        fillColorButton = new JButton();
        fillColorButton.setBorder(LoweredBB);
        fillColorButton.setToolTipText("填充颜色");
        fillColorButton.setBackground(Color.WHITE);
        fillColorButton.setBounds(15, 15, 20, 20);
//        fillColorButton.addActionListener(e -> {
//            //拿到被选中按钮的对象
//            JButton jbt = (JButton) e.getSource();
//            //将被选中按钮的背景颜色设为当前选中的填充颜色
//            MyFrame.fillColor = jbt.getBackground();
//            //MyFrame.abstractShapeList.get(MyFrame.index - 1).color = c;
//        });
        selectedColors.add(fillColorButton);
        //当前选中颜色按钮的JPanel就绪后，添加到主颜色面板中
        panelDownChild.add(selectedColors);

        //候选颜色的JPanel，每种颜色设置一个按钮供选
        JPanel candidateColors = new JPanel();
        candidateColors.setBackground(new Color(195, 195, 195));
        candidateColors.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        //---调转了一下宽和高
        candidateColors.setPreferredSize(new Dimension(240, 40));
        //循环将24个颜色按钮添加到候选颜色的JPanel
        for (int i = 0; i < 24; i++) {
            JButton candidateColorButton = new JButton();
            candidateColorButton.setOpaque(true);
            candidateColorButton.setBackground(colors[i]);
            candidateColorButton.setPreferredSize(new Dimension(20, 20));
            candidateColorButton.setBorder(raisedBB);
//            candidateColorButton.addActionListener(e -> {
//                //拿到被点击按钮的对象
//                JButton jbt = (JButton) e.getSource();
//                //拿到被选中按钮的背景颜色
//                Color c = jbt.getBackground();
//                //把背景颜色赋给给MyFrame中的笔触颜色
//                MyFrame.color = c;
//                //把左面板中的按钮颜色设置成选中按钮的背景颜色
//                penColorButton.setBackground(c);
//                //MyFrame.abstractShapeList.get(MyFrame.index - 1).color = c;
//            });
            candidateColorButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int buttonClicked = e.getButton(); //获得鼠标被点击的按钮
                    Color targetColor = ((JButton)e.getSource()).getBackground(); //获得被选中按钮的背景颜色
                    if (buttonClicked == MouseEvent.BUTTON1) { //若以左键点击，设置笔触颜色
                        MyFrame.penColor = targetColor;
                        penColorButton.setBackground(targetColor);
                    } else if (buttonClicked == MouseEvent.BUTTON3) { //若以右键点击，设置填充颜色
                        MyFrame.fillColor = targetColor;
                        fillColorButton.setBackground(targetColor);
                    }
                }
            });
            candidateColors.add(candidateColorButton);
        }
        //候选颜色按钮的JPanel就绪后，添加到主颜色面板中
        panelDownChild.add(candidateColors);

        //配置一个颜色选择器按钮并添加到主颜色面板中
        JButton colorChooserButton = new JButton();
        colorChooserButton.setIcon(new ImageIcon(getClass().getResource("/image/color_48.png")));
        colorChooserButton.setPreferredSize(new Dimension(40, 40));
        colorChooserButton.setToolTipText("更多颜色（左键点击以设置笔触颜色，右键点击以设置填充颜色）");
        colorChooserButton.addMouseListener(new colorChooserMouseAdapter());
        panelDownChild.add(colorChooserButton);

        //整个颜色面板就绪后，添加到MyFrame中 - 暂调至MyMenu
        //---this.parent.add(this, BorderLayout.WEST);
        //this.getParent();
    }

    /**
     * 选择当前颜色程序段
     */
    public void choosePenColor() {
        MyFrame.penColor = JColorChooser.showDialog(null, "请选择颜色", MyFrame.penColor);
        //把左面板中的按钮颜色设置成选中按钮的背景颜色
        penColorButton.setBackground(MyFrame.penColor);
        //MyFrame.abstractShapeList.get(MyFrame.index - 1).color = MyFrame.color;
    }

    class colorChooserMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Color targetColor = JColorChooser.showDialog(null, "请选择颜色", MyFrame.penColor);
            int buttonClicked = e.getButton(); //获得鼠标被点击的按钮
            if (buttonClicked == MouseEvent.BUTTON1) { //若以左键点击，设置笔触颜色
                MyFrame.penColor = targetColor;
                penColorButton.setBackground(targetColor);
            } else if (buttonClicked == MouseEvent.BUTTON3) { //若以右键点击，设置填充颜色
                MyFrame.fillColor = targetColor;
                fillColorButton.setBackground(targetColor);
            }
        }
    }
}
