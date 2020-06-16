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
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooMessage;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTAYahooAnalysisMessage extends CPTAYahooMessage
{

    @Override
    public String getMessageType()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected String getURL(String symbol)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void parseResult(JsonObject data, JsonArrayBuilder responses) throws CPTAException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
