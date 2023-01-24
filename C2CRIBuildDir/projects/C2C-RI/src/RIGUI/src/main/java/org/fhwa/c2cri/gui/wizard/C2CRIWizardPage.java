/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard;

import com.github.cjwizard.WizardPage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class C2CRIWizardPage extends WizardPage {

    private final List<C2CRIWizardPage> subPages = new ArrayList<C2CRIWizardPage>();
    private Object subPageLock = new Object();

    public C2CRIWizardPage(String title, String description) {
        super(title, description);
    }

    /**
     * Add a new Wizard Page as a subPage to this Wizard Page.
     *
     * @param newSubPage
     */
    public void addSubPage(C2CRIWizardPage newSubPage) {
        synchronized (subPageLock) {
            subPages.add(newSubPage);
        }
    }

    /**
     * If this WizardPage contains the provided subPage, then remove it from the
     * set of subPages.
     *
     * @param newSubPage
     */
    public void removeSubPage(C2CRIWizardPage newSubPage) {
        synchronized (subPageLock) {
            if (subPages.contains(newSubPage)) {
                subPages.remove(newSubPage);
            }
        }
    }

    public List<C2CRIWizardPage> getSubPages() {
        synchronized (subPageLock) {
            return subPages;
        }
    }

    public boolean isCheckRequiredBeforeCancel(){
        return false;
    }
    
    /**
     * Returns a string representation of this wizard page.
     */
    @Override
    public String toString() {
        return getTitle();
    }

}
