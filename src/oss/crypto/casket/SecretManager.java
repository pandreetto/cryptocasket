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
import java.util.HashMap;

import android.util.Log;

public class SecretManager {

    private String login;

    private String pwd;

    private ArrayList<String> secrets;

    protected SecretManager(String login, String pwd) {
        this.login = login;
        this.pwd = pwd;
        secrets = new ArrayList<String>();
        for (int k = 0; k < 20; k++) {
            secrets.add("Secret for " + login + ": " + k);
        }
    }

    public String[] getSecrets() {
        Log.d(SecretManager.class.getName(), "Login: " + login + " pwd: " + pwd);
        String[] result = new String[secrets.size()];
        secrets.toArray(result);
        return result;
    }

    public void addSecret(String secret) {
        secrets.add(secret);
    }

    private static HashMap<String, SecretManager> managers = new HashMap<String, SecretManager>();

    public static SecretManager getManager(String login, String pwd) {
        SecretManager result = managers.get(login);

        if (result == null) {
            result = new SecretManager(login, pwd);
            managers.put(login, result);
        }
        return result;
    }
}