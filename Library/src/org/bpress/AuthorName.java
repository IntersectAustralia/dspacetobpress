//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.02 at 02:45:47 PM EST 
//


package org.bpress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuthorName complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorName">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="email" type="{}excelStringType" minOccurs="0"/>
 *         &lt;element name="institution" type="{}excelStringType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorName", propOrder = {
    "email",
    "institution"
})
@XmlSeeAlso({
    Individual.class,
    Corporate.class
})
public class AuthorName {

    protected ExcelStringType email;
    protected ExcelStringType institution;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setEmail(ExcelStringType value) {
        this.email = value;
    }

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelStringType }
     *     
     */
    public ExcelStringType getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelStringType }
     *     
     */
    public void setInstitution(ExcelStringType value) {
        this.institution = value;
    }

}