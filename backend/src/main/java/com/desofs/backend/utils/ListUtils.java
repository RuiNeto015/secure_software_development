package com.desofs.backend.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {

    public static <T> boolean hasDuplicates(List<T> list) {
        Set<T> set = new HashSet<>();
        return list.stream().anyMatch(e -> !set.add(e));
    }

}
