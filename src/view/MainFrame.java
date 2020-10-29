package view;

import model.Acation;
import model.entity.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * @author Yhaobo
 * @since 2020/10/25
 */
public class MainFrame extends JFrame {
    private MainPanel mainPanel;

    public MainFrame(String title, List<Unit> units, Acation acation) throws HeadlessException {
        super(title);
        mainPanel = new MainPanel(units);
        init( acation);
    }

    /**
     * 设置窗口居中
     *
     * @param frame 目标窗口
     */
    private void setWindowCenter(JFrame frame) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension d = kit.getScreenSize();
        double screenWidth = d.getWidth();
        double screenHeight = d.getHeight();
        int width = frame.getWidth();
        int height = frame.getHeight();
        frame.setLocation((int) (screenWidth - width) / 2, (int) (screenHeight - height) / 3);
    }

    private void init(Acation acation) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.getWidth() * 0.95), (int) (d.getHeight() * 0.95));
        setWindowCenter(this);
        setResizable(false);
        addPanel();
        //关闭窗口的监听
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        addKeyListener(acation);
        pack();
        setVisible(true);
    }

    private void addPanel() {
        this.add(mainPanel, BorderLayout.CENTER);
    }

}
