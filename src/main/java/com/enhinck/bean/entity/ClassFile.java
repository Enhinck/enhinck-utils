package com.enhinck.bean.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClassFile {
    private List<ClassField> fieldList = new ArrayList<>();
    private String beanName;
    private String sourcebeanName;
    public void addField(ClassField classField) {
        fieldList.add(classField);
    }
}
