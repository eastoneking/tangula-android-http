package com.tangula.android.http;

import java.io.Serializable;
import java.util.Date;

public class PaginationForm implements Serializable {

    private int index;

    private int size;

    private String condition;

    private String condition2;

    private String condition3;

    private Date start;

    private Date end;

    public PaginationForm(){
        this(1,10,null);
    }

    public PaginationForm(int index, int size){
        this(index,size,null);
    }

    public  PaginationForm(int index, int size, String condition){
        this.index = index;
        this.size = size;
        this.condition = condition;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }



    public String getCondition2() {
        return condition2;
    }

    public void setCondition2(String condition2) {
        this.condition2 = condition2;
    }

    public String getCondition3() {
        return condition3;
    }

    public void setCondition3(String condition3) {
        this.condition3 = condition3;
    }





    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
