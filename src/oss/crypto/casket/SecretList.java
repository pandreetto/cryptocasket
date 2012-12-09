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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class SecretList
    extends Activity
    implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    private String login;

    private String password;

    private LinearLayout listView;

    private String currentSelItem;

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

                Secret[] secrets = SecretManager.getManager(this, login, password, newSecret).getSecrets();

                prepareList(secrets);

            } catch (Exception ex) {
                Log.e(getLocalClassName(), ex.getMessage(), ex);
                showError(R.string.list_err1 + ": " + ex.getMessage(), true);
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
            showError(R.string.manager_err1 + ": " + ex.getMessage(), false);
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
            for (int k = 0; k < listView.getChildCount(); k++) {
                SecretItemView secView = (SecretItemView) listView.getChildAt(k);
                if (secView.isSelected()) {
                    try {
                        manager.removeSecret(secView.getSecretId());
                        needRefresh = true;
                    } catch (Exception ex) {
                        showError(R.string.manager_err1 + ": " + ex.getMessage(), false);
                    }
                }
            }
            break;
        }

        if (needRefresh) {
            try {

                prepareList(manager.getSecrets());

            } catch (Exception ex) {
                showError(R.string.manager_err1 + ": " + ex.getMessage(), false);
            }
        }

        return false;
    }

    public void onClick(View sView) {
        TextView tView = (TextView) sView;
        String sId = tView.getText().toString();
        Intent intent = new Intent(this, SecretActivity.class);
        intent.putExtra(CasketConstants.LOGIN_TAG, login);
        intent.putExtra(CasketConstants.PWD_TAG, password);
        intent.putExtra(CasketConstants.SECID_TAG, sId);
        startActivity(intent);
    }

    public boolean onLongClick(View tView) {

        currentSelItem = ((TextView) tView).getText().toString();

        PopupMenu popMenu = new PopupMenu(this, tView);
        popMenu.setOnMenuItemClickListener(this);
        Menu menu = popMenu.getMenu();
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.sel_item);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.rmv_item);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.canc_pop);
        popMenu.show();

        return true;
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            /*
             * TODO to be implemented
             */
            currentSelItem = null;
            return true;
        case 2:
            try {
                SecretManager manager = SecretManager.getManager(this, login, password, false);
                manager.removeSecret(currentSelItem);
                prepareList(manager.getSecrets());
            } catch (Exception ex) {
                showError(R.string.manager_err1 + ": " + ex.getMessage(), false);
            }
            currentSelItem = null;
            return true;
        case 3:
            currentSelItem = null;
            return true;
        }
        return false;
    }

    private void prepareList(Secret[] secrets) {

        listView = new LinearLayout(this);
        listView.setOrientation(LinearLayout.VERTICAL);

        for (Secret secItem : secrets) {
            SecretItemView secView = new SecretItemView(this, secItem);
            listView.addView(secView);
        }

        this.setContentView(listView);

    }

    private void showError(String errMsg, boolean goon) {
        /*
         * TODO show dialog for error and exit if not goon
         */
    }

    public class SecretItemView
        extends LinearLayout {

        public SecretItemView(SecretList ctx, Secret secret) {
            super(ctx);

            this.setOrientation(HORIZONTAL);

            ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(lParams);

            TextView tView = new TextView(ctx);
            ViewGroup.LayoutParams tParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            tView.setLayoutParams(tParams);

            tView.setClickable(true);
            tView.setLongClickable(true);
            tView.setOnClickListener(ctx);
            tView.setOnLongClickListener(ctx);

            tView.setText(secret.getId());

            this.addView(tView);
        }

        public String getSecretId() {
            TextView tmpv = (TextView) this.getChildAt(0);
            return tmpv.getText().toString();
        }

        /*
         * TODO missing implementation of isSelected
         */
    }

}
