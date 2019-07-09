package com.crud.objects;

import com.crud.helpers.DateHelper;
import com.crud.helpers.TestData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Objects;

public class Computer {

    private static Logger log = Logger.getLogger(Computer.class);

    private String id;
    private String name;
    private String introducedDate;
    private String discontinuedDate;
    private String company;

    public Computer() {
        this("Computer" + TestData.random(7));
    }

    public Computer(String name) {
        this.name = name;
        this.introducedDate = StringUtils.EMPTY;
        this.discontinuedDate = StringUtils.EMPTY;
        this.company = StringUtils.EMPTY;
    }

    public Computer(String name, String company) {
        this(name);
        this.company = company;
    }

    public Computer(String name, String introducedDate, String discontinuedDate, String company) {
        this(name, company);
        this.introducedDate = introducedDate;
        this.discontinuedDate = discontinuedDate;
    }

    public String getName() {
        return name;
    }

    public String getIntroducedDate() {
        return introducedDate;
    }

    public String getDiscontinuedDate() {
        return discontinuedDate;
    }

    public String getCompany() {
        return company;
    }

    public String getId() {
        if (id == null) setId(MockComputer.getComputerIdByName(getName()));
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return same computer object with data fields adjusted to default format
     */
    public Computer normalized() {
        try {
            introducedDate = DateHelper.convertToDefaultFormat(introducedDate, DateHelper.inputFormat);
            discontinuedDate = DateHelper.convertToDefaultFormat(discontinuedDate, DateHelper.inputFormat);
        } catch (Exception ignored) {
        }
        return this;
    }

    public static void main(String[] args) {
    }

    @Override
    public String toString() {
        String format = "Computer:{name:%s, introducedDate:%s, discontinuedDate:%s, company:%s, id:%s]";
        return String.format(format, name, introducedDate, discontinuedDate, company, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Computer)) return false;

        Computer empObj = (Computer) obj;
        return Objects.equals(this.name, empObj.name) &&
                Objects.equals(this.introducedDate, empObj.introducedDate) &&
                Objects.equals(this.discontinuedDate, empObj.discontinuedDate) &&
                Objects.equals(this.company, empObj.company);
    }

    @Override
    public int hashCode() {
        return (name + introducedDate + discontinuedDate + company).hashCode();
    }
}
