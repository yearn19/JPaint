package jpaint.start;

/**
 * 启动界面
 */
public class StartProject {
    public static MyFrame wds;
    public static void main(String[] args) {
//		try {
//			//调用Windows的文件系统
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//
//		}
        wds = new MyFrame("Jpaint");
    }
}