package test;

import main.*;
public class testLogic {
    public static void main(String[] args) throws Exception {
        Logic service = new Logic();
        service.init();
        System.out.println("initial test");
//        System.out.println(service.randomId());
////        Integer id = (int)Math.random() % 10000000 + 10000000;
//        System.out.println((int)(Math.random() * 10000000) + 10000000);
        service.dao.addUser("12345678", "12345678", "huixin");
        System.out.println(service.isLoginValid("183374", "1234567"));
        service.destroy();
    }
}
