package metaData;

public class AcceptVarifyMessage extends Message {
    String sendId;
    String receiveId;
    int status; // 1 for accepting, 0 for refusing
    long time;

    public AcceptVarifyMessage(String sendId, String receiveId, int status, long time) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.status = status;
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AcceptVarifyMessage() {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
