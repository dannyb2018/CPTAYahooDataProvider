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

import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import java.util.List;

/**
 *
 * @author Danny
 */
public class CPTAYahooEODMessage extends CPTAYahooTimeSeriesMessage
{
    @Override
    public String getMessageType()
    {
        return CPTAYahooConstants.EOD_MESSAGE_TYPE;
    }
    
    @Override
    protected void processProperties(List<CPTADataProperty> properties)
    {
        // We are only interested in one day back
        range = "1d";
        interval = "1d";    
    }    
}
