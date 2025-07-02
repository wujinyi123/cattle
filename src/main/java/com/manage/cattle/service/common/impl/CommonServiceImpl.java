package com.manage.cattle.service.common.impl;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.manage.cattle.dto.common.ExportInfo;
import com.manage.cattle.dto.common.TemplateInfo;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.PageQO;
import com.manage.cattle.service.common.CommonService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public ExportInfo exportFile(Map<String, String> params, String templateCode) {
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
        return getExportInfo(info, jsonArray);
    }

    @Override
    public ExportInfo templateFile(String templateCode) {
        TemplateInfo info = getTemplateInfo(new HashMap<>(), templateCode, true);
        info.setFileName("模板_" + info.getFileName());
        return getExportInfo(info, new JSONArray());
    }

    private ExportInfo getExportInfo(TemplateInfo info, JSONArray jsonArray) {
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
        return new ExportInfo(info.getFileName(), bytes);
    }

    @Override
    public List<String> importRequireField(String templateCode) {
        TemplateInfo info = getTemplateInfo(new HashMap<>(), templateCode, true);
        return info.getFields().stream().filter(item -> "true".equals(item.getRequire())).map(TemplateInfo.Field::getTitle).toList();
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
        info.setExportMethed(templateElement.attributeValue("exportMethed"));
        String fileName = templateElement.attributeValue("fileName");
        info.setFileName(StringUtils.isBlank(fileName) ? templateCode + ".xlsx" : fileName);
        if (!isImport) {
            try {
                Class<? extends PageQO> qoClass = (Class<? extends PageQO>) Class.forName(templateElement.attributeValue("qo"));
                Field[] fields = qoClass.getDeclaredFields();
                Map<String, String> map = new HashMap<>();
                for (Field field : fields) {
                    String value = params.get(field.getName());
                    if (StringUtils.isBlank(value)) {
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
