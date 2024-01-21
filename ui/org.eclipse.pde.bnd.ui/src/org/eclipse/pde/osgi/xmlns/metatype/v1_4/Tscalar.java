/*******************************************************************************
 * Copyright (c) 2023 eclipse contributors and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     initially generated by jaxb reference implementation v2.2.8-b130911.1802
 *     Christoph Läubrich - adjusted to use the jakarta.xml namespace
 *******************************************************************************/
package org.eclipse.pde.osgi.xmlns.metatype.v1_4;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <pre>
 * &lt;simpleType name="Tscalar">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="String"/>
 *     &lt;enumeration value="Long"/>
 *     &lt;enumeration value="Double"/>
 *     &lt;enumeration value="Float"/>
 *     &lt;enumeration value="Integer"/>
 *     &lt;enumeration value="Byte"/>
 *     &lt;enumeration value="Character"/>
 *     &lt;enumeration value="Boolean"/>
 *     &lt;enumeration value="Short"/>
 *     &lt;enumeration value="Password"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Tscalar")
@XmlEnum
public enum Tscalar {

    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("Long")
    LONG("Long"),
    @XmlEnumValue("Double")
    DOUBLE("Double"),
    @XmlEnumValue("Float")
    FLOAT("Float"),
    @XmlEnumValue("Integer")
    INTEGER("Integer"),
    @XmlEnumValue("Byte")
    BYTE("Byte"),
    @XmlEnumValue("Character")
    CHARACTER("Character"),
    @XmlEnumValue("Boolean")
    BOOLEAN("Boolean"),
    @XmlEnumValue("Short")
    SHORT("Short"),
    @XmlEnumValue("Password")
    PASSWORD("Password");
    private final String value;

    Tscalar(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Tscalar fromValue(String v) {
        for (Tscalar c: Tscalar.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}