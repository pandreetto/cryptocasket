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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SecretList
    extends ListActivity {

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
        boolean newSecret = intent.getBooleanExtra(CasketConstants.ACT_TAG, false);

        if (login == null || password == null) {

            Log.d(getLocalClassName(), "No login or pwd");

        } else {

            try {

                ListView lView = this.getListView();
                Secret[] secrets = SecretManager.getManager(this, login, password, newSecret).getSecrets();

                setListAdapter(new ArrayAdapter<Secret>(this, R.layout.secretitem, R.id.secret_id, secrets));
                lView.setTextFilterEnabled(true);

            } catch (Exception ex) {
                Log.e(getLocalClassName(), ex.getMessage(), ex);
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

        menu.add(R.string.add_secret);
        menu.add(R.string.rmv_secret);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        default:
            Log.d(getLocalClassName(), "Selected item: " + item.getTitle());
            return super.onOptionsItemSelected(item);
        }
    }

    public void selectSecret(View sView) {
        TextView tView = (TextView) sView;
        String sId = tView.getText().toString();
        Intent intent = new Intent(this, SecretActivity.class);
        intent.putExtra(CasketConstants.LOGIN_TAG, login);
        intent.putExtra(CasketConstants.PWD_TAG, password);
        intent.putExtra(CasketConstants.SECID_TAG, sId);
        startActivity(intent);
    }

}
