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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataMessage;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public abstract class CPTAYahooMessage extends CPTADataMessage
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
        this.fields.addAll(fields);
        this.symbology = symbols.get(0);
        
        msgLogger = logger;
        msgLogger.trace("getting data");
        
        JsonArrayBuilder result = Json.createArrayBuilder();
        for(CPTAInstrumentSymbology currentInstrument : symbols)
        {
            JsonObject dataAsJsonObject = null;

            msgLogger.trace("About to make request for data");
            Client client = jakarta.ws.rs.client.ClientBuilder.newClient();            
            String url = getURL(currentInstrument.getID());
            msgLogger.trace("url for requesting data " + url);
            WebTarget webTarget = client.target(url);
            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            builder.header("content-type", "application/json");

            Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
            // Get the response status code
            int respStatusCode = response.getStatus();
            msgLogger.trace("got response, status " + Integer.toString(respStatusCode) );

            // If it is either of the successes
            if( 200 == respStatusCode )
            {
                msgLogger.trace("HTTP status: " + respStatusCode + " - A response is available now!" );
                msgLogger.trace("got data");
                // Need to break down the html and get the relevent json object embedded in it
                dataAsJsonObject = getDataAsJsonObjectFromHTML(response);

            }
            else 
            {
                msgLogger.trace("returned an error");
                handleError(response);
            }

            // parse the response
            parseResult(dataAsJsonObject, responses);
        }
    }
                                       
    protected abstract String getURL(String symbol);
    protected abstract void parseResult(JsonObject data, JsonArrayBuilder responses) throws CPTAException;        
            
    protected JsonObject getDataAsJsonObjectFromHTML(Response webpageTextAsResponseToParse)
    {
        // Get the text of the response
    	String responseText = webpageTextAsResponseToParse.readEntity(String.class);
        JsonReader responseReader = Json.createReader(new StringReader(responseText));
        JsonObject responseContent = responseReader.readObject();
        msgLogger.trace("result is " + responseText);
        
        // return the json content
        return responseContent;        
    }    

    protected void handleError(Response response) throws CPTAException
    {
        msgLogger.trace("Got an error");
        // Get the response code
        int respStatusCode = response.getStatus();

        String jsonAsString = response.readEntity(String.class);
        msgLogger.trace("error text is " + jsonAsString);
        // Read in all the text
        msgLogger.trace("parsing error text");
        JsonReader responseReader = Json.createReader(new StringReader(jsonAsString));
        JsonObject responseAsJson = responseReader.readObject();
        responseAsJson = responseAsJson.getJsonObject("error");
        String errorMessage = responseAsJson.getString("message");
        msgLogger.trace("error text successfully parsed, error message is " + errorMessage);
        msgLogger.error("Error requesting data, " + Integer.toString(respStatusCode)+ ", response is " + jsonAsString + ", errorMsg is " + errorMessage);

        // handle the error
        switch(respStatusCode)
        {
            case 400:
            {
                msgLogger.error("HTTP status: 400 (Bad Request).  Request content is malformed and cannot be parsed");
                break;
            }
            //HTTP/1.1 403 Forbidden
            case 403:
            {
                msgLogger.error("HTTP status: HTTP status: 403 (Forbidden).  Account not permissioned for this type of data");
                break;                
            }
            case 401:
            {
                msgLogger.error("HTTP status: HTTP status: 401 (Unauthorized).  Authentication token is invalid or has expired");
                break;                                
            }
            default:
            {
                msgLogger.error("Cannot proceed. Please check the meaning of HTTP status " + respStatusCode);
                break;
            }
        }
        
        msgLogger.trace("building exception");
        Exception createdException = new Exception(errorMessage);
        CPTAException exception = new CPTAException(createdException);
        msgLogger.trace("throwing exception");
        throw exception;
    }
    
    protected CPTAInstrumentSymbology symbology;
    protected HashSet<String> fields = new HashSet<>();
    protected ComponentLog msgLogger;
}
