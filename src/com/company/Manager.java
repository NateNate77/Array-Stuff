package com.company;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 26.06.2018.
 */
@XmlRootElement
public class Manager extends Employee {
    @XmlElement
    ArrayList<Integer> subordinate = new ArrayList<>();
}
