package com.enhinck.bean.entity;

import lombok.Data;

import java.util.List;

@Data
public class ClassField {
    private FieldTypeEnum fieldType;
    private String fieldTypeName;
    private String fieldName;
    private String sourceName;
    private ClassFile typeClassFile;
    private ListClassFile classFieldList;

    public enum FieldTypeEnum {
        // 对象类型
        OBJECT,
        // 基本类型
        BASIC,
        // 列表
        LIST
    }
}
