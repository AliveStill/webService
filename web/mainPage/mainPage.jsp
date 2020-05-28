<%--
  Created by IntelliJ IDEA.
  User: mac
  Date: 2020/5/25
  Time: 09:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId = request.getParameter("userId");
    String password = request.getParameter("password");
%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        * {
            box-sizing: border-box;
        }
        body {
            margin: 0;
            padding: 0;
            background: #494A5F;
            font-family: "Microsoft YaHei","宋体","Segoe UI", "Lucida Grande", Helvetica, Arial,sans-serif, FreeSans, Arimo;
        }
        .main_page {
            position: relative;
            margin: 5% auto auto;
            height: 600px;
            width: 1000px;
            background-color: #fbe2a0;
            border-radius: 5px;
            overflow: hidden;
        }
        .left {
            position: absolute;
            left: 0px;
            top: 0px;
            height: 600px;
            width: 200px;
            background-color: white;
            overflow: auto;
        }
        .left::-webkit-scrollbar {
            width: 1px;
            height: 100px;
            background-color: peru;
        }
        .right {
            position: absolute;
            left: 200px;
            top: 0px;
            height: 600px;
            width: 800px;
            background-color: #66CCFF;
            border-left: 2px solid gray;    /* gray border */
        }
        .info {
            height: 50px;
            width: 200px;
            background-color: #D9DFDB;
            overflow: auto;
        }
        .info>.user_image {
            position: absolute;
            height: 50px;
            width: 50px;
            background-color: #D9DFDB;
        }
        .info>.user_image>.user_profile {
            position: absolute;
            top: 5%;
            left: 5%;
            height: 45px;
            width: 45px;
            border-radius: 100%;
        }
        .info>.user_id_prefix {
            position: absolute;
            top: 0px;
            /*bottom: 25px;*/
            left: 50px;
            height: 25px;
            width: 40px;
            background-color: #D9DFDB;
            font-size: 15px;
            line-height: 25px;
            text-align: center;
        }
        .info>.user_id {
            /*temporarily left blank*/
            position: absolute;
            top: 0;
            right: 0;
            background: #D9DFDB;
            height: 25px;
            width: 110px;
            padding-left: 5px;
            font-size: 15px;
            line-height: 25px;
        }
        .info>.user_nickname_prefix {
            position: absolute;
            top: 25px;
            /*bottom: 0px;*/
            left: 50px;
            height: 25px;
            width: 40px;
            background-color: #D9DFDB;
            font-size: 15px;
            line-height: 25px;
            text-align: center;
        }
        .info>.user_nickname {
            position: absolute;
            top: 25px;
            right: 0;
            background: #D9DFDB;
            height: 25px;
            width: 110px;
            padding-left: 5px;
            font-size: 15px;
            line-height: 25px;
        }
        .add_friend {
            position: relative;
            height: 60px;
            width: 200px;
            background-color: white;
        }
        .verify_msg {
            position: relative;
            height: 50px;
            width: 200px;
            background-color: white;
            font-size: 20px;
            line-height: 18px;
        }
        .verify_msg>.vfb {
            position: absolute;
            left: 65px;
            top: 15px;
            height: 20px;
            width: 70px;
            background-color: #7ccdef;
            color: white;
            border-radius: 5px;
        }
        .verify_msg>.vfb:hover {
            background-color: #1E90FF;
            color: white;
        }
        .friend_item {
            position: relative;
            height: 50px;
            width: 200px;
            background-color: white;
        }
        .friend_item>.fi_image {
            /* works only when father use relative as property */
            position: absolute;
            left: 0px;
            top: 0px;
            height: 50px;
            width: 50px;
            background-color: white;
            overflow: hidden;
        }
        .friend_item>.fi_image>.fi_profile {
            position: absolute;
            top: 5%;
            left: 5%;
            height: 45px;
            width: 45px;
            border-radius: 100%;
        }
        .friend_item>.fi_info {
            position: absolute;
            left: 50px;
            top: 0px;
            height: 50px;
            width: 100px;
            background-color: white;
            overflow: hidden;
        }
        .friend_item>.fi_info>.fi_nickname {
            height: 25px;
            width: 100px;
            overflow: hidden;
            background-color: white;
        }
        .friend_item>.fi_info>.fi_chatmsg {
            height: 25px;
            width: 100px;
            overflow: hidden;
            text-align: left;
            background-color: white;
        }
        .friend_item>.fi_msg {
            position: absolute;
            left: 150px;
            top: 0px;
            height: 50px;
            width: 50px;
            background-color: white;
            overflow: hidden;
        }
        .friend_item>.fi_msg>.fi_time {
            height: 25px;
            width: 50px;
            overflow: hidden;
            background-color: white;
        }
        .friend_item>.fi_msg>.fi_bubble {
            position: absolute;
            right: 0px;
            height: 25px;
            width: 25px;
            overflow: hidden;
            background-color: white;
            border-radius: 100%;
        }
        .header {
            position: relative;
            height: 50px;
            width: 800px;
            background-color: #D9DFDB;
            border-bottom: 2px solid rgba(128, 128, 128, 0.71);
            line-height: 50px;
            font-size: 22px;
            color: white;
        }
        .header>.friend_pic>.header_pic {
            float: left;
            margin-top: 2px;
            margin-left: 2px;
            border-radius: 100%;
            height: 45px;
            width: 45px;
        }
        .header>.friend_nickname {
            text-align: center;
            margin: 0 auto;
        }
        .header>.remove_friend_btn {
            float: right;
            margin-right: 5px;
            margin-top: 15px;
            position: relative;
            border-radius: 5px;
            color: white;
            background-color: rgba(30, 144, 255, 0.77);
            font-size: 17px;
        }
        .session {
            height: 400px;
            width: 800px;
            background-color: white;
            border-bottom: 2px solid gray;
            overflow: auto;
        }
        .session::-webkit-scrollbar {
            width: 5px;
        }
        .session>.oppo {
            display: flex;
            position: relative;
            max-width: 700px;
            min-height: 50px;
            margin: 20px auto 20px 40px;
            background: white;
        }
        .session>.oppo>.oppo_msg {
            border-radius: 12px;
            max-width: 650px;
            background: lightgray;
            word-wrap: break-word;
            word-break: break-all;
            padding: 10px;
        }
        .session>.oppo>.oppo_profile {
            margin-left: 0;
            height: 50px;
            width: 50px;
            background: white;
        }
        .session>.oppo>.oppo_profile>img {
            height: 45px;
            width: 45px;
            border-radius: 100%;
            overflow: hidden;
        }
        .session>.self {
            max-width: 700px;
            min-height: 50px;
            margin: 20px 40px 20px auto;
            background: white;
        }
        .session>.self>.self_msg {
            border-radius: 12px;
            max-width: 650px;
            background: #D1F1FE;
            word-wrap: break-word;
            word-break: break-all;
            padding: 10px;
        }
        .session>.self>.self_profile {
            position: relative;
            float: right;
            height: 50px;
            width: 50px;
        }
        .session>.self>.self_profile>img {
            float: right;
            height: 45px;
            width: 45px;
            border-radius: 100%;
            overflow: hidden;
        }
        .userInput {
            height: 170px;
            width: 800px;
            background-color: white;
            overflow: auto;
        }
        .userInput::-webkit-scrollbar {
            width: 3px;
            /*height: 5px;*/
            background: gray;
        }
        .sendBtn {
            position: absolute;
            right: 13px;
            bottom: 12px;
            height: 35px;
            width: 60px;
            background-color: #1e90ff;
            font-size: 20px;
            color: white;
            border: none;
            border-radius: 5px;
        }
        .chat_input {
            height: 170px;
            width: 800px;
            background-color: #f2f8fc;
            font-size: 20px;
        }
        input, button {
            border: none;
            outline: none;
        }
        button {
            cursor: pointer;
        }
        .add_friend>form>button {
            background: none;
            position: absolute;
            margin-top: 32px;
            right: 12px;
            height: 21px;
            width: 40px;
            color: black;
            cursor: pointer;
            border: 1px solid #cebab1;
            border-radius: 4px;
            text-align: center;
        }
        .add_friend>form>button:hover {
            background-color: #1E90FF;
            color: white;
        }
        .add_friend>form>input {
            padding-left: 10px;
            height: 25px;
            width: 80%;
            margin-left: 5px;
            position: relative;
            float: left;
            border: 2px solid gray;
            border-radius: 15px;
        }
        #af_id {
            margin-top: 3px;
        }
        #af_msg {
            margin-top: 2px;
            width: 95%;
        }
        #verify_panel {
            padding: 50px 50px;
            overflow: auto;
        }
        .right>.vm_item {
            border-radius: 10px;
            height: 100px;
            width: 500px;
            margin: 50px auto;
            background: #82650f;
            padding: 20px;
        }
        .right>.vm_item>.msg_text {
            margin: 0 auto;
            width: 450px;
            min-height: 70%;
            background: coral;
            padding: 10px;
            border-radius: 5px;
            font-size: 20px;
            word-wrap: break-word;
            word-break: break-all;
        }
        .right>.vm_item>.msg_time {
            position: relative;
            float: left;
            margin-top: 5px;
            background: none;
            margin-left: 5px;
        }
        .right>.vm_item>.msg_acc {
            position: relative;
            float: right;
            border-radius: 4px;
            background: #99CCFF;
            margin-top: 5px;
            margin-right: 5px;
        }
        .right>.vm_item>.msg_dec {
            position: relative;
            float: right;
            border-radius: 4px;
            background: #99CCFF;
            margin-top: 5px;
            margin-right: 5px;
        }
    </style>
    <script src="parse.js"></script>
    <script>
        userId = <%= userId %>;
        password = <%= password %>;
        userNickname = "";
        login();
    </script>
