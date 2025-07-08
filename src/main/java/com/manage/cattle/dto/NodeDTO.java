package com.manage.cattle.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class NodeDTO {
    private String id;
    private String value;
    private String name;
    private String label;
    private String type;
    private String parentId;
    private List<NodeDTO> children;
    private List<String> ids;
    private List<String> nameList;

    public static List<NodeDTO> getTree(List<NodeDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        for (NodeDTO dto : list) {
            dto.setValue(dto.getId());
            dto.setLabel(dto.getName());
            dto.setChildren(list.stream().filter(item -> StrUtil.equals(dto.getId(), item.getParentId())).toList());
        }
        List<NodeDTO> resultList = list.stream().filter(item -> StrUtil.isBlank(item.getParentId()) || "0".equals(item.getParentId())).toList();
        path(resultList, new ArrayList<>(), new ArrayList<>());
        return resultList;
    }

    private static void path(List<NodeDTO> list, List<String> ids, List<String> nameList) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (NodeDTO dto:list) {
            dto.setIds(new ArrayList<>(ids));
            dto.getIds().add(dto.getId());
            dto.setNameList(new ArrayList<>(nameList));
            dto.getNameList().add(dto.getName());
            path(dto.getChildren(), dto.getIds(), dto.getNameList());
        }
    }
}
