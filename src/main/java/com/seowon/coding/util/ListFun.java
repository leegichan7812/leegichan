package com.seowon.coding.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * List 조작을 돕기 위한 Utils
 * 필요시 활용해도 좋음
 */
@UtilityClass
public class ListFun {

    /**
     * 두 목록을 Pair 목록으로 ZIP List 로 반환
     * @param e1s
     * @param e2s
     * @return
     */
    public static <E1, E2> List<Pair<E1, E2>> zip(List<E1> e1s, List<E2> e2s) {
        if (e1s.size() != e2s.size()) {
            throw new IllegalArgumentException("Lists must be same size");
        }
        return e1s.stream().map(e1 -> Pair.of(e1, e2s.get(e1s.indexOf(e1)))).toList();
    }


    /**
     * Index 와 element 를 사용하여 목록을 맵핑
     * @param list
     * @param mapper
     * @return
     */
    public static <E, R> List<R> mapIndexed(List<E> list, BiFunction<Integer, E, R> mapper) {
        var result = new ArrayList<R>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(mapper.apply(i, list.get(i)));
        }
        return result;
    }

    /**
     * Key 추출기를 이용해서 목록을 해시 맵으로 변환
     * @param list
     * @param keyExtractor
     * @return
     */
    public static <E, K> HashMap<K, E> toHashMap(List<E> list, Function<E, K> keyExtractor) {
        var result = new HashMap<K, E>();
        for (E e : list) {
            result.put(keyExtractor.apply(e), e);
        }
        return result;
    }
}
