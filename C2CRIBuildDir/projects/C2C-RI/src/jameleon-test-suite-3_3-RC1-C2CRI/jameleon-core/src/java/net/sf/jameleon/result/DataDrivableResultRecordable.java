/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.result;

/**
 * Describes a tag as being able to record DataDrivable results. This interface helps define
 * which parent tag can contain DataDrivable tags and therefore DataDrivable results. This enables
 * the DataDrivable tag to not be concerned with the logistics of which parent tag to search for
 * and how to add the DataDrivableResult to that tag.
 */
public interface DataDrivableResultRecordable {

    /**
     * Records a DataDrivableResult to the tag's results
     * @param result - the result to add
     */
    public void recordDataDrivableResult(DataDrivableResultContainer result);

}
