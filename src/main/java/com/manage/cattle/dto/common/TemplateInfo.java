package com.manage.cattle.dto.common;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateInfo {
    private String code;
    private String fileName;
    private String exportMethed;
    private String importMethed;
    private PageQO qo;
    private String dtoClass;
    private List<Field> fields = new ArrayList<>();

    @Data
    public static class Field {
        private String name;
        private String title;
        private String require;
    }
}
