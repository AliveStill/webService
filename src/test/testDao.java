package test;

import main.Dao;
import main.SonOfDao;
import metaData.*;

import java.util.Vector;

public class testDao {
    public static void main(String[] args) {
        Dao dao = new SonOfDao();
        dao.setup();
        Vector<ChatMessage> v = dao.getHistoryMessageList("183375");
        for (ChatMessage vm: v) {
            System.out.println("chat");
        }
        Vector<VarifyMessage> v2 = dao.getVarifyMessageList("183375");
        for (VarifyMessage vm: v2) {
            System.out.println("verify");
        }
        Vector<AcceptVarifyMessage> v3 = dao.getAcceptVarifyMessage("183374");
        for (AcceptVarifyMessage vm: v3) {
            System.out.println("accept");
        }
        Vector<RemoveFriendMessage> v4 = dao.getRemoveFriendMessage("183374");
        for (RemoveFriendMessage vm: v4) {
            System.out.println("remove");
        }
        dao.teardown();
        System.out.println("TEST SUCCESS");
    }
}
