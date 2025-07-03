package com.manage.cattle.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileByteInfo {
    private String fileName;
    private byte[] bytes;
}
