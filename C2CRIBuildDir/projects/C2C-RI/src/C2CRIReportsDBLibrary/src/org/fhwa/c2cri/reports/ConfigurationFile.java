/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Captures all information related to a configuration file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "ConfigurationFile")
@NamedQueries({
    @NamedQuery(name = "ConfigurationFile.findAll", query = "SELECT c FROM ConfigurationFile c"),
    @NamedQuery(name = "ConfigurationFile.findById", query = "SELECT c FROM ConfigurationFile c WHERE c.id = :id"),
    @NamedQuery(name = "ConfigurationFile.findBySelectedInfoLayerTestSuite", query = "SELECT c FROM ConfigurationFile c WHERE c.selectedInfoLayerTestSuite = :selectedInfoLayerTestSuite"),
    @NamedQuery(name = "ConfigurationFile.findBySelectedAppLayerTestSuite", query = "SELECT c FROM ConfigurationFile c WHERE c.selectedAppLayerTestSuite = :selectedAppLayerTestSuite"),
    @NamedQuery(name = "ConfigurationFile.findByConfigVersion", query = "SELECT c FROM ConfigurationFile c WHERE c.configVersion = :configVersion"),
    @NamedQuery(name = "ConfigurationFile.findByTestDescription", query = "SELECT c FROM ConfigurationFile c WHERE c.testDescription = :testDescription"),
    @NamedQuery(name = "ConfigurationFile.findByConfigurationAuthor", query = "SELECT c FROM ConfigurationFile c WHERE c.configurationAuthor = :configurationAuthor")})
public class ConfigurationFile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "selectedInfoLayerTestSuite")
    private String selectedInfoLayerTestSuite;
    @Column(name = "selectedAppLayerTestSuite")
    private String selectedAppLayerTestSuite;
    @Column(name = "configVersion")
    private String configVersion;
    @Column(name = "testDescription")
    private String testDescription;
    @Column(name = "configurationAuthor")
    private String configurationAuthor;

    public ConfigurationFile() {
    }

    public ConfigurationFile(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSelectedInfoLayerTestSuite() {
        return selectedInfoLayerTestSuite;
    }

    public void setSelectedInfoLayerTestSuite(String selectedInfoLayerTestSuite) {
        this.selectedInfoLayerTestSuite = selectedInfoLayerTestSuite;
    }

    public String getSelectedAppLayerTestSuite() {
        return selectedAppLayerTestSuite;
    }

    public void setSelectedAppLayerTestSuite(String selectedAppLayerTestSuite) {
        this.selectedAppLayerTestSuite = selectedAppLayerTestSuite;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getConfigurationAuthor() {
        return configurationAuthor;
    }

    public void setConfigurationAuthor(String configurationAuthor) {
        this.configurationAuthor = configurationAuthor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConfigurationFile)) {
            return false;
        }
        ConfigurationFile other = (ConfigurationFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.ConfigurationFile[id=" + id + "]";
    }

}
