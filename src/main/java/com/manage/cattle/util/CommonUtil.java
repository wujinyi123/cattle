package com.manage.cattle.util;

import com.manage.cattle.dto.common.FileByteInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CommonUtil {
    /**
     * 获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    public static List<String> stringToList(String str) {
        if (StringUtils.isBlank(str)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }

    public static String encode(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        try {
            String encode = URLEncoder.encode(str, StandardCharsets.UTF_8);
            return encode.replace("+", "%20");
        } catch (Exception e) {
            log.error("encode失败：" + e);
        }
        return str;
    }

    public static String decode(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decode失败：" + e);
        }
        return str;
    }

    public static ResponseEntity<byte[]> responseByteArr(FileByteInfo info) {
        return responseByteArr(info.getBytes(), info.getFileName());
    }

    public static ResponseEntity<byte[]> responseByteArr(byte[] bytes, String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", encode(fileName));
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}
