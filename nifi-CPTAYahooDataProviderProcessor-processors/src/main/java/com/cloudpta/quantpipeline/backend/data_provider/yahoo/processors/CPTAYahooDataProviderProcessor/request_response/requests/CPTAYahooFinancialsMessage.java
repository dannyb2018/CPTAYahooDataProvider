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

import com.cloudpta.utilites.exceptions.CPTAException;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

/**
 *
 * @author Danny
 */
public class CPTAYahooFinancialsMessage extends CPTAYahooWebscrapeMessage
{
    @Override
    public String getMessageType()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
/*    public CPTAYahooFinancialsMessage(String theRange, String theInterval)
    {
        queryType = "financials";
        storeToQuery = "QuoteSummaryStore";
        range = theRange;
        interval = theInterval;
    }

    @Override
    protected List<CPTADataFieldValueBlock> parseResult(JsonObject data) throws CPTAException
    {
        try
        {
            // Acceptable values for interval are 3 months
            if( 0 == interval.compareTo(CPTAYahooInterval.THREE_MONTH))
            {
                // for 3 months, the range can be 3 months, 6 months and 1 year
                if( 0 == range.compareTo(CPTAYahooInterval.THREE_MONTH))
                {
                    numberOfItems = 1;
                }
                else if( 0 == range.compareTo(CPTAYahooInterval.SIX_MONTH))
                {
                    numberOfItems = 2;                
                }
                else if( 0 == range.compareTo(CPTAYahooInterval.ONE_YEAR))
                {
                    numberOfItems = 4;                
                }

                // Get quarterlies
                balanceSheet = data.get("balanceSheetHistoryQuarterly").asObject().get("balanceSheetStatements").asArray();
                incomeStatement = data.get("incomeStatementHistoryQuarterly").asObject().get("incomeStatementHistory").asArray();
                cashflow = data.get("cashflowStatementHistoryQuarterly").asObject().get("cashflowStatements").asArray();            
            }
            // and one year
            else if( 0 == interval.compareTo(CPTAYahooInterval.YEAR))
            {
                //  for one year the range can be 1 year, 2 years and 5 years
                if( 0 == range.compareTo(CPTAYahooInterval.ONE_YEAR))
                {
                    numberOfItems = 1;
                }
                else if( 0 == range.compareTo(CPTAYahooInterval.TWO_YEAR))
                {
                    numberOfItems = 2;                
                }
                else if( 0 == range.compareTo(CPTAYahooInterval.FIVE_YEAR))
                {
                    numberOfItems = 4;                
                }

                // Get annual
                balanceSheet = data.get("balanceSheetHistory").asObject().get("balanceSheetStatements").asArray();
                incomeStatement = data.get("incomeStatementHistory").asObject().get("incomeStatementHistory").asArray();
                cashflow = data.get("cashflowStatementHistory").asObject().get("cashflowStatements").asArray();
            }
        }
        catch(Exception E)
        {
            // Get the error
            String error = "Error whilst getting financial data for  " + super.symbology.getID() + ", " + E.toString();
            List<String> errors = new ArrayList<>();
            errors.add(error);
            int errorCode = CPTAServiceBusAPIConstants.STATUS_INTERNAL_SERVER_ERROR;
            CPTAServiceException ex = new CPTAServiceException(errorCode, errors);
            throw ex;
        }
        // BUGBUGDB throw invalid parameter value if not these
        return getValues();
    }
    
    protected abstract List<CPTADataFieldValueBlock> getValues() throws CPTAServiceException;
    
    protected List<CPTADataFieldValueBlock> getValuesForType(JsonArray arrayOfTopicsInterestedIn, String topicName) throws CPTAServiceException
    {
        List<CPTADataFieldValueBlock> blocks = new ArrayList<>();

        // Loop through the number of items = quarters or years
        for( int i = 0; i < numberOfItems; i++ )
        {            
            // get the values for the current querters
            JsonObject currentIncomeStatement = arrayOfTopicsInterestedIn.get(i).asObject();

            // Create a block
            CPTADataFieldValueBlock newBlock = new CPTADataFieldValueBlockImpl();
            List<CPTADataFieldValue> values = new ArrayList<>();
            // We always want instrumentID
            CPTADataFieldValueImpl instrumentIDField = new CPTADataFieldValueImpl();
            instrumentIDField.setName(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME);
            instrumentIDField.setValue(symbology.getID());
            values.add(instrumentIDField);
            // and id source which is always ticker
            CPTADataFieldValueImpl instrumentIDSourceField = new CPTADataFieldValueImpl();
            instrumentIDSourceField.setName(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME);
            instrumentIDSourceField.setValue("Ticker");
            values.add(instrumentIDSourceField);
            
            List<String> incomingFieldMap = new ArrayList<>();
            incomingFieldMap.add(topicName);
            incomingFieldMap.add("");
            
            // Loop through each value
            for( String currentField : fields)
            {
                // get the mapping of the name back to 
                incomingFieldMap.set(1, currentField);
                String CPTAField = mapper.getIncomingMapping(incomingFieldMap);
                
                // Look in current version of income statement
                System.out.println(currentField);
                JsonValue valueAsJson = currentIncomeStatement.get(currentField);
                String value = CPTAMarketDataConstants.DATA_FIELD_NOT_AVAILABLE;
                if( false == valueAsJson.asObject().isEmpty())
                {
                    // Get long format
                    JsonValue formattedValue = valueAsJson.asObject().get("longFmt");
                    if( null != formattedValue)
                    {
                        value = formattedValue.asString();
                    }
                    else
                    {
                        value = valueAsJson.asObject().get("fmt").asString();
                    }
                }
                
                // create the data value
                CPTADataFieldValue newDataValue = new CPTADataFieldValueImpl();
                newDataValue.setName(CPTAField);
                newDataValue.setValue(value);
                // Add it to list
                values.add(newDataValue);
            }     
            
            // add values to new block
            newBlock.setFields(values);
            // Add to blocks
            blocks.add(newBlock);
        }
        
        return blocks;
        
    }

    @Override
    protected JsonArray parseResult(JsonObject data) throws CPTAException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMessageType()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected JsonArray balanceSheet = null;
    protected JsonArray incomeStatement = null;
    protected JsonArray cashflow = null;
    protected int numberOfItems = 1;
    protected String range = null;
    protected String interval = null;*/

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
