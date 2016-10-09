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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CasketLogin
    extends Activity
    implements DialogInterface.OnClickListener {

    private static final int LOAD_REQUEST_CODE = 90;

    private static final int NEW_REQUEST_CODE = 91;

    private static final String TAG = "CasketLogin";

    private Uri pictureURI;

    private String password;

    private boolean loadMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        resetView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (pictureURI == null)
            resetView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // see
        // https://developer.android.com/guide/topics/providers/document-provider.html

        String pictureName = null;

        if (resultCode != Activity.RESULT_OK) {
            /*
             * TODO message
             */
            showError(R.string.casket_err);
        }

        if (resultData == null) {
            /*
             * TODO message
             */
            showError(R.string.casket_err);
        }

        pictureURI = resultData.getData();
        Log.i(TAG, "Selected image " + pictureURI.toString());

        Cursor cursor = getContentResolver().query(pictureURI, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                pictureName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                for (int k = 0; k < cursor.getColumnCount(); k++) {
                    Log.i(TAG, "Found value: " + cursor.getString(k));
                }

            } else {
                pictureName = "Unknown image";
            }
        } finally {
            cursor.close();
        }

        ((TextView) findViewById(R.id.picture_name)).setText(pictureName);
        Log.i(TAG, "Loaded picture " + pictureName);

        if (requestCode == NEW_REQUEST_CODE) {
            loadMode = false;
            ((TextView) findViewById(R.id.repwd_text)).setVisibility(TextView.VISIBLE);
            ((EditText) findViewById(R.id.repwd_message)).setVisibility(TextView.VISIBLE);

        } else if (requestCode == LOAD_REQUEST_CODE) {
            loadMode = true;
        } else {
            showError(R.string.casket_err);
        }

    }

    public void onClick(DialogInterface dialog, int id) {
        resetView();
    }

    public void loadPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, LOAD_REQUEST_CODE);
    }

    public void newPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, NEW_REQUEST_CODE);
    }

    public void goOn(View view) {

        EditText nPwdValue = (EditText) findViewById(R.id.pwd_message);
        String pwd = nPwdValue.getText().toString();
        EditText rePwdValue = (EditText) findViewById(R.id.repwd_message);
        String rePwd = rePwdValue.getText().toString();

        if (rePwd.trim().length() > 0 && !rePwd.equals(pwd)) {
            showError(R.string.pwd_mismatch);
            return;
        }

        password = pwd;

        try {

            SecretManager manager = SecretManager.getManager();
            manager.init(this, pictureURI, password, loadMode);

        } catch (SecretException sEx) {

            showError(sEx.getMsgRef());
            return;

        } catch (Exception ex) {

            showError(R.string.casket_err);
            return;
        }

        Intent intent = new Intent(this, SecretList.class);
        intent.putExtra(CasketConstants.PICT_TAG, pictureURI);
        intent.putExtra(CasketConstants.PWD_TAG, pwd);

        startActivity(intent);
    }

    private void resetView() {
        TextView pictText = (TextView) findViewById(R.id.picture_name);
        pictText.setText("");
        EditText pwdMsg = (EditText) findViewById(R.id.pwd_message);
        pwdMsg.setText("");
        ((TextView) findViewById(R.id.repwd_text)).setVisibility(TextView.GONE);
        EditText rePwdMsg = (EditText) findViewById(R.id.repwd_message);
        rePwdMsg.setText("");
        rePwdMsg.setVisibility(TextView.GONE);

        pictureURI = null;
        password = null;
    }

    private void showError(int msgId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.err_dmsg);
        builder.setMessage(msgId);

        builder.setPositiveButton(R.string.ok_dbtn, this);

        AlertDialog errDialog = builder.create();
        errDialog.show();

    }

}
