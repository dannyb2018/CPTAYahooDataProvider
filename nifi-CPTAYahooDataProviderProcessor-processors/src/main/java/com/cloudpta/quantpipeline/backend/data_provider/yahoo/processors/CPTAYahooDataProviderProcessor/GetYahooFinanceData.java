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
package com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor;

import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderProcessor;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataRetriever;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooBalanceSheetMessage;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooCashflowMessage;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooEODMessage;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooIncomeStatementMessage;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooOptionSeriesMessage;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.requests.CPTAYahooTimeSeriesMessage;
import java.util.HashMap;
import java.util.List;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;


@Tags({"Yahoo finance data provider"})
@CapabilityDescription("Gets data from Yahoo")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class GetYahooFinanceData extends CPTADataProviderProcessor<CPTADataRetriever>
{
    @Override
    public void addProperties(List<PropertyDescriptor> thisInstanceDescriptors)
    {
        // No Yahoo specific properties
    }
    
    @Override
    protected void setUpDataRetriever()
    {
        HashMap<String, Class> typeToMessageClassMap = new HashMap<>();
        
        // Set up the mapper
        typeToMessageClassMap = new HashMap<>();
        // Populate with types
        // Time series
        Class eodMessageClass = CPTAYahooEODMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.EOD_MESSAGE_TYPE, eodMessageClass);
        // Time series
        Class timeSeriesMessageClass = CPTAYahooTimeSeriesMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE, timeSeriesMessageClass);
        // income statement
        Class incomeStatementMessageClass = CPTAYahooIncomeStatementMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.INCOME_STATEMENT_MESSAGE_TYPE, incomeStatementMessageClass);
        // balancesheet
        Class balancesheetMessageClass = CPTAYahooBalanceSheetMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.BALANCE_SHEET_MESSAGE_TYPE, balancesheetMessageClass);
        // cashflow 
        Class cashflowMessageClass = CPTAYahooCashflowMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.CASHFLOW_MESSAGE_TYPE, cashflowMessageClass);
        // options series
        Class optionSeriesMessageClass = CPTAYahooOptionSeriesMessage.class;
        typeToMessageClassMap.put(CPTAYahooConstants.OPTION_SERIES_MESSAGE_TYPE, optionSeriesMessageClass);
        
        dataRetriever = CPTADataRetriever.getInstance(typeToMessageClassMap);
    }
}