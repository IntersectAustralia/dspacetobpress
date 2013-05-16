//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.02 at 02:45:47 PM EST 
//


package org.bpress;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for excelStringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="excelStringType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="excelRow" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="excelCol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "excelStringType", propOrder = {
    "value"
})
public class ExcelStringType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected BigInteger excelRow;
    @XmlAttribute
    protected String excelCol;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the excelRow property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExcelRow() {
        return excelRow;
    }

    /**
     * Sets the value of the excelRow property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExcelRow(BigInteger value) {
        this.excelRow = value;
    }

    /**
     * Gets the value of the excelCol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExcelCol() {
        return excelCol;
    }

    /**
     * Sets the value of the excelCol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExcelCol(String value) {
        this.excelCol = value;
    }

}
