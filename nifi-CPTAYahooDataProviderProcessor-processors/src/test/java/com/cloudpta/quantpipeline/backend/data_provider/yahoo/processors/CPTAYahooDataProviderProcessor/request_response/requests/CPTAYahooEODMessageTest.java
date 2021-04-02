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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Danny
 */
public class CPTAYahooEODMessageTest 
{
    
    /**
     * Test of getMessageType method, of class CPTAYahooEODMessage.
     */
    @Test
    public void testGetMessageType() 
    {
        System.out.println("getMessageType");
        CPTAYahooEODMessage instance = new CPTAYahooEODMessage();
        String expResult = "";
        String result = instance.getMessageType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getURL method, of class CPTAYahooEODMessage.
     */
    @Test
    public void testGetURL() 
    {
        System.out.println("getURL");
        String symbol = "";
        CPTAYahooEODMessage instance = new CPTAYahooEODMessage();
        String expResult = "";
        String result = instance.getURL(symbol);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseResult method, of class CPTAYahooEODMessage.
     */
    @Test
    public void testParseResult() throws Exception 
    {
        System.out.println("parseResult");
        JsonObject data = null;
        CPTAYahooEODMessage instance = new CPTAYahooEODMessage();
        JsonArray expResult = null;
        instance.parseResult(data, null);
    }

    /**
     * Test of getDateFromYahooDate method, of class CPTAYahooEODMessage.
     */
    @Test
    public void testGetDateFromYahooDate() 
    {
        System.out.println("getDateFromYahooDate");
        long yahooDateTime = 0L;
        String timezone = "";
        CPTAYahooEODMessage instance = new CPTAYahooEODMessage();
        String expResult = "";
        String result = instance.getDateFromYahooDate(yahooDateTime, 0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processProperties method, of class CPTAYahooEODMessage.
     */
    @Test
    public void testProcessProperties() 
    {
        System.out.println("processProperties");
        List<CPTADataProperty> properties = null;
        CPTAYahooEODMessage instance = new CPTAYahooEODMessage();
        instance.processProperties(properties);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
