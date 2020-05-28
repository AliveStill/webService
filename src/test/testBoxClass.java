package test;

import javafx.util.Pair;

public class testBoxClass {
    public static void main(String[] args) {
        Integer x = Integer.valueOf(0);
        changeStatus(x);
        System.out.println(x);
//        Pair<String, String> pair = new Pair<String, String>();
    }
    // stupid java
    public static void changeStatus(Integer i) {
        i.byteValue();
    }
}
