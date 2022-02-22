/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.create;

import org.fhwa.c2cri.gui.wizard.testconfig.create.page.TestConfigCreatePage;
import org.fhwa.c2cri.gui.wizard.testconfig.create.page.CreateTestMainPage;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.ErrorLogDialog;
import org.fhwa.c2cri.gui.wizard.C2CRIExtraButtonListener;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPageProvider;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardSwingAdapterFactory;
import org.fhwa.c2cri.gui.wizard.testconfig.create.page.EditOrFinishPage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.C2CRITestModePage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.ConfigureEmulationDataPage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.ConfigureTestCaseDataPage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.SelectNeedsPage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.SelectRequirementsPage;
import org.fhwa.c2cri.gui.wizard.testconfig.edit.page.SelectSUTSettingsPage;
import org.fhwa.c2cri.testmodel.Need;

/**
 *
 */
public class TestConfigCreatePageProvider implements C2CRIWizardPageProvider {

    private final Logger log = Logger.getLogger(TestConfigCreatePageProvider.class.getSimpleName());
    /**
     * Implementation of PageProvider to generate the wizard pages needed for
     * the wizard.
     */
    // To keep things simple, we'll just create an array of wizard pages:
    private final List<C2CRIWizardPage> pages = new ArrayList<C2CRIWizardPage>();
    private final TestConfigurationController configController;
    private C2CRIExtraButtonListener buttonListener;
    private boolean createConfigPathActive = false;
    private boolean editConfigPathActive = false;
    private boolean editingNewConfig = false;
    private final JButton validateButton;
    private String testConfigFileName = "";
    private WizardPage lastPageProvided;

