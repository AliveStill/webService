var userId;
var password;
var userNickname;
var currentFriendId;


// 1.向服务器发送请求
// 2。解析结果
// 3。去掉两个button，换成另一个"已处理"的gray字体的button
// 4。如果成功添加，向好友列表中插入一个item，并加上对方的一条消息：
//      我们已经成为好友了，开始对话吧。同时显示小红点，值为1
function accept(ele) {
    var xhr = new XMLHttpRequest();
    var node = ele.target.parentNode;
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0].innerHTML;
            if (status == 1) {
                var fromId = xml.getElementsByTagName("fromId")[0].innerHTML;
                var fromNickname = xml.getElementsByTagName("fromNickname")[0].innerHTML;
                // var time = xml.getElementsByTagName("time")[0].value;
                newFriendItem(fromId, fromNickname);
                newFriendSession(fromId, fromNickname);
            } else {
                // nothing happened, or some informed message
            }
            var btn1 = node.getElementsByClassName("msg_acc")[0];
            var btn2 = node.getElementsByClassName("msg_dec")[0];
            var btn3 = document.createElement("button");
            btn3.className = "msg_acc";
            btn3.innerText = "已处理";
            btn2.parentNode.removeChild(btn2);
            btn1.parentNode.appendChild(btn3);
            btn1.parentNode.removeChild(btn1);
            node.getElementsByClassName("msg_time")[0].innerHTML = new Date().toLocaleString();
        }
    }
    xhr.open("POST", "/webService_war_exploded/Logic?" +
        "RequestType=5" + "&" + "sendId=" + userId + "&"
        + "receiveId=" + node.id.slice(3) + "&" + "accept=1");
    xhr.send();
}

function newFriendItem(fromId, fromNickname) {
    var friend_item = document.createElement("div");
    friend_item.className = "friend_item";
    friend_item.id = fromId;
    friend_item.addEventListener("click", function() {
        clickFriendItem(this);
    });

    var fi_image = document.createElement("div");
    fi_image.className = "fi_image";
    var img = document.createElement("img");
    img.src = "evil.jpeg"; img.alt="profile"; img.className="fi_profile";
    fi_image.appendChild(img);

    var fi_info = document.createElement("div");
    fi_info.className = "fi_info";
    var fi_nickname = document.createElement("div");
    fi_nickname.className = "fi_nickname";
    fi_nickname.innerText = fromNickname;
    var fi_chatmsg = document.createElement("div");
    fi_chatmsg.className = "fi_chatmsg";
    fi_info.appendChild(fi_nickname);
    fi_info.appendChild(fi_chatmsg);

    var fi_msg = document.createElement("div");
    fi_msg.className = "fi_msg";
    var fi_time = document.createElement("div");
    fi_time.className = "fi_time";
    var fi_bubble = document.createElement("div");
    fi_bubble.className = "fi_bubble";
    fi_bubble.hidden = true;
    fi_msg.appendChild(fi_time);
    fi_msg.appendChild(fi_bubble);

    friend_item.appendChild(fi_image);
    friend_item.appendChild(fi_info);
    friend_item.appendChild(fi_msg);

    var left = document.getElementsByClassName("left")[0];
    left.appendChild(friend_item);
}

// 效果同上
function decline(ele) {
    var xhr = new XMLHttpRequest();
    var node = ele.target.parentNode;
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0].innerHTML;
            if (status == 1) {
               // nothing to do
            } else {
                // nothing happened, or some informed message
            }
            var btn1 = node.getElementsByClassName("msg_acc")[0];
            var btn2 = node.getElementsByClassName("msg_dec")[0];
            var btn3 = document.createElement("button");
            btn3.className = "msg_acc";
            btn3.innerText = "已处理";
            btn2.parentNode.removeChild(btn2);
            btn1.parentNode.appendChild(btn3);
            btn1.parentNode.removeChild(btn1);
            node.getElementsByClassName("msg_time")[0].innerHTML = new Date().toLocaleString();
        }
    }
    xhr.open("POST", "/webService_war_exploded/Logic?" +
        "RequestType=5" + "&" + "sendId=" + userId + "&"
        + "receiveId=" + node.id.slice(3) + "&" + "accept=0");
    xhr.send();
}

