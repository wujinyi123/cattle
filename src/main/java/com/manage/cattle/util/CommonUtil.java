package com.manage.cattle.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.TemplateInfo;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.PageQO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static TemplateInfo getTemplateInfo(Map<String, String> params, String templateCode) {
        return getTemplateInfo(params, templateCode, true);
    }

    public static TemplateInfo getTemplateInfo(String templateCode) {
        return getTemplateInfo(new HashMap<>(), templateCode, false);
    }

    public static TemplateInfo getTemplateInfo(Map<String, String> params, String templateCode, boolean needQo) {
        SAXReader reader = new SAXReader();
        Document document;
        try (InputStream inputStream = ResourceUtil.getStream("template.xml")) {
            document = reader.read(inputStream);
        } catch (Exception e) {
            throw new BusinessException("读取配置文件失败");
        }
        Element rootElement = document.getRootElement();
        List<Element> templateList = rootElement.elements("template");
        Element templateElement = templateList.stream().filter(item -> templateCode.equals(item.attributeValue("code"))).findFirst().orElse(null);
        if (Objects.isNull(templateElement)) {
            throw new BusinessException("templateCode不正确");
        }
        TemplateInfo info = new TemplateInfo();
        info.setCode(templateCode);
        String fileName = templateElement.attributeValue("fileName");
        info.setFileName(StrUtil.isBlank(fileName) ? templateCode + ".xlsx" : fileName);
        if (StrUtil.isNotBlank(params.get("fileName"))) {
            info.setFileName(params.get("fileName"));
        }
        if (!info.getFileName().endsWith(".xlsx")) {
            info.setFileName(info.getFileName() + ".xlsx");
        }
        info.setExportMethed(templateElement.attributeValue("exportMethed"));
        info.setImportMethed(templateElement.attributeValue("importMethed"));
        info.setDtoClass(templateElement.attributeValue("dto"));
        if (needQo) {
            try {
                Class<? extends PageQO> qoClass = (Class<? extends PageQO>) Class.forName(templateElement.attributeValue("qo"));
                Field[] fields = qoClass.getDeclaredFields();
                Map<String, String> map = new HashMap<>();
                for (Field field : fields) {
                    String value = params.get(field.getName());
                    if (StrUtil.isBlank(value)) {
                        continue;
                    }
                    map.put(field.getName(), value);
                }
                String json = JSONUtil.toJsonStr(map);
                info.setQo(JSONUtil.toBean(json, qoClass));
            } catch (Exception e) {
                throw new BusinessException("通过配置文件赋值qo失败");
            }
        }
        List<Element> fieldList = templateElement.elements("field");
        for (Element fieldElement : fieldList) {
            String noImport = fieldElement.attributeValue("noImport");
            if (needQo && "true".equals(noImport)) {
                continue;
            }
            String name = fieldElement.attributeValue("name");
            String title = fieldElement.attributeValue("title");
            String require = fieldElement.attributeValue("require");
            String enumVal = fieldElement.attributeValue("enumVal");
            String regex = fieldElement.attributeValue("regex");
            TemplateInfo.Field field = new TemplateInfo.Field();
            field.setName(name);
            field.setTitle(title);
            field.setRequire(require);
            field.setEnumVal(enumVal);
            field.setRegex(regex);
            info.getFields().add(field);
        }
        return info;
    }

    public static void writeSheet(Sheet sheet, TemplateInfo info, List<?> list) {
        String json = JSONUtil.toJsonStr(list);
        JSONArray jsonArray = JSONUtil.parseArray(json);
        writeSheet(sheet, info, jsonArray);
    }

    public static void writeSheet(Sheet sheet, TemplateInfo info, JSONArray jsonArray) {
        // 默认行高、列宽
        sheet.setDefaultRowHeightInPoints(20);
        sheet.setDefaultColumnWidth(20);
        // 默认单元格格式
        CellStyle defaultStyle = getDefaultStyle(sheet.getWorkbook());
        // 标题单元格格式
        CellStyle titleStyle = getTitleStyle(sheet.getWorkbook());
        // 设置默认格式及标题赋值
        Row row = sheet.createRow(0);
        Cell cell;
        for (int index = 0; index < info.getFields().size(); index++) {
            sheet.setDefaultColumnStyle(index, defaultStyle);
            cell = row.createCell(index, CellType.STRING);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(info.getFields().get(index).getTitle());
        }
        // 内容赋值
        for (int i = 0; i < jsonArray.size(); i++) {
            row = sheet.createRow(i + 1);
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            for (int j = 0; j < info.getFields().size(); j++) {
                cell = row.createCell(j, CellType.STRING);
                String key = info.getFields().get(j).getName();
                String value = jsonObject.getStr(key);
                cell.setCellValue(value == null ? "" : value);
            }
        }
    }

    private static CellStyle getDefaultStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    private static CellStyle getTitleStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        // 边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 背景颜色
        XSSFColor color = new XSSFColor();
        color.setARGBHex("CCCCCC");
        ((XSSFCellStyle) cellStyle).setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }
}
