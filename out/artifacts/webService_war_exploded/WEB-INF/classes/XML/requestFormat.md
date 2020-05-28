### example: 
    http://url?param1=xx&param2=yy
    url=localhost:8080/webService_war_exploded/Logic
    
### register:
    ?RequestType=0&nickname=___&password=___
     
### login:
    ?RequestType=1&userId=___&password=___
    
### listen:
    ?RequestType=2&userId=___
    
### chat
    ?RequestType=3&sendId=___&receiveId=___&content=___
    
### add_friend
    ?RequestType=4&sendId=___&receiveId=___&content=___
    
### accept_request 同意/拒绝好友申请
    ?RequestType=5&sendId=___&receiveId=___&accept=___
    accept=0: refuse; 1: accept
    
### remove_friend
    ?RequestType=6&sendid=___&receiveId=___        

### update_online_status
    ?RequestType=7&userId=___
    
### try_to_login:
    ?RequestType=8&userId=___&password=___    