</head>
<body>
<div class="main_page">
    <div class="left">
        <div class="info" id="userInfo">
            <!--                // pic, id, nickname-->
            <div class="user_image">
                <img src="evil.jpeg" class="user_profile" alt="kilikili"/>
            </div>
            <div class="user_id_prefix">账号:</div>
            <div class="user_id">me</div>
            <div class="user_nickname_prefix">昵称:</div>
            <div class="user_nickname">me</div>
        </div>
        <div class="add_friend">
            <form>
                <input type="text" id="af_id" placeholder="请输入待查找好友id...">
                <input type="text" id="af_msg" placeholder="请输入验证消息...">
                <button type="button" onclick="addFriend()">添加</button>
            </form>
            <!--                <div class="af_id">-->
            <!--                    <label>-->
            <!--                        <input type="text">-->
            <!--                    </label>-->
            <!--                </div>-->
            <!--                <div class="af_content">-->
            <!--                    <label>-->
            <!--                        <input type="text">-->
            <!--                    </label>-->
            <!--                </div>-->
            <!--                <button class="add_mark" type="submit">add</button>-->
        </div>
        <div class="verify_msg">
            <button class="vfb" onclick="clickVerifyPanel()">验证消息</button>
        </div>
<%--        <div class="friend_item" id="lucy">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="123456" onclick="clickFriendItem(this)">--%>
<%--            <div class="fi_image">--%>
<%--                <img src="evil.jpeg" alt="profile" class="fi_profile"/>--%>
<%--            </div>--%>
<%--            <div class="fi_info">--%>
<%--                <div class="fi_nickname"></div>--%>
<%--                <div class="fi_chatmsg"></div>--%>
<%--            </div>--%>
<%--            <div class="fi_msg">--%>
<%--                <div class="fi_time"></div>--%>
<%--                <div class="fi_bubble"></div>--%>
<%--            </div>--%>
<%--            <!--                <img alt="img"/>-->--%>
<%--            <!--                <div class="friendInfo"></div>-->--%>
<%--        </div>--%>
<%--        <div class="friend_item" id="lucy2">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy3">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy4">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy5">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy6">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy7">--%>

