/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * The Class NRTMMatcher.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NRTMMatcher {

    /** The event. */
    private static String EVENTSTRING = "NRTMUpdateTable_Item";
    
    /** The idevent. */
    private static String IDEVENT = "ID";
    
    /** The unidevent. */
    private static String UNIDEVENT = "UNID";
    
    /** The requirementidevent. */
    private static String REQUIREMENTIDEVENT = "RequirementID";
    
    /** The matchtypeevent. */
    private static String MATCHTYPEEVENT = "MatchType";
    
    /** The selected event.  */
    private static String SELECTEDEVENT = "Selected";
    
    /** The nrtm match map. */
    private HashMap<String, ArrayList<NRTMInstance>> nrtmMatchMap = new HashMap();
    
    /** The log file. */
    private File logFile;
    
    /** The event reader. */
    private XMLEventReader eventReader = null;
    
    /** The event. */
    private XMLEvent event = null;


    /**
     * Instantiates a new nRTM matcher.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param fileName the file name
     * @throws Exception the exception
     */
    public NRTMMatcher(String fileName) throws Exception {

        logFile = new File(fileName);
        if (!logFile.exists()) {
            throw new Exception("Log file " + fileName + " does not exist.");
        }


        // First create a new XMLInputFactory
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Setup a new eventReader
        InputStream in = new FileInputStream(logFile);
        eventReader = inputFactory.createXMLEventReader(in);
        // Read the XML document




        while (eventReader.hasNext()) {
            NRTMInstance eventSet = new NRTMInstance();
            event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                // If we have a item element we create a new item
                if (EVENTSTRING.equals(startElement.getName().getLocalPart())) {
                    eventSet = processEvent();
                }

            }
            // If we reach the end of an item element we add it to the list
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (EVENTSTRING.equals(endElement.getName().getLocalPart())) {
                    if (nrtmMatchMap.containsKey(eventSet.getNeedID())) {
                        nrtmMatchMap.get(eventSet.getNeedID()).add(eventSet);
                    } else {
                        ArrayList<NRTMInstance> newList = new ArrayList();
                        newList.add(eventSet);
                        nrtmMatchMap.put(eventSet.getNeedID(), newList);
                    }

                }
            }

        }

    }

    /**
     * Gets the related requirements.
     *
     * @param needID the need id
     * @param requirementType the requirement type
     * @return the related requirements
     */
    public ArrayList<String> getRelatedRequirements(String needID, String requirementType) {
        ArrayList<String> thisArray = new ArrayList();

        Iterator thisIterator;
        thisIterator = nrtmMatchMap.keySet().iterator();
        while (thisIterator.hasNext()) {
            String thisNeedID = (String) thisIterator.next();
            for (NRTMInstance thisInstance : nrtmMatchMap.get(thisNeedID)) {
                if ((thisInstance.getRequirementType()!=null)&&thisInstance.getRequirementType().equalsIgnoreCase(requirementType)) {
                    if (thisNeedID.equals(needID)&&thisInstance.isSelected()){
                        if (!thisArray.contains(thisInstance.getRequirementID()))thisArray.add(thisInstance.getRequirementID());
                    }
                }
            }
        }

        return thisArray;
    }

    /**
     * Process event.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the nRTM instance
     * @throws SQLException the sQL exception
     */
    private NRTMInstance processEvent() throws SQLException {
        NRTMInstance eventSet = null;
        eventSet = new NRTMInstance();

        while (!(event.isEndElement() && (event.asEndElement().getName().getLocalPart().equals(EVENTSTRING)))) {

            try {
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(IDEVENT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setTraceID(Integer.parseInt(event.asCharacters().getData()));
                        }

                        event = eventReader.nextEvent();
                        continue;

                    } else if (event.asStartElement().getName().getLocalPart().equals(UNIDEVENT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setNeedID(event.asCharacters().getData());
                        }
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(SELECTEDEVENT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setSelected(Boolean.valueOf(event.asCharacters().getData()));
                        }
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(REQUIREMENTIDEVENT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setRequirementID(event.asCharacters().getData());
                        }
                        event = eventReader.nextEvent();
                        continue;
                    } else if (event.asStartElement().getName().getLocalPart().equals(MATCHTYPEEVENT)) {
                        event = eventReader.nextEvent();
                        if (event.isCharacters()) {
                            eventSet.setRequirementType(event.asCharacters().getData());
                            event = eventReader.nextEvent();
                            continue;
                        }
                    }
                }
                if (eventReader.hasNext()) {
                    event = eventReader.nextEvent();

                } else {
                    System.out.println("No More Events");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        return eventSet;
    }

    /**
     * The Class NRTMInstance.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class NRTMInstance {

        /** The need id. */
        private String needID;
        
        /** The trace id. */
        private Integer traceID;
        
        /** The requirement id. */
        private String requirementID;
        
        /** The requirement type. */
        private String requirementType;

        /** The requirement is selected. */
        private boolean selected;
        
        /**
         * Gets the trace id.
         *
         * @return the trace id
         */
        public Integer getTraceID() {
            return traceID;
        }

        /**
         * Gets the need id.
         *
         * @return the need id
         */
        public String getNeedID() {
            return needID;
        }

        /**
         * Sets the need id.
         *
         * @param needID the new need id
         */
        public void setNeedID(String needID) {
            this.needID = needID;
        }

        /**
         * Sets the trace id.
         *
         * @param traceID the new trace id
         */
        public void setTraceID(Integer traceID) {
            this.traceID = traceID;
        }

        /**
         * Gets the requirement id.
         *
         * @return the requirement id
         */
        public String getRequirementID() {
            return requirementID;
        }

        /**
         * Sets the requirement id.
         *
         * @param requirementID the new requirement id
         */
        public void setRequirementID(String requirementID) {
            this.requirementID = requirementID;
        }

        /**
         * Gets the requirement type.
         *
         * @return the requirement type
         */
        public String getRequirementType() {
            return requirementType;
        }

        /**
         * Sets the requirement type.
         *
         * @param requirementType the new requirement type
         */
        public void setRequirementType(String requirementType) {
            this.requirementType = requirementType;
        }
        

        /**
         * Gets the selected flag.
         *
         * @return the selection flag value
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * Sets the selected flag.
         *
         * @param selected the selection flag value
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
        
    }
}
