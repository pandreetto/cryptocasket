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

import java.util.ArrayList;
import java.util.Iterator;

import org.xml.sax.Attributes;

public class GroupOfSecret
    implements Secret, Iterable<Secret> {

    private String id;

    private ArrayList<Secret> secretList;

    private Secret currSecret;

    public GroupOfSecret() {
        secretList = new ArrayList<Secret>();
        id = null;
        currSecret = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addSecret(Secret sec) {
        secretList.add(sec);
    }

    public void removeSecret(Secret sec) {
        secretList.remove(sec);
    }

    public Iterator<Secret> iterator() {
        return secretList.iterator();
    }

    public void processStartElement(String qName, Attributes attributes) {
        if (qName.equals("group")) {
            id = attributes.getValue("id");
        } else if (qName.equals("secretitem")) {
            String className = attributes.getValue("class");
            try {
                currSecret = (Secret) Class.forName(className).newInstance();
            } catch (Exception ex) {
                /*
                 * TODO handle exception
                 */
            }
        } else {
            currSecret.processStartElement(qName, attributes);
        }
    }

    public void processEndElement(String qName) {
        if (qName.equals("secretitem")) {

            secretList.add(currSecret);
            currSecret = null;

        } else if (!qName.equals("group")) {

            currSecret.processEndElement(qName);

        }
    }

    public void processText(char[] ch, int start, int length) {
        if (currSecret != null) {
            currSecret.processText(ch, start, length);
        }
    }

    public String toXML() {

        StringBuffer buff = new StringBuffer();
        buff.append("<group id=\"").append(id).append("\"/>\n");
        for (Secret secItem : secretList) {
            buff.append("<secretitem class=\"");
            buff.append(secItem.getClass().getName()).append("\">");
            buff.append(secItem.toXML());
            buff.append("</secretitem>\n");
        }

        return buff.toString();
    }

    public String toString() {
        return getId();
    }

}