// 1.向服务器发送请求，获取userId和content
function addFriend() {
    var af_id = document.getElementById("af_id");
    var af_msg = document.getElementById("af_msg");
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0].innerHTML;
            if (status == 1) {
                af_id.value = "";
                af_msg.value = "";
            } else {
                // nothing happened
                alert("send failed");
            }
        }
    };
    xhr.open("POST", "/webService_war_exploded/Logic?" +
        "RequestType=4" + "&" + "sendId=" + userId + "&" +
        "receiveId=" + af_id.value + "&" + "content=" + af_msg.value);
    xhr.send();
}

// 1.向服务器发送请求
// 2。摘除好友列表中的一个item
// 3。右边转到任意一个好友界面
function removeFriend() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0].innerHTML;
            if (status == 1) {
                var item = document.getElementById(currentFriendId);
                item.parentNode.removeChild(item);
                var right_ele = document.getElementById(currentFriendId + "_chat_panel");
                right_ele.parentNode.removeChild(right_ele);
                switchToWelcomePanel();
            } else {
                // nothing to do here, failed
            }
        }
    };
    xhr.open("POST", "/webService_war_exploded/Logic?" + "&" +
        "RequestType=6" + "&" + "sendId=" + userId + "&" + "receiveId=" + currentFriendId);
    xhr.send();
}

// 1.向服务器发送请求，获取userId和content
// 2。输出一条信息到对话框
// 3.清空输入框
function sendChatMessage() {
    var xhr = new XMLHttpRequest();
    var cur_session = document.getElementById(currentFriendId + "_chat_panel");
    var input = cur_session.getElementsByClassName("chat_input")[0];
    var content = input.value;
    input.value = "";
    // render send message
    newChatMessage(currentFriendId, content,true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0].innerHTML;
            if (status == 1) {
                // send succeed
            } else {
                // failed
            }
        }
    };
    xhr.open("POST", "/webService_war_exploded/Logic?"
            + "RequestType=3" + "&" + "sendId=" + userId + "&"
            + "receiveId=" + currentFriendId + "&" + "content=" + content);
    xhr.send();
}

// 1.向服务器发送请求，如果发生错误则跳转到404界面或者什么都不做，alert错误
// 2。记录自身信息并将其渲染到userInfo小框框里面
// 3。获取好友列表，并逐一渲染成friend——item贴到那啥上面， 并为每一个好友
//      生成一个session界面，以id_session唯一标识
// 4。生成一个verify_message界面并获取验证消息，根据不同类型渲染到验证消息session界面
// 5。获取好友消息，并将小红点和时间渲染到friend_item上
function login() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;
            var status = xml.getElementsByTagName("status")[0];
            if (status.innerHTML == 1) {
                // render friend list
                userId = xml.getElementsByTagName("id")[0].innerHTML;
                userNickname = xml.getElementsByTagName("nickname")[0].innerHTML;
                document.getElementsByClassName("user_id")[0].innerHTML = userId;
                document.getElementsByClassName("user_nickname")[0].innerHTML = userNickname;
                var friendList = xml.getElementsByTagName("friendList")[0];
                // render friend list
                var friends = friendList.getElementsByTagName("friend");
                for (var i = 0, lim = friends.length; i < lim; ++ i) {
                    var friend_id = friends[i].getElementsByTagName("friendId")[0].innerHTML;
                    var friend_nickname = friends[i].getElementsByTagName("friendNickname")[0].innerHTML;
                    newFriendItem(friend_id, friend_nickname);
                    newFriendSession(friend_id, friend_nickname);
                }

                // render chat message
                var chatmessages = xml.getElementsByTagName("message");
                for (var i = 0, lim = chatmessages.length; i < lim; ++ i) {
                    var fromId = chatmessages[i].getElementsByTagName("fromId")[0].innerHTML;
                    var fromNickname = chatmessages[i].getElementsByTagName("fromNickname")[0].innerHTML;
                    var content = chatmessages[i].getElementsByTagName("content")[0].innerHTML;
                    var time = chatmessages[i].getElementsByTagName("time")[0].innerHTML;
                    newChatMessage(fromId, content, 0);
                }

                // render verify message

                // render friend requests
                var friendRequest = xml.getElementsByTagName("friendRequest");
                for (var i = 0, lim = friendRequest.length; i < lim; ++ i) {
                    var fromId = friendRequest[i].getElementsByTagName("fromId")[0].innerHTML;
                    var fromNickname = friendRequest[i].getElementsByTagName("fromNickname")[0].innerHTML;
                    var time = friendRequest[i].getElementsByTagName("time")[0].innerHTML;
                    newFriendRequestItem(fromId, fromNickname, time);
                }

                // render accepting / declining message
                var acceptRequest = xml.getElementsByTagName("acceptRequest");
                for (var i = 0, lim = acceptRequest.length; i < lim; ++ i) {
                    var status = acceptRequest[i].getElementsByTagName("status")[0].innerHTML;
                    var fromId = acceptRequest[i].getElementsByTagName("fromId")[0].innerHTML;
                    var fromNickname = acceptRequest[i].getElementsByTagName("fromNickname")[0].innerHTML;
                    var time = acceptRequest[i].getElementsByTagName("time")[0].innerHTML;
                    if (status == 1) {
                        newAcceptRequestItem(fromId, fromNickname, time);
                        // won't add it anymore
                        // newFriendItem(fromId, fromNickname);
                    } else {
                        newDeclineRequestItem(fromId, fromNickname, time);
                    }
                }

                var deleteFriend = xml.getElementsByTagName("deleteFriend");
                for (var i = 0, lim = deleteFriend.length; i < lim; ++ i) {
                    var fromId = deleteFriend[i].getElementsByTagName("fromId")[0].innerHTML;
                    var fromNickname = deleteFriend[i].getElementsByTagName("fromNickname")[0].innerHTML;
                    var time = deleteFriend[i].getElementsByTagName("time")[0].innerHTML;
                    newDeleteVerifyMessage(fromId, fromNickname, time);
                }

                listen();
                setInterval("updateOnlineStatus()", 500);
            } else {
                alert("login failed");
            }
        }
    };
    xhr.open("POST", "/webService_war_exploded/Logic?RequestType=1&" +
            "userId=" + userId + "&" + "password=" + password);
    xhr.send();
}


