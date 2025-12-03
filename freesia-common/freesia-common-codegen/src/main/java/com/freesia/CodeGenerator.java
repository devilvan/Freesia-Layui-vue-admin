package com.freesia;

import cn.hutool.core.util.StrUtil;
import com.freesia.dto.ColumnFieldDto;
import com.freesia.dto.DataBaseDto;
import com.freesia.dto.TableDto;
import com.freesia.exception.BaseException;
import com.freesia.handler.FreemarkerTemplateHandler;
import com.freesia.util.UEmpty;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * @author Evad.Wu
 * @Description 代码生成器 主入口
 * @date 2022-09-12
 */
@SuppressWarnings(value = "all")
@Slf4j
public class CodeGenerator {
    private static final List<String> textType = List.of("VARCHAR", "CHAR", "TINYTEXT", "MEDIUNTEXT", "TEXT", "LONGTEXT");
    private static final List<String> dateType = List.of("DATE", "TIME", "DATETIME", "TIMESTAMP", "YEAR");
    private static final List<String> intType = List.of("TINYINT", "SMALLINT", "MEDIUMINT", "INT");
    private static final String SUFFIX_JAVA = ".java";
    private static final String SUFFIX_XML = ".xml";
    private static final Map<String, Object> basicMap;

    static {
        basicMap = loadBasic();
    }

    public static void main(String[] args) {
        List<String> tableList = Optional.of(Arrays.asList(String.valueOf(basicMap.get("tableName")).toUpperCase(Locale.ROOT).split(",")))
                .filter(f -> f.size() > 0)
                .orElseThrow(() -> new BaseException("待生成的数据库表名不能为空！"));
        TableDto tableDto = assemDataSourceTbDto(tableList);
        List<DataBaseDto> dataBaseDtoList = printTableStructure(tableDto);
        generate(dataBaseDtoList);
    }

    /**
     * 生成模型文件开关
     *
     * @param dataBaseDtoList 待生成代码的数据表信息
     */
    private static void generate(List<DataBaseDto> dataBaseDtoList) {
        for (DataBaseDto dataBaseDto : dataBaseDtoList) {
            generateVo(dataBaseDto);
            generateDto(dataBaseDto);
            generatePo(dataBaseDto);
            generateController(dataBaseDto);
            generateService(dataBaseDto);
            generateServiceImpl(dataBaseDto);
            generateRepository(dataBaseDto);
            generateMapper(dataBaseDto);
            generateMapperXml(dataBaseDto);
        }
    }

