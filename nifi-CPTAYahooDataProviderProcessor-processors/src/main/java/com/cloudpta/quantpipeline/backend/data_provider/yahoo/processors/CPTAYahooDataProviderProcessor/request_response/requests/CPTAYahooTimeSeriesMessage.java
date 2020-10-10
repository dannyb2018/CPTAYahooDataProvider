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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentDatabaseConstants;
import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderAPIConstants;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooMessage;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTAYahooTimeSeriesMessage extends CPTAYahooMessage
{
    @Override
    public void getResult
                             (
                             ComponentLog logger,
                             ProcessContext context,     
                             JsonArrayBuilder responses,
                             List<CPTAInstrumentSymbology> symbols, 
                             List<String> fields, 
                             List<CPTADataProperty> properties
                             )  throws CPTAException
    {
        // Get the properties
        processProperties(properties);
        // now get get data
        super.getResult(logger, context, responses, symbols, fields, properties);
    }
                             
    @Override
    public String getMessageType()
    {
        return CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE;
    }

    @Override
    protected String getURL(String symbol)
    {
        String urlHost = "https://query1.finance.yahoo.com/v8/finance/chart/"+ symbol + "?range=" + range + "&interval=" + interval + "&includePrePost=false";        
        return urlHost;
    }

    @Override
    protected void parseResult(JsonObject data, JsonArrayBuilder responses) throws CPTAException
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

        // Drill down to meta
        JsonObject chart = data.getJsonObject("chart");
        JsonArray yahooResultArray = chart.getJsonArray("result");        
        JsonObject yahooResult = yahooResultArray.getJsonObject(0);
        JsonObject meta = yahooResult.getJsonObject("meta");
        // Get the exchange
        String exchange = meta.getString(CPTAYahooConstants.EXCHANGE_CODE);
        // Get currency
        String currency = meta.getString(CPTAYahooConstants.CURRENCY);
        // Get instrument ticker
        String symbol = meta.getString(CPTAYahooConstants.SYMBOL);
        // Get offset to turn time into gmt
        long offset = (long)meta.getInt("gmtoffset");
        
        // Get timestamps
        JsonArray timestamps = yahooResult.getJsonArray(CPTAYahooConstants.TIMESTAMP);
        System.out.println(timestamps.toString());

        // Get quote
        JsonObject indicators = yahooResult.getJsonObject("indicators");
        JsonArray quoteAsArrayOfOneObject = indicators.getJsonArray("quote");
        JsonObject quote = quoteAsArrayOfOneObject.getJsonObject(0);
        // Get volumes, opens, closes, highs, lows, 
        JsonArray volumes = quote.getJsonArray(CPTAYahooConstants.VOLUME);
        JsonArray opens = quote.getJsonArray(CPTAYahooConstants.OPEN);
        JsonArray closes = quote.getJsonArray(CPTAYahooConstants.CLOSE);
        JsonArray highs = quote.getJsonArray(CPTAYahooConstants.HIGH);
        JsonArray lows = quote.getJsonArray(CPTAYahooConstants.LOW);

        // BUGBUGDB try to be a bit cleverer about checking which fields we want
        // Loop through each timestamp
        int numberOfDatapoints = timestamps.size();
        for( int i = 0; i < numberOfDatapoints; i++ )
        {
            JsonObjectBuilder datapointForThisTime = Json.createObjectBuilder();
            // Firstly add symbol and that it is ticker
            datapointForThisTime.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, symbol);
            datapointForThisTime.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
            
            // If we want exchange
            if( true == fields.contains(CPTAYahooConstants.EXCHANGE_CODE))
            {
                datapointForThisTime.add(CPTAYahooConstants.EXCHANGE_CODE, exchange);
            }
            
            // If we want currency
            if( true == fields.contains(CPTAYahooConstants.CURRENCY))
            {
                datapointForThisTime.add(CPTAYahooConstants.CURRENCY, currency);                
            }
            
            // If we want trade timestamp
            if( true == fields.contains(CPTAYahooConstants.TIMESTAMP))
            {
                String value = getDateFromYahooDate((long)timestamps.getInt(i), offset);
                // Add to datapoint
                datapointForThisTime.add(CPTAYahooConstants.TIMESTAMP, value);
            }
            
            // If we want close
            if( true == fields.contains(CPTAYahooConstants.CLOSE))
            {
                if(false == closes.isNull(i))
                {
                    String value = closes.getJsonNumber(i).bigDecimalValue().toString();
                    // Add to datapoint
                    datapointForThisTime.add(CPTAYahooConstants.CLOSE, value);
                }
                else
                {
                    // Add a null
                    datapointForThisTime.add(CPTAYahooConstants.CLOSE, JsonValue.NULL);
                }
            }
            
            // If we want open
            if( true == fields.contains(CPTAYahooConstants.OPEN))
            {
                if(false == opens.isNull(i))
                {
                    String value = opens.getJsonNumber(i).bigDecimalValue().toString();
                    // Add to datapoint
                    datapointForThisTime.add(CPTAYahooConstants.OPEN, value);
                }
                else
                {
                    // Add a null
                    datapointForThisTime.add(CPTAYahooConstants.OPEN, JsonValue.NULL);
                }
            }
            
            // If we want high
            if( true == fields.contains(CPTAYahooConstants.HIGH))
            {
                if(false == highs.isNull(i))
                {
                    String value = highs.getJsonNumber(i).bigDecimalValue().toString();
                    // Add to datapoint
                    datapointForThisTime.add(CPTAYahooConstants.HIGH, value);
                }
                else
                {
                    // Add a null
                    datapointForThisTime.add(CPTAYahooConstants.HIGH, JsonValue.NULL);
                }
            }
            
            // If we want low
            if( true == fields.contains(CPTAYahooConstants.LOW))
            {
                if(false == lows.isNull(i))
                {
                    String value = lows.getJsonNumber(i).bigDecimalValue().toString();
                    // Add to datapoint
                    datapointForThisTime.add(CPTAYahooConstants.LOW, value);
                }
                else
                {
                    // Add a null
                    datapointForThisTime.add(CPTAYahooConstants.LOW, JsonValue.NULL);
                }
            }
            
            // If we want volume
            if( true == fields.contains(CPTAYahooConstants.VOLUME))
            {
                if(false == volumes.isNull(i))
                {
                    String value = volumes.getJsonNumber(i).bigDecimalValue().toString();
                    // Add to datapoint
                    datapointForThisTime.add(CPTAYahooConstants.VOLUME, value);
                }
                else
                {
                    // Add a null
                    datapointForThisTime.add(CPTAYahooConstants.VOLUME, JsonValue.NULL);
                }
            }            
            
            // add to result
            responses.add(datapointForThisTime);
        }        
    }    
    
    protected String getDateFromYahooDate(long yahooDateTime, long offset)
    {
        // Convert milliseconds to String
        ZonedDateTime zonedDatetime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(yahooDateTime - offset), ZoneId.of("GMT"));
        LocalDateTime datetime = zonedDatetime.toLocalDateTime();
        String datetimeAsString = datetime.toString();
        return datetimeAsString;
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
                // And then lower case everything
                interval = interval.toLowerCase();
            }
            // If it is start offset
            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_START_DATE_PROPERTY))
            {
                // Add Start
                range = currentProperty.value;            
                // Remove the minus from the beginning                
                range = range.substring(1);
                // If last character is M then need to change that to mo
                if( true == range.endsWith("M"))
                {
                    range = range.substring(0, range.length()-1);
                    range = range + "mo";
                }
                // Make it lowercase
                range = range.toLowerCase();
            }
            // If it is end offset
//            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_END_DATE_PROPERTY))
//            {
                // Add End
  //
  //          }
        }
        
    }
    
    String range = null;
    String interval = null;
}
