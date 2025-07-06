package com.manage.cattle.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportInfo {
    private int success;
    private int fail;
    private List<String> errorList = new ArrayList<>();
}
