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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SecretActivity
    extends ListActivity {

    private GroupOfSecretView viewBox = null;

    private String login;

    private String password;

    private String secretId;

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
        secretId = intent.getStringExtra(CasketConstants.SECID_TAG);

        if (login == null || password == null || secretId == null) {

            showError(R.string.nologorpwd);

        } else {

            try {

                SecretManager secMan = SecretManager.getManager(this, login, password);

                GroupOfSecret secretCard = (GroupOfSecret) secMan.getSecret(secretId);
                SecretTableAdapter sAdapter = new SecretTableAdapter(secretCard, this);
                setListAdapter(sAdapter);

            } catch (SecretException sEx) {

                showError(sEx.getMsgRef());

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

        menu.add(Menu.NONE, 1, Menu.NONE, R.string.save_all);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.rmv_all);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.desel_all);

        if (viewBox instanceof GroupOfSecretView) {
            menu.add(Menu.NONE, 4, Menu.NONE, R.string.add_prop);
            menu.add(Menu.NONE, 5, Menu.NONE, R.string.add_phone);
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
            } catch (SecretException sEx) {
                showError(sEx.getMsgRef());
            }

            finish();

            return true;
        case 2:

            ListView interView = this.getListView();
            boolean needRefresh = false;

            try {
                SecretManager manager = SecretManager.getManager(this, login, password);
                GroupOfSecret secretCard = (GroupOfSecret) manager.getSecret(secretId);

                for (int k = 0; k < interView.getChildCount(); k++) {
                    LinearLayout llItem = (LinearLayout) interView.getChildAt(k);
                    CheckBox cBox = (CheckBox) llItem.findViewById(R.id.prop_check);
                    TextView tView = (TextView) llItem.findViewById(R.id.prop_key);
                    if (cBox.isChecked()) {
                        String secItem = tView.getText().toString();
                        secretCard.remove(secItem);
                        needRefresh = true;
                        Log.d("SecretProperty", secretId + ": removed " + secItem);
                    }
                }

                if (needRefresh) {
                    manager.putSecret(secretCard);
                    SecretTableAdapter sAdapter = new SecretTableAdapter(secretCard, this);
                    setListAdapter(sAdapter);
                }

            } catch (SecretException sEx) {
                showError(R.string.casket_operr);
            }

            break;

        case 3:
            return true;

        case 4:

            GroupOfSecretView gView = (GroupOfSecretView) viewBox;
            PropertySecretView pView = new PropertySecretView(this);
            gView.addView(pView);
            return true;

        case 5:

            GroupOfSecretView gView2 = (GroupOfSecretView) viewBox;
            PhoneSecretView phView = new PhoneSecretView(this);
            gView2.addView(phView);
            return true;

        }
        return false;
    }

    public void callPhone(View phoneView) {

    }

    private void showError(int msgId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.err_dmsg);
        builder.setMessage(msgId);

        builder.setPositiveButton(R.string.ok_dbtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*
                 * TODO implement
                 */
            }
        });
        AlertDialog errDialog = builder.create();
        errDialog.show();

    }

    private class SecretTableAdapter
        implements ListAdapter {

        private GroupOfSecret secrets;

        private LayoutInflater inflater;

        public SecretTableAdapter(GroupOfSecret secrets, SecretActivity context) {
            this.secrets = secrets;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return secrets.size();
        }

        public Object getItem(int position) {
            return secrets.get(position);
        }

        public long getItemId(int position) {
            return secrets.get(position).getId().hashCode();
        }

        public int getItemViewType(int position) {
            return Adapter.IGNORE_ITEM_VIEW_TYPE;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RenderableSecret tmpsec = (RenderableSecret) secrets.get(position);
            LinearLayout result = (LinearLayout) inflater.inflate(tmpsec.getLayoutId(), null);

            TextView keyText = (TextView) result.findViewById(R.id.prop_key);
            keyText.setText(tmpsec.getId());

            TextView valueText = (TextView) result.findViewById(R.id.prop_value);
            valueText.setText(tmpsec.getValue());

            return result;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return secrets.size() == 0;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            /*
             * TODO implement
             */
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            /*
             * TODO implement
             */
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int position) {
            return true;
        }

    }

}