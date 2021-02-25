package controller;

import model.Constant;
import model.Operation;
import model.entity.*;
import model.interaction.CollisionSimulationTool;
import model.interaction.GravitationSimulationTool;
import model.network.HostNetworkService;
import model.network.NetworkService;
import model.network.SlaveNetworkService;
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

    private Player player;

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

    private void generatePlayer() {
        player = Player.newPlayer();
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
        tankWarOnlineApplication.generateBlackHole();
        tankWarOnlineApplication.generatePlayer();
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

        System.out.println("开始从机模式");
        for (; ; ) {
            networkService.acceptData(this);
            networkService.sendOperation(operation);
            frame.repaint();
        }
    }

    /**
     * 开始主机模式
     */
    private void startHostMode() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(Constant.STEP_INTERVAL_TIME);
        final HostNetworkService networkService = (HostNetworkService) this.networkService;
        long startTime;
        List<Integer> removeUnitIndex;
        long spendTime;
        System.out.println("开始主机模式");

        for (; ; ) {
            startTime = System.currentTimeMillis();

            networkService.acceptAndHandleOperation(unitList, Constant.STEP_INTERVAL_TIME);
            removeUnitIndex = step();
            networkService.sendData(unitList, removeUnitIndex);
            //从后往前删除被标记的单位
            for (int i = removeUnitIndex.size() - 1; i >= 0; i--) {
                unitList.remove((int) removeUnitIndex.get(i));
            }
            spendTime = System.currentTimeMillis() - startTime;
            if (spendTime < Constant.STEP_INTERVAL_TIME) {
                TimeUnit.MILLISECONDS.sleep(Constant.STEP_INTERVAL_TIME - spendTime);
            }
        }
    }

    /**
     * 每一步的处理
     *
     * @return 被删除单位的下标索引集合
     */
    private List<Integer> step() {
        //处理玩家操作
        final Cannonball cannonball = operation.applyPlayerAction(this.player);
        if (cannonball != null) {
            unitList.add(cannonball);
        }
        //声明 被删除单位的下标索引集合
        List<Integer> removeIndex = new ArrayList<>(unitList.size() >> 1);
        //遍历所有单位
        for (int index = 0; index < unitList.size(); index++) {
            Unit unit1 = unitList.get(index);
            //处理可碰撞单位
            if (unit1 instanceof CollisionableUnit) {
                final CollisionableUnit collisionableUnit1 = (CollisionableUnit) unit1;
                //标记质量小于等于0的单位
                if (collisionableUnit1.getMass() <= 0) {
                    //记录被删除单位的下标索引
                    removeIndex.add(index);
                    continue;
                }
                //处理与其他可碰撞单位的互相影响(两个单位之间只处理一次)
                for (int j = index + 1; j < unitList.size(); j++) {
                    Unit unit2 = unitList.get(j);
                    if (unit2 instanceof CollisionableUnit) {
                        //万有引力
                        GravitationSimulationTool.handleGravitation(collisionableUnit1, (CollisionableUnit) unit2);
                        //碰撞
                        CollisionSimulationTool.handleCollision(collisionableUnit1, (CollisionableUnit) unit2);
                    }
                }
                //移动
                if (unit1 instanceof MovableUnit) {
                    ((MovableUnit) collisionableUnit1).move();
                }
            }
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

        //密度值置零表示此player待替换
        this.player.setDensity(0);
        //此id用于从主机传来的数据中找到对应的player，然后赋值给 controller.TankWarOnlineApplication.player
        this.player.setId(playerId);
    }

    /**
     * 重置数据, 变为主机
     */
    public void switchHostMode() throws InterruptedException {
        //切换状态
        this.appStatus = AppStatus.HOST;
        Thread.sleep(Constant.STEP_INTERVAL_TIME);

        unitList.clear();
        generatePlayer();
        generateBlackHole();
    }

    /**
     * 生成黑洞
     */
    private void generateBlackHole() {
        int blackHoleTotal;
        double random = Math.random();
        if (random <= 0.7) {
            blackHoleTotal = 1;
        } else if (random < .99) {
            blackHoleTotal = 2;
        } else {
            blackHoleTotal = 3;
        }
        for (int i = 0; i < blackHoleTotal; i++) {
            BlackHole blackHole = new BlackHole(BlackHole.randomPosition(), Math.round(Math.random() * 10 + 5), Constant.BlackHole.DENSITY);
            unitList.add(blackHole);
        }
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
