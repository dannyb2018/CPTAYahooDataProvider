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

import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderAPIConstants;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataFieldValue;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooMessage;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 *
 * @author Danny
 */
public class CPTAYahooEODMessage extends CPTAYahooMessage
{
    @Override
    public String getMessageType()
    {
        return CPTAYahooConstants.EOD_MESSAGE_TYPE;
    }

    @Override
    protected String getURL(String symbol)
    {
        String urlHost = "https://query1.finance.yahoo.com/v8/finance/chart/"+ symbol + "?range=" + range + "&interval=" + interval + "&includePrePost=false";        
        return urlHost;
    }

    @Override
    protected JsonArray parseResult(JsonObject data) throws CPTAException
    {
        // URL is of form https://query1.finance.yahoo.com/v8/finance/chart/GOOG?range=5y&interval=1d&includePrePost=false
        // result is json array chart->result->first item in array->timestamp with has list of timestamps
        // Then chart->result->0->indicators->quote->first item in array->open, high, close, low, Volume
        /*
        {
        "chart":
            {"result":
                [
                {"meta":{"currency":"USD","symbol":"GOOG","exchangeName":"NMS","instrumentType":"EQUITY","firstTradeDate":1092902400,"gmtoffset":-14400,"timezone":"EDT","exchangeTimezoneName":"America/New_York","currentTradingPeriod":{"pre":{"timezone":"EDT","end":1509715800,"start":1509696000,"gmtoffset":-14400},"regular":{"timezone":"EDT","end":1509739200,"start":1509715800,"gmtoffset":-14400},"post":{"timezone":"EDT","end":1509753600,"start":1509739200,"gmtoffset":-14400}},"dataGranularity":"1d","validRanges":["1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"]},
                "timestamp":[1509715800,1509739200],
                "indicators":
                    {"quote":
                        [
                        {
                        "close":[1032.47998046875,1032.47998046875],
                        "volume":[1059100,1076350],
                        "open":[1022.1099853515625,1022.1099853515625],
                        "low":[1020.3099975585938,1020.3099975585938],
                        "high":[1032.6500244140625,1032.6500244140625]}],
                        "unadjclose":[{"unadjclose":[1032.47998046875,1032.47998046875]}],"adjclose":[{"adjclose":[1032.47998046875,1032.47998046875]}]}}],
                "error":null}}        
        */

        JsonArrayBuilder result = Json.createArrayBuilder();
                
        List<String> incomingFieldMap = new ArrayList<>();
        List<CPTADataFieldValue> valuesForThisBlock = new ArrayList<>();
        
        String todaysDate = "";
        JsonObject meta = data.getJsonObject("meta");
        // If exchange is one of the requested fields
        if( true == fields.contains("exchangeName"))
        {
            // Get exchange            
            String exchange = meta.getString("exchangeName");
            // Add exchange value
            CPTADataFieldValue exchangeNameValue = new CPTADataFieldValue();
            exchangeNameValue.name = "exchangeName";
            exchangeNameValue.value = exchange;
            exchangeNameValue.date = todaysDate;
        }
        // If instrumentType is one of the requested fields
        if( true == fields.contains("instrumentType"))
        {
            // Get instrumentType            
            String instrumentType = meta.getString("instrumentType");
            // Add instrument type
            CPTADataFieldValue exchangeNameValue = new CPTADataFieldValue();
            exchangeNameValue.name = "instrumentType";
            exchangeNameValue.value = instrumentType;
            exchangeNameValue.date = todaysDate;
        }
        
        // If there are any fields in first block
        if( 0 != valuesForThisBlock.size())
        {
            // We always want instrumentID
            CPTADataFieldValue instrumentIDField = new CPTADataFieldValue();
            instrumentIDField.setName(CPTAInstrumentAPIConstants);
            instrumentIDField.setValue(symbology.getID());
            valuesForThisBlock.add(instrumentIDField);
            // and id source which is always ticker
            CPTADataFieldValue instrumentIDSourceField = new CPTADataFieldValue();
            instrumentIDSourceField.setName(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME);
            instrumentIDSourceField.setValue("Ticker");
            valuesForThisBlock.add(instrumentIDSourceField);
            
            // Create a block
            CPTADataFieldValueBlock block = new CPTADataFieldValueBlockImpl();
            block.setFields(valuesForThisBlock);
            // Add it to list
            result.add(block);
            // clear values for this block
            valuesForThisBlock.clear();
        }
        
        String timezone = meta.get("timezone").asString();

        
        // Get timestamps
        JsonArray timestamps = data.get("timestamp").asArray();
        System.out.println(timestamps.toString());

        // Get quote
        data = data.get("indicators").asObject();
        JsonArray quoteAsArrayOfOneObject = data.get("quote").asArray();
        JsonObject quote = quoteAsArrayOfOneObject.get(0).asObject();
        // Get volumes, opens, closes, highs, lows, 
        JsonArray volumes = quote.get("volume").asArray();
        JsonArray opens = quote.get("open").asArray();
        JsonArray closes = quote.get("close").asArray();
        JsonArray highs = quote.get("high").asArray();
        JsonArray lows = quote.get("low").asArray();

        // BUGBUGDB try to be a bit cleverer about checking which fields we want
        // Loop through each timestamp
        int numberOfDatapoints = timestamps.size();
        for( int i = 0; i < numberOfDatapoints; i++ )
        {
            // If we want trade timestamp
            if( true == fields.contains("timestamp"))
            {
                String value = getDateFromYahooDate(timestamps.get(i).asLong(), timezone);
                incomingFieldMap.set(1, "timestamp");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }
            // If we want close
            if( true == fields.contains("close"))
            {
                String value = closes.get(i).toString();
                incomingFieldMap.set(1, "close");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }
            // If we want open
            if( true == fields.contains("open"))
            {
                String value = opens.get(i).toString();
                incomingFieldMap.set(1, "open");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }
            // If we want high
            if( true == fields.contains("high"))
            {
                String value = highs.get(i).toString();
                incomingFieldMap.set(1, "high");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }
            // If we want low
            if( true == fields.contains("low"))
            {
                String value = lows.get(i).toString();
                incomingFieldMap.set(1, "low");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }
            // If we want volume
            if( true == fields.contains("volume"))
            {
                String value = volumes.get(i).toString();
                incomingFieldMap.set(1, "volume");
                // make the first block
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                CPTADataFieldValue field = new CPTADataFieldValueImpl();
                field.setName(CPTAField);
                field.setValue(value);
                valuesForThisBlock.add(field);                
            }            
            
            // Create a block
            CPTADataFieldValueBlock block = new CPTADataFieldValueBlockImpl();
            block.setFields(valuesForThisBlock);
            // Add it to list
            result.add(block);
            // clear values for this block
            valuesForThisBlock.clear();
        }        

        return result;
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
    
    // We are only interested in one day back
    String range = null;
    String interval = null;    
}
