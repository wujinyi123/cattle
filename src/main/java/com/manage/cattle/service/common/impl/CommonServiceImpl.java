package com.manage.cattle.service.common.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.manage.cattle.dto.BaseDTO;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.dto.common.TemplateInfo;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.service.common.CommonService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private ApplicationContext applicationContext;

    @Value("${cattle.help}")
    private String cattleHelp;

    @Override
    public FileByteInfo exportFile(Map<String, String> params, String templateCode) {
        TemplateInfo info = CommonUtil.getTemplateInfo(params, templateCode);
        String[] classMethod = info.getExportMethed().split("#");
        JSONArray jsonArray;
        try {
            Class<?> clazz = Class.forName(classMethod[0]);
            Method method = clazz.getDeclaredMethod(classMethod[1], info.getQo().getClass());
            Object bean = applicationContext.getBean(clazz);
            Object result = method.invoke(bean, info.getQo());
            String json = JSONUtil.toJsonStr(result);
            jsonArray = JSONUtil.parseArray(json);
        } catch (Exception e) {
            throw new BusinessException("获取数据失败");
        }
        return getFileByteInfo(info, jsonArray);
    }

    @Override
    public FileByteInfo templateFile(String templateCode) {
        TemplateInfo info = CommonUtil.getTemplateInfo(templateCode);
        info.setFileName("模板_" + info.getFileName());
        return getFileByteInfo(info, new JSONArray());
    }

    private FileByteInfo getFileByteInfo(TemplateInfo info, JSONArray jsonArray) {
        byte[] bytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("sheet");
            CommonUtil.writeSheet(sheet, info, jsonArray);
            workbook.write(outputStream);
            bytes = outputStream.toByteArray();
        } catch (Exception e) {
            log.error(CommonUtil.getExceptionDetails("生成xlsx失败", e));
            throw new BusinessException("生成xlsx失败");
        }
        return new FileByteInfo(info.getFileName(), bytes);
    }

    @Override
    public List<String> importRequireField(String templateCode) {
        TemplateInfo info = CommonUtil.getTemplateInfo(templateCode);
        return info.getFields().stream().filter(item -> "true".equals(item.getRequire())).map(TemplateInfo.Field::getTitle).toList();
    }

    @Override
    public ImportInfo importFile(MultipartFile file, String templateCode, String farmCode) {
        TemplateInfo info = CommonUtil.getTemplateInfo(templateCode);
        List<Map<String, String>> list = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = CommonUtil.getRow(sheet, 0);
            Cell cell;
            int cellSize = info.getFields().size();
            Map<String, String> titleMap = info.getFields().stream()
                    .collect(Collectors.toMap(TemplateInfo.Field::getTitle, TemplateInfo.Field::getName));
            List<String> fields = new ArrayList<>();
            for (int index = 0; index < cellSize; index++) {
                cell = CommonUtil.getCell(row, index);
                String title = CommonUtil.getCellString(cell);
                fields.add(titleMap.get(title));
            }
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                row = CommonUtil.getRow(sheet, i);
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < fields.size(); j++) {
                    String key = fields.get(j);
                    if (StrUtil.isBlank(key)) {
                        continue;
                    }
                    cell = CommonUtil.getCell(row, j);
                    String value = CommonUtil.getCellString(cell);
                    if (StrUtil.isNotBlank(value)) {
                        map.put(key, value);
                    }
                }
                list.add(map);
            }
        } catch (Exception e) {
            throw new BusinessException("读取xlsx数据失败");
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException("数据为空");
        }
        String username = UserUtil.getCurrentUsername();
        List<BaseDTO> importList = new ArrayList<>();
        for (Map<String, String> objMap : list) {
            BaseDTO dto = new BaseDTO();
            try {
                Class<? extends BaseDTO> dtoClass = (Class<? extends BaseDTO>) Class.forName(info.getDtoClass());
                objMap.put("createUser", username);
                objMap.put("updateUser", username);
                objMap.put("farmCode", farmCode);
                importBaseCheck(info, objMap);
                String json = JSONUtil.toJsonStr(objMap);
                dto = JSONUtil.toBean(json, dtoClass);
            } catch (Exception e) {
                log.error("赋值失败" + JSONUtil.toJsonStr(objMap));
            }
            importList.add(dto);
        }
        String[] classMethod = info.getImportMethed().split("#");
        try {
            Class<?> clazz = Class.forName(classMethod[0]);
            Method method = clazz.getDeclaredMethod(classMethod[1], List.class);
            Object bean = applicationContext.getBean(clazz);
            Map<Integer, String> errorMap = (Map<Integer, String>) method.invoke(bean, importList);
            List<String> errorList = new ArrayList<>();
            List<Integer> keyList = errorMap.keySet().stream().sorted(Comparator.comparingInt(a -> a)).toList();
            for (Integer index : keyList) {
                errorList.add("第" + (index + 2) + "行：" + errorMap.get(index));
            }
            ImportInfo importInfo = new ImportInfo();
            importInfo.setSuccess(importList.size() - errorList.size());
            importInfo.setFail(errorList.size());
            importInfo.setErrorList(errorList);
            return importInfo;
        } catch (Exception e) {
            log.error(CommonUtil.getExceptionDetails("导入方法导入失败", e));
            throw new BusinessException("导入方法导入失败");
        }
    }

    private void importBaseCheck(TemplateInfo info, Map<String, String> map) {
        // 非空校验
        String importError = info.getFields().stream()
                .filter(item -> "true".equals(item.getRequire()) && StrUtil.isBlank(map.get(item.getName())))
                .map(TemplateInfo.Field::getTitle)
                .collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(importError)) {
            map.put("importError", "必填项(" + importError + ")不能为空");
            return;
        }
        // 枚举校验
        importError = info.getFields().stream()
                .filter(item -> StrUtil.isNotBlank(map.get(item.getName()))
                        && StrUtil.isNotBlank(item.getEnumVal())
                        && !CommonUtil.stringToList(item.getEnumVal()).contains(map.get(item.getName())))
                .map(item -> item.getTitle() + "(" + item.getEnumVal() + ")")
                .collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(importError)) {
            map.put("importError", "赋值不正确，正确为：" + importError);
            return;
        }
        // 正则校验
        importError = info.getFields().stream()
                .filter(item -> StrUtil.isNotBlank(map.get(item.getName()))
                        && StrUtil.isNotBlank(item.getRegex())
                        && !Pattern.matches(item.getRegex(), map.get(item.getName())))
                .map(TemplateInfo.Field::getTitle)
                .collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(importError)) {
            map.put("importError", "格式不正确：" + importError);
        }
    }

    @Override
    public boolean hasHelpFile() {
        if (StrUtil.isBlank(cattleHelp)) {
            return false;
        }
        File file = new File(cattleHelp);
        return file.exists();
    }

    @Override
    public ResponseEntity<byte[]> downloadHelpFile() {
        File file = new File(cattleHelp);
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return CommonUtil.responseByteArr(bytes, file.getName());
        } catch (Exception e) {
            throw new BusinessException("获取文件失败");
        }
    }
}
