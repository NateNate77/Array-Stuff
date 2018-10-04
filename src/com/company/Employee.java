package com.company;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Created by Admin on 29.06.2018.
 */

public class Employee implements Comparable <Employee> {
    @XmlElement
    String name;
    @XmlElement
    String lastName;
    @XmlElement
    String middleName;
    @XmlElement
    Date birthDay;
    @XmlElement
    Date employmentDay;

    @Override
    public int compareTo(Employee o) {
        return lastName.compareTo(o.lastName);
    }

    @Override
    public String toString(){
        return  lastName + " " + name + " " + middleName + " " + birthDay + " " + employmentDay;
    }
}

