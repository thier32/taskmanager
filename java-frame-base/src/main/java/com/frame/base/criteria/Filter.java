package com.frame.base.criteria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;


public class Filter {

    public Filter() {}

    public Filter(String field) {
        this(field,null,null,null,null);
    }

    public Filter(String field, FilterOperator operator, String value, String entityName) {
        this(field,operator,value,null,entityName);
    }

    public Filter(String field, FilterOperator operator, Object value, String entityName) {
        this(field,operator,value,null,entityName);
    }

    public Filter(String field, FilterOperator operator, Object value, Object value2, String entityName) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.value2 = value2;
        this.entityName = entityName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /* Name of the variable from Entity class on which filter has to be applied */
    String field;

    /* Filter operator */
    FilterOperator operator;

    /* Filter value */
    Object value;

    /* Filter second value mostly in case of date between date1= value and date2= value2*/
    Object value2;

    /* Join identifier */
    String entityName;

    public boolean isNumber(Object inNumber) {
        return inNumber instanceof Number;
    }

    public boolean isList(Object inDate) {
        return inDate instanceof List;
    }

    public boolean isValidDate(String inDate) {
        String[] dateFormats = new String[]{"dd/MM/yyyy","MM-dd-yyyy","yyyy-MM-dd"};
        boolean isDate = false;
        for(String format : dateFormats){
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(inDate.trim());
                isDate = true;
                break;
            } catch (ParseException pe) {

            }
        }
        return isDate;
    }

    LocalDate getDate(String inDate){
        return LocalDate.parse(inDate.trim());
    }

}

