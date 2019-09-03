package com.enhinck.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.enhinck.bean.entity.ClassField;
import com.enhinck.bean.entity.ClassFile;
import com.enhinck.bean.entity.ListClassFile;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSON2JavaBean {
    public static void main(String[] args) {

        String json = "{\"faultDeviceNum\": {\n" +
                "          \"type\": \"integer\",\n" +
                "          \"format\": \"int32\",\n" +
                "          \"description\": \"故障设备数量\"\n" +
                "        }}";

        Object o = JSONObject.parse(json);

        if (o instanceof JSONObject) {
            ClassFile classFile = toJavaBean((JSONObject) o, "ROOT");


            writeJava(classFile);


        } else if (o instanceof JSONArray) {
            ListClassFile listClassFile = toJavaBean((JSONArray) o, "ROOT");

            writeJava(listClassFile);

        }
    }

    private static void writeJava(ClassFile classFile) {
        String beanName = classFile.getBeanName();
        System.out.println(beanName + ".java");

        StringBuilder builder = new StringBuilder();


        builder.append("public class ").append(beanName).append(" {").append("\n");

        classFile.getFieldList().forEach(classField -> {

            switch (classField.getFieldType()){
                // 對象
                case OBJECT:
                    builder.append("private ").append(beanName(classField.getFieldName())).append(" ").append(classField.getFieldName());
                    break;
                case LIST:




                    builder.append("private ").append("List<").append("").append(">").append(classField.getFieldName());
                    break;
                case BASIC:
                    builder.append("private ").append(classField.getFieldType()).append(" ").append(classField.getFieldName());
                    break;
                    default:
                        break;
            }
        });

    }





    private static void writeJava(ListClassFile listClassFile) {



    }


    public static ClassFile toJavaBean(JSONObject jsonObject, String sourceName) {
        ClassFile classFile = new ClassFile();
        String beanName = beanName(sourceName);
        classFile.setBeanName(beanName);
        classFile.setSourcebeanName(sourceName);
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
        entrySet.forEach(entry -> {
            ClassField classField = new ClassField();
            String fieldName = entry.getKey();
            classField.setSourceName(fieldName);
            classField.setFieldName(underScoreCaseToCamelCase(fieldName));
            Object fieldValue = entry.getValue();
            if (fieldValue instanceof JSONObject) {
                String subBeanName = beanName(fieldName);
                ClassFile subClassFile = toJavaBean((JSONObject) fieldValue, subBeanName);
                classField.setTypeClassFile(subClassFile);
                classField.setFieldType(ClassField.FieldTypeEnum.OBJECT);
            } else if (fieldValue instanceof JSONArray) {
                String subBeanName = beanName(fieldName);
                ListClassFile listClassFile = toJavaBean((JSONArray) fieldValue, subBeanName);
                classField.setClassFieldList(listClassFile);
                classField.setFieldType(ClassField.FieldTypeEnum.LIST);
            } else {
                String typeName = fieldValue.getClass().getName();
                classField.setFieldTypeName(typeName);
            }
            classFile.addField(classField);
        });
        return classFile;
    }

    public static ListClassFile toJavaBean(JSONArray jsonArray, String objectName) {
        ListClassFile listClassFile = new ListClassFile();
        if (jsonArray.size() > 0) {
            Object object = jsonArray.get(0);
            ClassFile classFile = new ClassFile();
            if (object instanceof JSONObject) {
                classFile.setSourcebeanName(objectName);
                String subBeanName = beanName(objectName);
                classFile = toJavaBean((JSONObject) object, subBeanName);
                listClassFile.setListObjectType(ClassField.FieldTypeEnum.OBJECT);
            } else if (object instanceof JSONArray) {
                JSONArray arry = (JSONArray) object;
                objectName = "sub" + objectName;
                ListClassFile subList = toJavaBean(arry, objectName);
                listClassFile.setListObjectType(ClassField.FieldTypeEnum.LIST);
                listClassFile.setListlistObject(subList);
            } else {
                String typeName = object.getClass().getName();
                listClassFile.setListObjectTypeName(typeName);
                listClassFile.setListObjectType(ClassField.FieldTypeEnum.BASIC);
            }
            listClassFile.setListObject(classFile);
        }

        return listClassFile;
    }

    private static Pattern camelCasePattern = Pattern.compile("[A-Z]");
    private static Pattern underScoreCasePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String underScoreCaseToCamelCase(String str) {
       // str = str.toLowerCase();
        Matcher matcher = underScoreCasePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 类名
     */
    public static String beanName(String str) {
        String camelName = underScoreCaseToCamelCase(str);
        return camelName.substring(0, 1).toUpperCase() + camelName.substring(1, camelName.length());
    }


    /**
     * 驼峰转下划线
     */
    public static String camelCaseToUnderScoreCase(String str) {
        Matcher matcher = camelCasePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
