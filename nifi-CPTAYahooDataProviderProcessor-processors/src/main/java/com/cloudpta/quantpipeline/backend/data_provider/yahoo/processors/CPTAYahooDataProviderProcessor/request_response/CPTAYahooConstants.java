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
package com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response;

/**
 *
 * @author Danny
 */
public interface CPTAYahooConstants
{
    public static final String EOD_MESSAGE_TYPE = "EOD";
    public static final String TIMESERIES_MESSAGE_TYPE = "TS";
    public static final String INCOME_STATEMENT_MESSAGE_TYPE = "IS";
    public static final String BALANCE_SHEET_MESSAGE_TYPE = "BS";
    public static final String CASHFLOW_MESSAGE_TYPE = "CF";
    public static final String OPTION_SERIES_MESSAGE_TYPE = "TS";

    public static final String INCOME_STATEMENT_ANNUAL = "IncomeStatementHistory";
    public static final String INCOME_STATEMENT_QUARTERLY = "IncomeStatementHistoryQuarterly";
    public static final String BALANCE_SHEET_STATEMENTS = "BalanceSheetStatements";
    public static final String BALANCE_SHEET_ANNUAL = "BalanceSheetHistory";
    public static final String BALANCE_SHEET_QUARTERLY = "BalanceSheetHistoryQuarterly";
    public static final String CASHFLOW_STATEMENTS = "cashflowStatements";
    public static final String CASHFLOW_ANNUAL = "cashflowStatementHistory";
    public static final String CASHFLOW_QUARTERLY = "cashflowStatementHistoryQuarterly";
    public static final String OPTION_SERIES = "optionseries";
    

    public final static String OPEN = "open";
    public final static String CLOSE = "close";
    public final static String HIGH = "high";
    public final static String LOW = "low";
    public final static String VOLUME = "volume";
    public final static String TIMESTAMP = "timestamp";
    public final static String SYMBOL = "symbol";
    public final static String CURRENCY = "currency";
    public final static String EXCHANGE_CODE = "exchangeName";
    public final static String ASSET_TYPE = "instrumentType";    
}
