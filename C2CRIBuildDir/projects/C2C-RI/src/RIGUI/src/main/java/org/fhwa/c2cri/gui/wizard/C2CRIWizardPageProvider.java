/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard;

import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface C2CRIWizardPageProvider {
       public List<C2CRIWizardPage> getPages();
       public WizardPage createPage(List<WizardPage> path, WizardSettings settings, DefaultTreeModel treeModel);       
       public void setButtonListener(C2CRIExtraButtonListener buttonListener); 
       public boolean isOKToFinish();
       public boolean isOKToCancel();
}
