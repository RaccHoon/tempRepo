package com.example.yujin2;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
        if (base.get(a) > base.get(b)) { //반대로 하면 오름차순 <=
            return -1;
        } else {
            return 1;
        }
    }
}
