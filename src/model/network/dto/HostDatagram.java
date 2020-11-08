package model.network.dto;

import controller.TankWarOnlineApplication;
import model.entity.Unit;

import java.util.List;

/**
 * 主机端的数据报文
 *
 * @author Yhaobo
 * @date 2020/10/31
 */
public class HostDatagram extends VersionedDatagram {
    private List<Unit> units;
    /**
     * 在{@link TankWarOnlineApplication#unitList}中被删除单位的索引数组(每次同步都会重置)
     */
    private Integer[] deleteUnitIndexList;

    public HostDatagram() {
    }

    public HostDatagram(List<Unit> units,Integer[] deleteUnitIndexList) {
        this.units = units;
        this.deleteUnitIndexList = deleteUnitIndexList;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public Integer[] getDeleteUnitIndexList() {
        return deleteUnitIndexList;
    }

    public void setDeleteUnitIndexList(Integer[] deleteUnitIndexList) {
        this.deleteUnitIndexList = deleteUnitIndexList;
    }

    @Override
    public String toString() {
        return "HostDatagram{" +
                "units=" + units +
//                ", synchronizedCode=" + getVersion() +
                '}';
    }
}
