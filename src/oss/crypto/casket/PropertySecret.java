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

public class PropertySecret implements Secret {
    
    private String key;
    
    private String value;
    
    public PropertySecret(String in) {
        String[] tmpt = in.split(":");
        key = tmpt[0].trim();
        value = tmpt[1].trim();
    }
    
    public String toString(){
        return key + ": " + value;
    }
    
    public String getId() {
        return key;
    }

}