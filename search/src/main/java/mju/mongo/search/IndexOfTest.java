package mju.mongo.search;

import java.util.Arrays;

import java.util.List;

public class IndexOfTest {
    public static void main(String[] args) {
        String[] arr = {"a", "b", "c"};
        //List<String> list = Arrays.asList(arr);

        //int index = list.indexOf("c");
        //System.out.println("Index: " + index); // 1이 출력됩니다.

        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals("c")) {
                index = i;
                break;
            }
        }
        System.out.println(index);
    }


}