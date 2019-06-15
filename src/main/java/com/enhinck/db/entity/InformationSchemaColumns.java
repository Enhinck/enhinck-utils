package com.enhinck.db.entity;


import com.enhinck.db.annotation.ETable;
import lombok.Data;

import java.math.BigInteger;

@Data
@ETable("information_schema.columns")
public class InformationSchemaColumns {
    private String columnName;
    private String columnType;
    private String isNullable;
    private String columnComment;
    private String columnDefault;
    private BigInteger ordinalPosition;
}
