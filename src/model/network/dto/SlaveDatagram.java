package model.network.dto;

import model.Operation;

/**
 * 从机端的数据报文
 *
 * @author Yhaobo
 * @date 2020/10/31
 */
public class SlaveDatagram extends VersionedDatagram {
    private Operation operation;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "SlaveDatagram{" +
                "operation=" + operation +
                '}';
    }
}
