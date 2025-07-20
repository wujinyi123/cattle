package com.manage.cattle.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class HomeStat {
    private List<Node> farmCattleList;
    private List<Node> farmZoneCattleList;

    @Data
    public static class Node {
        private String label;
        private String value;
        private Integer intValue;
    }
}