// 将界面切换到verify——panel
function clickVerifyPanel() {
    var fi_sessions = document.getElementsByClassName("right");
    for (var i = 0, lim = fi_sessions.length; i < lim; ++ i) {
        if (fi_sessions[i].id.toString() === "verify_panel") {
            fi_sessions[i].hidden = false;
        } else {
            fi_sessions[i].hidden = true;
        }
    }
}

//切换到对应好友界面，并将对应的小红点清零
function clickFriendItem(ele) {
    var wi = document.getElementById("welcome_interface");
    wi.hidden = true;
    var fi_sessions = document.getElementsByClassName("right");
    var friend_id = ele.id;
    currentFriendId = ele.id;
    for (var i = 0, lim = fi_sessions.length; i < lim; ++ i) {
        var exp_id = fi_sessions[i].id.toString();
        if (exp_id === friend_id + "_chat_panel") {
            fi_sessions[i].hidden = false;
            if (fi_sessions[i].className === "chat_panel") {
                ele.getElementsByClassName("fi_bubble")[0].hidden = true;
            }
        } else {
            fi_sessions[i].hidden = true;
        }
    }
}

// 将界面切换到欢迎界面
function switchToWelcomePanel() {
    var wi = document.getElementById("welcome_interface");
    var fi_sessions = document.getElementsByClassName("right");
    for (var i = 0, lim = fi_sessions.length; i < lim; ++ i) {
        fi_sessions[i].hidden = true;
    }
    wi.hidden = false;
}

// 将界面切换到任意聊天界面,若不存在这样好友，则切换到欢迎界面
function switchToRandomSessionPanel() {
    var fi_sessions = document.getElementsByClassName("right");
    // no_friend
    if (fi_sessions.length === 2) {
        switchToWelcomePanel();
    } else {
        var flag = 1;
        for (var i = 0, lim = fi_sessions.length; i < lim; ++ i) {
            if (flag === 1 && fi_sessions[i].className === "right chat_panel") {
                fi_sessions[i].hidden = false;
                flag = 0;
            }
            fi_sessions[i].hidden = true;
        }
    }
}

