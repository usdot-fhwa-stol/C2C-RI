/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import tmddv3verification.utilities.OperationSpecification;
import tmddv3verification.utilities.TMDDWSDL;

/**
 *
 * @author TransCore ITS
 */
public class DesignDetail {

    private HashMap designMap = new HashMap();
    private TMDDWSDL tmddWSDL;

    public TMDDWSDL getTmddWSDL() {
        return tmddWSDL;
    }

    public HashMap getDesignMap() {
        return designMap;
    }

    public DesignDetail(String wsdlFile) {
        tmddWSDL = new TMDDWSDL(wsdlFile);

        Map servicesMap = tmddWSDL.getServiceNames();
        Iterator servicesIterator = servicesMap.keySet().iterator();
        while (servicesIterator.hasNext()) {
            String serviceName = (String) servicesIterator.next();
            QName serviceQName = (QName) servicesMap.get(serviceName);
            Map servicePorts = tmddWSDL.getServicePortNames(serviceQName);

            Iterator servicePortsIterator = servicePorts.keySet().iterator();
            while (servicePortsIterator.hasNext()) {
                String servicePort = (String) servicePortsIterator.next();
                QName servicePortQName = (QName) servicePorts.get(servicePort);
                List<String> operations = tmddWSDL.getServicePortOperationNames(serviceQName, servicePort);

                for (String operation : operations) {
                    DesignElement theElement = new DesignElement();
                    theElement.setElementName(operation);
                    theElement.setElementSource(wsdlFile.substring(wsdlFile.lastIndexOf("\\") + 1));
                    theElement.setElementType("dialog");
                    Map<String, OperationSpecification> theOperations = tmddWSDL.getServicePortOperationInformation(serviceQName, servicePort);
                    OperationSpecification theSpec = theOperations.get(operation);

                    if (theSpec != null) {
                        if (theSpec.getInputMessage() != null) {
                            List<String> inputMessages = theSpec.getInputMessage();

                            for (String message : inputMessages) {
                                DesignElement subElement = new DesignElement();
                                subElement.setElementName(message);
                                subElement.setElementType("message");
                                theElement.getSubElements().add(subElement);
                            }
                        }

                        if (theSpec.getOutputMessage() != null) {
                            List<String> outputMessages = theSpec.getOutputMessage();

                            for (String message : outputMessages) {
                                DesignElement subElement = new DesignElement();
                                subElement.setElementName(message);
                                subElement.setElementType("message");
                                theElement.getSubElements().add(subElement);
                            }
                        }

                        if (theSpec.getFaultMessage() != null) {

                            List<String> faultMessages = theSpec.getFaultMessage();
                            for (String message : faultMessages) {
                                DesignElement subElement = new DesignElement();
                                subElement.setElementName(message);
                                subElement.setElementType("message");
                                theElement.getSubElements().add(subElement);
                            }
                        }
                    }
                    designMap.put(operation, theElement);
                    System.out.println(operation + "," + wsdlFile.substring(wsdlFile.lastIndexOf("\\") + 1) + ",dialog");
                }


            }
        }
        SchemaType[] globalElems = tmddWSDL.getSchemaTypeSystem().documentTypes();
        SchemaType elem = null;
        String msgName = null;

        for (int ii = 0; ii < globalElems.length; ii++) {
            msgName = globalElems[ii].getDocumentElementName().getLocalPart();
            DesignElement theElement = new DesignElement();
            theElement.setElementName(msgName);
            theElement.setElementSource(globalElems[ii].getSourceName().substring(globalElems[ii].getSourceName().lastIndexOf("/") + 1));
            theElement.setElementType("message");
            theElement.setReferenceName("\\"+msgName);

            String messageType = "";
            if (globalElems[ii].getContentModel().getType().isAnonymousType()){
                theElement.setReferenceName("\\"+msgName+"\\"+globalElems[ii].getContentModel().getType().getContentModel().getParticleChild(0).getName().getLocalPart());
                messageType = globalElems[ii].getContentModel().getType().getContentModel().getParticleChild(0).getType().getName().getLocalPart();
                theElement.setMaxOccurs(globalElems[ii].getContentModel().getType().getContentModel().getIntMaxOccurs());
                theElement.setMinOccurs(globalElems[ii].getContentModel().getType().getContentModel().getIntMinOccurs());

            } else{
                messageType = globalElems[ii].getContentModel().getType().getName().getLocalPart();
                theElement.setMaxOccurs(globalElems[ii].getContentModel().getIntMaxOccurs());
                theElement.setMinOccurs(globalElems[ii].getContentModel().getIntMinOccurs());
            }
            theElement.setSubType(messageType);
            System.out.println(msgName + "," + globalElems[ii].getSourceName().substring(globalElems[ii].getSourceName().lastIndexOf("/") + 1) + ",message"+", "+messageType+","+theElement.getReferenceName()+","+theElement.getMaxOccurs());
            //            System.out.println("isAnonymousType? " + globalElems[ii].getContentModel().getType().isAnonymousType() + "  "+ messageType);
            getDesignMessageElementContents(globalElems[ii], theElement);

            designMap.put(msgName, theElement);
        }



        SchemaType[] tmddTypes = tmddWSDL.getSchemaTypeSystem().globalTypes();
        for (int ii = 0; ii < tmddTypes.length; ii++) {
            System.out.println(tmddTypes[ii].getName().getLocalPart() + "," + tmddTypes[ii].getSourceName().substring(tmddTypes[ii].getSourceName().lastIndexOf("/") + 1) + "," + (tmddTypes[ii].isSimpleType() ? "data-element" : "data-frame"));
            DesignElement theElement = new DesignElement();
            theElement.setElementName(tmddTypes[ii].getName().getLocalPart());
            theElement.setElementSource(tmddTypes[ii].getSourceName().substring(tmddTypes[ii].getSourceName().lastIndexOf("/") + 1));
            theElement.setElementType((tmddTypes[ii].isSimpleType() ? "data-element" : "data-frame"));

            if (! tmddTypes[ii].isSimpleType()) getDesignFrameElementContents(tmddTypes[ii], theElement);

            designMap.put(tmddTypes[ii].getName().getLocalPart(), theElement);
        }

    }

