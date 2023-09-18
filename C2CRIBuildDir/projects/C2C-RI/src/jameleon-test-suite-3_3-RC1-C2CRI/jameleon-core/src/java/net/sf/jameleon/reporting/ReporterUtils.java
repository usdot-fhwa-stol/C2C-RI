/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)

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
package net.sf.jameleon.reporting;

import net.sf.jameleon.exception.JameleonException;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.TemplateProcessor;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * A utility class for the reporting package
 */
public class ReporterUtils {
    private static final String shortFormat = "yyyyMMddHHmmssSSS";

    private ReporterUtils(){}

    public static String getExecutionTime(Calendar startTime, Calendar endTime){
        return JameleonUtility.executionTimeToString(endTime.getTimeInMillis() - startTime.getTimeInMillis());
    }

    public static String formatTime(Calendar time){
		SimpleDateFormat oSdf = new SimpleDateFormat(shortFormat);
        return oSdf.format(time.getTime());
    }

    public static void outputToTemplate(Writer writer, String template, Map params){
        try {
            TemplateProcessor processor = new TemplateProcessor(template);
            writer.write(processor.transformToString(params));
            writer.flush();
        } catch (IOException e) {
            throw new JameleonException("Could not write results to main HTML results file: "+e.getMessage());
        }
    }

    public static File getBaseDir(){
        return new File(Configurator.getInstance().getValue("baseDir", JameleonDefaultValues.BASE_DIR.getPath()));
    }

    public static File getResultsDir(){
        return new File(getBaseDir(), Configurator.getInstance().getValue("resultsDir", JameleonDefaultValues.RESULTS_DIR));

    }
    
}
