/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Captures all information related to an initialization event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "InitEvent")
@NamedQueries({
    @NamedQuery(name = "InitEvent.findAll", query = "SELECT i FROM InitEvent i"),
    @NamedQuery(name = "InitEvent.findById", query = "SELECT i FROM InitEvent i WHERE i.initEventPK.id = :id"),
    @NamedQuery(name = "InitEvent.findByEventID", query = "SELECT i FROM InitEvent i WHERE i.initEventPK.eventID = :eventID"),
    @NamedQuery(name = "InitEvent.findByFileName", query = "SELECT i FROM InitEvent i WHERE i.fileName = :fileName"),
    @NamedQuery(name = "InitEvent.findByStartTime", query = "SELECT i FROM InitEvent i WHERE i.startTime = :startTime"),
    @NamedQuery(name = "InitEvent.findByCreator", query = "SELECT i FROM InitEvent i WHERE i.creator = :creator"),
    @NamedQuery(name = "InitEvent.findByDescription", query = "SELECT i FROM InitEvent i WHERE i.description = :description"),
    @NamedQuery(name = "InitEvent.findByC2criVersion", query = "SELECT i FROM InitEvent i WHERE i.c2criVersion = :c2criVersion"),
    @NamedQuery(name = "InitEvent.findByConfigFileName", query = "SELECT i FROM InitEvent i WHERE i.configFileName = :configFileName"),
    @NamedQuery(name = "InitEvent.findByChecksum", query = "SELECT i FROM InitEvent i WHERE i.checksum = :checksum"),
    @NamedQuery(name = "InitEvent.findBySelectedAppLayerTestSuite", query = "SELECT i FROM InitEvent i WHERE i.selectedAppLayerTestSuite = :selectedAppLayerTestSuite"),
    @NamedQuery(name = "InitEvent.findBySelectedInfoLayerTestSuite", query = "SELECT i FROM InitEvent i WHERE i.selectedInfoLayerTestSuite = :selectedInfoLayerTestSuite"),
    @NamedQuery(name = "InitEvent.findByConfigVersion", query = "SELECT i FROM InitEvent i WHERE i.configVersion = :configVersion"),
    @NamedQuery(name = "InitEvent.findByConfigurationAuthor", query = "SELECT i FROM InitEvent i WHERE i.configurationAuthor = :configurationAuthor"),
    @NamedQuery(name = "InitEvent.findByExternalCenterOperation", query = "SELECT i FROM InitEvent i WHERE i.externalCenterOperation = :externalCenterOperation"),
    @NamedQuery(name = "InitEvent.findByEmulationEnabled", query = "SELECT i FROM InitEvent i WHERE i.emulationEnabled = :emulationEnabled"),
    @NamedQuery(name = "InitEvent.findByEmulationInitialization", query = "SELECT i FROM InitEvent i WHERE i.emulationInitialization = :emulationInitialization")})
public class InitEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InitEventPK initEventPK;
    @Column(name = "fileName")
    private String fileName;
    @Column(name = "startTime")
    private String startTime;
    @Column(name = "creator")
    private String creator;
    @Column(name = "description")
    private String description;
    @Column(name = "c2criVersion")
    private String c2criVersion;
    @Column(name = "configFileName")
    private String configFileName;
    @Column(name = "checksum")
    private String checksum;
    @Column(name = "selectedAppLayerTestSuite")
    private String selectedAppLayerTestSuite;
    @Column(name = "selectedInfoLayerTestSuite")
    private String selectedInfoLayerTestSuite;
    @Column(name = "configVersion")
    private String configVersion;
    @Column(name = "configurationAuthor")
    private String configurationAuthor;
    @Column(name = "externalCenterOperation")
    private String externalCenterOperation;
    @Column(name = "emulationEnabled")
    private String emulationEnabled;
    @Column(name = "emulationInitialization")
    private String emulationInitialization;

    public InitEvent() {
    }

    public InitEvent(InitEventPK initEventPK) {
        this.initEventPK = initEventPK;
    }

    public InitEvent(Integer id, Integer eventID) {
        this.initEventPK = new InitEventPK(id, eventID);
    }

    public InitEventPK getInitEventPK() {
        return initEventPK;
    }

    public void setInitEventPK(InitEventPK initEventPK) {
        this.initEventPK = initEventPK;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getC2criVersion() {
        return c2criVersion;
    }

    public void setC2criVersion(String c2criVersion) {
        this.c2criVersion = c2criVersion;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getSelectedAppLayerTestSuite() {
        return selectedAppLayerTestSuite;
    }

    public void setSelectedAppLayerTestSuite(String selectedAppLayerTestSuite) {
        this.selectedAppLayerTestSuite = selectedAppLayerTestSuite;
    }

    public String getSelectedInfoLayerTestSuite() {
        return selectedInfoLayerTestSuite;
    }

    public void setSelectedInfoLayerTestSuite(String selectedInfoLayerTestSuite) {
        this.selectedInfoLayerTestSuite = selectedInfoLayerTestSuite;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getConfigurationAuthor() {
        return configurationAuthor;
    }

    public void setConfigurationAuthor(String configurationAuthor) {
        this.configurationAuthor = configurationAuthor;
    }

    public String getExternalCenterOperation() {
        return externalCenterOperation;
    }

    public void setExternalCenterOperation(String externalCenterOperation) {
        this.externalCenterOperation = externalCenterOperation;
    }

    public String getEnableEmulation()
    {
        return emulationEnabled;
    }
    
    public void setEnableEmulation(String enableEmulation)
    {
        this.emulationEnabled = enableEmulation;
    }
    
    public String getReInitializeEmulation()
    {
        
        return emulationInitialization;
    }
    
    public void setReInitializeEmulation(String reinitializeEmulation)
    {
        this.emulationInitialization = reinitializeEmulation;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (initEventPK != null ? initEventPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InitEvent)) {
            return false;
        }
        InitEvent other = (InitEvent) object;
        if ((this.initEventPK == null && other.initEventPK != null) || (this.initEventPK != null && !this.initEventPK.equals(other.initEventPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.InitEvent[initEventPK=" + initEventPK + "]";
    }

}