    private void getDesignMessageElementContents(SchemaType messageType, DesignElement messageElement) {
//            if (!globalTypes[ii].isSimpleType()) {
        SchemaParticle[] frameList = messageType.getContentModel().getParticleChildren();
        Integer elementIndex = 0;
        if (frameList != null) {
            for (int jj = 0; jj < frameList.length; jj++) {
                elementIndex = elementIndex + 1;
                if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
                    try {
//                                List<String> subElementReqs = getRequirements(frameList[jj]);
                        XmlInteger length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_LENGTH);
                        XmlInteger min = null;
                        if (length == null) {
                            min = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MIN_LENGTH);
                            length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MAX_LENGTH);
                        }

//                                if (subElementReqs.size() > 0) {
//                                    for (String theRequirement : subElementReqs) {
//                                        System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs()+","+frameList[jj].getMaxOccurs()+","+frameList[jj].getType().getBaseType().getName().getLocalPart()+","+min.getStringValue()+","+length.getStringValue());
//                                    }
//                                } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(frameList[jj].getType().getName().getLocalPart());
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        thisElement.setBaseType(frameList[jj].getType().getBaseType().getName().getLocalPart());
                        thisElement.setMaxInclusive(length.getBigIntegerValue().intValue());
                        thisElement.setMinInclusive(min.getBigIntegerValue().intValue());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + "," + frameList[jj].getMinOccurs() + "," + frameList[jj].getMaxOccurs() + "," + frameList[jj].getType().getBaseType().getName().getLocalPart() + "," + min.getStringValue() + "," + length.getStringValue()+","+thisElement.getReferenceName());

