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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentDatabaseConstants;
import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderAPIConstants;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import com.cloudpta.quantpipeline.backend.data_provider.yahoo.processors.CPTAYahooDataProviderProcessor.request_response.CPTAYahooConstants;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class CPTAYahooDataProviderProcessorTest 
{
    
    @Test
    public void testProcessorWithEmptyFlowFile() 
    {        
        // Mock the input file
        // If it is empty then it needs to have rics, fields and properties all empty
        String emptyRequestString = "{\""+ CPTADataProviderAPIConstants.INSTRUMENTS_ARRAY_NAME + "\":[], \""+ CPTADataProviderAPIConstants.FIELDS_ARRAY_NAME + "\":[], \""+ CPTADataProviderAPIConstants.PROPERTIES_ARRAY_NAME + "\":[]}";
        InputStream content = new ByteArrayInputStream(emptyRequestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTAYahooDataProviderProcessor());

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADataProviderAPIConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));
        System.out.println(resultValue);

    }

    @Test
    public void testProcessorEOD() 
    {        
        // Mock the input file
        // Add two instruments
        String ric1 = "2618.TW";
        String ric2 = "MSFT";
        String field1Name = CPTAYahooConstants.OPEN;
        String field2Name = CPTAYahooConstants.TIMESTAMP;
        String field3Name = CPTAYahooConstants.VOLUME;
        // Mock the input file
        // Build the instruments array
        JsonObjectBuilder instrument1 = Json.createObjectBuilder();
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric1);
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonObjectBuilder instrument2 = Json.createObjectBuilder();
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric2);
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonArrayBuilder instruments = Json.createArrayBuilder();
        instruments.add(instrument1);
        instruments.add(instrument2);
        // Now add field
        JsonObjectBuilder field1 = Json.createObjectBuilder();
        field1.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field1Name);
        field1.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.EOD_MESSAGE_TYPE);
        JsonObjectBuilder field2 = Json.createObjectBuilder();
        field2.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field2Name);
        field2.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.EOD_MESSAGE_TYPE);
        JsonObjectBuilder field3 = Json.createObjectBuilder();
        field3.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field3Name);
        field3.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.EOD_MESSAGE_TYPE);
        JsonArrayBuilder fields = Json.createArrayBuilder();
        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        // Empty properties array
        JsonArrayBuilder emptyPropertiesArray = Json.createArrayBuilder();
        // Add to the request
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add(CPTADataProviderAPIConstants.INSTRUMENTS_ARRAY_NAME, instruments);
        request.add(CPTADataProviderAPIConstants.FIELDS_ARRAY_NAME, fields);
        request.add(CPTADataProviderAPIConstants.PROPERTIES_ARRAY_NAME, emptyPropertiesArray);
        
        String requestString = request.build().toString();
        InputStream content = new ByteArrayInputStream(requestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTAYahooDataProviderProcessor());


        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADataProviderAPIConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));
        System.out.println(resultValue);

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
  //      result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorTimeseries() 
    { 
        // Mock the input file
        // Add two instruments
        String ric1 = "2618.TW";
        String ric2 = "MSFT";
        String field1Name = CPTAYahooConstants.OPEN;
        String field2Name = CPTAYahooConstants.TIMESTAMP;
        String field3Name = CPTAYahooConstants.VOLUME;
        // Mock the input file
        // Build the instruments array
        JsonObjectBuilder instrument1 = Json.createObjectBuilder();
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric1);
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonObjectBuilder instrument2 = Json.createObjectBuilder();
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric2);
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonArrayBuilder instruments = Json.createArrayBuilder();
        instruments.add(instrument1);
        instruments.add(instrument2);
        // Now add field
        JsonObjectBuilder field1 = Json.createObjectBuilder();
        field1.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field1Name);
        field1.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE);
        JsonObjectBuilder field2 = Json.createObjectBuilder();
        field2.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field2Name);
        field2.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE);
        JsonObjectBuilder field3 = Json.createObjectBuilder();
        field3.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field3Name);
        field3.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.TIMESERIES_MESSAGE_TYPE);
        JsonArrayBuilder fields = Json.createArrayBuilder();
        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        // Going to make it 3m and daily
        JsonArrayBuilder propertiesArray = Json.createArrayBuilder();
        // Properties are json object with name and value
        JsonObjectBuilder startDate = Json.createObjectBuilder();
        startDate.add(CPTADataProviderAPIConstants.PROPERTY_NAME_FIELD_NAME, CPTADataProviderAPIConstants.CPTA_START_DATE_PROPERTY);
        startDate.add(CPTADataProviderAPIConstants.PROPERTY_VALUE_FIELD_NAME, "-3M");
        propertiesArray.add(startDate);
        JsonObjectBuilder frequency = Json.createObjectBuilder();
        frequency.add(CPTADataProviderAPIConstants.PROPERTY_NAME_FIELD_NAME, CPTADataProviderAPIConstants.CPTA_FREQUENCY_PROPERTY);
        frequency.add(CPTADataProviderAPIConstants.PROPERTY_VALUE_FIELD_NAME, "1D");
        propertiesArray.add(frequency);
        // Add to the request
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add(CPTADataProviderAPIConstants.INSTRUMENTS_ARRAY_NAME, instruments);
        request.add(CPTADataProviderAPIConstants.FIELDS_ARRAY_NAME, fields);
        request.add(CPTADataProviderAPIConstants.PROPERTIES_ARRAY_NAME, propertiesArray);
        
        String requestString = request.build().toString();
        InputStream content = new ByteArrayInputStream(requestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTAYahooDataProviderProcessor());

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADataProviderAPIConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue( results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));
        System.out.println(resultValue);

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
//        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorEODTimeseries() 
    {
        // Mock the input file
        // Add two instruments
        String ric1 = "2618.TW";
        String ric2 = "MSFT";
        String field1Name = CPTAYahooConstants.OPEN;
        // Mock the input file
        // Build the instruments array
        JsonObjectBuilder instrument1 = Json.createObjectBuilder();
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric1);
        instrument1.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonObjectBuilder instrument2 = Json.createObjectBuilder();
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, ric2);
        instrument2.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_TICKER);
        JsonArrayBuilder instruments = Json.createArrayBuilder();
        instruments.add(instrument1);
        instruments.add(instrument2);
        // Now add field
        JsonObjectBuilder field1 = Json.createObjectBuilder();
        field1.add(CPTADataProviderAPIConstants.FIELD_NAME_FIELD_NAME, field1Name);
        field1.add(CPTADataProviderAPIConstants.MESSAGE_TYPE_FIELD_NAME, CPTAYahooConstants.EOD_MESSAGE_TYPE);
        JsonArrayBuilder fields = Json.createArrayBuilder();
        fields.add(field1);
        // Empty properties array
        JsonArrayBuilder emptyPropertiesArray = Json.createArrayBuilder();
        // Add to the request
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add(CPTADataProviderAPIConstants.INSTRUMENTS_ARRAY_NAME, instruments);
        request.add(CPTADataProviderAPIConstants.FIELDS_ARRAY_NAME, fields);
        request.add(CPTADataProviderAPIConstants.PROPERTIES_ARRAY_NAME, emptyPropertiesArray);
        
        String requestString = request.build().toString();
        InputStream content = new ByteArrayInputStream(requestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTAYahooDataProviderProcessor());

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADataProviderAPIConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));
        System.out.println(resultValue);
        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
//        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorWithOneRequestForDSSAndOneForDSWSFlowFile() 
    {
 
        // Mock the input file
        // If it is empty then it needs to have
        InputStream content = new ByteArrayInputStream("{\"hello\":\"nifi rocks\"}".getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTAYahooDataProviderProcessor());


        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADataProviderAPIConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }    
}
