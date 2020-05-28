// @archived 2020/2/20 11:35
package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.Integer.parseInt;

import javafx.util.Pair;
import metaData.*;

public class Logic extends HttpServlet {
    // useronline map, use special mechanism to do that
    public static Map<String, Long> onlineUserMap = new ConcurrentHashMap<String, Long>();
    // 500ms of every update operation on online status
    // 1000ms for operation delay
    public static final int ONLINE_TIME_GAP = 500;
    // 10s of AJAX long query of time cycle
    public static final int LISTEN_TIME_GAP = 10000;
    // 100ms of every query on listening chatting message
    public static final int CONTINOUS_QUERY_GAP = 100;
    // date access object
    public static SonOfDao dao = new SonOfDao();

    // @note ConcurrentMap and Vector are both thread-safe
    // buffer of chat message
    public static Map<String, Vector<ChatMessage>> chatMessageListenMap = new ConcurrentHashMap<String, Vector<ChatMessage>>();
    // buffer of verify message
    public static Map<String, Vector<VarifyMessage>> varifyMessageListenMap = new ConcurrentHashMap<String, Vector<VarifyMessage>>();
    // buffer of accepting verify message
    public static Map<String, Vector<AcceptVarifyMessage>> acceptVarifyMessageMap = new ConcurrentHashMap<String, Vector<AcceptVarifyMessage>>();
    // buffer of removing friend message
    public static Map<String, Vector<RemoveFriendMessage>>  removeFriendMessageMap = new ConcurrentHashMap<String, Vector<RemoveFriendMessage>>();

    // Constants merely for job distribution
    public static final int RT_RGST = 0;        // register
    public static final int RT_LGIN = 1;        // login
    public static final int RT_LSTN = 2;        // listen
    public static final int RT_CHAT = 3;        // send chat message
    public static final int RT_ADDF = 4;        // add friend
    public static final int RT_ACPT = 5;        // accept friend request
    public static final int RT_RMVF = 6;        // remove friend
    public static final int RT_UPOS = 7;        // update online status
    public static final int RT_TYLG = 8;        // try to login
    public static final int RT_OTHERS = -1;     // other jobs


    // @brief   do some initiation process, and deliver the handler to super class
    // @param   config: some config file object
    // @return
    // @throw   ServletException
    @Override
    public void init(ServletConfig config) throws ServletException {
        dao.setup();
        super.init(config);
    }


    // @brief   do some initiation process, and deliver the handler to super class
    // @param
    // @return
    // @throw   ServletException
    @Override
    public void init() throws ServletException {
        dao.setup();
        super.init();
    }


    // @brief   do some destroy process before the application container shut down
    //              in other words, before the server application shut down
    // @param
    // @return
    @Override
    public void destroy() {
        // TODO， add those unhandled message to database
        dao.teardown();
    }


    // @ThreadSafe
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // job distribution
        switch (parseInt(req.getParameter("RequestType"))) {
            case RT_RGST: processRegister(req, resp); break;
            case RT_LGIN: processLogin(req, resp); break;
            case RT_LSTN: processListen(req, resp); break;
            case RT_CHAT: processChat(req, resp); break;
            case RT_ADDF: processAddFriend(req, resp); break;
            case RT_ACPT: processAcceptFriendRequest(req, resp); break;
            case RT_RMVF: processRemoveFriend(req, resp); break;
            case RT_UPOS: processUpdateOnlineStatus(req, resp); break;
            case RT_TYLG: processTryToLogin(req, resp); break;
            case RT_OTHERS:
            default:
                // error
                System.err.println("Invalid Request Type");
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }


    // @brief   update user's online status based on it's userId and mapping mechanism
    // @param   id of specific user
    // @return
    public void updateOnlineStatus(String userId) {
        onlineUserMap.put(userId, new Date().getTime());
    }


    // @brief   query on if the specific is online
    // @param   id of specific user
    // @return  true: if user is online
    //          false: otherwise
    public boolean isUserOnline(String userId) {
        if (onlineUserMap.get(userId) == null) {
            return false;
        }
        if (new Date().getTime() - onlineUserMap.get(userId) <= ONLINE_TIME_GAP * 2) {
            return true;
        }
        return false;
    }


    // @brief   generate a random id that's not in the database
    // @param
    // @return  a string consisting of 8 Arabic numbers: if generation succeed
    //          null: if generation fails
    public String randomId() {
        String str;
        int cnt = 0;
        do {
            ++ cnt;
            Integer id = (int)(Math.random() * 10000000) + 10000000;
            str = id.toString();
            if (cnt >= 10000) {
                return null;
            }
        } while (dao.isUser(str)); // TODO 当str在不数据库中,可以终止
        return str;
    }


