package com.enhinck.bean.entity;

import lombok.Data;

@Data
public class ListClassFile {
    private ClassFile listObject;
    private ListClassFile listlistObject;
    private String listObjectTypeName;
    private ClassField.FieldTypeEnum listObjectType;
}
