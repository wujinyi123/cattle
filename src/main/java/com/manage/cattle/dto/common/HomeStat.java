package com.manage.cattle.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class HomeStat {
    private List<Node> farmCattleList;

    @Data
    public static class Node {
        private String label;
        private String value;
        private Integer intValue;
    }
}
