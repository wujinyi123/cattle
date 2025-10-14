package com.manage.cattle.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class HomeStat {
    private List<Node> cattleTypeList;
    private List<Node> currentFarmCattleTypeList;
    private List<Node> farmCattleList;
    private List<Node> farmZoneCattleList;

    @Data
    public static class Node {
        private String label;
        private Integer intValue;
        private String value;

        public Node() {
        }

        public Node(String label, Integer intValue) {
            this.label = label;
            this.intValue = intValue;
        }
    }
}
