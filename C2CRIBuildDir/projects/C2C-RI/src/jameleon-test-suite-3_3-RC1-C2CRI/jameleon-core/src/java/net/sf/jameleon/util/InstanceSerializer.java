/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
    
public class InstanceSerializer{

	private InstanceSerializer(){}
    /**
     * The file extension for serialized files
     */
    public static final String SERIALIZED_EXT = ".dat";

    /**
     * Serializes a given object to a given file.
     * @param obj - the object to serialize
     * @param file - the file to serialize the object to
     * @throws IOException if the file can't be serialized
     */
    public static void serialize(Object obj, File file) throws IOException{
        ObjectOutputStream s = null;
        if (file.getParentFile() != null) {
            JameleonUtility.createDirStructure(file.getParentFile());
        }
        try{
            s = new ObjectOutputStream(new FileOutputStream(file));
            s.writeObject(obj);
            s.flush();
        }finally{
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Deserializes an object from a given file name
     * @param fileName - A String representing the location of the serialized file
     * @throws IOException - If the file can not be deserialzed
     * @throws ClassNotFoundException - If a class corresponding to the serialized file
     *         is not in the Classpath
     */
    public static Object deserialize(String fileName) throws IOException, ClassNotFoundException{
        return deserialize(new FileInputStream(fileName));
    }

    /**
     * Deserializes an object from a given InputStream
     * @param in - An InputStream representing a serialized file.
     * @throws IOException - If the file can not be deserialzed
     * @throws ClassNotFoundException - If a class corresponding to the serialized file
     *         is not in the Classpath
     */
    public static Object deserialize(InputStream in) throws IOException, ClassNotFoundException{
        Object obj = null;
        ObjectInputStream s = null;
        if (in != null) {
            try{
                s = new ObjectInputStream(in);
                obj = s.readObject();
            }finally{
                if (s != null) {
                    s.close();
                }
            }
        }
        return obj;
    }

}
