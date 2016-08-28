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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SecretActivity
    extends ListActivity {

    private String login;

    private String password;

    private String secretId;

    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
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

                ListView interView = this.getListView();
                if (interView.getChildCount() == 0) {
                    View cardHeader = inflater.inflate(R.layout.cardheader, null);
                    TextView cardName = (TextView) cardHeader.findViewById(R.id.card_header);
                    cardName.setText(secretId);
                    interView.addHeaderView(cardHeader);
                }

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

        menu.add(Menu.NONE, 1, Menu.NONE, R.string.add_prop);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.add_phone);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.rmv_all);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:

            AddDialogFragment propDialog = new AddDialogFragment(this, R.layout.dialog_newprop);
            propDialog.show(getFragmentManager(), "ADDPROPERTY");
            break;

        case 2:

            AddDialogFragment phoneDialog = new AddDialogFragment(this, R.layout.dialog_newphone);
            phoneDialog.show(getFragmentManager(), "ADDPHONE");
            break;

        case 3:

            ListView interView = this.getListView();
            boolean needRefresh = false;

            try {
                SecretManager manager = SecretManager.getManager(this, login, password);
                GroupOfSecret secretCard = (GroupOfSecret) manager.getSecret(secretId);

                for (int k = 1; k < interView.getChildCount(); k++) {
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

        }

        return false;
    }

    public void addSecretItem(int type, String secKey, String secValue) {
        try {
            SecretManager manager = SecretManager.getManager(this, login, password);
            GroupOfSecret secretCard = (GroupOfSecret) manager.getSecret(secretId);
            RenderableSecret rSecret = null;

            if (type == R.layout.dialog_newprop) {
                rSecret = new PropertySecret();
            } else if (type == R.layout.dialog_newphone) {
                rSecret = new PhoneSecret();
            } else {
                throw new SecretException(R.string.unknwrenderable);
            }

            rSecret.setId(secKey);
            rSecret.setValue(secValue);
            secretCard.add(rSecret);

            manager.putSecret(secretCard);
            SecretTableAdapter sAdapter = new SecretTableAdapter(secretCard, this);
            setListAdapter(sAdapter);

        } catch (SecretException sEx) {
            showError(R.string.casket_operr);
        }
    }

    public void callPhone(View phoneView) {
        String phoneNum = ((TextView) phoneView).getText().toString();
        Uri number = Uri.parse("tel:" + phoneNum);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
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

        public SecretTableAdapter(GroupOfSecret secrets, SecretActivity context) {
            this.secrets = secrets;
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

        @SuppressLint("ViewHolder")
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

    private class AddDialogFragment
        extends DialogFragment {

        private SecretActivity context;

        private View dialogView;

        private int layoutId;

        public AddDialogFragment(SecretActivity secAct, int layoutId) {
            super();
            context = secAct;
            this.layoutId = layoutId;
        }

        @SuppressLint("InflateParams")
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            dialogView = inflater.inflate(layoutId, null);
            builder.setView(dialogView);

            builder.setPositiveButton(R.string.ok_dbtn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText secKeyView = (EditText) dialogView.findViewById(R.id.new_name_cnt);
                    EditText secValueView = (EditText) dialogView.findViewById(R.id.new_value_cnt);

                    context.addSecretItem(layoutId, secKeyView.getText().toString(), secValueView.getText().toString());
                }
            });

            builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Nothing to do
                }
            });

            return builder.create();
        }
    }
}