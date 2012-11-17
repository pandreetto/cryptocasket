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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SecretActivity
    extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = this.getIntent();
        String login = intent.getStringExtra(CasketConstants.LOGIN_TAG);
        String password = intent.getStringExtra(CasketConstants.PWD_TAG);
        String secId = intent.getStringExtra(CasketConstants.SECID_TAG);

        if (login == null || password == null) {

            Log.d(getLocalClassName(), "No login or pwd");

        } else {

            try {
                Secret secret = SecretManager.getManager(this, login, password, false).getSecret(secId);

                this.setContentView(SecretViewFactory.getSecretView(secret, this));

            } catch (Exception ex) {
                Log.e(getLocalClassName(), ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(getLocalClassName(), "Called onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getLocalClassName(), "Called onDestroy");
    }

}