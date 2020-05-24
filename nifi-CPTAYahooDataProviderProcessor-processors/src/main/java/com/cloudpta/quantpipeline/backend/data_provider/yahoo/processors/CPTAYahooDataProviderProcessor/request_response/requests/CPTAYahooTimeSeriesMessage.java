/*

Copyright 2017-2019 Advanced Products Limited, 
dannyb@cloudpta.com
github.com/dannyb2018

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests;

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderAPIConstants;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooMessage;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTAYahooTimeSeriesMessage extends CPTAYahooMessage
{
    @Override
    public JsonArray getResult
                             (
                             ComponentLog logger,
                             ProcessContext context,        
                             List<CPTAInstrumentSymbology> symbols, 
                             List<String> fields, 
                             List<CPTADataProperty> properties
                             )  throws CPTAException
    {
        // Get the properties
        processProperties(properties);
        // now get get data
        return super.getResult(logger, context, symbols, fields, properties);
    }
                             
    @Override
    public String getMessageType()
    {
        return CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE;
    }

    @Override
    protected String getURL()
    {
        String urlHost = "https://query1.finance.yahoo.com/v8/finance/chart/"+ symbology.getID() + "?range=" + range + "&interval=" + interval + "&includePrePost=false";        
        return urlHost;
    }

    @Override
    protected JsonArray parseResult(JsonObject data) throws CPTAException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
    
    protected void processProperties(List<CPTADataProperty> properties)
    {
        // Loop through properties
        for(CPTADataProperty currentProperty : properties )
        {
            // If it is frequency
            if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_FREQUENCY_PROPERTY))
            {
                // Add Frequency
                interval = currentProperty.value;
            }
            // If it is end offset
            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_END_DATE_PROPERTY))
            {
                // Add End
                range = currentProperty.value;
            }
            // If it is start offset
//            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_START_DATE_PROPERTY))
            {
                // Add Start
  //              dateObjectBuilder.add(CPTADSWSConstants.START_OFFSET_FIELD, currentProperty.value);
            }
        }
        
    }
    
    String range = null;
    String interval = null;
}
