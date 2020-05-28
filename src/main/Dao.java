package main;

import java.util.List;
import java.util.Vector;

import javafx.util.Pair;
import metaData.*;
public abstract class Dao {
    // 申请账号
    public abstract boolean addUser(String id, String psd,String nickname);
    //1.连接数据库2.获取现有所有用户，现有用户没有与id重复，则
    //则执行添加操作，返回true. 如果与现有重复，返回false.               /// impl

    // 返回好友列表，历史消息, 验证消息
    //public abstract List<String> getUserList(String id);                    /// impl
    public abstract List<ChatMessage> getChatMessageList(String id);         /// impl
    //public abstract List<VarifyMessage> getVarifyMessageList(String id);    /// impl

    // 添加好友，发送验证消息，同意验证消息，拒绝验证消息
    public abstract boolean addFriend(String sendId, String receiveId);     /// impl

    // 删除好友
    public abstract boolean removeFriend(String sendId, String receiveId);  /// impl

    // 发送和接收消息

    // 前期工作，打开数据库等
    public abstract void setup();                                           /// impl
    // 清理工作，关闭数据库等
    public abstract void teardown();                                        /// impl
    // 添加一条验证消息到数据库
    public abstract boolean addVarifyMessage(VarifyMessage vm); /// impl
    // 添加一条聊天信息到数据库
    public abstract boolean addChatMessage(ChatMessage cm);                  /// impl
    // newer API
    // 获取用户昵称
    public abstract String getUserNickname(String userId);  // 获取用户昵称，如用户不存在，返回null
    public abstract Vector<Pair<String, String>> getUserList(String id); // 获取好友信息，包括好友id和昵称，id在前，昵称在后
    public abstract Vector<ChatMessage> getHistoryMessageList(String id);// 获取离线消息
    public abstract Vector<VarifyMessage> getVarifyMessageList(String id);// 获取验证消息

    public abstract void addAcceptVarifyMessage(AcceptVarifyMessage am);// 添加一条"同意好友申请"消息
    public abstract void addRemoveFriendMessage(RemoveFriendMessage rm);// 添加一条"解除好友关系"消息
    public abstract Vector<AcceptVarifyMessage> getAcceptVarifyMessage(String id);// 获取。。。
    public abstract Vector<RemoveFriendMessage> getRemoveFriendMessage(String id);// 获取。。。
}
