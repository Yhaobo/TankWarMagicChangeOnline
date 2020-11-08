package model.network.dto;

/**
 * @author Yhaobo
 * @date 2020/10/31
 */
public class VersionedDatagram {
    /**
     * 版本号,从0开始自增 (暂时用不上)
     */
    private long version;

//    public long getVersion() {
//        return version;
//    }
//
//    public void setVersion(long version) {
//        this.version = version;
//    }

    public long increaseVersion() {
        return ++version;
    }
}
