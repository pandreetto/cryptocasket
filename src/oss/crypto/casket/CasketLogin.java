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
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CasketLogin
    extends Activity
    implements View.OnClickListener, DialogInterface.OnClickListener {

    private final static int LOG_ID = 1356514713;

    private final static int CANC_ID = 1356514760;

    private final static int REG_ID = 1356514785;

    private final static int DEL_ID = 1356514829;

    private final static int GO_ID = 1356514853;

    private final static int DISC_ID = 1356514886;

    private int currentActionMode;

    private LinearLayout loginView;

    private TextView loginLabel;

    private EditText loginValue;

    private TextView pwdLabel;

    private EditText pwdValue;

    private Button logBtn;

    private Button cancBtn;

    private Button regBtn;

    private Button destBtn;

    private TextView rePwdLabel;

    private EditText rePwdValue;

    private Button goBtn;

    private Button discBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActionMode = CasketConstants.NO_ACTION;

        PasswordTransformationMethod tMethod = new PasswordTransformationMethod();
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.5);

        loginView = new LinearLayout(this);
        loginView.setOrientation(LinearLayout.VERTICAL);

        loginLabel = new TextView(this);
        loginLabel.setText(R.string.login_text);
        loginView.addView(loginLabel);

        loginValue = new EditText(this);
        loginView.addView(loginValue);

        pwdLabel = new TextView(this);
        pwdLabel.setText(R.string.pwd_text);
        loginView.addView(pwdLabel);

        pwdValue = new EditText(this);
        pwdValue.setTransformationMethod(tMethod);
        loginView.addView(pwdValue);

        LinearLayout row1 = new LinearLayout(this);
        logBtn = new Button(this);
        logBtn.setId(LOG_ID);
        logBtn.setText(R.string.login_btn);
        logBtn.setLayoutParams(lParams);
        logBtn.setOnClickListener(this);
        row1.addView(logBtn);
        cancBtn = new Button(this);
        cancBtn.setId(CANC_ID);
        cancBtn.setText(R.string.cancel_btn);
        cancBtn.setLayoutParams(lParams);
        cancBtn.setOnClickListener(this);
        row1.addView(cancBtn);
        loginView.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        destBtn = new Button(this);
        destBtn.setId(DEL_ID);
        destBtn.setText(R.string.destroy_btn);
        destBtn.setLayoutParams(lParams);
        destBtn.setOnClickListener(this);
        row2.addView(destBtn);
        regBtn = new Button(this);
        regBtn.setId(REG_ID);
        regBtn.setText(R.string.register_btn);
        regBtn.setLayoutParams(lParams);
        regBtn.setOnClickListener(this);
        row2.addView(regBtn);
        loginView.addView(row2);

        rePwdLabel = new TextView(this);
        rePwdLabel.setText(R.string.repwd_text);
        loginView.addView(rePwdLabel);

        rePwdValue = new EditText(this);
        rePwdValue.setTransformationMethod(tMethod);
        loginView.addView(rePwdValue);

        LinearLayout row3 = new LinearLayout(this);
        goBtn = new Button(this);
        goBtn.setId(GO_ID);
        goBtn.setText(R.string.confirm_btn);
        goBtn.setLayoutParams(lParams);
        goBtn.setOnClickListener(this);
        row3.addView(goBtn);
        discBtn = new Button(this);
        discBtn.setId(DISC_ID);
        discBtn.setText(R.string.discard_btn);
        discBtn.setLayoutParams(lParams);
        discBtn.setOnClickListener(this);
        row3.addView(discBtn);
        loginView.addView(row3);

        setContentView(loginView);
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

    public void onClick(View sView) {
        int btnId = ((Button) sView).getId();

        if (btnId == LOG_ID) {

            String loginName = loginValue.getText().toString();
            String pwd = pwdValue.getText().toString();

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

        } else if (btnId == CANC_ID) {

            this.finish();

        } else if (btnId == REG_ID) {

            showConfirmPwd();
            currentActionMode = CasketConstants.CREATE_ACTION;

        } else if (btnId == DEL_ID) {

            showConfirmPwd();
            currentActionMode = CasketConstants.DESTROY_ACTION;

        } else if (btnId == GO_ID) {

            String loginName = loginValue.getText().toString();
            String pwd = pwdValue.getText().toString();
            String rePwd = rePwdValue.getText().toString();

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

        } else if (btnId == DISC_ID) {

            this.finish();

        }
    }

    public void onClick(DialogInterface dialog, int id) {
        resetView();
    }

    private void resetView() {
        currentActionMode = CasketConstants.NO_ACTION;

        hideConfirmPwd();

        loginValue.setText("");
        pwdValue.setText("");
        rePwdValue.setText("");
    }

    private void hideConfirmPwd() {

        destBtn.setVisibility(Button.VISIBLE);
        regBtn.setVisibility(Button.VISIBLE);
        logBtn.setVisibility(Button.VISIBLE);
        cancBtn.setVisibility(Button.VISIBLE);
        rePwdLabel.setVisibility(TextView.GONE);
        rePwdValue.setVisibility(EditText.GONE);
        goBtn.setVisibility(Button.GONE);
        discBtn.setVisibility(Button.GONE);

    }

    private void showConfirmPwd() {

        destBtn.setVisibility(Button.GONE);
        regBtn.setVisibility(Button.GONE);
        logBtn.setVisibility(Button.GONE);
        cancBtn.setVisibility(Button.GONE);
        rePwdLabel.setVisibility(TextView.VISIBLE);
        rePwdValue.setVisibility(EditText.VISIBLE);
        goBtn.setVisibility(Button.VISIBLE);
        discBtn.setVisibility(Button.VISIBLE);

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
