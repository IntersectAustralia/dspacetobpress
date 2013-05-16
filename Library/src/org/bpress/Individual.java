//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.02 at 02:45:47 PM EST 
//


package org.bpress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for individual complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="individual">
 *   &lt;complexContent>
 *     &lt;extension base="{}AuthorName">
 *       &lt;sequence>
 *         &lt;element name="lname" type="{}excelStringType"/>
 *         &lt;element name="fname" type="{}excelStringType" minOccurs="0"/>
 *         &lt;element name="mname" type="{}excelStringType" minOccurs="0"/>
 *         &lt;element name="suffix" type="{}excelStringType" minOccurs="0"/>
 *         &lt;element name="_author_num" type="{}excelIntegerType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "individual", propOrder = {
    "lname",
    "fname",
    "mname",
    "suffix",
    "authorNum"
})
public class Individual
    extends AuthorName
{

    @XmlElement(required = true)
    protected ExcelStringType lname;
    protected ExcelStringType fname;
    protected ExcelStringType mname;
    protected ExcelStringType suffix;
    @XmlElement(name = "_author_num")
    protected ExcelIntegerType authorNum;

    /**
     * Gets the value of the lname property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getLname() {
        return lname;
    }

    /**
     * Sets the value of the lname property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setLname(ExcelStringType value) {
        this.lname = value;
    }

    /**
     * Gets the value of the fname property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getFname() {
        return fname;
    }

    /**
     * Sets the value of the fname property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setFname(ExcelStringType value) {
        this.fname = value;
    }

    /**
     * Gets the value of the mname property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getMname() {
        return mname;
    }

    /**
     * Sets the value of the mname property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setMname(ExcelStringType value) {
        this.mname = value;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setSuffix(ExcelStringType value) {
        this.suffix = value;
    }

    /**
     * Gets the value of the authorNum property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelIntegerType }
     *     
     */
    public ExcelIntegerType getAuthorNum() {
        return authorNum;
    }

    /**
     * Sets the value of the authorNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelIntegerType }
     *     
     */
    public void setAuthorNum(ExcelIntegerType value) {
        this.authorNum = value;
    }

}
