/*
 * Copyright (c) Paolo Andreetto
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package oss.crypto.casket;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SecretParser
    extends DefaultHandler {

    private SAXParser saxParser;

    private InputSource input;

    private Secret currSecret;

    private ArrayList<Secret> secretList;

    public SecretParser(Reader xmlReader) throws IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        try {
            saxParser = spf.newSAXParser();
            input = new InputSource(xmlReader);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }

        secretList = new ArrayList<Secret>();
    }

    public void parse()
        throws IOException {
        try {
            saxParser.parse(input, this);
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage(), ex);
            throw new IOException(ex.getMessage());
        }
    }

    public void startElement(String uri, String name, String qName, Attributes attributes)
        throws SAXParseException {

        if (qName.equals("secret")) {
            String className = attributes.getValue("class");
            try {
                currSecret = (Secret) Class.forName(className).newInstance();
            } catch (Exception ex) {
                throw new SAXParseException(ex.getMessage(), null);
            }
        } else if (!qName.equals("secretlist")) {
            currSecret.processStartElement(qName, attributes);
        }
    }

    public void endElement(String uri, String name, String qName)
        throws SAXParseException {

        if (qName.equals("secret")) {
            secretList.add(currSecret);
            currSecret = null;
        } else if (!qName.equals("secretlist")) {
            currSecret.processEndElement(qName);
        }
    }

    public void characters(char[] ch, int start, int length)
        throws SAXParseException {
        if (currSecret != null) {
            currSecret.processText(ch, start, length);
        }
    }

    public ArrayList<Secret> getSecrets() {
        return secretList;
    }

}