                        //                                }

                    } catch (Exception ex) {
                        try {

                            SchemaLocalElement element = (SchemaLocalElement) frameList[jj];
                            String type = "Unknown";
                            if (element.getType().isAnonymousType()) {
                                SchemaParticle[] thisParticleTry = frameList[jj].getType().getContentModel().getParticleChildren();
                                for (SchemaParticle thisParticle : thisParticleTry) {
                                    //                  List<String> subElementReqs = getRequirements(thisParticle.getType());
                                    type = thisParticle.getType().getName().getLocalPart();

//                                            if (subElementReqs.size() > 0) {
//                                            if (typeReqs.size() > 0) {
//                                                for (String theRequirement : typeReqs) {
//                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs() +","+frameList[jj].getType().getContentModel().getMaxOccurs()+","+frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart());
//                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement);
//                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element: " + frameList[jj].getName().getLocalPart() + "Type " + type + ",  :  **** ERROR Couldn't handle this one !!!");
//                                                }
//                                            } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getType().getContentModel().getIntMaxOccurs());
                        thisElement.setBaseType(frameList[jj].getType().getBaseType().getName().getLocalPart());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + "" + "," + frameList[jj].getMinOccurs() + "," + frameList[jj].getType().getContentModel().getMaxOccurs() + "," + frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart()+","+thisElement.getReferenceName());

                                    //                                           }

                                }
                                //                                       SchemaType thisTry = frameList[jj].getType();
                                //                    element.getType().getContentModel().
                                //                    type = element.getType().getListItemType().getName().getLocalPart();
                            }
                        } catch (Exception ex1) {
                            String type = "***UNKNOWN***";
                            if (frameList[jj].getType() != null) {
                                type = frameList[jj].getType().getBaseType().getName().getLocalPart();
                                //                                   ex1.printStackTrace();
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());
//                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element:  **** ERROR Couldn't handle this one At ALL!!!");
                            } else {
                                // Sometimes there is a choice list inside of a sequence.  See if that's happening here
                                if (frameList[jj].getParticleType() == SchemaParticle.CHOICE) {
                                    SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                    for (SchemaParticle thisParticle : theParticles) {
                                        if ((thisParticle.getType() != null) && (thisParticle.getType().getName() != null)) {
                                            type = thisParticle.getType().getName().getLocalPart();
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName("Unknown");
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        }

                                    }
                                    //                                          System.out.println(ii + "," + msgType + ", !!!! Yes This is A Choice !!!!");

                                } else {
                                    // Looks like it is a sequence inside of a choice
                                    SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                    for (SchemaParticle thisParticle : theParticles) {
                                        if ((thisParticle.getType() != null) && (thisParticle.getType().getName() != null)) {
                                            type = thisParticle.getType().getName().getLocalPart();
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + messageType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        }

                                    }

//                                       System.out.println(ii + "," + msgType + ", !!!! Figure Out how to handle embedded Complex Types !!!!???");
                                }
                            }
                        }
//                                ex.printStackTrace();
                    }
                }
            }

        }
