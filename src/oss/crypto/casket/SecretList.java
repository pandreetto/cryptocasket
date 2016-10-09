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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SecretList
    extends ListActivity {

    private Uri pictureURI;

    private String password;

    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onStart() {

        super.onStart();

        Intent intent = this.getIntent();
        pictureURI = intent.getParcelableExtra(CasketConstants.PICT_TAG);
        password = intent.getStringExtra(CasketConstants.PWD_TAG);

        try {

            Secret[] secrets = SecretManager.getManager().getSecrets();

            SecretArrayAdapter<Secret> adapter = new SecretArrayAdapter<Secret>(this, R.layout.secretitem, secrets);
            setListAdapter(adapter);

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

        switch (item.getItemId()) {
        case 1:
            SecretDialogFragment sDialog = new SecretDialogFragment(this);
            sDialog.show(getFragmentManager(), "CREATE_SECRET");
            break;
        case 2:
            ListView interView = this.getListView();
            boolean needRefresh = false;

            try {
                SecretManager manager = SecretManager.getManager();

                for (int k = 0; k < interView.getChildCount(); k++) {
                    LinearLayout llItem = (LinearLayout) interView.getChildAt(k);
                    CheckBox cBox = (CheckBox) llItem.findViewById(R.id.secCheckBox);
                    TextView tView = (TextView) llItem.findViewById(R.id.secret_id);
                    if (cBox.isChecked()) {
                        String secId = tView.getText().toString();
                        manager.removeSecret(secId);
                        needRefresh = true;
                        Log.d("SecretList", "Removed " + secId);
                    }
                }

                if (needRefresh) {
                    SecretArrayAdapter<Secret> adapter = new SecretArrayAdapter<Secret>(this, R.layout.secretitem,
                            manager.getSecrets());
                    setListAdapter(adapter);
                }

            } catch (SecretException sEx) {
                showError(sEx.getMsgRef());
            }

            break;
        case 3:
            break;
        }

        return false;
    }

    public void openSecret(View sView) {
        TextView tView = (TextView) sView;
        String sId = tView.getText().toString();
        Intent intent = new Intent(this, SecretActivity.class);
        intent.putExtra(CasketConstants.PICT_TAG, pictureURI);
        intent.putExtra(CasketConstants.PWD_TAG, password);
        intent.putExtra(CasketConstants.SECID_TAG, sId);
        startActivity(intent);
    }

    public void createNewSecret(String secId) {
        try {
            GroupOfSecret gSecret = new GroupOfSecret();
            gSecret.setId(secId);

            SecretManager manager = SecretManager.getManager();
            manager.putSecret(gSecret);

        } catch (SecretException sEx) {
            showError(sEx.getMsgRef());
        }

        Intent intent = new Intent(this, SecretActivity.class);
        intent.putExtra(CasketConstants.PICT_TAG, pictureURI);
        intent.putExtra(CasketConstants.PWD_TAG, password);
        intent.putExtra(CasketConstants.SECID_TAG, secId);
        startActivity(intent);
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

    private class SecretArrayAdapter<T>
        extends ArrayAdapter<T> {

        public SecretArrayAdapter(Context ctx, int resource, T[] objects) {
            super(ctx, resource, objects);
        }

        @SuppressLint({ "InflateParams", "ViewHolder" })
        public View getView(int pos, View convView, ViewGroup parent) {
            if (convView != null)
                return convView;

            LinearLayout result = (LinearLayout) inflater.inflate(R.layout.secretitem, null);

            TextView tView = (TextView) result.findViewById(R.id.secret_id);
            tView.setText(getItem(pos).toString());
            return result;
        }
    }

    private class SecretDialogFragment
        extends DialogFragment {

        private SecretList context;

        private View dialogView;

        public SecretDialogFragment(SecretList ctx) {
            super();
            context = ctx;
        }

        @SuppressLint("InflateParams")
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            dialogView = inflater.inflate(R.layout.dialog_newsec, null);
            builder.setView(dialogView);

            builder.setPositiveButton(R.string.ok_dbtn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText secText = (EditText) dialogView.findViewById(R.id.newsec_value);
                    context.createNewSecret(secText.getText().toString());
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
