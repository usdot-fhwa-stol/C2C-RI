/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

import tmddv3verification.DesignElement;

/**
 *
 * @author TransCore ITS, LLC
 */
public class ElementMatch {
        private DesignElement theElement;
        private String theMessage;
        private String fullPath;

        public ElementMatch(DesignElement theElement, String theMessage){
            this.theElement = theElement;
            this.theMessage = theMessage;
            fullPath = "\\"+theMessage+theElement.getReferenceName();
        }

        public String getFullPath() {
            return fullPath;
        }

        public DesignElement getTheElement() {
            return theElement;
        }

        public String getTheMessage() {
            return theMessage;
        }


    

}
