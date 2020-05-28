package metaData;

public class RemoveFriendMessage extends Message {
    String sendId;
    String receiveId;
    long time;

    public RemoveFriendMessage() {
    }

    public RemoveFriendMessage(String sendId, String receiveId, long time) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.time = time;
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