// 最恶心的一个函数，在登录成功之后启动，随时监听消息
// 使用无限循环+sleep函数实现
// much of the logic part overlapped with the login func
function listen() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var xml = xhr.responseXML;

            // render chat message
            var chatmessages = xml.getElementsByTagName("message");
            for (var i = 0, lim = chatmessages.length; i < lim; ++ i) {
                var fromId = chatmessages[i].getElementsByTagName("fromId")[0].innerHTML;
                var fromNickname = chatmessages[i].getElementsByTagName("fromNickname")[0].innerHTML;
                var content = chatmessages[i].getElementsByTagName("content")[0].innerHTML;
                var time = chatmessages[i].getElementsByTagName("time")[0].innerHTML;
                newChatMessage(fromId, content, 0);
            }

            // render verify message

            // render friend requests
            var friendRequest = xml.getElementsByTagName("friendRequest");
            for (var i = 0, lim = friendRequest.length; i < lim; ++ i) {
                var fromId = friendRequest[i].getElementsByTagName("fromId")[0].innerHTML;
                var fromNickname = friendRequest[i].getElementsByTagName("fromNickname")[0].innerHTML;
                var time = friendRequest[i].getElementsByTagName("time")[0].innerHTML;
                newFriendRequestItem(fromId, fromNickname, time);
            }

            // render accepting / declining message
            var acceptRequest = xml.getElementsByTagName("acceptRequest");
            for (var i = 0, lim = acceptRequest.length; i < lim; ++ i) {
                var status = acceptRequest[i].getElementsByTagName("status")[0].innerHTML;
                var fromId = acceptRequest[i].getElementsByTagName("fromId")[0].innerHTML;
                var fromNickname = acceptRequest[i].getElementsByTagName("fromNickname")[0].innerHTML;
                var time = acceptRequest[i].getElementsByTagName("time")[0].innerHTML;
                if (status == 1) {
                    newAcceptRequestItem(fromId, fromNickname, time);
                    newFriendItem(fromId, fromNickname);
                    newFriendSession(fromId, fromNickname);
                } else {
                    newDeclineRequestItem(fromId, fromNickname, time);
                }
            }

            var deleteFriend = xml.getElementsByTagName("deleteFriend");
            for (var i = 0, lim = deleteFriend.length; i < lim; ++ i) {
                var fromId = deleteFriend[i].getElementsByTagName("fromId")[0].innerHTML;
                var fromNickname = deleteFriend[i].getElementsByTagName("fromNickname")[0].innerHTML;
                var time = deleteFriend[i].getElementsByTagName("time")[0].innerHTML;
                newDeleteVerifyMessage(fromId, fromNickname, time);
                var node = document.getElementById(fromId);
                node.parentNode.removeChild(node);      // remove friend item, different from login
                var sn = document.getElementById(fromId + "_chat_panel");
                sn.parentNode.removeChild(sn);
                switchToWelcomePanel();
            }

            listen();
        }
    };
    xhr.open("POST", "/webService_war_exploded/Logic?" + "RequestType=2" +
        "&" + "userId=" + userId, true);
    xhr.send();
}

// 在登录成功之后启动，周期为500ms， 使用定时器任务实现
function updateOnlineStatus() {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/webService_war_exploded/Logic?" +
        "RequestType=7" + "&" + "userId=" + userId, true);
    xhr.send();
}

function newFriendSession(friend_id, friend_nickname) {
    var friend_session = document.createElement("div");
    friend_session.className = "right chat_panel";
    friend_session.id = friend_id.toString() + "_chat_panel";

    var header = document.createElement("div");
    header.className = "header";
    var remove_friend_btn = document.createElement("button");
    remove_friend_btn.className = "remove_friend_btn";
    remove_friend_btn.innerText = "remove";
    remove_friend_btn.addEventListener("click", function() {
        removeFriend();
    });
    var friend_pic = document.createElement("div");
    friend_pic.className = "friend_pic";
    var img = document.createElement("img");
    img.src = "evil.jpeg";
    img.className = "header_pic";
    img.alt = "pic";
    friend_pic.appendChild(img);
    var fn = document.createElement("div");
    fn.className="friend_nickname";
    fn.innerText = friend_nickname;
    header.appendChild(remove_friend_btn);
    header.appendChild(friend_pic);
    header.appendChild(fn);

    var session = document.createElement("div");
    session.className = "session";

    var userInput = document.createElement("div");
    userInput.className = "userInput";
    var label = document.createElement("label");
    var textarea = document.createElement("textarea");
    textarea.className = "chat_input"
    label.appendChild(textarea);
    var btn = document.createElement("button");
    btn.type = "submit";
    btn.className = "sendBtn";
    btn.innerText = "Send";
    btn.addEventListener("click", function() { sendChatMessage(); });
    userInput.appendChild(label);
    userInput.appendChild(btn);

    friend_session.appendChild(header);
    friend_session.appendChild(session);
    friend_session.appendChild(userInput);
    // make it invisible,
    // FIXME, test only
    friend_session.hidden = true;
    document.getElementsByClassName("main_page")[0].appendChild(friend_session);
}

