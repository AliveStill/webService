package metaData;

import java.util.Date;

public class VarifyMessage extends Message {
    String sendId;
    String receiveId;
    String msg;
    long date;

    public VarifyMessage() {
    }

    public VarifyMessage(String sendId, String receiveId, String msg) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.msg = msg;
    }

    public VarifyMessage(String sendId, String receiveId, String msg, long date) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.msg = msg;
        this.date = date;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}