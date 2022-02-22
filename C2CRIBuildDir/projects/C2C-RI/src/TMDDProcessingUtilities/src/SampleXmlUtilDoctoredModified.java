/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */


/*
 * TODO:
 *  Comment on enumerations?
 *  Comment on facets?
 *  Have a verbose option?
 *  Have a sample data option, would create valid instance with sample data?
 *  Add the pattern facet; this is tricky, considering the relationship with length
 */
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDurationBuilder;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlTime;
import org.apache.xmlbeans.XmlGYear;
import org.apache.xmlbeans.XmlGYearMonth;
import org.apache.xmlbeans.XmlGMonth;
import org.apache.xmlbeans.XmlGMonthDay;
import org.apache.xmlbeans.XmlGDay;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlDuration;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.impl.util.Base64;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.impl.xpathgen.XPathGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaStringEnumEntry;

public class SampleXmlUtilDoctoredModified {

    private boolean _soapEnc;
    private static final int MAX_ELEMENTS = 2000;  // Initally set to 1000.  Assume it keeps memory usage in check
    private int _nElements;
    private String previousType;
    private int level = -1;
    private static ArrayList<String> elementPathList = new ArrayList<String>();
    private static ArrayList<String> elementPathTypeList = new ArrayList<String>();
    private static String messageName;
    private static String lastParent;
    private static String lastParentType;
    private static String messageValue;
    private static Integer lastparentMinOccurs=-1;
    private static Integer lastparentMaxOccurs=-1;
    private static boolean messageValueStored;
    private static Integer recordId=0;

    private SampleXmlUtilDoctoredModified(boolean soapEnc) {
        _soapEnc = soapEnc;
    }

    private void updateMsgValue(String value) {
        messageValueStored = true;
        messageValue = value;
    }

    private boolean isMsgValueStored() {
        return messageValueStored;
    }

    private String getMessageValue() {
        return messageValue;
    }

    private void clearMessageValue() {
        messageValueStored = false;
        messageValue = "";
    }

