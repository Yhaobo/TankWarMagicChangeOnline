package controller;

import model.Operation;
import model.entity.Cannonball;
import model.entity.MovableUnit;
import model.entity.Player;
import model.entity.Unit;
import model.network.HostNetworkService;
import model.network.NetworkService;
import model.network.SlaveNetworkService;
import util.Constant;
import view.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public class TankWarOnlineApplication {
    private final List<Unit> unitList = new ArrayList<>();

    private Player player = Player.newPlayer();

    /**
     * 联机服务
     */
    private NetworkService networkService = new NetworkService();

    /**
     * 玩家操作处理类
     */
    private final Operation operation = new Operation();

    private volatile int appStatus = AppStatus.OFFLINE;

    MainFrame frame = new MainFrame("坦克大战魔改联机版", this);

    public static final ScheduledExecutorService SCHEDULED_THREAD_POOL = new ScheduledThreadPoolExecutor(Math.max(1, Runtime.getRuntime().availableProcessors()), new ThreadPoolExecutor.CallerRunsPolicy());

    public TankWarOnlineApplication() {
        unitList.add(player);
    }

    public int getAppStatus() {
        return this.appStatus;
    }

    public Player getPlayer() {
        return this.player;
    }

    public interface AppStatus {
        int OFFLINE = 0;
        int HOST = 1;
        int SLAVE = 2;
    }

    public static void main(String[] args) throws InterruptedException {
        final TankWarOnlineApplication tankWarOnlineApplication = new TankWarOnlineApplication();
        tankWarOnlineApplication.start();
    }

    /**
     * 开始默认单机模式
     */
    private void start() throws InterruptedException {
        long start;
        long end;
        while (appStatus == AppStatus.OFFLINE) {
            start = System.currentTimeMillis();
            step();
            end = System.currentTimeMillis();
            TimeUnit.MILLISECONDS.sleep(Constant.STEP_INTERVAL_TIME - (end - start));
        }

        if (appStatus == AppStatus.HOST) {
            startHostMode();
        } else if (appStatus == AppStatus.SLAVE) {
            startSlaveMode();
        }
    }

    /**
     * 开始从机模式
     */
    private void startSlaveMode() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(Constant.STEP_INTERVAL_TIME);
        final SlaveNetworkService networkService = (SlaveNetworkService) this.networkService;
        for (; ; ) {
            networkService.sendOperation(operation);
            networkService.acceptData(unitList);
            frame.repaint();
        }
    }

    /**
     * 开始主机模式
     */
    private void startHostMode() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(Constant.STEP_INTERVAL_TIME);
        final HostNetworkService networkService = (HostNetworkService) this.networkService;
        long start;
        long end;
        List<Integer> removeUnitIndex;
        long timeout;
        for (; ; ) {
            start = System.currentTimeMillis();

            networkService.acceptAndHandleOperation(unitList);
            removeUnitIndex = step();
            networkService.sendData(unitList, removeUnitIndex);

            end = System.currentTimeMillis();
            timeout = Constant.STEP_INTERVAL_TIME - (end - start);
            if (timeout > 0) {
                TimeUnit.MILLISECONDS.sleep(timeout);
            }

        }
    }

    /**
     * 每一步的处理
     */
    private List<Integer> step() {
        final Cannonball cannonball = operation.handlePlayerAction(this.player);
        if (cannonball != null) {
            unitList.add(cannonball);
        }
        //要删除单位的索引列表
        List<Integer> removeIndex = new ArrayList<>(unitList.size() >> 1);
        for (int i = 0; i < unitList.size(); i++) {
            Unit unit1 = unitList.get(i);
            if (unit1 instanceof MovableUnit) {
                if (((MovableUnit) unit1).getCollisionRadius() < 3) {
                    removeIndex.add(i);
                    continue;
                }
                final MovableUnit movableUnit1 = (MovableUnit) unit1;
                for (int j = i + 1; j < unitList.size(); j++) {
                    Unit unit2 = unitList.get(j);
                    if (unit2 instanceof MovableUnit) {
                        if (MovableUnit.CollisionHandler.inAdvanceDetectionIntersects(movableUnit1, (MovableUnit) unit2)) {
                            //相撞
                            MovableUnit.CollisionHandler.handleCollision((MovableUnit) unit1, (MovableUnit) unit2);
                        }
                    }
                }
                movableUnit1.move();
            }
        }
        for (int index : removeIndex) {
            unitList.remove(index);
        }
        frame.repaint();
        return removeIndex;
    }

    /**
     * 重置数据, 变为从机
     */
    public void switchSlaveMode(String playerId) throws InterruptedException {
        //切换状态
        this.appStatus = AppStatus.SLAVE;
        Thread.sleep(Constant.STEP_INTERVAL_TIME);

        this.unitList.clear();
        this.player.setId(playerId);
    }



    /**
     * 重置数据, 变为主机
     */
    public void switchHostMode() throws InterruptedException {
        //切换状态
        this.appStatus = AppStatus.HOST;
        Thread.sleep(Constant.STEP_INTERVAL_TIME);

        unitList.removeIf(unit -> !(unit instanceof Player));
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }
}