    // @auxiliary refer to randomId() to see its actual part
    // @param   password consisting of 1 to 16 legal numbers
    // @return  @return of randomId()
    public String register(String passwd) {
        return randomId();
    }


    // @brief   process the register and return some XML doc
    // @param   req: HTTP request
    // @param   resp: to be processed response
    // @return
    // @throw  ServletException, IOException
    public void processRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");
        String userId = register(password);
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");        // write header
        if (userId != null) {
            dao.addUser(userId, password, nickname);
            out.println("<status>1</status>");
            out.println("<userId>" + userId + "</userId>");
        } else {
            out.println("<status>0</status>");
        }
        out.println("</SimpleMessage>");        // writer tail
    }


    // @brief   check if login is valid
    // @param   userId: specific id
    //          password: specific password
    // @return  true: if login is valid
    //          false: otherwise
    public boolean isLoginValid(String userId, String password) {
        if (!dao.isUser(userId)) {
            return false;
        }
        if (isUserOnline(userId)) {
            return false;
        }
        return dao.isLoginValid(userId, password);
    }

    // @brief   try to login so that we can be redirected to the main page
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processTryToLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");
        if (isLoginValid(userId, password)) {
            out.println("<status>1</status>");
        } else {
            out.println("<status>0</status>");
        }
        out.println("</SimpleMessage>");
    }


    // @brief   process the login and return some XML doc, including
    //              friend-list, history message, etc.
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");     // set header
        if (isLoginValid(userId, password)) {
            out.println("<status>1</status>");
            out.println("<userInfo>");
            out.println("<id>" + userId + "</id>");
            out.println("<nickname>" + dao.getUserNickname(userId) + "</nickname>");
            out.println("</userInfo>");
            // FIXME Dao operation
            Vector<Pair<String, String>> ap = dao.getUserList(userId);
            out.println("<friendList>");
            for (Pair<String, String> item: ap) {
                out.println("<friend>");
                out.println("<friendId>" + item.getKey() + "</friendId>");
                out.println("<friendNickname>" + item.getValue() + "</friendNickname>");
                out.println("</friend>");
            }
            out.println("</friendList>");
            out.println("<chatMessageList>");
            // FIXME Dao operation
            Vector<ChatMessage> acm = dao.getHistoryMessageList(userId);
            for (ChatMessage cm: acm) {
                out.println("<message>");
                out.println("<fromId>" + cm.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(cm.getSendId()) + "</fromNickname>");
                out.println("<content>" + cm.getMsg() + "</content>");
                out.println("<time>" + cm.getDate() + "</time>");
                out.println("</message>");
            }
            out.println("</chatMessageList>");
            out.println("<varifyMessage>");
            out.println("<friendRequestList>");
            // FIXME Dao operation
            Vector<VarifyMessage> vvm = dao.getVarifyMessageList(userId);
            for (VarifyMessage vm: vvm) {
                out.println("<friendRequest>");
                out.println("<fromId>" + vm.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(vm.getSendId()) +  "</fromNickname>");
                out.println("<time>" + vm.getDate() + "</time>");
                out.println("</friendRequest>");
            }
            out.println("</friendRequestList>");
            out.println("<acceptRequestList>");
            // FIXME Dao operation
            Vector<AcceptVarifyMessage> vam = dao.getAcceptVarifyMessage(userId);
            for (AcceptVarifyMessage am: vam) {
                out.println("<acceptRequest>");
                out.println("<status>" + am.getStatus() + "</status>");
                out.println("<fromId>" + am.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(am.getSendId()) + "</fromNickname>");
                out.println("<time>" + am.getTime() + "</time>");
                out.println("</acceptRequest>");
            }
            out.println("</acceptRequestList>");
            out.println("<deleteFriendList>");
            // FIXME Dao Operation
            Vector<RemoveFriendMessage> vrm = dao.getRemoveFriendMessage(userId);
            for (RemoveFriendMessage rm: vrm) {
                out.println("<deleteFriend>");
                out.println("<fromId>" + rm.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(rm.getSendId()) + "</fromNickname>");
                out.println("<time>" + rm.getTime() + "</time>");
                out.println("</deleteFriend>");
            }
            out.println("</deleteFriendList>");
            out.println("</varifyMessage>");
        } else {
            out.println("<status>0</status>");
        }
        out.println("</SimpleMessage>");    // set tailor
    }


    // @brief   process listening and return some XML doc
    // @details
    // @note    pay attention that if we have some higher level info
    //              then we won't have lower lever info returned
    //              e.g. if we have both VarifyMessage and chat message
    //              then we just return VarifyMessage, for detailed level
    //              info, refer to XML/listen.XML
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processListen(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
        String id = req.getParameter("userId");
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");     // write header
        out.println("<varifyMessage>");
        out.println("<friendRequestList>");
        // FIXME release buffer
        Vector<VarifyMessage> avm = varifyMessageListenMap.remove(id);
        if (avm != null) {
            for (VarifyMessage vm: avm) {
                out.println("<friendRequest>");
                out.println("<fromId>" + vm.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(vm.getSendId()) + "</fromNickname>");
                out.println("<content>" + vm.getMsg() + "</content>");
                out.println("<time>" + vm.getDate() + "</time>");
                out.println("</friendRequest>");
            }
            out.println("</friendRequestList>");
            out.println("</varifyMessage>");
            out.println("</SimpleMessage>");
            return ;
        }
        out.println("</friendRequestList>");
        // FIXME release buffer
        Vector<AcceptVarifyMessage> aam = acceptVarifyMessageMap.remove(id);
        out.println("<acceptRequestList>");
        if (aam != null) {
            for (AcceptVarifyMessage am: aam) {
                out.println("<acceptRequest>");
                out.println("<status>" + am.getStatus() + "</status>");
                out.println("<fromId>" + am.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(am.getSendId()) + "</fromNickname>");
                out.println("<time>" + am.getTime() + "</time>");
                out.println("</acceptRequest>");
            }
            out.println("</acceptRequestList>");
            out.println("</varifyMessage>");
            out.println("</SimpleMessage>");
            return ;
        }
        out.println("</acceptRequestList>");
        // FIXME release buffer
        Vector<RemoveFriendMessage> arm = removeFriendMessageMap.remove(id);
        out.println("<deleteFriendList>");
        if (arm != null) {
            for (RemoveFriendMessage rm: arm) {
                out.println("<deleteFriend>");
                out.println("<fromId>" + rm.getSendId() + "</fromId>");
                out.println("<fromNickname>" + dao.getUserNickname(rm.getSendId()) + "</fromNickname>");
                out.println("<time>" + rm.getTime() + "</time>");
                out.println("</deleteFriend>");
            }
            out.println("</deleteFriendList>");
            out.println("</varifyMessage>");
            out.println("</SimpleMessage>");
            return ;
        }
        out.println("</deleteFriendList>");
        out.println("</varifyMessage>");
        out.println("<chatMessageList>");
        for (int i = 0, lim = LISTEN_TIME_GAP / CONTINOUS_QUERY_GAP; i < lim; ++ i) {
            // FIXME release buffer
            Vector<ChatMessage> acm = chatMessageListenMap.remove(id);
            if (acm != null) {
                for (ChatMessage cm: acm){
                    out.println("<message>");
                    out.println("<fromId>" + cm.getSendId() + "</fromId>");
                    out.println("<fromNickname>" + dao.getUserNickname(cm.getSendId()) + "</fromNickname>");
                    out.println("<content>" + cm.getMsg() + "</content>");
                    out.println("<time>" + cm.getDate() + "</time>");
                    out.println("</message>");
                }
                break;
            }
            try {
                Thread.sleep(CONTINOUS_QUERY_GAP);  // 100ms for sleeping, may cause InterruptedException
            } catch (InterruptedException e) {
                e.printStackTrace();
                out.println("</chatMessageList>");
                out.println("</SimpleMessage>");
                return ;
            }
        }
        out.println("</chatMessageList>");
        out.println("</SimpleMessage>");    // write tailor
    }


    // @brief   process chatting and return some XML doc
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processChat(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String sendId = req.getParameter("sendId");
        String receiveId = req.getParameter("receiveId");
        String content = req.getParameter("content");
        ChatMessage cm = new ChatMessage(sendId, receiveId, content, new Date().getTime());
        if (isUserOnline(receiveId)) {
            // FIXME modify buffer
            Vector<ChatMessage> acm = chatMessageListenMap.get(receiveId);
            if (acm == null) {
                Vector<ChatMessage> vcm = new Vector<ChatMessage>();
                vcm.add(cm);
                chatMessageListenMap.put(receiveId, vcm);
            } else {
                acm.add(cm);
            }
        } else {
            dao.addChatMessage(cm);
        }
        // return text, won't have status == 0 returned,
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");
        out.println("<status>1</status>");
        out.println("</SimpleMessage>");
    }


    // @brief   process adding friend request and return some XML doc
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processAddFriend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String sendId = req.getParameter("sendId");
        String receiveId = req.getParameter("receiveId");
        String content = req.getParameter("content");
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>");
        // friend already exists, have status = false returned
        if (dao.isFriend(sendId, receiveId)) {
            out.println("<status>0</status>");
            out.println("</SimpleMessage>");
            return ;
        }
        VarifyMessage vm = new VarifyMessage(sendId, receiveId, content, new Date().getTime());
        out.println("<status>1</status>");
        if (isUserOnline(receiveId) || true) {
            // FIXME modify buffer
            Vector<VarifyMessage> vvm = varifyMessageListenMap.get(receiveId);
            if (vvm == null) {
                Vector<VarifyMessage> vv = new Vector<VarifyMessage>();
                vv.add(vm);
                varifyMessageListenMap.put(receiveId, vv);
            } else {
                vvm.add(vm);
            }
        } else {
            dao.addVarifyMessage(vm);
        }
        out.println("</SimpleMessage>");
    }


    // @brief   process accepting/declining adding friend request and return some XML doc
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw   ServletException, IOException
    public void processAcceptFriendRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String sendId = req.getParameter("sendId");
        String receiveId = req.getParameter("receiveId");
        int accept = parseInt(req.getParameter("accept"));
        AcceptVarifyMessage am = new AcceptVarifyMessage(sendId, receiveId, accept, new Date().getTime());
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<SimpleMessage>"); // write header
        if (accept == 1) {
            if (isUserOnline(receiveId)) {
                // FIXME modify buffer
                Vector<AcceptVarifyMessage> aam = acceptVarifyMessageMap.get(receiveId);
                if (aam == null) {
                    Vector<AcceptVarifyMessage> vv = new Vector<AcceptVarifyMessage>();
                    vv.add(am);
                    acceptVarifyMessageMap.put(receiveId, vv);
                } else {
                    aam.add(am);
                }
            } else {
                // FIXME Dao operation
                dao.addAcceptVarifyMessage(am);
            }
            dao.addFriend(sendId, receiveId);
            dao.addFriend(receiveId, sendId);
            out.println("<status>1</status>");
            out.println("<acceptFriendRequest>");
            out.println("<fromId>" + receiveId + "</fromId>");
            out.println("<fromNickname>" + dao.getUserNickname(receiveId) + "</fromNickname>");
            out.println("<time>" + new Date().getTime() + "</time>");
            out.println("</acceptFriendRequest>");
        } else {
            if (isUserOnline(receiveId)) {
                // FIXME modify buffer
                Vector<AcceptVarifyMessage> aam = acceptVarifyMessageMap.get(receiveId);
                if (aam == null) {
                    Vector<AcceptVarifyMessage> vv = new Vector<AcceptVarifyMessage>();
                    vv.add(am);
                    acceptVarifyMessageMap.put(receiveId, vv);
                } else {
                    aam.add(am);
                }
            } else {
                dao.addAcceptVarifyMessage(am);
            }
            out.println("<status>0</status>");
        }
        out.println("</SimpleMessage>");    // write tailor
    }

    // @brief   process removing friend request and return some XML doc
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw ServletException, IOException
    public void processRemoveFriend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String sendId = req.getParameter("sendId");
        String receiveId = req.getParameter("receiveId");
        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        RemoveFriendMessage rm = new RemoveFriendMessage(sendId, receiveId, new Date().getTime());
        out.println("<SimpleMessage>"); // write header
        if (!dao.isFriend(sendId, receiveId)) {
            out.println("<status>0</status>");
            out.println("</SimpleMessage>");
            return ;
        } else {
            dao.removeFriend(sendId, receiveId);
            dao.removeFriend(receiveId, sendId);
            if (isUserOnline(receiveId)) {
                Vector<RemoveFriendMessage> arm = removeFriendMessageMap.get(receiveId);
                if (arm == null) {
                    Vector<RemoveFriendMessage> vv = new Vector<RemoveFriendMessage>();
                    vv.add(rm);
                    removeFriendMessageMap.put(receiveId, vv);
                } else {
                    arm.add(rm);
                }
            } else {
                dao.addRemoveFriendMessage(rm);
            }
            out.println("<status>1</status>");
        }
        out.println("</SimpleMessage>");
    }


    // @brief   update online status using mapping mechanism
    // @param   req: HTTP request
    //          resp: to be processed response
    // @return
    // @throw ServletException, IOException
    void processUpdateOnlineStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        updateOnlineStatus(req.getParameter("userId"));
    }
}


