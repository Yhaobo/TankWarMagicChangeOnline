package view;

import controller.TankWarOnlineApplication;
import model.network.HostNetworkService;
import model.network.SlaveNetworkService;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    private final TankWarOnlineApplication application;

    private final ScheduledExecutorService scheduledThreadPool = TankWarOnlineApplication.SCHEDULED_THREAD_POOL;

    public MainFrame(String title, TankWarOnlineApplication application) throws HeadlessException {
        super(title);
        mainPanel = new MainPanel(application);
        this.application = application;
        init();
    }

    private void init() {
        setResizable(false);
        addPanel();
        //关闭窗口事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        packAndSetLocationRelativeToCenter();
        setVisible(true);
        requestFocus();
        mainPanel.requestFocus();
    }

    private void addPanel() {
        this.add(mainPanel, BorderLayout.CENTER);

        //右侧功能区
        final JPanel rightSidePanel = new JPanel();
        rightSidePanel.setBackground(Color.LIGHT_GRAY);
        rightSidePanel.setPreferredSize(new DimensionUIResource(100, this.getHeight()));
        rightSidePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));

        JButton discoverHostBtn = new JButton("寻找房间");

        final DefaultListModel<String> hosts = new DefaultListModel<>();
        final JList<String> hostList = new JList<>(hosts);
        hostList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        discoverHostBtn.addActionListener(e -> {
            //点击寻找房间按钮后
            scheduledThreadPool.execute(() -> {
                hosts.clear();
                try {
                    Set<InetAddress> hostAddressList = application.getNetworkService().discoverHost();
                    for (InetAddress inetAddress : hostAddressList) {
                        hosts.addElement(inetAddress.getHostAddress());
                    }
                } catch (SocketException | UnknownHostException socketException) {
                    socketException.printStackTrace();
                }
            });
        });
        hostList.addListSelectionListener(e -> {
            //选择房间后
            final String selectValue = hostList.getSelectedValue();
            if (e.getValueIsAdjusting() && selectValue != null) {
                System.out.println("选择房间号为: " + selectValue);
                int result = JOptionPane.showConfirmDialog(null, "确定进入此房间 (" + selectValue + ") ?", "询问", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    scheduledThreadPool.execute(() -> {
                        try {
                            final SlaveNetworkService slaveNetworkService = new SlaveNetworkService();
                            application.setNetworkService(slaveNetworkService);
                            final String playerId = slaveNetworkService.connectHost(InetAddress.getByName(selectValue));
                            if (!playerId.isEmpty()) {
                                rightSidePanel.setVisible(false);
                                packAndSetLocationRelativeToCenter();
                                application.switchSlaveMode(playerId);
                                mainPanel.requestFocus();
                                setTitle(getTitle() + "（联机模式：从机）");
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            }
            hostList.clearSelection();
        });

        final JButton createRoomBtn = new JButton("创建房间");
        createRoomBtn.addActionListener((event) -> {
            if (!(application.getNetworkService() instanceof HostNetworkService)) {
                //启动主机网络服务
                try {
                    final HostNetworkService hostNetworkService = new HostNetworkService();

                    application.setNetworkService(hostNetworkService);
                    rightSidePanel.setVisible(false);
                    packAndSetLocationRelativeToCenter();
                    mainPanel.requestFocus();
                    scheduledThreadPool.execute(() -> {
                        try {
                            application.switchHostMode();
                            setTitle(getTitle() + "（联机模式：主机）");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    //监听连接端口
                    hostNetworkService.listenConnect();
                    scheduledThreadPool.execute(() -> {
                        hostNetworkService.respondConnect(scheduledThreadPool, application.getUnitList());
                    });
                } catch (UnknownHostException | SocketException e) {
                    JOptionPane.showMessageDialog(null, "创建房间失败", "提示", JOptionPane.OK_CANCEL_OPTION);
                }
            }
        });

        rightSidePanel.add(discoverHostBtn);
        rightSidePanel.add(hostList);
        rightSidePanel.add(createRoomBtn);
        this.add(rightSidePanel, BorderLayout.EAST);

        //下方提示区
        JPanel noticePanel = new JPanel();
        noticePanel.setBackground(Color.LIGHT_GRAY);
        JLabel label = new JLabel();
        label.setFont(new Font("楷体", Font.BOLD, 20));
        label.setText("操作说明: 按【W】【A】【S】【D】来控制单位，按【J】【K】【L】射击");
        noticePanel.add(label);
        this.add(noticePanel, BorderLayout.SOUTH);
    }

    private void packAndSetLocationRelativeToCenter() {
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
