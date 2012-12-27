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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SecretActivity
    extends Activity {

    private View viewBox = null;

    private String login;

    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = this.getIntent();
        login = intent.getStringExtra(CasketConstants.LOGIN_TAG);
        password = intent.getStringExtra(CasketConstants.PWD_TAG);
        String secId = intent.getStringExtra(CasketConstants.SECID_TAG);

        if (login == null || password == null) {

            Log.d(getLocalClassName(), "No login or pwd");

        } else {

            try {

                if (secId == null) {

                    viewBox = new GroupOfSecretView(this);

                } else {
                    SecretManager secMan = SecretManager.getManager(this, login, password);
                    Secret currentSec = secMan.getSecret(secId);
                    viewBox = SecretViewFactory.getSecretView(this, currentSec);
                }

                this.setContentView(viewBox);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 1, Menu.NONE, R.string.save_all);

        if (viewBox instanceof GroupOfSecretView) {
            menu.add(Menu.NONE, 2, Menu.NONE, R.string.add_prop);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:

            try {
                Secret newSecret = SecretViewFactory.getSecret(viewBox);
                SecretManager.getManager(this, login, password).putSecret(newSecret);
            } catch (Exception ex) {
                Log.e(getLocalClassName(), ex.getMessage(), ex);
            }

            return true;
        case 2:

            GroupOfSecretView gView = (GroupOfSecretView) viewBox;
            PropertySecretView pView = new PropertySecretView(this);
            gView.addView(pView);
            return true;

        }
        return false;
    }
}