    /**
     * 初始化方法，加载basic.properties 获取如表名、包名、作者等基础信息
     *
     * @return 解析properties的key-value后封装的Map
     */
    private static Map<String, Object> loadBasic() {
        HashMap<String, Object> map = new HashMap<>();
        Properties pro = new Properties();
        try {
            pro.load(new FileReader("freesia-common\\freesia-common-codegen\\src\\main\\resources\\basic.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tableName = String.valueOf(pro.get("tableName"));
        String projectDirectory = String.valueOf(pro.get("projectDirectory"));
        String secondaryModule = String.valueOf(pro.get("secondaryModule"));
        String path = pro.get("path") + secondaryModule + "\\";
        String author = String.valueOf(pro.get("author"));
        String packageName;
        if (UEmpty.isNotEmpty(secondaryModule)) {
            packageName = pro.get("packageName") + "." + secondaryModule;
        } else {
            packageName = pro.get("packageName").toString();
        }
        map.put("tableName", tableName);
        map.put("projectDirectory", projectDirectory);
        map.put("secondaryModule", secondaryModule);
        map.put("path", path);
        map.put("author", author);
        map.put("packageName", packageName);
        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return map;
    }

    /**
     * springboot项目，获取application.yml中数据源信息
     *
     * @param tableList 需要生成的表名
     * @return 数据源信息
     */
    private static TableDto assemDataSourceTbDto(List<String> tableList) {
        ClassPathResource resource = new ClassPathResource("application.yml");
        YamlPropertiesFactoryBean ymlFactory = new YamlPropertiesFactoryBean();
        ymlFactory.setResources(resource);
        Properties pro = Optional.ofNullable(ymlFactory.getObject()).orElseThrow(() -> new BaseException("获取配置失败！"));
        String driver = String.valueOf(pro.get("spring.datasource.driver-class-name"));
        String url = String.valueOf(pro.get("spring.datasource.url"));
        String uname = String.valueOf(pro.get("spring.datasource.username"));
        String pwd = String.valueOf(pro.get("spring.datasource.password"));
        TableDto tbDto = new TableDto(driver, url, uname, pwd);
        tbDto.setTableList(tableList);
        return tbDto;
    }

    /**
     * 根据数据源信息获取到对应数据表的字段
     *
     * @param tbDto 数据源信息
     * @return 数据表、字段信息
     */
    private static List<DataBaseDto> printTableStructure(TableDto tbDto) {
        List<DataBaseDto> dataBaseDtoList = new ArrayList<>();
        // 每张表默认的审计字段，在生成的Model中只显示业务字段，而通过继承一个BasePo管理这些审计字段
        List<String> auditList = List.of("ID", "CREATOR", "CREATE_TIME", "MODIFIER", "MODIFY_TIME", "LOGIC_DEL", "REC_VER", "BUILD_IN", "TENANT_ID");
        try {
            // JDBC的过程了，加载MySQL驱动-连接-执行-结果集
            Class.forName(tbDto.getDriver());
            Connection connection = DriverManager.getConnection(tbDto.getUrl(), tbDto.getUname(), tbDto.getPwd());
            DatabaseMetaData metaData = connection.getMetaData();
            // 获取所有表
            List<String> tableList = tbDto.getTableList();
            for (String table : tableList) {
                // 根据表名获取数据表的元数据
                ResultSet tableResultSet = metaData.getTables(null, null, table, new String[]{"TABLE"});
                while (tableResultSet.next()) {
                    String tableName = tableResultSet.getString("TABLE_NAME").toUpperCase(Locale.ROOT);
                    // 将表名转化成类名
                    String className = tableName2className(table);
                    System.out.println("tableName: " + tableName);
                    // 获取数据表的描述-comment
                    String comment = tableResultSet.getString("REMARKS");
                    if (StrUtil.isEmpty(comment)) {
                        throw new BaseException("表描述不能为空！");
                    }
                    System.out.println("comment: " + comment);
                    DataBaseDto dataBaseDto = new DataBaseDto(table, className, comment);
                    // 获取表字段结构
                    ResultSet columnResultSet = metaData.getColumns(null, "%", tableName, "%");
                    List<ColumnFieldDto> columnFieldDtoList = new ArrayList<>();
                    // 遍历表中每个字段
                    while (columnResultSet.next()) {
                        String columnName = columnResultSet.getString("COLUMN_NAME");
                        if (auditList.contains(columnName)) {
                            continue;
                        }
                        String fieldName = columnName2Field(columnName);
                        String columnType = columnResultSet.getString("TYPE_NAME");
                        String javaType = columnType2JavaType(columnType);
                        int datasize = columnResultSet.getInt("COLUMN_SIZE");
                        int digits = columnResultSet.getInt("DECIMAL_DIGITS");
                        // false-不可为空
                        boolean nullable = columnResultSet.getInt("NULLABLE") == 1;
                        String remarks = columnResultSet.getString("REMARKS");
                        ColumnFieldDto columnFieldDto = new ColumnFieldDto(columnName, fieldName, columnType, javaType, datasize, digits, nullable, remarks);
                        columnFieldDtoList.add(columnFieldDto);
                    }
                    dataBaseDto.setFieldList(columnFieldDtoList);
                    dataBaseDtoList.add(dataBaseDto);
                    System.out.println("=================================");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return dataBaseDtoList;
    }

    /**
     * 表名转类名
     *
     * @param table 表名
     * @return 类名
     */
    private static String tableName2className(String table) {
        StringBuilder sb = new StringBuilder();
        String[] split = table.toLowerCase(Locale.ROOT).split("_");
        for (String s : split) {
            if (s.length() == 1) {
                sb.append(s.toUpperCase(Locale.ROOT));
            } else {
                sb.append(s.substring(0, 1).toUpperCase(Locale.ROOT)).append(s.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * DB字段类型转Java类型
     *
     * @param columnType 字段类型
     * @return Java类型
     */
    private static String columnType2JavaType(String columnType) {
        if (textType.contains(columnType)) {
            return "String";
        } else if (dateType.contains(columnType)) {
            return "Date";
        } else if (intType.contains(columnType)) {
            return "Integer";
        } else if ("BIGINT".equals(columnType)) {
            return "Long";
        } else if ("FLOAT".equals(columnType)) {
            return "Float";
        } else if ("DOUBLE".equals(columnType)) {
            return "Double";
        } else if ("DECIMAL".equals(columnType)) {
            return "BigDecimal";
        }
        return "String";
    }

    /**
     * DB字段名转Java属性名
     *
     * @param columnName DB字段名
     * @return Java属性名
     */
    private static String columnName2Field(String columnName) {
        StringBuilder sb = new StringBuilder();
        String[] split = columnName.toLowerCase(Locale.ROOT).split("_");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                sb.append(split[0]);
            } else if (split[i].length() == 1) {
                sb.append(split[i].toUpperCase(Locale.ROOT));
            } else {
                sb.append(split[i].substring(0, 1).toUpperCase(Locale.ROOT)).append(split[i].substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * 根据freemarker模板，生成文件到对应目录下
     *
     * @param templateName 模板名称
     * @param file         待生成的文件名
     * @param basicMap     待生成的文件中的基本信息
     */
    private static void generateFileByTemplate(String templateName, File file, Map<String, Object> basicMap) {
        try {
            Template template = FreemarkerTemplateHandler.getTemplate(templateName);
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8), 10240);
            template.process(basicMap, out);
            FreemarkerTemplateHandler.clearCache();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private static void generateVo(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\vo\\";
        String templateName = "vo.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Vo" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generatePo(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\po\\";
        String templateName = "po.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Po" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateController(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\controller\\";
        String templateName = "controller.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Controller" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateDto(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\dto\\";
        String templateName = "dto.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Dto" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateService(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\service\\";
        String templateName = "service.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Service" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateServiceImpl(DataBaseDto dataBaseDto) {
        String parentpPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\service\\impl\\";
        String templateName = "serviceImpl.ftl";
        File mapperFile = new File(parentpPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentpPath + dataBaseDto.getClassName() + "ServiceImpl" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateMapper(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\mapper\\";
        String templateName = "mapper.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Mapper" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateMapperXml(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\src\\main\\resources\\mapper\\";
        String templateName = "mapperXml.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Mapper" + SUFFIX_XML);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }

    private static void generateRepository(DataBaseDto dataBaseDto) {
        String parentPath = basicMap.get("projectDirectory") + "\\" + basicMap.get("path") + "\\repository\\";
        String templateName = "repository.ftl";
        File mapperFile = new File(parentPath);
        if (!mapperFile.exists()) {
            mapperFile.mkdirs();
        }
        File path = new File(parentPath + dataBaseDto.getClassName() + "Repository" + SUFFIX_JAVA);
        Map<String, Object> map = new HashMap<>(basicMap);
        map.put("dataBaseDto", dataBaseDto);
        generateFileByTemplate(templateName, path, map);
    }
}
