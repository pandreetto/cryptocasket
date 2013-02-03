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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class SecretList
    extends Activity
    implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener,
    DialogInterface.OnClickListener {

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

        try {

            Secret[] secrets = SecretManager.getManager(this, login, password).getSecrets();

            prepareList(secrets);

        } catch (SecretException sEx) {

            showError(sEx.getMsgRef());
            finish();

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
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.desel_all);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

                        SecretManager manager = SecretManager.getManager(this, login, password);

                        manager.removeSecret(secView.getSecretId());
                        needRefresh = true;

                    } catch (SecretException sEx) {
                        showError(sEx.getMsgRef());
                    }
                }
            }
            break;
        case 3:
            for (int k = 0; k < listView.getChildCount(); k++) {
                SecretItemView secView = (SecretItemView) listView.getChildAt(k);
                secView.setSelected(false);
            }
            break;
        }

        if (needRefresh) {
            try {

                SecretManager manager = SecretManager.getManager(this, login, password);

                prepareList(manager.getSecrets());

            } catch (SecretException sEx) {
                showError(sEx.getMsgRef());
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
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.canc_pop);
        popMenu.show();

        return true;
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            for (int k = 0; k < listView.getChildCount(); k++) {
                SecretItemView secView = (SecretItemView) listView.getChildAt(k);
                if (secView.getSecretId() == currentSelItem) {
                    secView.setSelected(!secView.isSelected());
                }
            }
            currentSelItem = null;
            return true;
        case 2:
            currentSelItem = null;
            return true;
        }
        return false;
    }

    public void onClick(DialogInterface dialog, int id) {

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

    private void showError(int msgId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.err_dmsg);
        builder.setMessage(msgId);

        builder.setPositiveButton(R.string.ok_dbtn, this);

        AlertDialog errDialog = builder.create();
        errDialog.show();

    }

    public class SecretItemView
        extends LinearLayout {

        private final static int TEXT_SIZE = 32;

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
            ColorStateList cList = this.getResources().getColorStateList(R.color.secretviewitem);
            tView.setTextColor(cList);
            tView.setTextSize(TEXT_SIZE);
            tView.setPadding(10, 10, 10, 10);

            tView.setText(secret.getId());

            this.addView(tView);
        }

        public String getSecretId() {
            TextView tmpv = (TextView) this.getChildAt(0);
            return tmpv.getText().toString();
        }

        public boolean isSelected() {
            TextView tmpv = (TextView) this.getChildAt(0);
            return tmpv.isSelected();
        }

        public void setSelected(boolean sel) {
            TextView tmpv = (TextView) this.getChildAt(0);
            tmpv.setSelected(sel);
        }
    }

}
