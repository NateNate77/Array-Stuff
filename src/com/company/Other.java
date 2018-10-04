package com.company;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by Admin on 26.06.2018.
 */
@XmlRootElement
public class Other extends Employee{
    @XmlElement
    String discription;
}
