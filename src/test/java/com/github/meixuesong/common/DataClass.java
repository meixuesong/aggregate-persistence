package com.github.meixuesong.common;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DataClass implements Versionable{
    private String id = "";
    private boolean checked = true;
    private int age = 0;
    private long milliseconds = 0;
    private float length = 0.00F;
    private double area = 0.00D;
    private BigDecimal money = BigDecimal.ZERO;
    private Date birthday = new Date();
    private LocalDate meetingTime = LocalDate.now();
    private List<DataClass> children = new ArrayList<>();
    private int version = 0;

    @Override
    public int getVersion() {
        return this.version;
    }

}