<%--        </div>--%>
<%--        <div class="friend_item" id="lucy8">--%>

<%--        </div>--%>
    </div>
<%--    <div class="right chat_panel" id="123456_chat_panel">--%>
<%--        <div class="header">--%>
<%--            <button class="remove_friend_btn">--%>
<%--                remove--%>
<%--            </button>--%>
<%--            <div class="friend_pic">--%>
<%--                <img src="evil.jpeg" class="header_pic" alt="pic"/>--%>
<%--            </div>--%>
<%--            <div class="friend_nickname">--%>
<%--                西红柿炒蛋挞--%>
<%--            </div>--%>
<%--        </div>--%>
<%--        <div class="session">--%>
<%--            <!--                main area for chatting message-->--%>
<%--            <div class="self">--%>
<%--                <div class="self_profile">--%>
<%--                    <img src="evil.jpeg" alt="me"/>--%>
<%--                </div>--%>
<%--                <div class="self_msg">--%>
<%--                    我的消息jsaflk;hfashf;lksajf;lajsflk;asjfkl;ajsflkjsafl;jaslfkjaslkfjsalkfjaslkfjslakjflkewtawlfha;uetopajsl;fiwiutj'asfj;ihtiowtajsflwuroe'awnehklg;twaer'jf;ahrwtieojfkl;hatwoejfsd;ahrtuj'ofdsa;toruoepf--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <div class="oppo">--%>
<%--                <div class="oppo_profile">--%>
<%--                    <img src="evil.jpeg" alt="oppo"/>--%>
<%--                </div>--%>
<%--                <div class="oppo_msg">--%>
<%--                    对方的消息jsaflk;hfashf;lksajf;lajsflk;asjfkl;ajsflkjsafl;jaslfkjaslkfjsalkfjaslkfjslakjflkewtawlfha;uetopajsl;fiwiutj'asfj;ihtiowtajsflwuroe'awnehklg;twaer'jf;ahrwtieojfkl;hatwoejfsd;ahrtuj'ofdsa;toruoepf--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--        <div class="userInput">--%>
<%--            <label>--%>
<%--                <textarea class="chat_input"></textarea>--%>
<%--            </label>--%>
<%--            <button type="submit" class="sendBtn" onclick="sendChatMessage()">Send</button>--%>
<%--        </div>--%>
<%--    </div>--%>
    <div id="verify_panel" class="right">
<%--        <div class="vm_item type_req" id="req12345">--%>
<%--            <!--                xxx(1234567)请求添加您为好友-->--%>
<%--            <!--                xxx(1234567)已经和您解除了好友关系-->--%>
<%--            <!--                您已经和xxx(1234567)解除了好友关系-->--%>
<%--            <div class="msg_text">we wish it never happen</div>--%>
<%--            <div class="msg_time">2020/05/20</div>--%>
<%--            <button class="msg_acc" onclick="accept(this)">同意</button>--%>
<%--            <button class="msg_dec" onclick="decline(this)">拒绝</button>--%>
<%--        </div>--%>
<%--        <div class="vm_item"></div>--%>
<%--        <div class="vm_item"></div>--%>
<%--        <div class="vm_item"></div>--%>
<%--        <div class="vm_item"></div>--%>
    </div>
    <div id="welcome_interface" class="right">
        <img src="welcomeInterface.jpg" alt="welcomeInterface"/>
    </div>
</div>
</body>
</html>
