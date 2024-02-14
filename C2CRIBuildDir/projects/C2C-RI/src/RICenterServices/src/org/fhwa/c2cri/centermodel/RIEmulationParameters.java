package org.fhwa.c2cri.centermodel;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.classworlds.Launcher;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;

/**
 * The set of parameters that are specified for entity emulation.
 *
 * @author TransCore ITS, LLC
 * @version 1.0
 * @created 26-Jan-2016 1:29:28 PM
 */
public class RIEmulationParameters implements Serializable {

    private static int maxCommandQueueLength = 100;
    /**
     * The depth of the command queue that will be used during entity emulation.
     */
    private int commandQueueLength;
    /**
     * A list containing each entity data name and associated data byte array.
     */
    @XmlElement(name="EntityData")
    private ArrayList<RIEmulationEntityValueSet> entityDataMap;

    public RIEmulationParameters() {
        entityDataMap = new ArrayList<>();
    }

    public RIEmulationParameters(String jarFilePath, String emulationDataPath) {
        if (emulationDataPath != null) {

//final String path = "sample/folder";
//final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            final String path = emulationDataPath;
            try {
                final URL jarURL = new URL(jarFilePath);
                final File jarFile = new File(jarURL.toURI());
                if (jarFile.isFile()) {  // Run with JAR file
                    try (final JarFile jar = new JarFile(jarFile))
					{
						final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
						while (entries.hasMoreElements()) {
							final JarEntry entry = entries.nextElement();
							if (entry.getName().startsWith(path) && !entry.isDirectory()) { //filter according to the path and files only            
								System.out.println(entry.getName());
								RIEmulationEntityValueSet thisEntityValueSet = new RIEmulationEntityValueSet();
								thisEntityValueSet.setValueSetName(entry.getName());
								thisEntityValueSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(new URL("jar:"+jarFilePath +"!/"+ entry.getName())));
								entityDataMap.add(thisEntityValueSet);
							}
						}
					}
                } else { // Run with IDE
                    final URL url = Launcher.class.getResource("/" + path);
                    if (url != null) {
                        try {
                            final File apps = new File(url.toURI());
                            for (File app : apps.listFiles()) {
                                if (app.getName().startsWith(path) && !app.isDirectory()) { //filter according to the path and files only            
                                    RIEmulationEntityValueSet thisEntityValueSet = new RIEmulationEntityValueSet();
                                    thisEntityValueSet.setValueSetName(app.getName());
                                    thisEntityValueSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(jarURL.toURI() + "!" + app.getName()));
                                    entityDataMap.add(thisEntityValueSet);
                                    System.out.println(app);
                                }
                            }
                        } catch (URISyntaxException ex) {
                            // never happens
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

//            try {                
//                URL tempURL = new URL(emulationDataPath);
//                File mapsDir = new File(tempURL.toURI());
//                System.out.println(mapsDir.exists());
//
//                // loop through all files in the directory and store each
//                File[] names = mapsDir.listFiles();
//                entityDataMap = new ArrayList<>();                
//                for (int i = 0; i < names.length; i++) {
//                    if (names[i].isFile()) {
//                        RIEmulationEntityValueSet thisEntityValueSet = new RIEmulationEntityValueSet();                        
//                        thisEntityValueSet.setValueSetName(names[i].getName());
//                        thisEntityValueSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(names[i].toURI().toURL()));
//                    }
//                }
//            } catch (Exception ex) {
//            }
        }
    }

    public RIEmulationParameters(URL[] emulationDataURLs) throws EntityEmulationException {
        if (emulationDataURLs != null) {
            try {
                
//                URL tempURL = new URL(emulationDataURLs);
//                File mapsDir = new File(tempURL.toURI());
//                System.out.println(mapsDir.exists());

                // loop through all files in the directory and store each
//                File[] names = mapsDir.listFiles();
                entityDataMap = new ArrayList<>();
                for (int i = 0; i < emulationDataURLs.length; i++) {
                        RIEmulationEntityValueSet thisEntityValueSet = new RIEmulationEntityValueSet();
                        
                        String entitySetName = emulationDataURLs[i].getFile();
                        if (entitySetName.contains("/"))entitySetName = entitySetName.substring(emulationDataURLs[i].getFile().lastIndexOf("/")+1);
                        
                        thisEntityValueSet.setValueSetName(entitySetName);
                        thisEntityValueSet.setEntityDataSet(EmulationDataFileProcessor.getByteArray(emulationDataURLs[i]));
                        entityDataMap.add(thisEntityValueSet);
                }
            } catch (Exception ex) {
                throw new EntityEmulationException(ex.getMessage());
            }
        }
    }

    /**
     * Returns the Command Queue Length currently specified.
     */
    public int getCommandQueueLength() {
        return this.commandQueueLength;
    }

    /**
     * Sets the queue length to be used.
     *
     * @param queueLength
     */
    public void setCommandQueueLength(int queueLength) {
        if (queueLength < 0) {
            this.commandQueueLength = 0;
        } else if (queueLength <= maxCommandQueueLength) {
            this.commandQueueLength = queueLength;
        } else {
            this.commandQueueLength = maxCommandQueueLength;
        }
    }

    /**
     * set the entity emulation data.
     *
     * @param entityEmulationData
     */
    public void setEntityEmulationDataList(List entityEmulationData) {
		// original implementation was empty
    }

    /**
     * get the entity emulation data.
     *
     */
    @XmlTransient
    public ArrayList<RIEmulationEntityValueSet> getEntityDataMap() {
        return entityDataMap;
    }

    /**
     * set the entity emulation data.
     *
     */
    public void setEntityDataMap(ArrayList<RIEmulationEntityValueSet> entityDataMap) {
        this.entityDataMap = entityDataMap;
    }

}
