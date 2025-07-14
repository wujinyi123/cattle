package com.manage.cattle.service.common.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.manage.cattle.dto.BaseDTO;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.dto.common.TemplateInfo;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.PageQO;
import com.manage.cattle.service.common.CommonService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public FileByteInfo exportFile(Map<String, String> params, String templateCode) {
        TemplateInfo info = getTemplateInfo(params, templateCode);
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
        TemplateInfo info = getTemplateInfo(new HashMap<>(), templateCode, true);
        info.setFileName("模板_" + info.getFileName());
        return getFileByteInfo(info, new JSONArray());
    }

    private FileByteInfo getFileByteInfo(TemplateInfo info, JSONArray jsonArray) {
        List<List<String>> headList = new ArrayList<>();
        for (TemplateInfo.Field field : info.getFields()) {
            headList.add(List.of(field.getTitle()));
        }
        List<List<String>> dataList = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            List<String> data = new ArrayList<>();
            for (TemplateInfo.Field field : info.getFields()) {
                String value = jsonObject.getStr(field.getName());
                data.add(value == null ? "" : value);
            }
            dataList.add(data);
        }
        byte[] bytes;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            EasyExcel.write(os).excelType(ExcelTypeEnum.XLSX).sheet(info.getCode()).head(headList).doWrite(dataList);
            bytes = os.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("生成xlsx失败");
        }
        return new FileByteInfo(info.getFileName(), bytes);
    }

    @Override
    public List<String> importRequireField(String templateCode) {
        TemplateInfo info = getTemplateInfo(new HashMap<>(), templateCode, true);
        return info.getFields().stream().filter(item -> "true".equals(item.getRequire())).map(TemplateInfo.Field::getTitle).toList();
    }

    @Override
    public ImportInfo importFile(MultipartFile file, String templateCode) {
        TemplateInfo info = getTemplateInfo(new HashMap<>(), templateCode, true);
        List<Object> list;
        try (InputStream is = file.getInputStream()) {
            list = EasyExcel.read(is).excelType(ExcelTypeEnum.XLSX).sheet(0).doReadSync();
        } catch (Exception e) {
            throw new BusinessException("读取xlsx数据失败");
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException("数据为空");
        }
        List<BaseDTO> importList = new ArrayList<>();
        for (Object obj : list) {
            BaseDTO dto = new BaseDTO();
            try {
                Class<? extends BaseDTO> dtoClass = (Class<? extends BaseDTO>) Class.forName(info.getDtoClass());
                Map<?, ?> objMap = (Map<?, ?>) obj;
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < info.getFields().size(); i++) {
                    Object value = objMap.get(i);
                    if (value == null || "".equals(value.toString())) {
                        continue;
                    }
                    TemplateInfo.Field fieldObj = info.getFields().get(i);
                    map.put(fieldObj.getName(), value.toString());
                }
                String json = JSONUtil.toJsonStr(map);
                dto = JSONUtil.toBean(json, dtoClass);
            } catch (Exception e) {
                log.error("赋值失败" + JSONUtil.toJsonStr(obj));
            }
            importList.add(dto);
        }
        String[] classMethod = info.getImportMethed().split("#");
        try {
            Class<?> clazz = Class.forName(classMethod[0]);
            Method method = clazz.getDeclaredMethod(classMethod[1], String.class, List.class);
            Object bean = applicationContext.getBean(clazz);
            String requireFields = info.getFields().stream()
                    .filter(item -> "true".equals(item.getRequire()))
                    .map(TemplateInfo.Field::getName)
                    .collect(Collectors.joining(","));
            List<String> errorList = (List<String>) method.invoke(bean, requireFields, importList);
            ImportInfo importInfo = new ImportInfo();
            importInfo.setSuccess(importList.size()-errorList.size());
            importInfo.setFail(errorList.size());
            importInfo.setErrorList(errorList);
            return importInfo;
        } catch (Exception e) {
            log.error("导入方法导入失败", e);
            throw new BusinessException("导入方法导入失败");
        }
    }

    private TemplateInfo getTemplateInfo(Map<String, String> params, String templateCode) {
        return getTemplateInfo(params, templateCode, false);
    }

    private TemplateInfo getTemplateInfo(Map<String, String> params, String templateCode, boolean isImport) {
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
        info.setExportMethed(templateElement.attributeValue("exportMethed"));
        info.setImportMethed(templateElement.attributeValue("importMethed"));
        info.setDtoClass(templateElement.attributeValue("dto"));
        if (!isImport) {
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
            if (isImport && "true".equals(noImport)) {
                continue;
            }
            String name = fieldElement.attributeValue("name");
            String title = fieldElement.attributeValue("title");
            String require = fieldElement.attributeValue("require");
            TemplateInfo.Field field = new TemplateInfo.Field();
            field.setName(name);
            field.setTitle(title);
            field.setRequire(require);
            info.getFields().add(field);
        }
        return info;
    }
}
