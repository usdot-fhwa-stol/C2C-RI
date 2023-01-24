
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TransCore ITS
 */
public class MessagePathMaker {
    private static ArrayList<String> pathList = new ArrayList<String>();

    private static MessagePathMaker theMaker;
    private static boolean applyToNext=false;

    public static MessagePathMaker getMaker(){
        if (theMaker == null){
            theMaker = new MessagePathMaker();
        }
        return theMaker;
    }
    private MessagePathMaker(){
        
    }

    public static String getPath(String elementName, String path, Integer maxOccurs){
        if(maxOccurs > 1){
            if (elementName.endsWith("Msg")){
                applyToNext = true;
            } else {
            pathList.add(elementName);
            }
        } else if (applyToNext){
            pathList.add(elementName);
            applyToNext = false;
        }
        String [] pathParts = path.split("/");

        String newPath=path;
//        for (Integer ii=1;ii<pathParts.length; ii++){
            for (String thisPart: pathList){
                if (newPath.contains(thisPart+"/")){
                    newPath = newPath.replace(thisPart+"/", thisPart+"[*]/");
                } else if (newPath.contains(thisPart)){
                    newPath = newPath.replace(thisPart, thisPart+"[*]");
                }
//            }

        }
        return newPath;
    }

    public static void initialize(){
        pathList.clear();
    }

}