// maybe time is unnecessary at all
function newChatMessage(fromId, content, isSelf) {
    if (content == "") {
        return ;
    }
    // isSelf means whether it is self message or opposite message
    var chat_panel = document.getElementById(fromId.toString() + "_chat_panel");
    var sf = document.createElement("div");
    sf.className = isSelf ? "self" : "oppo";

    var self_profile = document.createElement("div");
    self_profile.className = isSelf ? "self_profile" : "oppo_profile";
    var img = document.createElement("img");
    img.src = "evil.jpeg";
    img.alt = isSelf ? "me" : "oppo";
    self_profile.appendChild(img);
    var self_msg = document.createElement("div");
    self_msg.className = isSelf ? "self_msg": "oppo_msg";
    self_msg.innerText = content;

    sf.appendChild(self_profile);
    sf.appendChild(self_msg);
    var sn = chat_panel.getElementsByClassName("session")[0];
    sn.appendChild(sf);
    sn.scrollTop = sn.scrollHeight;
}

function newFriendRequestItem(fromId, formNickname, time) {
    var verify_panel = document.getElementById("verify_panel");

    var item = document.createElement("div");
    item.className = "vm_item type_req";
    item.id = "req" + fromId.toString();
    var msg_text = document.createElement("div");
    msg_text.class = "msg_text";
    msg_text.innerText = "用户" + formNickname.toString() + "(" + fromId.toString() +
        ")" + "请求成为您的好友";
    var msg_time = document.createElement("div");
    msg_time.className = "msg_time";
    msg_time.innerText = new Date(parseInt(time)).toLocaleString();
    var msg_acc = document.createElement("button");
    msg_acc.className = "msg_acc";
    msg_acc.innerText = "同意";
    msg_acc.addEventListener("click", accept.bind(msg_acc));
    var msg_dec = document.createElement("button");
    msg_dec.className = "msg_dec";
    msg_dec.innerText = "拒绝";
    msg_dec.addEventListener("click", decline.bind(msg_dec));
    item.appendChild(msg_text);
    item.appendChild(msg_time);
    item.appendChild(msg_acc);
    item.appendChild(msg_dec);

    verify_panel.appendChild(item);
}

function newAcceptRequestItem(fromId, fromNickname, time) {
    var verify_panel = document.getElementById("verify_panel");

    var item = document.createElement("div");
    item.className = "vm_item type_req";
    item.id = "req" + fromId.toString();
    var msg_text = document.createElement("div");
    msg_text.class = "msg_text";
    msg_text.innerText = "用户" + fromNickname.toString() + "(" + fromId.toString() +
        ")" + "已经同意了你的好友请求";
    var msg_time = document.createElement("div");
    msg_time.className = "msg_time";
    msg_time.innerText = new Date(parseInt(time)).toLocaleString();

    item.appendChild(msg_text);
    item.appendChild(msg_time);

    verify_panel.appendChild(item);
}

function newDeclineRequestItem(fromId, fromNickname, time) {
    var verify_panel = document.getElementById("verify_panel");

    var item = document.createElement("div");
    item.className = "vm_item type_req";
    item.id = "req" + fromId.toString();
    var msg_text = document.createElement("div");
    msg_text.class = "msg_text";
    msg_text.innerText = "用户" + fromNickname.toString() + "(" + fromId.toString() +
        ")" + "拒绝了你的好友请求";
    var msg_time = document.createElement("div");
    msg_time.className = "msg_time";
    msg_time.innerText = new Date(parseInt(time)).toLocaleString();

    item.appendChild(msg_text);
    item.appendChild(msg_time);

    verify_panel.appendChild(item);
}
function newDeleteVerifyMessage(fromId, fromNickname, time) {
    var verify_panel = document.getElementById("verify_panel");

    var item = document.createElement("div");
    item.className = "vm_item type_req";
    item.id = "req" + fromId.toString();
    var msg_text = document.createElement("div");
    msg_text.class = "msg_text";
    msg_text.innerText = "用户" + fromNickname.toString() + "(" + fromId.toString() +
        ")" + "和你解除了好友关系";
    var msg_time = document.createElement("div");
    msg_time.className = "msg_time";
    msg_time.innerText = new Date(parseInt(time)).toLocaleString();

    item.appendChild(msg_text);
    item.appendChild(msg_time);

    verify_panel.appendChild(item);
}
