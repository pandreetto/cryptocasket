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

import java.io.OutputStream;
import java.io.PrintWriter;

public class SecretWriter {

    private PrintWriter writer;

    public SecretWriter(OutputStream out) {
        writer = new PrintWriter(out);
        writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.println("<secretlist>");
    }

    /*
     * TODO check special XML chars
     */
    public void write(Secret secret) {
        String tmps = secret.toXML();
        writer.println(tmps);
    }

    public void close() {
        writer.println("</secretlist>");
        writer.close();
    }
}