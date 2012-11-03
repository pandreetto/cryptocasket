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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SecretList
    extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {

        super.onStart();

        Intent intent = this.getIntent();
        String login = intent.getStringExtra(CasketLogin.LOGIN_TAG);
        String pwd = intent.getStringExtra(CasketLogin.PWD_TAG);
        boolean newSecret = intent.getBooleanExtra(CasketLogin.ACT_TAG, false);

        if (login == null || pwd == null) {

            Log.d(getPackageName(), "No login or pwd");

        } else {

            try {

                String[] mStrings = SecretManager.getManager(this, login, pwd, newSecret).getSecrets();

                setListAdapter(new ArrayAdapter<String>(this, R.layout.secretitem, mStrings));
                getListView().setTextFilterEnabled(true);

            } catch (Exception ex) {
                Log.e(getPackageName(), ex.getMessage());
            }
        }

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Add Secret");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        default:
            Log.d(getPackageName(), "Selected item: " + item.getTitle());
            return super.onOptionsItemSelected(item);
        }
    }
}
