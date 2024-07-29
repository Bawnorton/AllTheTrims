package com.bawnorton.allthetrims.versioned;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VLists {
    public static <T> List<T> reverse(List<T> list) {
        //? if java: >=21 {
        /*return list.reversed();
        *///?} else {
        List<T> reversedList = new ArrayList<>(list);
        Collections.reverse(reversedList);
        return reversedList;
        //?}
    }

    public static <T> T getLast(List<T> list) {
        //? if java: >=21 {
        /*return list.getLast();
        *///?} else {
        return list.get(list.size() - 1);
        //?}
    }
}
