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
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
        int actionMode = intent.getIntExtra(CasketConstants.ACT_TAG, CasketConstants.NO_ACTION);
        boolean newSecret = actionMode == CasketConstants.CREATE_ACTION;

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

        menu.add(Menu.NONE, 1, Menu.NONE, R.string.add_secret);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.rmv_secret);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SecretManager manager = null;
        try {
            manager = SecretManager.getManager(this, login, password, false);
        } catch (Exception ex) {
            /*
             * TODO handle exception
             */
            return false;
        }

        boolean needRefresh = false;
        switch (item.getItemId()) {
        case 1:
            Intent intent = new Intent(this, SecretActivity.class);
            intent.putExtra(CasketConstants.LOGIN_TAG, login);
            intent.putExtra(CasketConstants.PWD_TAG, password);
            startActivity(intent);
            break;
        case 2:
            ListView lView = this.getListView();
            for (int k = 0; k < lView.getChildCount(); k++) {
                LinearLayout tmpl = (LinearLayout) lView.getChildAt(k);
                CheckBox cBox = (CheckBox) tmpl.getChildAt(0);
                TextView tView = (TextView) tmpl.getChildAt(1);
                if (cBox.isChecked()) {
                    String secId = tView.getText().toString();
                    Log.d(this.getClass().getName(), "Removing " + secId);
                    try {
                        manager.removeSecret(secId);
                        needRefresh = true;
                    } catch (Exception ex) {
                        /*
                         * TODO handle exception
                         */
                    }
                }
            }
            break;
        }

        if (needRefresh) {
            try {
                Secret[] secrets = manager.getSecrets();
                setListAdapter(new ArrayAdapter<Secret>(this, R.layout.secretitem, R.id.secret_id, secrets));
            } catch (Exception ex) {
                /*
                 * TODO handle exception
                 */
            }
        }

        return false;
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
