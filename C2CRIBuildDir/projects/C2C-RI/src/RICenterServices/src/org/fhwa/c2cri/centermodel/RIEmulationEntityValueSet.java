/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class defines a set of entity set values.
 *
 * @author TransCore ITS, LLC Last Updated: 12/8/2015
 */
public class RIEmulationEntityValueSet implements Serializable {

    public static enum ENTITYDATASTATE {
        DEFAULT, UPDATED
    };

    @XmlElement(name = "dataSetName")
    private String valueSetName;
    @XmlElement(name = "dataSetData")
    @XmlJavaTypeAdapter(org.fhwa.c2cri.centermodel.RIEmulationDataSetDataAdapter.class)
    private byte[] entityDataSet;

    @XmlElement
    private ENTITYDATASTATE dataSetSource;

    @XmlTransient
    public String getValueSetName() {
        return valueSetName;
    }

    public void setValueSetName(String valueSetName) {
        this.valueSetName = valueSetName;
    }

    @XmlTransient
    public byte[] getEntityDataSet() {
        return entityDataSet;
    }

    public void setEntityDataSet(byte[] entityDataSet) {
        this.entityDataSet = entityDataSet;
        if (this.dataSetSource == null) {
            this.dataSetSource = ENTITYDATASTATE.DEFAULT;
        } else {
            this.dataSetSource = ENTITYDATASTATE.UPDATED;
        }
    }

    @XmlTransient
    public ENTITYDATASTATE getDataSetSource() {
        return dataSetSource;
    }

    public void setDataSetSource(ENTITYDATASTATE dataSetSource) {
        this.dataSetSource = dataSetSource;
    }

}
