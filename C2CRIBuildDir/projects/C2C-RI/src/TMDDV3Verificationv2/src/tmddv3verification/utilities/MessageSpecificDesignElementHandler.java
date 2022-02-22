/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tmddv3verification.DesignElement;

/**
 *
 * @author TransCore ITS
 */
public class MessageSpecificDesignElementHandler {

    private HashMap<String,List<ElementMatch>> theMap = new HashMap<String,List<ElementMatch>>();
    private String message;
    private String relativePath;

    public MessageSpecificDesignElementHandler(String message, DesignElement theElement){
        this.message = message;
        this.relativePath = "\\"+message;
        Integer level = 1;
        addElement(theElement, relativePath, level);
    }
    
    public List<ElementMatch> getMatchingElements(String elementName){
        List<ElementMatch> responseList = new ArrayList<ElementMatch>();
        
        
        if (theMap.containsKey(elementName)){
            responseList = theMap.get(elementName);
        }
        
        return responseList;
    }
    
    public void addElement(DesignElement thisElement, String relativePath, Integer currentLevel){
        ElementMatch theMatch = new ElementMatch(thisElement,this.message);
 //       theMatch.getTheElement().setReferenceName(relativePath+"\\"+theMatch.getFullPath());
        if (!theMap.containsKey(thisElement.getElementName())){
            List<ElementMatch> matchList = new ArrayList<ElementMatch>();
            matchList.add(theMatch);
            theMap.put(thisElement.getReferenceName(), matchList);
        } else {
            theMap.get(thisElement.getReferenceName()).add(theMatch);
        }
        String newRelativePath = relativePath+"\\"+theMatch.getTheElement().getReferenceName();
        Integer theLevel = currentLevel+1;
        if (theLevel<=5){
        for (DesignElement anElement : thisElement.getSubElements()){
            addElement(anElement, newRelativePath, theLevel);
        }
        }
    }

}
