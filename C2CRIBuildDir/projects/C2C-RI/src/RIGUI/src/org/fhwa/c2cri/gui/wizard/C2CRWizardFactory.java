/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard;

import com.github.cjwizard.APageFactory;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.tree.DefaultTreeModel;
import org.fhwa.c2cri.domain.testmodel.DefaultTestConfigController;
import org.fhwa.c2cri.gui.wizard.testconfig.create.TestConfigCreatePageProvider;

/**
 *
 */
public class C2CRWizardFactory extends APageFactory {

    private final Logger log = Logger.getLogger(C2CRWizardFactory.class.getSimpleName());
    /**
     * Implementation of PageFactory to generate the wizard pages needed for the
     * wizard.
     */

    private DefaultTreeModel treeModel = null;
    private Object treeModelLock = new Object();
    public static enum FactoryType {TEST_CONFIGURATION, TEST_EXECUTION, TEST_REPORT};
    private C2CRIWizardPageProvider pageProvider;
    private C2CRIWizardPage rootPage = new C2CRIWizardPage("Root Page", "Should not be seen.");
    private C2CRIWizardFactoryListener factoryListener;
            
    public C2CRWizardFactory(FactoryType wizardType) {
        switch(wizardType){
            case TEST_CONFIGURATION:
                pageProvider = new TestConfigCreatePageProvider(new DefaultTestConfigController());
                break;
            case TEST_EXECUTION:
                break;
            case TEST_REPORT:
                break;
            default:
                break;
        }
        
        synchronized (treeModelLock) {

            List<C2CRIWizardPage> pageList = new ArrayList<>();
            for (C2CRIWizardPage subPage : pageProvider.getPages()){
                rootPage.addSubPage(subPage);
            }
            pageList.add(rootPage);
            treeModel = C2CRIWizardSwingAdapterFactory.toTreeModel(pageList);
        }        
    }    

    // return the page list.
    public List<C2CRIWizardPage> getPages() {
            rootPage.getSubPages().clear();
            
            List<C2CRIWizardPage> pageList = new ArrayList<>();
            for (C2CRIWizardPage subPage : pageProvider.getPages()){
                rootPage.addSubPage(subPage);
            }            
            pageList.add(rootPage);
            return pageList;
    }

    public DefaultTreeModel getTreeModel() {
        synchronized (treeModelLock) {
            return treeModel;
        }
    }

    public void addFactoryListener(C2CRIWizardFactoryListener listener){
        this.factoryListener = listener;
    }

    public void removeFactoryListener(C2CRIWizardFactoryListener listener){
        this.factoryListener = null;
    }    
    
    public void registerButtonListener(C2CRIExtraButtonListener buttonListener){
        pageProvider.setButtonListener(buttonListener);
    }

    public boolean isOKToFinish(){
        return pageProvider.isOKToFinish();
    }
    
    public boolean isOKToCancel(){
        return pageProvider.isOKToCancel();
    }
        
    
    /* (non-Javadoc)
       * @see com.github.cjwizard.PageFactory#createPage(java.util.List, com.github.cjwizard.WizardSettings)
     */
    @Override
    public WizardPage createPage(List<WizardPage> path,
            WizardSettings settings) {
        log.fine("creating page " + path.size());
                
        return pageProvider.createPage(path, settings, treeModel);

    }

}
