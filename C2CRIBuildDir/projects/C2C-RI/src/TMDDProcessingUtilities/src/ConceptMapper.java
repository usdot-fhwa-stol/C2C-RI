
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TransCore ITS
 */
public class ConceptMapper {
    private static HashMap<String, String> conceptMap = new HashMap<String, String>();

    private static ConceptMapper theMapper;

    public static ConceptMapper getMapper(){
        if (theMapper == null){
            theMapper = new ConceptMapper();
        }
        return theMapper;
    }
    private ConceptMapper(){
        
    }

    public static void setConcept(String elementType, String conceptType){
        if (!conceptMap.containsKey(elementType)){
            conceptMap.put(elementType, conceptType);
        }
    }

    public static String getConceptType(String elementType){
        if (conceptMap.containsKey(elementType)){
            return conceptMap.get(elementType);
        }
        return "*** No Type Defined ***";
    }
}