    public TestConfigCreatePageProvider(TestConfigurationController controller) {

        this.configController = controller;

        validateButton = new JButton("Validate");
        validateButton.setToolTipText("Check whether the Test Configuration is Valid");
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String[] results = configController.validateConfiguration();
                if (results.length > 0) {
//                    JTextArea ta = new JTextArea(60, 20);
//                    StringBuilder sb = new StringBuilder();
//                    for (int ii = 0; ii < results.length; ii++) {
//                        sb.append(results[ii] + "\n");
//                    }
//                    ta.setText(sb.toString());
//                                   
//                    switch (JOptionPane.showConfirmDialog(null, new JScrollPane(ta), "Validation Errors", JOptionPane.OK_OPTION)) {
//                        case JOptionPane.OK_OPTION:
//                            System.out.println(ta.getText());
//                            break;
//                    }
                    ErrorLogDialog errorDialog = new ErrorLogDialog(null, true);
                    errorDialog.initialize(new ArrayList(Arrays.asList(results)));
                    errorDialog.setTitle("Validation Errors");
                    errorDialog.showDialog();  // True means Continue, False means Return    

                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "The Configuration File is Valid", "Configuration Validation Results", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        CreateTestMainPage masterPage = new CreateTestMainPage("Create a Test", "Create/Edit Test Configuration", controller) {
            public void rendering(List<WizardPage> path, WizardSettings settings) {
                super.rendering(path, settings);
                setFinishEnabled(true);
                setPrevEnabled(false);
                validateButton.setVisible(false);
            }
        };

        pages.add(masterPage);

    }

    // return the page list.
    public List<C2CRIWizardPage> getPages() {
        return pages;
    }


    /* (non-Javadoc)
       * @see com.github.cjwizard.PageFactory#createPage(java.util.List, com.github.cjwizard.WizardSettings)
     */
    @Override
    public WizardPage createPage(List<WizardPage> path,
            WizardSettings settings, DefaultTreeModel treeModel) {
        log.fine("creating page " + path.size());
        System.out.println("TestField Settings Value = " + settings.get("testField"));

        if (path.size() > 0 && (path.get(path.size() - 1) instanceof CreateTestMainPage)) {
            validateButton.setVisible(true);
            processCreateTestMainPage(path, settings, treeModel);
        } else if (!editingNewConfig && path.size() > 0 && (path.get(path.size() - 1) instanceof EditOrFinishPage)) {
            processEditOrFinishPage(path, settings, treeModel);
            //       } else if (path.size() > 0 && (path.get(path.size() - 1) instanceof TestConfigCreateUI)) {
            //           processTestConfigCreateUI(path, settings, treeModel);
        } else if (!editingNewConfig && path.size() > 0 && (path.get(path.size() - 1) instanceof SelectNeedsPage)) {
            processSelectedNeedsPage(path, settings, treeModel);
        }

        // Get the next page to display.  The path is the list of all wizard
        // pages that the user has proceeded through from the start of the
        // wizard, so we can easily see which step the user is on by taking
        // the length of the path.  This makes it trivial to return the next
        // WizardPage:        
//                            lastPage.addSubPage(testPage3);
//                            C2CRIWizardSwingAdapterFactory.addNode(treeModel, lastPage, testPage3, 2);
//                    extensionPage.removeSubPage(extensionTestPage1);
//                    C2CRIWizardSwingAdapterFactory.removeNode(treeModel, extensionTestPage1);
        C2CRIWizardPage lastPage = path.size() > 0 ? (C2CRIWizardPage) path.get(path.size() - 1) : null;
        WizardPage page = getNextPage(lastPage);

        // if we wanted to, we could use the WizardSettings object like a
        // Map<String, Object> to change the flow of the wizard pages.
        // In fact, we can do arbitrarily complex computation to determine
        // the next wizard page.
        log.fine(
                "Returning page: " + page);
        lastPageProvided = page;
        return page;
    }

    private void processCreateTestMainPage(List<WizardPage> path,
            WizardSettings settings, DefaultTreeModel treeModel) {

        boolean createConfig = (settings.get("WizardCreateNewConfigRadioButton") != null) && (Boolean) settings.get("WizardCreateNewConfigRadioButton") && !createConfigPathActive;
        boolean editConfig = (settings.get("WizardEditExistingConfigRadioButton") != null) && (Boolean) settings.get("WizardEditExistingConfigRadioButton") && !editConfigPathActive;
        System.out.println("New Config Settings Value = " + settings.get("WizardCreateNewConfigRadioButton") + " createConfig = " + createConfig);
        System.out.println("Edit Existing Settings Value = " + settings.get("WizardEditExistingConfigRadioButton") + " editConfig = " + editConfig);

        if ((Boolean) settings.get("WizardCreateNewConfigRadioButton") && createConfig) {
            if (pages.size() > 1) {
                List<C2CRIWizardPage> pagesToRemove = new ArrayList<C2CRIWizardPage>();
                for (int ii = 1; ii < pages.size(); ii++) {
                    pagesToRemove.add(pages.get(ii));
                }
                for (C2CRIWizardPage page : pagesToRemove) {
                    C2CRIWizardSwingAdapterFactory.removeNode(treeModel, page);
                    pages.remove(page);
                }
            }

            List<C2CRIWizardPage> newPages = createNewPages();
            for (int ii = 0; ii < newPages.size(); ii++) {
                pages.add(newPages.get(ii));
                C2CRIWizardSwingAdapterFactory.addNode(treeModel, (C2CRIWizardPage) ((DefaultMutableTreeNode) treeModel.getRoot()).getUserObject(), newPages.get(ii), pages.size() - 1);
            }

            if (buttonListener != null) {
                buttonListener.setButtons();
            }
            createConfigPathActive = true;
            editConfigPathActive = false;

        } else if ((Boolean) settings.get("WizardEditExistingConfigRadioButton") && (editConfig)) {
            if (pages.size() > 1) {
                List<C2CRIWizardPage> pagesToRemove = new ArrayList<C2CRIWizardPage>();
                for (int ii = 1; ii < pages.size(); ii++) {
                    pagesToRemove.add(pages.get(ii));
                }
                for (C2CRIWizardPage page : pagesToRemove) {
                    C2CRIWizardSwingAdapterFactory.removeNode(treeModel, page);
                    pages.remove(page);
                }
            }

            editingNewConfig = false;
            System.out.println("About to Load config File.");
//            if (loadTestConfigFile()) {
            if (settings.get("TestConfigFileLoaded") != null && !((String) settings.get("TestConfigFileLoaded")).isEmpty()) {

                List<C2CRIWizardPage> newPages = createEditPages();
                for (int ii = 0; ii < newPages.size(); ii++) {
                    pages.add(newPages.get(ii));
                    C2CRIWizardSwingAdapterFactory.addNode(treeModel, (C2CRIWizardPage) ((DefaultMutableTreeNode) treeModel.getRoot()).getUserObject(), newPages.get(ii), pages.size() - 1);
                }

                editConfigPathActive = true;

            }
            createConfigPathActive = false;
//            } else {
//                return path.get(path.size()-1);
        } else if (editConfigPathActive) {
            if (buttonListener != null) {
                buttonListener.setButtons(validateButton);
            }
        }
    }

    private void processEditOrFinishPage(List<WizardPage> path,
            WizardSettings settings, DefaultTreeModel treeModel) {

        editingNewConfig = false;
        List<C2CRIWizardPage> newPages = createEditPages();
        for (int ii = 0; ii < newPages.size(); ii++) {
            pages.add(newPages.get(ii));
            C2CRIWizardSwingAdapterFactory.addNode(treeModel, (C2CRIWizardPage) ((DefaultMutableTreeNode) treeModel.getRoot()).getUserObject(), newPages.get(ii), pages.size() - 1);
        }
        editConfigPathActive = false;

        testConfigFileName = (String) settings.get("TestConfigurationFilePath") + "\\" + (String) settings.get("TestConfigurationFileName")+".ricfg";
        if (buttonListener != null) {
            buttonListener.setButtons(validateButton);
        }

    }

    private void processSelectedNeedsPage(List<WizardPage> path,
            WizardSettings settings, DefaultTreeModel treeModel) {

        C2CRIWizardPage currentPage = (C2CRIWizardPage) path.get(path.size() - 1);
        List<C2CRIWizardPage> subPages = new ArrayList<>(currentPage.getSubPages());
        for (int ii = 0; ii < subPages.size(); ii++) {
            C2CRIWizardSwingAdapterFactory.removeNode(treeModel, subPages.get(ii));
            System.out.println("Removing " + subPages.get(ii).getTitle());
            currentPage.removeSubPage(subPages.get(ii));
        }

        List<C2CRIWizardPage> newPages = createRequirementsPages(((SelectNeedsPage) currentPage).isAppLayerNeedsPage());
        for (int ii = 0; ii < newPages.size(); ii++) {
            currentPage.addSubPage(newPages.get(ii));
            C2CRIWizardSwingAdapterFactory.addNode(treeModel, currentPage, newPages.get(ii), ii);
        }
    }

//    private void processTestConfigCreateUI(List<WizardPage> path,
//            WizardSettings settings, DefaultTreeModel treeModel) {
//
//        System.out.println("Test Configuration Name Value = " + settings.get("TestConfigurationFileName"));
//        System.out.println("Test Configuration Path Value = " + settings.get("TestConfigurationFilePath"));
//        System.out.println("Test Configuration Description Value = " + settings.get("TestConfigurationDescription"));
//        System.out.println("Information Test Suite Value = " + settings.get("SelectedInformationTestSuite"));
//        System.out.println("Application Test Suite Value = " + settings.get("SelectedApplicationTestSuite"));
//        try {
//            configController.createConfig((String) settings.get("TestConfigurationFilePath") + "\\" + (String) settings.get("TestConfigurationFileName"),
//                    (String) settings.get("TestConfigurationDescription"),
//                    (String) settings.get("SelectedInformationTestSuite"),
//                    (String) settings.get("SelectedApplicationTestSuite"));
//            testConfigFileName = (String) settings.get("TestConfigurationFilePath") + "\\" + (String) settings.get("TestConfigurationFileName");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    private C2CRIWizardPage getNextPage(C2CRIWizardPage currentPage) {

        if (currentPage == null) {
            lastPageProvided = pages.get(0);
            return pages.get(0);
        } else {
            List<C2CRIWizardPage> pageList = new ArrayList<C2CRIWizardPage>();
            for (C2CRIWizardPage thisPage : pages) {
                addWizardPagesToList(pageList, thisPage);
            }

            boolean currentPageFound = false;
            for (C2CRIWizardPage wizardPage : pageList) {
                if (wizardPage.equals(currentPage)) {
                    currentPageFound = true;
                    // If there's only one page in the list return it.  There is no next page.
                    if (pageList.size() == 1) {
                        lastPageProvided = wizardPage;
                        return wizardPage;
                    }
                } else if (currentPageFound) {
                    lastPageProvided = wizardPage;
                    return wizardPage;
                }
            }
        }
        return null;
    }