    public static String createSampleForType(SchemaType sType) {
        messageName = sType.getDocumentElementName().getLocalPart();
        lastParentType = messageName;
        lastParent = messageName;
        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        // Skip the document node
        cursor.toNextToken();
        // Using the type and the cursor, call the utility method to get a
        // sample XML payload for that Schema element
        new SampleXmlUtilDoctoredModified(false).createSampleForType(sType, cursor);
        lastParentType = "";
        lastParent = "";
        elementPathList.clear();
        elementPathTypeList.clear();
        // Cursor now contains the sample payload
        // Pretty print the result.  Note that the cursor is positioned at the
        // end of the doc so we use the original xml object that the cursor was
        // created upon to do the xmlText() against.
        XmlOptions options = new XmlOptions();
        options.put(XmlOptions.SAVE_PRETTY_PRINT);
        options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 2);
        options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
        String result = object.xmlText(options);
        return result;
    }
    Random _picker = new Random(1);

    /**
     * Cursor position
     * Before:
     * <theElement>^</theElement>
     * After:
     * <theElement><lots of stuff/>^</theElement>
     */
    private void createSampleForType(SchemaType stype, XmlCursor xmlc) {
        if (stype.getName() != null) {
            level++;
            previousType = stype.getName().getLocalPart();
//X            System.out.println("Increased Level to " + level + " for item "+stype.getName().toString());
//            System.out.println("Type Name = " + stype.getName());
        } else {
//X            System.out.println("Maintained Level @ " + level + " for item ??????");
//            System.out.println("    prevType Name = " + previousType);
        }
        if (_typeStack.contains(stype)) {
            return;
        }

        _typeStack.add(stype);

        try {
            if (stype.isSimpleType() || stype.isURType()) {
                processSimpleType(stype, xmlc);
                return;
            }

            // complex Type
            // <theElement>^</theElement>
            processAttributes(stype, xmlc);

            // <theElement attri1="string">^</theElement>
            switch (stype.getContentType()) {
                case SchemaType.NOT_COMPLEX_TYPE:
                case SchemaType.EMPTY_CONTENT:
                    // noop
                    break;
                case SchemaType.SIMPLE_CONTENT: {
                    processSimpleType(stype, xmlc);
                }
                break;
                case SchemaType.MIXED_CONTENT:
                    updateMsgValue(pick(WORDS) + " ");
                    xmlc.insertChars(getMessageValue());
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), xmlc, true);
                    }
                    updateMsgValue(pick(WORDS));
                    xmlc.insertChars(getMessageValue());
                    break;
                case SchemaType.ELEMENT_CONTENT:
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), xmlc, false);
                    }
                    break;
            }
        } finally {
            _typeStack.remove(_typeStack.size() - 1);


//            if (elementPathList.size() > 0) {
//                if (elementPathList.size() == (level)) {
////X                System.out.println("   ---- Removing Element "+ elementPathList.get(level-1)+ " @ array index=" +(level-1) + "  level = "+level+"  Array Size ="+elementPathList.size());
//                    elementPathList.remove(level - 1);
//                } else {
////X                    System.out.println("   **** Not Removing Element  @ array index=" +(level-1) + "  level = "+level+"  Array Size ="+elementPathList.size());
//                }
//            }
//            if (elementPathTypeList.size() > 0) {
//                if (elementPathTypeList.size() == (level)) {
//                    elementPathTypeList.remove(level - 1);
//                }
//            }
            if ((stype.getName() != null) && (level >= 0)) {
                level--;
//X                System.out.println("Decreased Level to " + level + " for item "+stype.getName().toString());
            }
//X            else System.out.println("   --** Did not Decrease Level as planned");
        }
    }

    private void processSimpleType(SchemaType stype, XmlCursor xmlc) {
        //     System.out.println("       Simple Element => " + stype.toString());
        String sample = sampleDataForSimpleType(stype);
        updateMsgValue(sample);
        xmlc.insertChars(sample);
    }

    private String SimpleTypeDetails(SchemaType sType, ElementDetails theDetails) {
//        "MinLength: "+", MaxLength: "+", MinInclusive: "+", MaxInclusive: "+", Enumeration: "+""
        if (XmlObject.type.equals(sType)) {
            return "anyType";
        }

        if (XmlAnySimpleType.type.equals(sType)) {
            return "anySimpleType";
        }

        if (sType.getSimpleVariety() == SchemaType.LIST) {
            SchemaType itemType = sType.getListItemType();
            XmlInteger length = (XmlInteger) sType.getFacet(SchemaType.FACET_LENGTH);
            if (length != null) {
                return ((Integer) length.getBigIntegerValue().intValue()).toString();
            } else {
                XmlInteger min = (XmlInteger) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
                XmlInteger max = (XmlInteger) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
                int minInt, maxInt;
                if (min == null) {
                    minInt = 0;
                } else {
                    minInt = min.getBigIntegerValue().intValue();
                }
                if (max == null) {
                    maxInt = Integer.MAX_VALUE;
                } else {
                    maxInt = max.getBigIntegerValue().intValue();
                }
                return ((Integer) minInt).toString() + "||" + ((Integer) maxInt).toString();
            }
        }
        if (sType.getSimpleVariety() == SchemaType.UNION) {
            SchemaType[] possibleTypes = sType.getUnionConstituentTypes();
            if (possibleTypes.length == 0) {
                return "";
            }
            String returnString = "";
            for (int ii = 0; ii < possibleTypes.length; ii++) {
//                returnString = returnString+SimpleTypeDetails(possibleTypes[ii]);
                // Added the condition to be able to keep enumeration values for ITIS codes.
                // Need to modify to handle user-defined enumeration values.
                if (ii<=1) returnString = SimpleTypeDetails(possibleTypes[ii], theDetails);
            }
            return returnString;
        }

//        XmlAnySimpleType[] enumValues = sType.getEnumerationValues();
        SchemaStringEnumEntry[] enumEntries = sType.getStringEnumEntries();
        if (enumEntries != null && enumEntries.length > 0) {
            String returnString = "{";

            for (int ii = 0; ii < enumEntries.length; ii++) {
                returnString = returnString + "(##) " + enumEntries[ii].getString();
                if (ii + 1 < enumEntries.length) {
                    returnString = returnString + "; ";
                }
            }
            returnString = returnString + "}";
//            returnString = "Enumeration: "+returnString;
            if (returnString.contains("insert-extension-values-here")) {
                returnString = returnString.replace("{(0) insert-extension-values-here}", "");
            } else {
                theDetails.setEnumeration(returnString);
            }
//            returnString = "MinLength: "+", MaxLength: "+", MinInclusive: "+", MaxInclusive: "+", Enumeration: "+returnString;
//            returnString = "MinLength: "+", MaxLength: "+", MinInclusive: "+", MaxInclusive: "+", Enumeration: "+returnString;

            return returnString;
        }
        try {
            switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
                default:
                case SchemaType.BTC_NOT_BUILTIN:
                    return "";

                case SchemaType.BTC_ANY_TYPE:
                case SchemaType.BTC_ANY_SIMPLE:
                    return "anything";

                case SchemaType.BTC_BOOLEAN:
                    return pick(2) == 0 ? "true" : "false";

                case SchemaType.BTC_BASE_64_BINARY: {
                    String result = null;
                    try {
                        result = new String(Base64.encode(formatToLength(pick(WORDS), sType).getBytes("utf-8")));
                    } catch (java.io.UnsupportedEncodingException e) {  /* Can't possibly happen */ }
                    return result;
                }

                case SchemaType.BTC_HEX_BINARY:
                    return HexBin.encode(formatToLength(pick(WORDS), sType));

                case SchemaType.BTC_ANY_URI:
                    return formatToLength("http://www." + pick(DNS1) + "." + pick(DNS2) + "/" + pick(WORDS) + "/" + pick(WORDS), sType);

                case SchemaType.BTC_QNAME:
                    return formatToLength("qname", sType);

                case SchemaType.BTC_NOTATION:
                    return formatToLength("notation", sType);

                case SchemaType.BTC_FLOAT:
                    return "1.5E2";
                case SchemaType.BTC_DOUBLE:
                    return "1.051732E7";
                case SchemaType.BTC_DECIMAL:
                    switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                        case SchemaType.BTC_SHORT:
                        case SchemaType.BTC_UNSIGNED_SHORT:
                        case SchemaType.BTC_BYTE:
                        case SchemaType.BTC_UNSIGNED_BYTE:
                        case SchemaType.BTC_INT:
                        case SchemaType.BTC_UNSIGNED_INT:
                        case SchemaType.BTC_LONG:
                        case SchemaType.BTC_UNSIGNED_LONG:
                        case SchemaType.BTC_INTEGER:
                        case SchemaType.BTC_NON_POSITIVE_INTEGER:
                        case SchemaType.BTC_NEGATIVE_INTEGER:
                        case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                        case SchemaType.BTC_POSITIVE_INTEGER:
                        default:
                        case SchemaType.BTC_DECIMAL:
                            theDetails.setMaxInclusive(Integer.parseInt(getMaxInclusive(sType)));
                            theDetails.setMinInclusive(Integer.parseInt(getMinInclusive(sType)));
                            return "MinLength: " + ", MaxLength: " + ", MinInclusive: " + getMinInclusive(sType) + ", MaxInclusive: " + getMaxInclusive(sType) + ", Enumeration1: " + "";
                    }

                case SchemaType.BTC_STRING: {
                    String tmpresult = "BTC_STRING";
                    try {
                        SimpleValue min = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
                        if (min == null) {
                            min = (SimpleValue) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
                        }
                        SimpleValue max = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
                        if (max == null) {
                            max = (SimpleValue) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
                        }
                        theDetails.setMaxLength(Integer.parseInt(max.getStringValue().toString()));
                        theDetails.setMinLength(Integer.parseInt(min.getStringValue().toString()));
                        tmpresult = "MinLength: " + min.getStringValue().toString() + ", MaxLength: " + max.getStringValue().toString() + ", MinInclusive: " + ", MaxInclusive: " + ", Enumeration2: " + "";

                    } catch (Exception e) // intValue can be out of range
                    {
                    }
                    return tmpresult;
                    //                return formatToLength(result, sType);
                }

                case SchemaType.BTC_DURATION:
                    return formatDuration(sType);

                case SchemaType.BTC_DATE_TIME:
                case SchemaType.BTC_TIME:
                case SchemaType.BTC_DATE:
                case SchemaType.BTC_G_YEAR_MONTH:
                case SchemaType.BTC_G_YEAR:
                case SchemaType.BTC_G_MONTH_DAY:
                case SchemaType.BTC_G_DAY:
                case SchemaType.BTC_G_MONTH:
                    return formatDate(sType);
            }
        } catch (Exception ex) {
            return "Exception Occurred!!" + ex.getMessage();
        }
    }

    private String getMinInclusive(SchemaType sType) {
        XmlDecimal xmlD;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        BigDecimal min = xmlD != null ? xmlD.getBigDecimalValue() : null;
        return min.toString();
    }

    private String getMaxInclusive(SchemaType sType) {
        XmlDecimal xmlD;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        BigDecimal max = xmlD != null ? xmlD.getBigDecimalValue() : null;
        return max.toString();
    }

    private String sampleDataForSimpleType(SchemaType sType) {
        if (XmlObject.type.equals(sType)) {
            return "anyType";
        }

        if (XmlAnySimpleType.type.equals(sType)) {
            return "anySimpleType";
        }

        if (sType.getSimpleVariety() == SchemaType.LIST) {
            SchemaType itemType = sType.getListItemType();
            StringBuffer sb = new StringBuffer();
            int length = pickLength(sType);
            if (length > 0) {
                sb.append(sampleDataForSimpleType(itemType));
            }
            for (int i = 1; i < length; i += 1) {
                sb.append(' ');
                sb.append(sampleDataForSimpleType(itemType));
            }
            return sb.toString();
        }

        if (sType.getSimpleVariety() == SchemaType.UNION) {
            SchemaType[] possibleTypes = sType.getUnionConstituentTypes();
            if (possibleTypes.length == 0) {
                return "";
            }
            return sampleDataForSimpleType(possibleTypes[pick(possibleTypes.length)]);
        }

        XmlAnySimpleType[] enumValues = sType.getEnumerationValues();
        if (enumValues != null && enumValues.length > 0) {
//            System.out.println("Value = " + (enumValues.length >1 ? enumValues.length-1: 1));
            return enumValues[pick((enumValues.length > 1 ? enumValues.length - 1 : 1))].getStringValue();
//            return"MinLength: "+", MaxLength: "+", MinInclusive: "+", MaxInclusive: "+", Enumeration: "+enumValues[pick(enumValues.length)].getStringValue();
        }



        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            default:
            case SchemaType.BTC_NOT_BUILTIN:
                return "";

            case SchemaType.BTC_ANY_TYPE:
            case SchemaType.BTC_ANY_SIMPLE:
                return "anything";

            case SchemaType.BTC_BOOLEAN:
                return pick(2) == 0 ? "true" : "false";

            case SchemaType.BTC_BASE_64_BINARY: {
                String result = null;
                try {
                    result = new String(Base64.encode(formatToLength(pick(WORDS), sType).getBytes("utf-8")));
                } catch (java.io.UnsupportedEncodingException e) {  /* Can't possibly happen */ }
                return result;
            }

            case SchemaType.BTC_HEX_BINARY:
                return HexBin.encode(formatToLength(pick(WORDS), sType));

            case SchemaType.BTC_ANY_URI:
                return formatToLength("http://www." + pick(DNS1) + "." + pick(DNS2) + "/" + pick(WORDS) + "/" + pick(WORDS), sType);

            case SchemaType.BTC_QNAME:
                return formatToLength("qname", sType);

            case SchemaType.BTC_NOTATION:
                return formatToLength("notation", sType);

            case SchemaType.BTC_FLOAT:
                return "1.5E2";
            case SchemaType.BTC_DOUBLE:
                return "1.051732E7";
            case SchemaType.BTC_DECIMAL:
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_SHORT:
                        return formatDecimal("1", sType);
                    case SchemaType.BTC_UNSIGNED_SHORT:
                        return formatDecimal("5", sType);
                    case SchemaType.BTC_BYTE:
                        return formatDecimal("2", sType);
                    case SchemaType.BTC_UNSIGNED_BYTE:
                        return formatDecimal("6", sType);
                    case SchemaType.BTC_INT:
                        return formatDecimal("3", sType);
                    case SchemaType.BTC_UNSIGNED_INT:
                        return formatDecimal("7", sType);
                    case SchemaType.BTC_LONG:
                        return formatDecimal("10", sType);
                    case SchemaType.BTC_UNSIGNED_LONG:
                        return formatDecimal("11", sType);
                    case SchemaType.BTC_INTEGER:
                        return formatDecimal("100", sType);
                    case SchemaType.BTC_NON_POSITIVE_INTEGER:
                        return formatDecimal("-200", sType);
                    case SchemaType.BTC_NEGATIVE_INTEGER:
                        return formatDecimal("-201", sType);
                    case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                        return formatDecimal("200", sType);
                    case SchemaType.BTC_POSITIVE_INTEGER:
                        return formatDecimal("201", sType);
                    default:
                    case SchemaType.BTC_DECIMAL:
                        return formatDecimal("1000.00", sType);
                }

            case SchemaType.BTC_STRING: {
                String result;
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_STRING:
                    case SchemaType.BTC_NORMALIZED_STRING:
                        result = "string";
                        break;

                    case SchemaType.BTC_TOKEN:
                        result = "token";
                        break;

                    default:
                        result = "string";
                        break;
                }

                return formatToLength(result, sType);
            }

            case SchemaType.BTC_DURATION:
                return formatDuration(sType);

            case SchemaType.BTC_DATE_TIME:
            case SchemaType.BTC_TIME:
            case SchemaType.BTC_DATE:
            case SchemaType.BTC_G_YEAR_MONTH:
            case SchemaType.BTC_G_YEAR:
            case SchemaType.BTC_G_MONTH_DAY:
            case SchemaType.BTC_G_DAY:
            case SchemaType.BTC_G_MONTH:
                return formatDate(sType);
        }
    }
    // a bit from the Aenid
    public static final String[] WORDS = new String[]{
        "ipsa", "iovis", "rapidum", "iaculata", "e", "nubibus", "ignem",
        "disiecitque", "rates", "evertitque", "aequora", "ventis",
        "illum", "exspirantem", "transfixo", "pectore", "flammas",
        "turbine", "corripuit", "scopuloque", "infixit", "acuto",
        "ast", "ego", "quae", "divum", "incedo", "regina", "iovisque",
        "et", "soror", "et", "coniunx", "una", "cum", "gente", "tot", "annos",
        "bella", "gero", "et", "quisquam", "numen", "iunonis", "adorat",
        "praeterea", "aut", "supplex", "aris", "imponet", "honorem",
        "talia", "flammato", "secum", "dea", "corde", "volutans",
        "nimborum", "in", "patriam", "loca", "feta", "furentibus", "austris",
        "aeoliam", "venit", "hic", "vasto", "rex", "aeolus", "antro",
        "luctantis", "ventos", "tempestatesque", "sonoras",
        "imperio", "premit", "ac", "vinclis", "et", "carcere", "frenat",
        "illi", "indignantes", "magno", "cum", "murmure", "montis",
        "circum", "claustra", "fremunt", "celsa", "sedet", "aeolus", "arce",
        "sceptra", "tenens", "mollitque", "animos", "et", "temperat", "iras",
        "ni", "faciat", "maria", "ac", "terras", "caelumque", "profundum",
        "quippe", "ferant", "rapidi", "secum", "verrantque", "per", "auras",
        "sed", "pater", "omnipotens", "speluncis", "abdidit", "atris",
        "hoc", "metuens", "molemque", "et", "montis", "insuper", "altos",
        "imposuit", "regemque", "dedit", "qui", "foedere", "certo",
        "et", "premere", "et", "laxas", "sciret", "dare", "iussus", "habenas",};
    private static final String[] DNS1 = new String[]{"corp", "your", "my", "sample", "company", "test", "any"};
    private static final String[] DNS2 = new String[]{"com", "org", "com", "gov", "org", "com", "org", "com", "edu"};

    private int pick(int n) {
        return _picker.nextInt(n);
    }

    private String pick(String[] a) {
        return a[pick(a.length)];
    }

    private String pick(String[] a, int count) {
        if (count <= 0) {
            return "";
        }

        int i = pick(a.length);
        StringBuffer sb = new StringBuffer(a[i]);
        while (count-- > 0) {
            i += 1;
            if (i >= a.length) {
                i = 0;
            }
            sb.append(' ');
            sb.append(a[i]);
        }
        return sb.toString();
    }

    private String pickDigits(int digits) {
        StringBuffer sb = new StringBuffer();
        while (digits-- > 0) {
            sb.append(Integer.toString(pick(10)));
        }
        return sb.toString();
    }

    private int pickLength(SchemaType sType) {
        XmlInteger length = (XmlInteger) sType.getFacet(SchemaType.FACET_LENGTH);
        if (length != null) {
            return length.getBigIntegerValue().intValue();
        }
        XmlInteger min = (XmlInteger) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
        XmlInteger max = (XmlInteger) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
        int minInt, maxInt;
        if (min == null) {
            minInt = 0;
        } else {
            minInt = min.getBigIntegerValue().intValue();
        }
        if (max == null) {
            maxInt = Integer.MAX_VALUE;
        } else {
            maxInt = max.getBigIntegerValue().intValue();
        }
        // We try to keep the length of the array within reasonable limits,
        // at least 1 item and at most 3 if possible
        if (minInt == 0 && maxInt >= 1) {
            minInt = 1;
        }
        if (maxInt > minInt + 2) {
            maxInt = minInt + 2;
        }
        if (maxInt < minInt) {
            maxInt = minInt;
        }
        return minInt + pick(maxInt - minInt);
    }

    /**
     * Formats a given string to the required length, using the following operations:
     * - append the source string to itself as necessary to pass the minLength;
     * - truncate the result of previous step, if necessary, to keep it within minLength.
     */
    private String formatToLength(String s, SchemaType sType) {
        String result = s;
        try {
            SimpleValue min = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (min == null) {
                min = (SimpleValue) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
            }
            if (min != null) {
                int len = min.getIntValue();
                while (result.length() < len) {
                    result = result + result;
                }
            }
            SimpleValue max = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (max == null) {
                max = (SimpleValue) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
            }
            if (max != null) {
                int len = max.getIntValue();
                if (result.length() > len) {
                    result = result.substring(0, len);
                }
            }
        } catch (Exception e) // intValue can be out of range
        {
        }
        return result;
    }

    private String formatDecimal(String start, SchemaType sType) {
        BigDecimal result = new BigDecimal(start);
        XmlDecimal xmlD;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        BigDecimal min = xmlD != null ? xmlD.getBigDecimalValue() : null;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        BigDecimal max = xmlD != null ? xmlD.getBigDecimalValue() : null;
        boolean minInclusive = true, maxInclusive = true;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal minExcl = xmlD.getBigDecimalValue();
            if (min == null || min.compareTo(minExcl) < 0) {
                min = minExcl;
                minInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal maxExcl = xmlD.getBigDecimalValue();
            if (max == null || max.compareTo(maxExcl) > 0) {
                max = maxExcl;
                maxInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_TOTAL_DIGITS);
        int totalDigits = -1;
        if (xmlD != null) {
            totalDigits = xmlD.getBigDecimalValue().intValue();

            StringBuffer sb = new StringBuffer(totalDigits);
            for (int i = 0; i < totalDigits; i++) {
                sb.append('9');
            }
            BigDecimal digitsLimit = new BigDecimal(sb.toString());
            if (max != null && max.compareTo(digitsLimit) > 0) {
                max = digitsLimit;
                maxInclusive = true;
            }
            digitsLimit = digitsLimit.negate();
            if (min != null && min.compareTo(digitsLimit) < 0) {
                min = digitsLimit;
                minInclusive = true;
            }
        }

        int sigMin = min == null ? 1 : result.compareTo(min);
        int sigMax = max == null ? -1 : result.compareTo(max);
        boolean minOk = sigMin > 0 || sigMin == 0 && minInclusive;
        boolean maxOk = sigMax < 0 || sigMax == 0 && maxInclusive;

        // Compute the minimum increment
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_FRACTION_DIGITS);
        int fractionDigits = -1;
        BigDecimal increment;
        if (xmlD == null) {
            increment = new BigDecimal(1);
        } else {
            fractionDigits = xmlD.getBigDecimalValue().intValue();
            if (fractionDigits > 0) {
                StringBuffer sb = new StringBuffer("0.");
                for (int i = 1; i < fractionDigits; i++) {
                    sb.append('0');
                }
                sb.append('1');
                increment = new BigDecimal(sb.toString());
            } else {
                increment = new BigDecimal(1.0);
            }
        }

        if (minOk && maxOk) {
            // OK
        } else if (minOk && !maxOk) {
            // TOO BIG
            if (maxInclusive) {
                result = max;
            } else {
                result = max.subtract(increment);
            }
        } else if (!minOk && maxOk) {
            // TOO SMALL
            if (minInclusive) {
                result = min;
            } else {
                result = min.add(increment);
            }
        } else {
            // MIN > MAX!!
        }

        // We have the number
        // Adjust the scale according to the totalDigits and fractionDigits
        int digits = 0;
        BigDecimal ONE = new BigDecimal(BigInteger.ONE);
        for (BigDecimal n = result; n.abs().compareTo(ONE) >= 0; digits++) {
            n = n.movePointLeft(1);
        }

        if (fractionDigits > 0) {
            if (totalDigits >= 0) {
                result = result.setScale(Math.max(fractionDigits, totalDigits - digits));
            } else {
                result = result.setScale(fractionDigits);
            }
        } else if (fractionDigits == 0) {
            result = result.setScale(0);
        }

        return result.toString();
    }

    private String formatDuration(SchemaType sType) {
        XmlDuration d =
                (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        GDuration minInclusive = null;
        if (d != null) {
            minInclusive = d.getGDurationValue();
        }

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        GDuration maxInclusive = null;
        if (d != null) {
            maxInclusive = d.getGDurationValue();
        }

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        GDuration minExclusive = null;
        if (d != null) {
            minExclusive = d.getGDurationValue();
        }

        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        GDuration maxExclusive = null;
        if (d != null) {
            maxExclusive = d.getGDurationValue();
        }

        GDurationBuilder gdurb = new GDurationBuilder();
        BigInteger min, max;

        gdurb.setSecond(pick(800000));
        gdurb.setMonth(pick(20));

        // Years
        // Months
        // Days
        // Hours
        // Minutes
        // Seconds
        // Fractions
        if (minInclusive != null) {
            if (gdurb.getYear() < minInclusive.getYear()) {
                gdurb.setYear(minInclusive.getYear());
            }
            if (gdurb.getMonth() < minInclusive.getMonth()) {
                gdurb.setMonth(minInclusive.getMonth());
            }
            if (gdurb.getDay() < minInclusive.getDay()) {
                gdurb.setDay(minInclusive.getDay());
            }
            if (gdurb.getHour() < minInclusive.getHour()) {
                gdurb.setHour(minInclusive.getHour());
            }
            if (gdurb.getMinute() < minInclusive.getMinute()) {
                gdurb.setMinute(minInclusive.getMinute());
            }
            if (gdurb.getSecond() < minInclusive.getSecond()) {
                gdurb.setSecond(minInclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(minInclusive.getFraction()) < 0) {
                gdurb.setFraction(minInclusive.getFraction());
            }
        }

        if (maxInclusive != null) {
            if (gdurb.getYear() > maxInclusive.getYear()) {
                gdurb.setYear(maxInclusive.getYear());
            }
            if (gdurb.getMonth() > maxInclusive.getMonth()) {
                gdurb.setMonth(maxInclusive.getMonth());
            }
            if (gdurb.getDay() > maxInclusive.getDay()) {
                gdurb.setDay(maxInclusive.getDay());
            }
            if (gdurb.getHour() > maxInclusive.getHour()) {
                gdurb.setHour(maxInclusive.getHour());
            }
            if (gdurb.getMinute() > maxInclusive.getMinute()) {
                gdurb.setMinute(maxInclusive.getMinute());
            }
            if (gdurb.getSecond() > maxInclusive.getSecond()) {
                gdurb.setSecond(maxInclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(maxInclusive.getFraction()) > 0) {
                gdurb.setFraction(maxInclusive.getFraction());
            }
        }

        if (minExclusive != null) {
            if (gdurb.getYear() <= minExclusive.getYear()) {
                gdurb.setYear(minExclusive.getYear() + 1);
            }
            if (gdurb.getMonth() <= minExclusive.getMonth()) {
                gdurb.setMonth(minExclusive.getMonth() + 1);
            }
            if (gdurb.getDay() <= minExclusive.getDay()) {
                gdurb.setDay(minExclusive.getDay() + 1);
            }
            if (gdurb.getHour() <= minExclusive.getHour()) {
                gdurb.setHour(minExclusive.getHour() + 1);
            }
            if (gdurb.getMinute() <= minExclusive.getMinute()) {
                gdurb.setMinute(minExclusive.getMinute() + 1);
            }
            if (gdurb.getSecond() <= minExclusive.getSecond()) {
                gdurb.setSecond(minExclusive.getSecond() + 1);
            }
            if (gdurb.getFraction().compareTo(minExclusive.getFraction()) <= 0) {
                gdurb.setFraction(minExclusive.getFraction().add(new BigDecimal(0.001)));
            }
        }

        if (maxExclusive != null) {
            if (gdurb.getYear() > maxExclusive.getYear()) {
                gdurb.setYear(maxExclusive.getYear());
            }
            if (gdurb.getMonth() > maxExclusive.getMonth()) {
                gdurb.setMonth(maxExclusive.getMonth());
            }
            if (gdurb.getDay() > maxExclusive.getDay()) {
                gdurb.setDay(maxExclusive.getDay());
            }
            if (gdurb.getHour() > maxExclusive.getHour()) {
                gdurb.setHour(maxExclusive.getHour());
            }
            if (gdurb.getMinute() > maxExclusive.getMinute()) {
                gdurb.setMinute(maxExclusive.getMinute());
            }
            if (gdurb.getSecond() > maxExclusive.getSecond()) {
                gdurb.setSecond(maxExclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(maxExclusive.getFraction()) > 0) {
                gdurb.setFraction(maxExclusive.getFraction());
            }
        }

        gdurb.normalize();
        return gdurb.toString();
    }

    private String formatDate(SchemaType sType) {
        GDateBuilder gdateb = new GDateBuilder(new Date(1000L * pick(365 * 24 * 60 * 60) + (30L + pick(20)) * 365 * 24 * 60 * 60 * 1000));
        GDate min = null, max = null;
        GDate temp;

        // Find the min and the max according to the type
        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            case SchemaType.BTC_DATE_TIME: {
                XmlDateTime x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_TIME: {
                XmlTime x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_DATE: {
                XmlDate x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_YEAR_MONTH: {
                XmlGYearMonth x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_YEAR: {
                XmlGYear x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_MONTH_DAY: {
                XmlGMonthDay x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_DAY: {
                XmlGDay x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_MONTH: {
                XmlGMonth x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }

                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
        }

        if (min != null && max == null) {
            if (min.compareToGDate(gdateb) >= 0) {
                // Reset the date to min + (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min == null && max != null) {
            if (max.compareToGDate(gdateb) <= 0) {
                // Reset the date to max - (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 0 - pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min != null && max != null) {
            if (min.compareToGDate(gdateb) >= 0 || max.compareToGDate(gdateb) <= 0) {
                // Find a date between the two
                Calendar c = min.getCalendar();
                Calendar cmax = max.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 1);
                if (c.after(cmax)) {
                    c.add(Calendar.HOUR_OF_DAY, -1);
                    c.add(Calendar.MINUTE, 1);
                    if (c.after(cmax)) {
                        c.add(Calendar.MINUTE, -1);
                        c.add(Calendar.SECOND, 1);
                        if (c.after(cmax)) {
                            c.add(Calendar.SECOND, -1);
                            c.add(Calendar.MILLISECOND, 1);
                            if (c.after(cmax)) {
                                c.add(Calendar.MILLISECOND, -1);
                            }
                        }
                    }
                }
                gdateb = new GDateBuilder(c);
            }
        }

        gdateb.setBuiltinTypeCode(sType.getPrimitiveType().getBuiltinTypeCode());
        if (pick(2) == 0) {
            gdateb.clearTimeZone();
        }
        return gdateb.toString();
    }

    private SchemaType closestBuiltin(SchemaType sType) {
        while (!sType.isBuiltinType()) {
            sType = sType.getBaseType();
        }
        return sType;
    }

    /**
     * Cracks a combined QName of the form URL:localname
     */
    public static QName crackQName(String qName) {
        String ns;
        String name;

        int index = qName.lastIndexOf(':');
        if (index >= 0) {
            ns = qName.substring(0, index);
            name = qName.substring(index + 1);
        } else {
            ns = "";
            name = qName;
        }

        return new QName(ns, name);
    }

    /**
     * Cursor position:
     * Before this call:
     * <outer><foo/>^</outer>  (cursor at the ^)
     * After this call:
     * <<outer><foo/><bar/>som text<etc/>^</outer>
     */
    private void processParticle(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        int loop = determineMinMaxForSample(sp, xmlc);

        while (loop-- > 0) {
            switch (sp.getParticleType()) {
                case (SchemaParticle.ELEMENT):
                    processElement(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.SEQUENCE):
                    processSequence(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.CHOICE):
                    processChoice(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.ALL):
                    processAll(sp, xmlc, mixed);
                    break;
                case (SchemaParticle.WILDCARD):
                    processWildCard(sp, xmlc, mixed);
                    break;
                default:
                // throw new Exception("No Match on Schema Particle Type: " + String.valueOf(sp.getParticleType()));
            }

        }
    }

    private int determineMinMaxForSample(SchemaParticle sp, XmlCursor xmlc) {
        int minOccurs = sp.getIntMinOccurs();
        int maxOccurs = sp.getIntMaxOccurs();

        if (minOccurs == maxOccurs) {
            return minOccurs;
        }

        int result = minOccurs;
        if (result == 0 && _nElements < MAX_ELEMENTS) {
            result = 1;
        }
        if (_nElements > MAX_ELEMENTS) {
            System.out.println("****** ERROR:  MAX_ELEMENTS Exceeded ******");
        }

        if (sp.getParticleType() != SchemaParticle.ELEMENT) {
            return result;
        }

        // it probably only makes sense to put comments in front of individual elements that repeat

        if (sp.getMaxOccurs() == null) {
            // xmlc.insertComment("The next " + getItemNameOrType(sp, xmlc) + " may be repeated " + minOccurs + " or more times");
            if (minOccurs == 0) {
                xmlc.insertComment("Zero or more repetitions:");
            } else {
                xmlc.insertComment(minOccurs + " or more repetitions:");
            }
        } else if (sp.getIntMaxOccurs() > 1) {
            xmlc.insertComment(minOccurs + " to " + String.valueOf(sp.getMaxOccurs()) + " repetitions:");
        } else {
            xmlc.insertComment("Optional:");
        }
        return result;
    }

    /*
    Return a name for the element or the particle type to use in the comment for minoccurs, max occurs
     */
    private String getItemNameOrType(SchemaParticle sp, XmlCursor xmlc) {
        String elementOrTypeName = null;
        if (sp.getParticleType() == SchemaParticle.ELEMENT) {
            elementOrTypeName = "Element (" + sp.getName().getLocalPart() + ")";
        } else {
            elementOrTypeName = printParticleType(sp.getParticleType());
        }
        return elementOrTypeName;
    }

    private void processElement(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        // cast as schema local element
        SchemaLocalElement element = (SchemaLocalElement) sp;
        /// ^  -> <elemenname></elem>^


        String pad = "";
        String previousPath = "";
        String parent = "";
        String parentType = "";
        String outputLine = "";

        try {
            for (int ii = 0; ii < level; ii++) {
                pad = pad + "  ";
            }

            for (int ii = 0; ii < elementPathList.size(); ii++) {
                previousPath = previousPath + "\\" + elementPathList.get(ii);
            }
            if (elementPathList.size() > 0) {
                parent = elementPathList.get(elementPathList.size() - 1);
            } else {
                elementPathList.add(lastParent);
            }
            if (elementPathTypeList.size() > 0) {
                parentType = elementPathTypeList.get(elementPathTypeList.size() - 1);
            } else{
                elementPathTypeList.add(lastParentType);
            }


            //            System.out.println(pad+"processElement => "+element.getName().getLocalPart()+ "   Prefix="+element.getType().getSourceName()+"    type "+element.getType().getName().getLocalPart()+ "   @level"+level + "    Message="+messageName+"   Parent =" + parent+"   ParentType"+parentType+"   Path= "+previousPath);
            String elementName = element.getName().getLocalPart();
             if (!parent.equals("")) {
                lastParent = parent;
            } else {
                parent = lastParent;
            }

            if (!parentType.equals("")) {
                lastParentType = parentType;
            } else {
                parentType = lastParentType;
            }
            String elementType = element.getType().getName().getLocalPart();
            if (messageName.equals(element.getName().getLocalPart())) {
                elementType = messageName;
            }
            outputLine = element.getName().getLocalPart() + "," + element.getType().getSourceName().substring(element.getType().getSourceName().lastIndexOf("/") + 1) + "," + elementType+","+ConceptMapper.getMapper().getConceptType(elementType) + "," + level + "," + messageName + "," + parent + "," + lastParentType;
            //       System.out.println(element.getName().getLocalPart() + "," + element.getType().getSourceName() + "," + element.getType().getName().getLocalPart() + "," + level + "," + messageName + "," + parent + "," + parentType + "," + previousPath);
//            if (level >= 0) {
//                elementPathList.add(level, element.getName().getLocalPart());
//                elementPathTypeList.add(level, element.getType().getName().getLocalPart());
//X                System.out.println("   +++ Adding Element "+ element.getName().getLocalPart()+ " @ Array location "+ (elementPathList.size()-1)+ " with level ="+level);
                elementPathList.add(element.getName().getLocalPart());
                elementPathTypeList.add(element.getType().getName().getLocalPart());
//            }
        } catch (NullPointerException nex) {
//              nex.printStackTrace();
            boolean cantFindType = false;
            String simpleElementName = "";
            if (elementPathList.size() > 0) {
                parent = elementPathList.get(elementPathList.size() - 1);
            }

            if (elementPathTypeList.size() > 0) {
                parentType = elementPathTypeList.get(elementPathTypeList.size() - 1);
            }

            if (!parentType.equals("")) {
                lastParentType = parentType;
            } else {
                parentType = lastParentType;
            }
            String elementType = "";
            if (messageName.equals(element.getName().getLocalPart())) {
                elementType = messageName;
            } else{
                elementType = "***TypeError***";
                cantFindType = true;
                if (element.getType().getBaseType().getName() != null) {
//                    System.out.print(element.getType().getBaseType().getName().getLocalPart());
                    simpleElementName = element.getType().getBaseType().getName().getLocalPart();
                    if (simpleElementName.equals("anyType")){
                        int ii = 2;
                    }
                    elementType = simpleElementName;
                } else {
                    elementType = peekAhead(sp);                    
                }
            }
            outputLine = element.getName().getLocalPart() + "," + element.getType().getSourceName().substring(element.getType().getSourceName().lastIndexOf("/") + 1) + "," + elementType+","+(cantFindType?(simpleElementName.equals("")?"data-frame":"data-element"):ConceptMapper.getMapper().getConceptType(elementType)) + "," + level + "," + messageName + "," + parent + "," + parentType;
            //      System.out.println(element.getName().getLocalPart() + "," + element.getType().getSourceName() + "," + "***TypeError***" + "," + level + "," + messageName + "," + parent + "," + parentType + "," + previousPath);
       //     if (level >= 0) {
//                elementPathList.add(level, element.getName().getLocalPart());
//                elementPathTypeList.add(level, element.getType().getName().getLocalPart());
//X                System.out.println("   +++ Adding Element "+ element.getName().getLocalPart()+ " @ Array location "+ (elementPathList.size()-1)+ " with level ="+level);
                elementPathList.add(element.getName().getLocalPart());
//                elementPathTypeList.add("***UnresolvableType****");
                elementPathTypeList.add(elementType);
       //     }
//                System.out.println(pad+"skippedElement*** => "+element.getName().getLocalPart()+ "   Prefix="+element.getType().getSourceName()+ "    type "+element.getType() + "   @level"+level);
//            level--;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(pad + "skippedElement*** => " + element.getName().getLocalPart() + "   Prefix=" + element.getType().getSourceName() + "    type " + element.getType() + "   @level" + level);
//            if ((level >0)&&(elementPathList.size()==(level))){
       //     if ((level >= 0)) {
//X                System.out.println("   +++ Adding Element(Exception) "+ element.getName().getLocalPart()+ " @ Array location "+ (elementPathList.size()-1)+ " with level ="+level);
//                elementPathList.add(level, element.getName().getLocalPart());
//                elementPathTypeList.add(level, element.getType().toString());
                elementPathList.add(element.getName().getLocalPart());
                elementPathTypeList.add(element.getType().toString());
       //     }
        }

//       System.out.println("Parent: "+parent + "   ParentType: "+parentType);

        try {
            xmlc.push();
            xmlc.toParent();

//            String xmlContent = xmlc.getObject().xmlText();
            String elementPath = XPathGenerator.generateXPath(xmlc, null, new TMDDMessageNameSpaceContext());
            ElementDetails theDetails = new ElementDetails();
            SimpleTypeDetails(element.getType(), theDetails);
            if (!element.getType().getBaseType().getName().getLocalPart().equals("anyType")) {
                if (element.getType().getBaseType().getName().getLocalPart().equals("anySimpleType")) {
                    SchemaType[] unionTypes = element.getType().getUnionConstituentTypes();
                    XmlAnySimpleType[] enumValues = unionTypes[1].getEnumerationValues();
                    if (enumValues != null && enumValues.length > 0) {
//            System.out.println("Value = " + (enumValues.length >1 ? enumValues.length-1: 1));
                        updateMsgValue(enumValues[pick((enumValues.length > 1 ? enumValues.length - 1 : 1))].getStringValue());
//            return"MinLength: "+", MaxLength: "+", MinInclusive: "+", MaxInclusive: "+", Enumeration: "+enumValues[pick(enumValues.length)].getStringValue();
                    }
                String[] enumValueStrings = theDetails.getEnumeration().split(";");
                int offset = theDetails.getMinInclusive();
                String fixedEnumList = "";
                for (int jj = 0; jj<enumValueStrings.length; jj++){
                    fixedEnumList = fixedEnumList.concat(enumValueStrings[jj].replace("##", String.valueOf(jj+offset)).concat(";"));
                }
                theDetails.setEnumeration(fixedEnumList.substring(0, fixedEnumList.lastIndexOf(";")));
                }


                if (!element.getType().getBaseType().getName().getLocalPart().equals("anySimpleType")) {
                    updateMsgValue(sampleDataForSimpleType(element.getType().getBaseType()));
                }
//                if (element.getType().getBaseType().getName().getLocalPart().equals(offset)updateMsgValue(createSampleForType(element.getType().getBaseType()));
                String [] outputParts = outputLine.split(",");
                System.out.println(++recordId+","+outputLine + "," + MessagePathMaker.getPath(outputParts[0], elementPath + "/" + element.getName().getLocalPart(),element.getMaxOccurs().intValue()) + "," + element.getType().getBaseType().getName().getLocalPart() + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMinOccurs:element.getMinOccurs())) + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMaxOccurs:element.getMaxOccurs())) + ", " + theDetails.output() + "," + isMsgValueStored() + "," + (isMsgValueStored() ? getMessageValue() : "")+",No,No,None");
                lastparentMinOccurs = element.getMinOccurs().intValue();
                lastparentMaxOccurs = element.getMaxOccurs().intValue();
                clearMessageValue();

            } else {

                if (!element.getType().isSimpleType()) {
                    if (element.getType().getBaseType().getName().getLocalPart().equals("anyType")){
                        clearMessageValue();
                    }
                    String [] outputParts = outputLine.split(",");
//                    System.out.println(++recordId+","+outputLine + "," + MessagePathMaker.getPath(outputParts[0], elementPath + "/" + element.getName().getLocalPart(),element.getType().getContentModel().getMaxOccurs().intValue()) + "," + element.getType().getBaseType().getName().getLocalPart() + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMinOccurs:element.getType().getContentModel().getMinOccurs())) + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMaxOccurs:element.getType().getContentModel().getMaxOccurs())) + ", " + theDetails.output() + "," + isMsgValueStored() + "," + (isMsgValueStored() ? getMessageValue() : "")+",No,No,None");
                    System.out.println(++recordId+","+outputLine + "," + MessagePathMaker.getPath(outputParts[0], elementPath + "/" + element.getName().getLocalPart(),element.getType().getContentModel().getMaxOccurs()!=null?element.getType().getContentModel().getMaxOccurs().intValue():1) + "," + element.getType().getBaseType().getName().getLocalPart() + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMinOccurs:element.getMinOccurs())) + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMaxOccurs:element.getMaxOccurs())) + ", " + theDetails.output() + "," + isMsgValueStored() + "," + (isMsgValueStored() ? getMessageValue() : "")+",No,No,None");

                    lastparentMinOccurs = element.getType().getContentModel().getMinOccurs().intValue();
                    lastparentMaxOccurs = element.getType().getContentModel().getMaxOccurs()!=null?element.getType().getContentModel().getMaxOccurs().intValue():1;
                    clearMessageValue();
//                    SchemaParticle[] frameList = element.getType().getContentModel().getParticleChildren();
//                    if (frameList != null) {
//                        for (int jj = 0; jj < frameList.length; jj++) {
//                            if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
//                            }
//                        }
//                    }
                } else {
                    String [] outputParts = outputLine.split(",");
                    System.out.println(++recordId+","+outputLine + "," + MessagePathMaker.getPath(outputParts[0], elementPath + "/" + element.getName().getLocalPart(),element.getType().getContentModel().getMaxOccurs().intValue()) + "," + element.getType().getBaseType().getName().getLocalPart() + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMinOccurs:element.getType().getContentModel().getIntMinOccurs())) + ", " + (element.getName().getLocalPart().endsWith("Msg")?1:(lastParentType.endsWith("Msg") ? lastparentMaxOccurs:element.getType().getContentModel().getMaxOccurs())) + ", " + theDetails.output() + "," + isMsgValueStored() + "," + (isMsgValueStored() ? getMessageValue() : "")+",No,No,None");
                    lastparentMinOccurs = element.getType().getContentModel().getMinOccurs().intValue();
                    lastparentMaxOccurs = element.getType().getContentModel().getMaxOccurs().intValue();
                    clearMessageValue();
                }

            }
//            System.out.println("BaseType: "+element.getType().getBaseType().getName().getLocalPart()+" MinOccurs: " + element.getMinOccurs() + " MaxOccurs: " + element.getMaxOccurs() + " Details: " + SimpleTypeDetails(element.getType()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        xmlc.pop();









        if (_soapEnc) {
            xmlc.insertElement(element.getName().getLocalPart()); // soap encoded? drop namespaces.
        } else {
            xmlc.insertElement(element.getName().getLocalPart(), element.getName().getNamespaceURI());
        }
        _nElements++;
        /// -> <elem>^</elem>
        xmlc.toPrevToken();
        // -> <elem>stuff^</elem>

        int elementPathListSize = elementPathList.size();
        int elementPathTypeListSize = elementPathTypeList.size();
        createSampleForType(element.getType(), xmlc);

        if (elementPathList.size()>0){
            for (int ii = elementPathListSize-1; ii< elementPathList.size();ii++){
//                System.out.println("Removing: "+elementPathList.get(ii));
                elementPathList.remove(ii);
            }
//            if (elementPathList.size()>0)System.out.println("Next Element:"+elementPathList.get(elementPathList.size() -1)+ "  Remaining Size: "+elementPathList.size());
        }

        if (elementPathTypeList.size()>0){
            for (int ii = elementPathTypeListSize-1; ii< elementPathTypeList.size();ii++){
//               System.out.println("Removing Type: "+elementPathTypeList.get(ii));
               elementPathTypeList.remove(ii);
            }
//            if (elementPathTypeList.size()>0)System.out.println("Next Element Type:"+elementPathTypeList.get(elementPathTypeList.size() -1) + "  Remaining Size: "+elementPathTypeList.size());
        }
        // -> <elem>stuff</elem>^
        xmlc.toNextToken();

    }

    private String peekAhead(SchemaParticle sp) {
        // cast as schema local element
        SchemaLocalElement element = (SchemaLocalElement) sp;
        /// ^  -> <elemenname></elem>^

        String result = "***TypeError***";


        try {
            SchemaType type = sp.getType();
            SchemaParticle[] spc = sp.getParticleChildren();
            spc = type.getContentModel().getParticleChildren();

            result = spc[0].getType().getName().getLocalPart();
//            String elementName = element.getName().getLocalPart();

//           String elementType = element.getType().getName().getLocalPart();
//            if (messageName.equals(element.getName().getLocalPart())) {
//                elementType = messageName;
//            }
        }    catch (Exception ex){
                SchemaType type = sp.getType();
                result = type.getOuterType().getName().getLocalPart();
//                result = "***TypeError***";
        }

        return result;
    }

    private void moveToken(int numToMove, XmlCursor xmlc) {
        for (int i = 0; i
                < Math.abs(numToMove); i++) {
            if (numToMove < 0) {
                xmlc.toPrevToken();


            } else {
                xmlc.toNextToken();


            }
        }
    }

    private static final String formatQName(XmlCursor xmlc, QName qName) {
        XmlCursor parent = xmlc.newCursor();
        parent.toParent();
        String prefix = parent.prefixForNamespace(qName.getNamespaceURI());
        parent.dispose();
        String name;


        if (prefix == null || prefix.length() == 0) {
            name = qName.getLocalPart();


        } else {
            name = prefix + ":" + qName.getLocalPart();


        }
        return name;


    }
    private static final QName HREF = new QName("href");
    private static final QName ID = new QName("id");
    private static final QName XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    private static final QName ENC_ARRAYTYPE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
    private static final QName ENC_OFFSET = new QName("http://schemas.xmlsoap.org/soap/encoding/", "offset");
    private static final Set SKIPPED_SOAP_ATTRS = new HashSet(Arrays.asList(new QName[]{HREF, ID, ENC_OFFSET}));

    private void processAttributes(SchemaType stype, XmlCursor xmlc) {
        if (_soapEnc) {
            QName typeName = stype.getName();


            if (typeName != null) {
                xmlc.insertAttributeWithValue(XSI_TYPE, formatQName(xmlc, typeName));


            }
        }

        SchemaProperty[] attrProps = stype.getAttributeProperties();


        for (int i = 0; i
                < attrProps.length; i++) {
            SchemaProperty attr = attrProps[i];


            if (_soapEnc) {
                if (SKIPPED_SOAP_ATTRS.contains(attr.getName())) {
                    continue;


                }
                if (ENC_ARRAYTYPE.equals(attr.getName())) {
                    SOAPArrayType arrayType = ((SchemaWSDLArrayType) stype.getAttributeModel().getAttribute(attr.getName())).getWSDLArrayType();


                    if (arrayType != null) {
                        xmlc.insertAttributeWithValue(attr.getName(), formatQName(xmlc, arrayType.getQName()) + arrayType.soap11DimensionString());


                    }
                    continue;


                }
            }
            String defaultValue = attr.getDefaultText();
            updateMsgValue(defaultValue == null
                    ? sampleDataForSimpleType(attr.getType()) : defaultValue);

            xmlc.insertAttributeWithValue(attr.getName(), getMessageValue());


        }
    }

    private void processSequence(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();


        for (int i = 0; i
                < spc.length; i++) {
            /// <parent>maybestuff^</parent>
            processParticle(spc[i], xmlc, mixed);
            //<parent>maybestuff...morestuff^</parent>

//            if (elementPathList.size() > 0) {
//                elementPathList.remove(elementPathList.size());
//            }
//            if (elementPathTypeList.size() > 0) {
//                elementPathTypeList.remove(elementPathList.size());
//            }

            if (mixed && i < spc.length - 1) {
                updateMsgValue(pick(WORDS));
                xmlc.insertChars(getMessageValue());


            }
        }
    }

    private void processChoice(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();
        xmlc.insertComment("You have a CHOICE of the next " + String.valueOf(spc.length) + " items at this level");


        for (int i = 0; i
                < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed);



//            if (elementPathList.size() > 0) {
//                if (elementPathList.size() == (level)) {
////X                System.out.println("   ---- Removing Element "+ elementPathList.get(level-1)+ " @ array index=" +(level-1) + "  level = "+level+"  Array Size ="+elementPathList.size());
//                    elementPathList.remove(level - 1);
//
//
//                } else {
////X                    System.out.println("   **** Not Removing Element  @ array index=" +(level-1) + "  level = "+level+"  Array Size ="+elementPathList.size());
//                }
//            }
//            if (elementPathTypeList.size() > 0) {
//                if (elementPathTypeList.size() == (level)) {
//                    elementPathTypeList.remove(level - 1);
//
//
//                }
//            }
            if (level >= 0) {
                level--;


            }
        }
    }

    private void processAll(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        System.out.println("*****    processAll => " + sp.getDefaultText());
        SchemaParticle[] spc = sp.getParticleChildren();
        // xmlc.insertComment("You may enter the following " + String.valueOf(spc.length) + " items in any order");


        for (int i = 0; i
                < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed);


            if (mixed && i < spc.length - 1) {
                updateMsgValue(pick(WORDS));
                xmlc.insertChars(getMessageValue());


            }
        }
    }

    private void processWildCard(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        xmlc.insertComment("You may enter ANY elements at this point");
        xmlc.insertElement("AnyElement");


    }

    /**
     * This method will get the base type for the schema type
     */
    private static QName getClosestName(SchemaType sType) {
        while (sType.getName() == null) {
            sType = sType.getBaseType();


        }

        return sType.getName();


    }

    private String printParticleType(int particleType) {
        StringBuffer returnParticleType = new StringBuffer();
        returnParticleType.append("Schema Particle Type: ");



        switch (particleType) {
            case SchemaParticle.ALL:
                returnParticleType.append("ALL\n");


                break;


            case SchemaParticle.CHOICE:
                returnParticleType.append("CHOICE\n");


                break;


            case SchemaParticle.ELEMENT:
                returnParticleType.append("ELEMENT\n");


                break;


            case SchemaParticle.SEQUENCE:
                returnParticleType.append("SEQUENCE\n");


                break;


            case SchemaParticle.WILDCARD:
                returnParticleType.append("WILDCARD\n");


                break;


            default:
                returnParticleType.append("Schema Particle Type Unknown");


                break;


        }

        return returnParticleType.toString();




    }
    private ArrayList _typeStack = new ArrayList();

    class ElementDetails {

        private int minLength = -1;
        private int maxLength = -1;
        private int minInclusive = -1;
        private int maxInclusive = -1;
        private String enumeration = "";

        public String getEnumeration() {
            return enumeration;
        }

        public void setEnumeration(String enumeration) {
            this.enumeration = enumeration;
        }

        public int getMaxInclusive() {
            return maxInclusive;
        }

        public void setMaxInclusive(int maxInclusive) {
            this.maxInclusive = maxInclusive;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        public int getMinInclusive() {
            return minInclusive;
        }

        public void setMinInclusive(int minInclusive) {
            this.minInclusive = minInclusive;
        }

        public int getMinLength() {
            return minLength;
        }

        public void setMinLength(int minLength) {
            this.minLength = minLength;
        }

        public String output() {
            String outputString = getMinLength() + "," + getMaxLength() + "," + getMinInclusive() + "," + getMaxInclusive() + "," + getEnumeration();

            return outputString;
        }
    }
}