//    }
    }

    private void getDesignFrameElementContents(SchemaType frameType, DesignElement messageElement) {
        //            if (!globalTypes[ii].isSimpleType()) {
        SchemaParticle[] frameList = frameType.getContentModel().getParticleChildren();
        Integer elementIndex = 1;
        if (frameList != null) {
            for (int jj = 0; jj < frameList.length; jj++) {
                if (frameList[jj].getParticleType() != SchemaParticle.WILDCARD) {
                    try {
                        //                                List<String> subElementReqs = getRequirements(frameList[jj]);
                        XmlInteger length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_LENGTH);
                        XmlInteger min = null;
                        if (length == null) {
                            min = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MIN_LENGTH);
                            length = (XmlInteger) frameList[jj].getType().getFacet(SchemaType.FACET_MAX_LENGTH);
                        }

                        //                                if (subElementReqs.size() > 0) {
                        //                                    for (String theRequirement : subElementReqs) {
                        //                                        System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs()+","+frameList[jj].getMaxOccurs()+","+frameList[jj].getType().getBaseType().getName().getLocalPart()+","+min.getStringValue()+","+length.getStringValue());
                        //                                    }
                        //                                } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(frameList[jj].getType().getName().getLocalPart());
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
//                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        thisElement.setReferenceName(frameList[jj].getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        thisElement.setBaseType(frameList[jj].getType().getBaseType().getName().getLocalPart());
                        thisElement.setMaxInclusive(length != null ? length.getBigIntegerValue().intValue():-1);
                        thisElement.setMinInclusive(min != null ? min.getBigIntegerValue().intValue():-1);
                        messageElement.getSubElements().add(thisElement);
                        System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + "," + frameList[jj].getMinOccurs() + "," + frameList[jj].getMaxOccurs() + "," + frameList[jj].getType().getBaseType().getName().getLocalPart() + "," + (min != null ? min.getStringValue():"") + "," + (length != null ? length.getStringValue() : "")+","+thisElement.getReferenceName());
                        //                                }

                    } catch (Exception ex) {
        //                ex.printStackTrace();
                        try {

                            SchemaLocalElement element = (SchemaLocalElement) frameList[jj];
                            String type = "Unknown";
                            if (element.getType().isAnonymousType()) {
                                SchemaParticle[] thisParticleTry = frameList[jj].getType().getContentModel().getParticleChildren();
                                for (SchemaParticle thisParticle : thisParticleTry) {
                                    //                  List<String> subElementReqs = getRequirements(thisParticle.getType());
                                    type = thisParticle.getType().getName().getLocalPart();

                                    //                                            if (subElementReqs.size() > 0) {
                                    //                                            if (typeReqs.size() > 0) {
                                    //                                                for (String theRequirement : typeReqs) {
                                    //                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement+","+frameList[jj].getMinOccurs() +","+frameList[jj].getType().getContentModel().getMaxOccurs()+","+frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart());
                                    //                                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + frameList[jj].getType().getName().getLocalPart() + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + theRequirement);
                                    //                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element: " + frameList[jj].getName().getLocalPart() + "Type " + type + ",  :  **** ERROR Couldn't handle this one !!!");
                                    //                                                }
                                    //                                            } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
//                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        thisElement.setReferenceName(frameList[jj].getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getType().getContentModel().getIntMaxOccurs());
                        thisElement.setBaseType(frameList[jj].getType().getBaseType().getName().getLocalPart());
                        messageElement.getSubElements().add(thisElement);
                                    System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + "," + "" + "," + frameList[jj].getMinOccurs() + "," + frameList[jj].getType().getContentModel().getMaxOccurs() + "," + frameList[jj].getType().getContentModel().getParticleChild(0).getName().getLocalPart()+","+thisElement.getReferenceName());

                                    //                                           }

                                }
                                //                                       SchemaType thisTry = frameList[jj].getType();
                                //                    element.getType().getContentModel().
                                //                    type = element.getType().getListItemType().getName().getLocalPart();
                            }
                        } catch (Exception ex1) {
                            String type = "***UNKNOWN***";
                            if (frameList[jj].getType() != null) {
                                type = frameList[jj].getType().getBaseType().getName().getLocalPart();
                                //                                   ex1.printStackTrace();
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+frameList[jj].getName().getLocalPart());
                        messageElement.getSubElements().add(thisElement);
                                System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + frameList[jj].getName().getLocalPart() + "," + type + "," + (frameList[jj].getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());
                                //                                    System.out.println(ii + "," + msgType + "," + (jj + 1) + "Element:  **** ERROR Couldn't handle this one At ALL!!!");
                            } else {
                                // Sometimes there is a choice list inside of a sequence.  See if that's happening here
                                if (frameList[jj].getParticleType() == SchemaParticle.CHOICE) {
                                    SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                    for (SchemaParticle thisParticle : theParticles) {
                                        if ((thisParticle.getType() != null) && (thisParticle.getType().getName() != null)) {
                                            type = thisParticle.getType().getName().getLocalPart();
                       DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((thisParticle.getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                                            System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName("Unknown");
                        thisElement.setElementType((thisParticle.getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                                            System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        }

                                    }
                                    //                                          System.out.println(ii + "," + msgType + ", !!!! Yes This is A Choice !!!!");

                                } else {
                                    // Looks like it is a sequence inside of a choice
                                    SchemaParticle[] theParticles = frameList[jj].getParticleChildren();
                                    for (SchemaParticle thisParticle : theParticles) {
                                        if ((thisParticle.getType() != null) && (thisParticle.getType().getName() != null)) {
                                            type = thisParticle.getType().getName().getLocalPart();
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((thisParticle.getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                                            System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + "," + type + "," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        } else {
                        DesignElement thisElement = new DesignElement();
                        thisElement.setElementName(type);
                        thisElement.setElementType((thisParticle.getType().isSimpleType() ? "data-element" : "data-frame"));
                        thisElement.setReferenceName(messageElement.getReferenceName()+"\\"+thisParticle.getName().getLocalPart());
                        thisElement.setMinOccurs(frameList[jj].getIntMinOccurs());
                        thisElement.setMaxOccurs(frameList[jj].getIntMaxOccurs());
                        messageElement.getSubElements().add(thisElement);
                                            System.out.println(elementIndex + "," + frameType.getName().getLocalPart() + "," + (jj + 1) + "," + thisParticle.getName().getLocalPart() + ",UNKNOWN," + (thisParticle.getType().isSimpleType() ? "data-element" : "data-frame") + ","+","+thisElement.getReferenceName());

                                        }

                                    }

                                    //                                       System.out.println(ii + "," + msgType + ", !!!! Figure Out how to handle embedded Complex Types !!!!???");
                                }
                            }
                        }
                        //                                ex.printStackTrace();
                    }
                }
            }

        }
        //
    }
}
