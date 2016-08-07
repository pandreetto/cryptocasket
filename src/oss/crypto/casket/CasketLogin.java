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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CasketLogin
    extends Activity
    implements DialogInterface.OnClickListener {

    private int currentActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActionMode = CasketConstants.NO_ACTION;

        setContentView(R.layout.login);
    }

    @Override
    public void onStart() {
        super.onStart();

        resetView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onClick(DialogInterface dialog, int id) {
        resetView();
    }

    public void login(View view) {
        EditText nLoginValue = (EditText) findViewById(R.id.login_message);
        EditText nPwdValue = (EditText) findViewById(R.id.pwd_message);
        String loginName = nLoginValue.getText().toString();
        String pwd = nPwdValue.getText().toString();

        try {

            SecretManager manager = SecretManager.getManager(this, loginName, pwd);
            manager.getSecrets();

        } catch (SecretException sEx) {

            showError(sEx.getMsgRef());
            return;

        } catch (Exception ex) {

            showError(R.string.casket_err);
            return;
        }

        Intent intent = new Intent(this, SecretList.class);
        intent.putExtra(CasketConstants.LOGIN_TAG, loginName);
        intent.putExtra(CasketConstants.PWD_TAG, pwd);

        startActivity(intent);

    }

    public void goOut(View view) {
        this.finish();
    }

    public void registerUser(View view) {
        showConfirmPwd();
        currentActionMode = CasketConstants.CREATE_ACTION;
    }

    public void destroyUser(View view) {
        showConfirmPwd();
        currentActionMode = CasketConstants.DESTROY_ACTION;
    }

    public void doAction(View view) {
        String loginName = ((EditText) findViewById(R.id.login_message)).getText().toString();
        String pwd = ((EditText) findViewById(R.id.pwd_message)).getText().toString();
        String rePwd = ((EditText) findViewById(R.id.repwd_message)).getText().toString();

        if (!pwd.equals(rePwd)) {

            showError(R.string.pwd_mismatch);
            return;
        }

        try {

            SecretManager manager = SecretManager.getManager(this, loginName, pwd);

            if (currentActionMode == CasketConstants.DESTROY_ACTION) {

                manager.destroy();
                resetView();

            } else {

                manager.create();

                Intent intent = new Intent(this, SecretList.class);
                intent.putExtra(CasketConstants.LOGIN_TAG, loginName);
                intent.putExtra(CasketConstants.PWD_TAG, pwd);

                startActivity(intent);
            }

        } catch (SecretException sEx) {

            showError(sEx.getMsgRef());

        }
    }

    private void resetView() {
        currentActionMode = CasketConstants.NO_ACTION;

        hideConfirmPwd();

        ((EditText) findViewById(R.id.login_message)).setText("");
        ((EditText) findViewById(R.id.pwd_message)).setText("");
        ((EditText) findViewById(R.id.repwd_message)).setText("");
    }

    private void hideConfirmPwd() {

        ((Button) findViewById(R.id.destroy_btn)).setVisibility(Button.VISIBLE);
        ((Button) findViewById(R.id.register_btn)).setVisibility(Button.VISIBLE);
        ((Button) findViewById(R.id.login_btn)).setVisibility(Button.VISIBLE);
        ((Button) findViewById(R.id.canc_btn)).setVisibility(Button.VISIBLE);
        ((TextView) findViewById(R.id.repwd_text)).setVisibility(TextView.GONE);
        ((EditText) findViewById(R.id.repwd_message)).setVisibility(EditText.GONE);
        ((Button) findViewById(R.id.confirm_btn)).setVisibility(Button.GONE);
        ((Button) findViewById(R.id.discard_btn)).setVisibility(Button.GONE);

    }

    private void showConfirmPwd() {

        ((Button) findViewById(R.id.destroy_btn)).setVisibility(Button.GONE);
        ((Button) findViewById(R.id.register_btn)).setVisibility(Button.GONE);
        ((Button) findViewById(R.id.login_btn)).setVisibility(Button.GONE);
        ((Button) findViewById(R.id.canc_btn)).setVisibility(Button.GONE);
        ((TextView) findViewById(R.id.repwd_text)).setVisibility(TextView.VISIBLE);
        ((EditText) findViewById(R.id.repwd_message)).setVisibility(EditText.VISIBLE);
        ((Button) findViewById(R.id.confirm_btn)).setVisibility(Button.VISIBLE);
        ((Button) findViewById(R.id.discard_btn)).setVisibility(Button.VISIBLE);
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
