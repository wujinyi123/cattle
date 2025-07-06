package com.manage.cattle.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class CommonUtil {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToStr(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date strToDate(String string) {
        try {
            return DATE_FORMAT.parse(string);
        } catch (ParseException e) {
            log.error("字符串转日期失败" + e);
            return null;
        }
    }

    public static boolean checkRequire(String[] requireFieldArr, Object obj) {
        String json = JSONUtil.toJsonStr(obj);
        JSONObject jsonObject = JSONUtil.parseObj(json);
        for (String field:requireFieldArr) {
            Object value = jsonObject.get(field);
            if (value==null || "".equals(value)) {
                return false;
            }
        }
        return true;
    }

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