    private void addWizardPagesToList(List<C2CRIWizardPage> pageList, C2CRIWizardPage page) {
        pageList.add(page);
        for (C2CRIWizardPage subPage : page.getSubPages()) {
            addWizardPagesToList(pageList, subPage);
        }
    }

    private List<C2CRIWizardPage> createEditPages() {

        if (buttonListener != null) {
            buttonListener.setButtons(validateButton);
        }

        C2CRIWizardPage secondPage = new C2CRITestModePage("C2C RI Test Mode", "Specify whether C2C RI will act as owner center or external center", configController) {
            {
//                add(new ConfigurationPanel());
            }

            @Override
            public boolean onPrev(WizardSettings settings) {
                int result = javax.swing.JOptionPane.showConfirmDialog(null, "This action will result in a loss of any configuration file changes.  Would you like to save the configuration file before proceeding?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);

                boolean returnResult = true;
                if (result == JOptionPane.YES_OPTION) {
                    returnResult = saveConfigurationFile();
                }

                returnResult = super.onPrev(settings) && returnResult;

                // If it's OK to go to the previous screen remove the validate button.
                if (returnResult) {
                    if (buttonListener != null) {
                        buttonListener.setButtons();
                    }
                }

                return returnResult;

            }
        };
//        pages.add(secondPage);

        C2CRIWizardPage thirdPage = new SelectNeedsPage("Select Application Layer Needs", "Select the Application Layer Needs for the project", configController, true) {
            {
                add(new JLabel("Three!"));
                JCheckBox box = new JCheckBox("testBox");
                box.setName("box");
                add(box);

                //               setBackground(Color.green);
            }

            @Override
            public boolean onNext(WizardSettings settings) {

                return true;
            }
        };

//        pages.add(thirdPage);
        C2CRIWizardPage fourthPage = new SelectNeedsPage("Select Information Layer Needs", "Select the Information Layer Needs for the project", configController, false) {
            {

            }

        };

//        pages.add(fourthPage);
        C2CRIWizardPage fifthPage = new SelectSUTSettingsPage("Select System Under Test Settings", "Specify the parameters necessary for connecting to the System Under Test", configController);

        C2CRIWizardPage sixthPage = new ConfigureTestCaseDataPage("Configure Application Test Case Data", "Specify the parameters for each test case", configController, true);

        C2CRIWizardPage seventhPage = new ConfigureTestCaseDataPage("Configure Information Test Case Data", "Specify the parameters for each test case", configController, false);

//        pages.add(sixthPage);
        C2CRIWizardPage finalPage = new ConfigureEmulationDataPage("Configure Entity Emulation Data", "Specify the parameters for Entity Emulation", configController) {

            /**
             * Returns a string representation of this wizard page.
             */
            @Override
            public String toString() {
                return getTitle();
            }

            /**
             * This is the last page in the wizard, so we will enable the finish
             * button and disable the "Next >" button just before the page is
             * displayed:
             */
            public void rendering(List<WizardPage> path, WizardSettings settings) {
                super.rendering(path, settings);
                setFinishEnabled(true);
                setNextEnabled(false);
            }

        };

        List<C2CRIWizardPage> returnedPages = new ArrayList<>();
        returnedPages.add(secondPage);
        returnedPages.add(thirdPage);
        returnedPages.add(fourthPage);
        returnedPages.add(fifthPage);
        returnedPages.add(sixthPage);
        returnedPages.add(seventhPage);
        returnedPages.add(finalPage);
        return returnedPages;

    }

    private List<C2CRIWizardPage> createNewPages() {

        C2CRIWizardPage firstPage = new TestConfigCreatePage("New Test Configuration", "Create the test configuration file", "C:\\c2cri", configController) {
            @Override
            public boolean onNext(WizardSettings settings) {
                
                System.out.println("Test Configuration Name Value = " + settings.get("TestConfigurationFileName"));
                System.out.println("Test Configuration Path Value = " + settings.get("TestConfigurationFilePath"));
                System.out.println("Test Configuration Description Value = " + settings.get("TestConfigurationDescription"));
                System.out.println("Information Test Suite Value = " + settings.get("SelectedInformationTestSuite"));
                System.out.println("Application Test Suite Value = " + settings.get("SelectedApplicationTestSuite"));
                try {
                    configController.createConfig((String) settings.get("TestConfigurationFilePath") + "\\" + (String) settings.get("TestConfigurationFileName"),
                            (String) settings.get("TestConfigurationDescription"),
                            (String) settings.get("SelectedInformationTestSuite"),
                            (String) settings.get("SelectedApplicationTestSuite"));
                    testConfigFileName = (String) settings.get("TestConfigurationFilePath") + "\\" + (String) settings.get("TestConfigurationFileName");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
                this.setFinishEnabled(true);
                return true;
            }

            @Override
            public boolean onPrev(WizardSettings settings) {
                settings.put("TestConfigurationFileName", "");
                clearTestNameField();
                boolean result = super.onPrev(settings);
                if (buttonListener != null) {
                    buttonListener.setButtons();
                }
                return result;
            }

        };

        //       pages.add(firstPage);
        C2CRIWizardPage secondPage = new EditOrFinishPage("New Test Created", "Specify whether you want to Exit or Edit.") {
            {
            }

            /* (non-Javadoc)
                * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
             */
            @Override
            public void updateSettings(WizardSettings settings) {
                super.updateSettings(settings);

                // This is called when the user clicks next, so we could do
                // some longer-running processing here if we wanted to, and
                // pop up progress bars, etc.  Once this method returns, the
                // wizard will continue.  Beware though, this runs in the
                // event dispatch thread (EDT), and may render the UI
                // unresponsive if you don't issue a new thread for any long
                // running ops.  Future versions will offer a better way of
                // doing this.
            }

            /**
             * This is the last page in the wizard, so we will enable the finish
             * button and disable the "Next >" button just before the page is
             * displayed:
             */
            public void rendering(List<WizardPage> path, WizardSettings settings) {
                super.rendering(path, settings);
                setFinishEnabled(true);
                setNextEnabled(true);
            }

            @Override
            public boolean onPrev(WizardSettings settings) {
                boolean result = super.onPrev(settings);
                if (buttonListener != null) {
                    buttonListener.setButtons();
                }
                return result;
            }
        };

        List<C2CRIWizardPage> returnedPages = new ArrayList<>();
        returnedPages.add(firstPage);
        returnedPages.add(secondPage);
        return returnedPages;

    }

    private List<C2CRIWizardPage> createRequirementsPages(boolean applicationLayerSelections) {
        List<C2CRIWizardPage> returnedPages = new ArrayList<>();
        List<Need> needsList = applicationLayerSelections ? configController.getAppLayerParams().getUserNeeds().needs : configController.getInfoLayerParams().getUserNeeds().needs;
        for (Need un : needsList) {
            if (un.getFlagValue()) {
                C2CRIWizardPage reqPage = new SelectRequirementsPage("Need - " + un.getTitle(), "Select the Requirements for the need - " + un.getText(), configController, applicationLayerSelections, un.getTitle());
                returnedPages.add(reqPage);
                System.out.println((applicationLayerSelections ? "AppLayer" : "InfoLayer") + " Added " + "Need - " + un.getTitle());
            }
        }
        return returnedPages;
    }

//    private boolean loadTestConfigFile() {
//
//        boolean fileLoaded = false;
//
//        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
//        String default_Path = "c:\\c2cri";
//
//        File current_dir = new File(default_Path);
//        System.out.println(" Path = " + default_Path + " is valid? " + current_dir.exists());
//        fc.setCurrentDirectory(current_dir);
//        FileFilter ricfgFilter = new FileFilter() {
//
//            public boolean accept(File f) {
//                return f.isDirectory() || f.getName().endsWith(".ricfg");
//            }
//
//            public String getDescription() {
//                return "Filter for RI Config Files";
//            }
//        };
//        fc.setFileFilter(ricfgFilter);
//
//        boolean normalExit = false;
//        int returnVal = 0;
//        while (!normalExit) {
//
//            returnVal = fc.showOpenDialog(null);
//
//            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//
//                if (file == null || file.getName().equals("")) {
//                    javax.swing.JOptionPane.showMessageDialog(null, "Invalid File Name", "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
//                } else {
//                    try {
//
//                        configController.openConfig(fc.getSelectedFile().getAbsolutePath());
//                        fileLoaded = true;
//                        normalExit = true;
//
//                    } catch (Exception e) {
//                        javax.swing.JOptionPane.showMessageDialog(null, "Error Opening File:  " + fc.getSelectedFile().getName() + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
//                        e.printStackTrace();
//                    }
//
//                }
//
//            } else if (returnVal == javax.swing.JFileChooser.CANCEL_OPTION) {
//                normalExit = true;
//            }
//        }
//        fc.setVisible(false);
//        return fileLoaded;
//    }
    @Override
    public void setButtonListener(C2CRIExtraButtonListener buttonListener) {
        this.buttonListener = buttonListener;
        this.buttonListener.setButtons(validateButton);
    }

    @Override
    public boolean isOKToCancel() {
        if (lastPageProvided instanceof C2CRIWizardPage) {
            if (((C2CRIWizardPage) lastPageProvided).isCheckRequiredBeforeCancel()) {
                int result = javax.swing.JOptionPane.showConfirmDialog(null, "Would you like to save the configuration file before exit?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    return saveConfigurationFile();
                } else {
                    return true;
                }
            }

        }
        return true;
    }

    @Override
    public boolean isOKToFinish() {
        if (lastPageProvided instanceof C2CRIWizardPage) {
            if (!((C2CRIWizardPage) lastPageProvided).isCheckRequiredBeforeCancel()) {
                return true;
            }
        }
        return saveConfigurationFile();
    }

    private boolean saveConfigurationFile() {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        String default_Path = "c:\\c2cri";

        File current_dir = new File(default_Path);
        System.out.println(" Path = " + default_Path + " is valid? " + current_dir.exists());
        fc.setCurrentDirectory(current_dir);
        FileFilter ricfgFilter = new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".ricfg");
            }

            public String getDescription() {
                return "Filter for RI Config Files";
            }
        };
        fc.setFileFilter(ricfgFilter);
        fc.setSelectedFile(new File(testConfigFileName));
        boolean normalExit = false;
        int returnVal = 0;
        boolean okToFinish = false;
        while (!normalExit) {

            returnVal = fc.showSaveDialog(null);

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                boolean okToProceed = false;
                String[] validationErrors = configController.validateConfiguration();
                if (validationErrors.length > 0) {
                    int proceedResult = javax.swing.JOptionPane.showConfirmDialog(null, "File  " + fc.getSelectedFile().getName() + " contains validation errors. " + "\n" + "Would you like to save the file?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
                    if (proceedResult == JOptionPane.YES_OPTION) {
                        okToProceed = true;
                    }
                } else {
                    okToProceed = true;
                }

                if (okToProceed) {
                    if (file == null || file.getName().equals("")) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Invalid File Name", "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    } else if (file.exists()) {
                        int result = javax.swing.JOptionPane.showConfirmDialog(null, "File  " + fc.getSelectedFile().getName() + " exists. " + "\n" + "Would you like to overwrite the file?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            try {
                                configController.saveConfig(fc.getSelectedFile().getAbsolutePath());
                                normalExit = true;
                                okToFinish = true;
                            } catch (Exception e) {
                                javax.swing.JOptionPane.showMessageDialog(null, "Error Saving File:  " + fc.getSelectedFile().getName() + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            configController.saveConfig(fc.getSelectedFile().getAbsolutePath());
                            normalExit = true;
                            okToFinish = true;
                        } catch (Exception e) {
                            javax.swing.JOptionPane.showMessageDialog(null, "Error Opening File:  " + fc.getSelectedFile().getName() + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }

                    }
                }

            } else if (returnVal == javax.swing.JFileChooser.CANCEL_OPTION) {
                normalExit = true;
            }
        }
        fc.setVisible(false);
        return okToFinish;
    }

}
