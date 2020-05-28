<%--
  Created by IntelliJ IDEA.
  User: mac
  Date: 2020/5/19
  Time: 22:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>testLogic</title>
    <script>
        var RT_REGISTER             = 0;
        var RT_LOGIN                = 1;
        var RT_LISTEN               = 2;
        var RT_CHAT                 = 3;
        var RT_ADD_FRIEND           = 4;
        var RT_ACCEPT_REQUEST       = 5;
        var RT_REMOVE_FRIEND        = 6;
        var RT_UPDATE_ONLINE_STATUS = 7;
        var UPDATE_ONLINE_GAP       = 500;  // 500 ms for updating online status
        var LONG_QUERY_GAP          = 10000;// 10s for long query
        function doSomething(rt_type, param) {
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    alert(xhr.responseText);
                }
            }
            xhr.open("POST", "/webService_war_exploded/Logic" + "?" + "RequestType=" + rt_type + "&" + param);
            xhr.send();
        }
        function doSomethingTwo(rt_type, param) {
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    // nothing to do here...
                }
            }
            xhr.open("POST", "/webService_war_exploded/Logic" + "?" + "RequestType=" + rt_type + "&" + param);
            xhr.send();
        }
        function register() {
            var nickname = "Comet";
            var password = "Comet";
            doSomething(RT_REGISTER, "nickname=" + nickname + "&" + "password=" + password);
        }
        function login() {
            var userId = "183374";
            var password = "1234567";
            doSomething(RT_LOGIN, "userId=" + userId + "&" + "password=" + password);
        }
        function chat() {
            var sendId = 183374;
            var receiveId = 183375;
            var content = "have a good day";
            doSomething(RT_CHAT, "sendId=" + sendId + "&" + "receiveId="
                + receiveId + "&" + "content=" + content);
        }
        function add_friend() {
            var sendId = 183375;
            var receiveId = 183374;
            var content = "please be my friend.";
            doSomething(RT_ADD_FRIEND, "sendId=" + sendId + "&"
                + "receiveId=" + receiveId + "&" + "content=" + content);
        }
        function accept_request() {
            var sendId = 183374;
            var receiveId = 183375;
            doSomething(RT_ACCEPT_REQUEST, "sendId=" + sendId + "&"
                + "receiveId=" + receiveId + "&" + "accept=" + 1);
        }
        function remove_friend() {
            var sendId = 183375;
            var receiveId = 183374;
            doSomething(RT_REMOVE_FRIEND, "sendId=" + sendId + "&"
                    + "receiveId=" + receiveId);
        }
        // var uos = window.setInterval(function updateOnlineStatus() {
        //     var userId = 183374;
        //     doSomethingTwo(RT_UPDATE_ONLINE_STATUS, "userId=" + userId);
        // }, UPDATE_ONLINE_GAP);
    </script>
</head>
<body>
    <button onclick="register()">register</button>
    <button onclick="login()">login</button>
    <button onclick="chat()">chat</button>
    <button onclick="add_friend()">add_friend</button>
    <button onclick="accept_request()">accept_request</button>
    <button onclick="remove_friend()">remove_friend</button>
</body>
</html>
