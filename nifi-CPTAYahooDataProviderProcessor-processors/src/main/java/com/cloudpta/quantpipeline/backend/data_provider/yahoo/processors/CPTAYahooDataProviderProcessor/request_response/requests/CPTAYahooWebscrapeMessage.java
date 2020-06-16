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
import java.io.StringReader;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public abstract class CPTAYahooWebscrapeMessage extends CPTAYahooMessage
{
    @Override
    protected String getURL(String symbol)
    {
        String urlHost = "https://finance.yahoo.com/quote/" + symbol + "/" + queryType + "?p=" + symbol;
        msgLogger.trace("url to query is " + urlHost);
        return urlHost;
}

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
        msgLogger.trace("getting data");
        
        JsonObject dataAsJsonObject = null;
        
        msgLogger.trace("About to make request for data");
        Client client = javax.ws.rs.client.ClientBuilder.newClient();            
        String url = getURL("");
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
                             

    @Override
    protected JsonObject getDataAsJsonObjectFromHTML(Response webpageTextAsResponseToParse)
    {
        // Get the text of the response
    	String responseText = webpageTextAsResponseToParse.readEntity(String.class);
        
        // cut out the json text we are interested in
        String textToFind = "root.App.main";
        int offset = responseText.indexOf(textToFind) + textToFind.length();
        responseText = responseText.substring(offset);
        offset = responseText.indexOf("{");
        responseText = responseText.substring(offset);
        textToFind = "}}}};";
        offset = responseText.indexOf(textToFind) + 4;
        String jsonAsString = responseText.substring(0, offset);
        JsonReader reader = Json.createReader(new StringReader(jsonAsString));
        JsonObject responseContent = reader.readObject();
        msgLogger.trace("result is " + jsonAsString);
        
        // need to parse down to the right object
        // context->dispatcher->stores->storeName
        responseContent = responseContent.getJsonObject("context");
        msgLogger.trace("got context " + responseContent.toString());
        responseContent = responseContent.getJsonObject("dispatcher");
        msgLogger.trace("got dispatcher " + responseContent.toString());
        responseContent = responseContent.getJsonObject("stores");
        msgLogger.trace("got stores " + responseContent.toString());
        msgLogger.trace("getting store " + storeToQuery);
        responseContent = responseContent.getJsonObject(storeToQuery);
        msgLogger.trace("got store " + storeToQuery + ", " + responseContent.toString());
        
        // return the json content
        return responseContent;        
    }
    
    protected String storeToQuery = null;
    protected String queryType = null;    
}
