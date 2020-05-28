package main;


import javafx.util.Pair;
import main.Dao;
import metaData.*;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class SonOfDao extends Dao {
    private String DBDriver;//定义数据库驱动程序
    private String DBUrl;
    private String user;
    private String passwd;
    private Connection cnn;//定义数据库连接对象
    private Statement stmt;//定义操作数据库对象
    private ResultSet rs;//用于存储查询结果
    private String sql;//定义字符串语句，用于存储SQL语句
    public  boolean addUser(String id, String psd,String nickname){
        //在保证和已有用户不重复的情况下插入新用户（id不允许重复)
        synchronized (this){
            boolean result=true;
            String sql="SELECT * FROM `qq_users`;";
            try {
                ResultSet rs=stmt.executeQuery(sql);
                while(rs.next()){
                    if(rs.getString("id").equals(id)){
                        result=false;
                        System.out.println("和已有用户id重复！");
                        return result;
                    }
                }
                //不和已有的id重名
                //INSERT INTO qq_users(id,password,nickname) VALUES('183374','123456','古力娜扎');
                String sql2="INSERT INTO qq_users(id,password,nickname) VALUES(\'"+id+"\',\'"+psd+"\',\'"+nickname+"\')";
                stmt.executeUpdate(sql2);
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("addUser()函数失败！");
                e.printStackTrace();
            }
            return result;
        }

    }

    public  List<String> getUserListOfList(String id){
        synchronized (this){
            List<String> list=new LinkedList<>();
            //返回好友列表，只需要查询friend_relation表即可，保证不重复,使用HashSet过渡
            HashSet<String> hashSet=new HashSet<>();
            String sql1="SELECT * FROM friend_relation WHERE id1='"+id+"'";
            try {
                ResultSet rs=stmt.executeQuery(sql1);
                while(rs.next()){
                    hashSet.add(rs.getString("id2"));
                }
            } catch (SQLException e) {
                System.out.println("getUserListOfList()函数失败");
                e.printStackTrace();
            }
            for (String temp:hashSet
            ) {
                list.add(temp);
            }

            return list;
        }

    }




    public  boolean addFriend(String sendId, String receiveId){
        //必须先确保sendId和receiveId都在qq_user中
        synchronized (this){
            if(isFriend(sendId,receiveId)){
                return true;
            }else{
                if(!isUser(sendId)){
                    System.out.println("sendId不是正确的用户");
                    return false;
                }
                if(!isUser(receiveId)){
                    System.out.println("receiveId不是正确的用户");
                    return false;
                }
                boolean result=true;
                String sql1="INSERT INTO friend_relation(id1,id2) " +
                        "VALUES('"+sendId+"','"+receiveId+"')";
                String sql2="INSERT INTO friend_relation(id1,id2) " +
                        "VALUES('"+receiveId+"','"+sendId+"')";
                try {
                    stmt.executeUpdate(sql1);
                    stmt.executeUpdate(sql2);
                } catch (SQLException e) {
                    System.out.println("addFriend函数调用失败！");
                    e.printStackTrace();
                }
                return result;
            }
        }
    }
    public boolean isFriend(String sendId,String receiveId){
        synchronized (this){
            List<String> list=this.getUserListOfList(sendId);
            for (String temp:list
            ) {
                if(temp.equals(receiveId)){
                    return true;
                }
            }
            return false;
        }

    }
    public boolean isUser(String str){
        synchronized (this){
            ResultSet rs=null;
            boolean result=false;
            String sql1="SELECT * FROM qq_users";
            try {
                rs=stmt.executeQuery(sql1);
                while(rs.next()){
                    if(rs.getString("id").equals(str)){
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

    }
    public  boolean removeFriend(String sendId, String receiveId){
        synchronized (this){
            boolean result=true;
            //解除sendId和receiveId的朋友关系
            if(!isUser(sendId)){
                return false;
            }
            if(!isUser(receiveId)){
                return false;
            }
            if(!isFriend(sendId,receiveId)){
                return false;
            }
            else{
                String sql="DELETE FROM friend_relation WHERE id1=\'"+sendId+"\'&&id2=\'"+receiveId+"\'";
                String sq2="DELETE FROM friend_relation WHERE id1=\'"+receiveId+"\'&&id2=\'"+sendId+"\'";
                try {
                    stmt.executeUpdate(sql);
                    stmt.executeUpdate(sq2);
                } catch (SQLException e) {
                    System.out.println("removeFriend出错了");
                    e.printStackTrace();
                }

            }
            return result;
        }

    }


    public  void setup(){
        DBDriver="com.mysql.cj.jdbc.Driver";//定义数据库驱动程序
        DBUrl=
                "jdbc:mysql://localhost:3306/wearecoders";
        user="root";
        passwd="asdfghjkl;'";//输入你自己的数据库密码
        try {
            Class.forName(DBDriver);
        }catch (Exception exception){
            System.out.print("数据库驱动程序加载失败"+exception);
        }
        try{
            cnn= DriverManager.getConnection(DBUrl,user,passwd);
            stmt=cnn.createStatement();
        }catch (Exception e){
            System.out.print("数据库连接失败！！"+e);
        }

    }

    public  void teardown(){
        synchronized (this){
            if(this.rs!=null){
                try{
                    rs.close();
                    if(stmt!=null){
                        stmt.close();
                    }
                    if(cnn!=null){
                        cnn.close();
                    }

                }catch (Exception e){
                    System.out.print("关闭数据库失败"+e);
                }
            }
            else{
                try{
                    if(stmt!=null){
                        stmt.close();
                    }
                    if(cnn!=null){
                        cnn.close();
                    }
                }catch (Exception e){
                    System.out.print("关闭数据库失败"+e);
                }
            }
        }
    }

    public  boolean addVarifyMessage(VarifyMessage vm){
        synchronized (this){
            String from_id=vm.getSendId();
            String to_id=vm.getReceiveId();
            String content=vm.getMsg();
            if(!isUser(from_id)){
                return false;
            }
            if(!isUser(to_id)){
                return false;
            }
            String sql="INSERT INTO verify_message(from_id,to_id,content) " +
                    "VALUES(\'"+from_id+"\',\'"+to_id+"\',\'"+content+"\')";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("addVarifyMessage调用出了问题");
                e.printStackTrace();
            }
            return true;
        }
    }
    public  boolean addChatMessage(ChatMessage cm){
        synchronized (this){
            String from_id=cm.getSendId();
            String to_id=cm.getReceiveId();
            String content=cm.getMsg();
            if(!isUser(from_id)){
                return false;
            }
            if(!isUser(to_id)){
                return false;
            }
            String sql="INSERT INTO common_message(from_id,to_id,content) " +
                    "VALUES(\'"+from_id+"\',\'"+to_id+"\',\'"+content+"\')";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("addChatMessage调用出了问题");
                e.printStackTrace();
            }
            return true;
        }
    }

    public  List<ChatMessage> getChatMessageList(String id){
        //拿到id收到的所有离线聊天消息
        synchronized (this){
            List<ChatMessage> list=new LinkedList<>();
            String sql="SELECT * FROM common_message WHERE to_id=\'"+id+"\'";
            try {
                ResultSet rs=stmt.executeQuery(sql);
                while(rs.next()){
                    ChatMessage tempVar=new ChatMessage();
                    tempVar.setDate(rs.getTimestamp("insert_time").getTime());
                    tempVar.setMsg(rs.getString("content"));
                    tempVar.setReceiveId(rs.getString("to_id"));
                    tempVar.setSendId(rs.getString("from_id"));
                    list.add(tempVar);
                }
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("getHistroyMessageList()函数调用出现问题！");
                e.printStackTrace();
            }

            return list;
        }

    }
    public  List<VarifyMessage> getVarifyMessageListOfList(String id){
        //拿到id收到的所有离线验证消息
        synchronized (this){
            LinkedList<VarifyMessage> list=new LinkedList<>();
            String sql="SELECT * FROM verify_message WHERE to_id=\'"+id+"\'";
            try {
                ResultSet rs=stmt.executeQuery(sql);
                while(rs.next()){
                    VarifyMessage tempVar=new VarifyMessage();
                    tempVar.setDate(rs.getTimestamp("insert_time").getTime());
                    tempVar.setMsg(rs.getString("content"));
                    tempVar.setReceiveId(rs.getString("to_id"));
                    tempVar.setSendId(rs.getString("from_id"));
                    list.add(tempVar);
                }
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("getVerifyMessageList()函数调用出现问题！");
                e.printStackTrace();
            }

            return list;
        }
    }

    public boolean isLoginValid(String userId,String password){
        synchronized (this){
            boolean result=false;
            if(!isUser(userId)){
                result=false;
                return result;
            }else{
                String sql="SELECT * FROM qq_users t WHERE t.id='"+userId+"'";
                try {
                    ResultSet rs=stmt.executeQuery(sql);
                    while(rs.next()){
                        if(rs.getString("password").equals(password)){
                            return true;
                        }
                    }
                    if(rs!=null){
                        rs.close();
                    }

                } catch (SQLException e) {
                    System.out.println("isLoginValid方法调用出现了错误");
                    e.printStackTrace();
                }
            }
            return result;
        }

    }

    //new
    public String getUserNickname(String userId){
        synchronized (this){
            String result=null;
            ResultSet rs=null;
            String sql="SELECT u.`nickname` FROM qq_users u WHERE u.`id`=\'"+userId+"\'";
            try {
                rs=this.stmt.executeQuery(sql);
                while(rs.next()){
                    result=rs.getString("nickname");
                }
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("getUserNickname函数失败");
                e.printStackTrace();
            }
            return result;
        }
    }
    public  Vector<Pair<String, String>> getUserList(String id){
        synchronized (this){
            Vector<Pair<String,String>> vec=new Vector<Pair<String, String>>();
            //返回好友列表，只需要查询friend_relation表即可，保证不重复,使用HashSet过渡
            HashSet<String> hashSet=new HashSet<>();
            String sql1="SELECT * FROM friend_relation WHERE id1='"+id+"'";
            try {
                ResultSet rs=stmt.executeQuery(sql1);
                while(rs.next()){
                    hashSet.add(rs.getString("id2"));
                }
            } catch (SQLException e) {
                System.out.println("getUserList()函数失败");
                e.printStackTrace();
            }
            for (String temp:hashSet
            ) {
                vec.add(new Pair<String,String>(temp,this.getUserNickname(temp)));
            }
            return vec;
        }
    }
    public  Vector<ChatMessage> getHistoryMessageList(String id){
        //拿到id收到的所有离线聊天消息
        synchronized (this){
            Vector<ChatMessage> list=new Vector<>();
            String sql="SELECT * FROM common_message WHERE to_id=\'"+id+"\'";
            try {
                ResultSet rs=stmt.executeQuery(sql);
                while(rs.next()){
                    ChatMessage tempVar=new ChatMessage();
//                    tempVar.setDate(rs.getTimestamp("insert_time").getTime());
                    // FIXME
                    tempVar.setDate(new Date().getTime());
                    tempVar.setMsg(rs.getString("content"));
                    tempVar.setReceiveId(rs.getString("to_id"));
                    tempVar.setSendId(rs.getString("from_id"));
                    list.add(tempVar);
                }
                String sql2="DELETE FROM common_message WHERE to_id=\'"+id+"\'";
                stmt.executeUpdate(sql2);
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("getHistroyMessageList()函数调用出现问题！");
                e.printStackTrace();
            }

            return list;
        }
    }
    public  Vector<VarifyMessage> getVarifyMessageList(String id){
        //拿到id收到的所有离线聊天消息
        synchronized (this){
            Vector<VarifyMessage> list=new Vector<>();
            String sql="SELECT * FROM verify_message WHERE to_id=\'"+id+"\'";
            try {
                ResultSet rs=stmt.executeQuery(sql);
                while(rs.next()){
                    VarifyMessage tempVar=new VarifyMessage();
//                    tempVar.setDate(rs.getTimestamp("insert_time").getTime());
                    // FIXME
                    tempVar.setDate(new Date().getTime());
                    tempVar.setMsg(rs.getString("content"));
                    tempVar.setReceiveId(rs.getString("to_id"));
                    tempVar.setSendId(rs.getString("from_id"));
                    list.add(tempVar);
                }
                String sql_="DELETE FROM verify_message WHERE to_id=\'"+id+"\'";
                stmt.executeUpdate(sql_);
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("getVarifyMessageList()函数调用出现问题！");
                e.printStackTrace();
            }
            return list;
        }
    }

    public  void addAcceptVarifyMessage(AcceptVarifyMessage am){
        synchronized(this){
            String sql="INSERT INTO acceptVarifyMessage(send_id,receive_id,STATUS,TIME) " +
                    "VALUES(\'"+am.getSendId()+"\',\'"+am.getReceiveId()+"\',"+am.getStatus()+","+am.getTime()+")";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("addAcceptVarifyMessage()函数调用出现了问题！");
                e.printStackTrace();
            }
        }
    }
    public  void addRemoveFriendMessage(RemoveFriendMessage rm){
        synchronized (this){
            String sql="INSERT INTO removefriendmessage(send_id,receive_id,TIME) VALUES(\'"+rm.getSendId()+"\',\'"+rm.getReceiveId()+"\',"+rm.getTime()+");";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("addRemoveFriendMessage()函数调用出现了问题！");
                e.printStackTrace();
            }
        }
    }
    public  Vector<AcceptVarifyMessage> getAcceptVarifyMessage(String id){
        synchronized (this){
            Vector<AcceptVarifyMessage> vec=new Vector<AcceptVarifyMessage>();
            ResultSet rs=null;
            String sql="SELECT * FROM acceptVarifyMessage a WHERE a.`receive_id`=\'"+id+"\'";
            try {
                rs=stmt.executeQuery(sql);
                while(rs.next()){
                    AcceptVarifyMessage temp=new AcceptVarifyMessage();
                    temp.setSendId(rs.getString("send_id"));
                    temp.setReceiveId(rs.getString("receive_id"));
                    temp.setStatus(rs.getInt("status"));
                    temp.setTime(rs.getLong("time"));
                    vec.add(temp);
                }
                if(rs!=null){
                    rs.close();
                }
                sql="DELETE FROM acceptVarifyMessage WHERE receive_id=\'"+id+"\'";
                stmt.executeUpdate(sql);//删除对应receive_id表中记录
            } catch (SQLException e) {
                System.out.println("getAcceptVarifyMessage函数调用出现问题");
                e.printStackTrace();
            }
            return vec;
        }
    }
    public  Vector<RemoveFriendMessage> getRemoveFriendMessage(String id){
        synchronized (this){
            Vector<RemoveFriendMessage> vec=new Vector<RemoveFriendMessage>();
            ResultSet rs=null;
            String sql="SELECT * FROM removefriendmessage a WHERE a.`receive_id`=\'"+id+"\'";
            try {
                rs=stmt.executeQuery(sql);
                while(rs.next()){
                    RemoveFriendMessage temp=new RemoveFriendMessage();
                    temp.setSendId(rs.getString("send_id"));
                    temp.setReceiveId(rs.getString("receive_id"));
                    temp.setTime(rs.getLong("time"));
                    vec.add(temp);
                }
                if(rs!=null){
                    rs.close();
                }
                sql="DELETE FROM removefriendmessage WHERE receive_id=\'"+id+"\'";
                stmt.executeUpdate(sql);//删除对应receive_id表中记录
            } catch (SQLException e) {
                System.out.println("getRemoveFriendMessage函数调用出现问题");
                e.printStackTrace();
            }
            return vec;
        }
    }
}
