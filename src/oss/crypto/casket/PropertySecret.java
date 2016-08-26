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

import org.xml.sax.Attributes;

public class PropertySecret
    implements RenderableSecret {

    private String key;

    private String value;

    private StringBuffer currText;

    public PropertySecret() {
    }

    public String toString() {
        return key;
    }

    public String getId() {
        return key;
    }

    public int getLayoutId() {
        return R.layout.secretprop;
    }

    public void setId(String id) {
        key = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void processStartElement(String qName, Attributes attributes) {
        currText = new StringBuffer();
    }

    public void processEndElement(String qName) {
        if (qName.equals("key")) {
            key = currText.toString().trim();
        } else {
            value = currText.toString().trim();
        }
        currText = null;

    }

    public void processText(char[] ch, int start, int length) {
        if (currText != null) {
            currText.append(ch, start, length);
        }
    }

    public String toXML() {
        StringBuffer buff = new StringBuffer();
        buff.append("<key>").append(key).append("</key>\n");
        buff.append("<value>").append(value).append("</value>\n");
        return buff.toString();
    }
}