function switchToLogin() {
    document.getElementById("Register").setAttribute("style", "border-bottom: 2px solid white; font-size: 20px;");
    document.getElementById("Login").setAttribute("style", "border-bottom: 3px solid rgba(0, 97, 196, 0.82); font-size: 24px");
    document.getElementById("loginText").setAttribute("placeholder", "Username");
    document.getElementById("loginBtn").innerText="Login";
    document.getElementById("loginBtn").setAttribute("onclick", "onLogin()");
}
function switchToRegister() {
    document.getElementById("Login").setAttribute("style", "border-bottom: 2px solid white; font-size: 20px;");
    document.getElementById("Register").setAttribute("style", "border-bottom: 3px solid rgba(0, 97, 196, 0.82); font-size: 24px");
    document.getElementById("loginText").setAttribute("placeholder", "Nickname");
    document.getElementById("loginBtn").innerText="Register";
    document.getElementById("loginBtn").setAttribute("onclick", "onRegister()");
}
function onRegister() {
    var nickname = document.getElementById("loginText").value;
    var password = document.getElementById("loginPassword").value;
    var xhr=new XMLHttpRequest();
    xhr.overrideMimeType('text/xml');
    xhr.onreadystatechange=function(){
        if (xhr.readyState===4 && xhr.status===200)
        {
            var xml=xhr.responseXML;
            var x=xml.getElementsByTagName("status");
            // alert(xml);
            // alert(x[0].innerHTML);
            if(x[0].innerHTML == 1){
                var str=xml.getElementsByTagName("userId")[0].innerHTML;
                alert("your id is " + str);
                document.getElementById("loginText").value = str;
                document.getElementById("loginPassword").value = "";
                switchToLogin();
            }else{
                alert("register failed");
            }
        }
    };
    xhr.open("POST","/webService_war_exploded/Logic?RequestType=0&"
        + "password=" + password + "&" + "nickname=" + nickname);
    xhr.send();
}
function onLogin() {
    var userId = document.getElementById("loginText").value;
    var password = document.getElementById("loginPassword").value;
    var xhr=new XMLHttpRequest();
    xhr.onreadystatechange=function(){
        if (xhr.readyState===4 && xhr.status===200)
        {
            var xml=xhr.responseXML;
            var x=xml.getElementsByTagName("status");
            if(x[0].innerHTML == 1){
                window.location.href = "/webService_war_exploded/mainPage/mainPage.jsp" + "?"
                    + "userId=" + userId + "&" + "password=" + password;
            }else{
                alert("register failed");
                document.getElementById("loginPassword").value = "";
            }
        }
    }
    // redirect to mainPage with parameters,
    // which means userId and userPassword
    xhr.open("POST","/webService_war_exploded/Logic?RequestType=8&"
        + "userId=" + userId + "&" + "password=" + password);
    xhr.send();
}