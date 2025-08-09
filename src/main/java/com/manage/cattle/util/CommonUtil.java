package com.manage.cattle.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.manage.cattle.dto.common.FileByteInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    public static List<String> stringToList(String str) {
        if (StrUtil.isBlank(str)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }

    public static String encode(String str) {
        if (StrUtil.isBlank(str)) {
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

    public static String getExceptionDetails(String error, Exception ex) {
        return error + "：" + getExceptionDetails(ex);
    }

    public static String getExceptionDetails(Exception ex) {
        StringBuilder details = new StringBuilder();
        details.append(ex.toString()).append("\n");
        details.append(ex.getMessage()).append("\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            details.append(element.toString()).append("\n");
        }
        return details.toString();
    }

    public static Row getRow(Sheet sheet, int index) {
        Row row = sheet.getRow(index);
        return row == null ? sheet.createRow(index) : row;
    }

    public static Cell getCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? row.createCell(index) : cell;
    }

    public static String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        String value = switch (cellType) {
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue() + "";
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) && cell.getDateCellValue() != null ? dateToStr(cell.getDateCellValue()).substring(0,
                    10) : cell.getNumericCellValue() + "";
            default -> "";
        };
        return value == null ? "" : value;
    }
}
