/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface RINRTMSelectionProvider {
    // Returns the set of selected requirements as a Map with Key -> NeedID and Value -> RequirementID.
    public ArrayList<RINRTMSelection> getNRTMSelections();
}
