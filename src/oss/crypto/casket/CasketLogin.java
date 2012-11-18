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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CasketLogin
    extends Activity {

    private int currentActionMode = CasketConstants.NO_ACTION;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(getPackageName(), "Called onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(getPackageName(), "Called onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getPackageName(), "Called onDestroy");
    }

    public void login(View loginView) {
        EditText loginText = (EditText) findViewById(R.id.login_message);
        String loginName = loginText.getText().toString();

        EditText pwdText = (EditText) findViewById(R.id.pwd_message);
        String pwd = pwdText.getText().toString();

        Intent intent = new Intent(this, SecretList.class);
        intent.putExtra(CasketConstants.LOGIN_TAG, loginName);
        intent.putExtra(CasketConstants.PWD_TAG, pwd);
        intent.putExtra(CasketConstants.ACT_TAG, CasketConstants.NO_ACTION);

        startActivity(intent);

    }

    public void confirmPwd(View btnView) {
        TextView confirmText = (TextView) findViewById(R.id.repwd_text);
        EditText repwdText = (EditText) findViewById(R.id.repwd_message);
        Button regBtn = (Button) findViewById(R.id.register_btn);
        Button destBtn = (Button) findViewById(R.id.destroy_btn);
        Button logBtn = (Button) findViewById(R.id.login_btn);
        Button cancBtn = (Button) findViewById(R.id.canc_btn);
        Button createBtn = (Button) findViewById(R.id.confirm_btn);
        Button discBtn = (Button) findViewById(R.id.discard_btn);

        destBtn.setVisibility(Button.GONE);
        regBtn.setVisibility(Button.GONE);
        logBtn.setVisibility(Button.GONE);
        cancBtn.setVisibility(Button.GONE);
        confirmText.setVisibility(TextView.VISIBLE);
        repwdText.setVisibility(EditText.VISIBLE);
        createBtn.setVisibility(Button.VISIBLE);
        discBtn.setVisibility(Button.VISIBLE);

        Button clickedBtn = (Button) btnView;
        if (clickedBtn.getId() == R.id.register_btn) {
            currentActionMode = CasketConstants.CREATE_ACTION;
        } else if (clickedBtn.getId() == R.id.destroy_btn) {
            currentActionMode = CasketConstants.DESTROY_ACTION;
        }
    }

    public void doAction(View btnView) {
        EditText loginText = (EditText) findViewById(R.id.login_message);
        String loginName = loginText.getText().toString();

        EditText pwdText = (EditText) findViewById(R.id.pwd_message);
        String pwd = pwdText.getText().toString();

        EditText rePwdText = (EditText) findViewById(R.id.repwd_message);
        String rePwd = rePwdText.getText().toString();

        if (pwd.equals(rePwd)) {
            if (currentActionMode == CasketConstants.DESTROY_ACTION) {

                /*
                 * TODO remove secret file
                 */
                Log.d(getPackageName(), "Removing secret file");
                setContentView(R.layout.login);

            } else {

                Intent intent = new Intent(this, SecretList.class);
                intent.putExtra(CasketConstants.LOGIN_TAG, loginName);
                intent.putExtra(CasketConstants.PWD_TAG, pwd);
                intent.putExtra(CasketConstants.ACT_TAG, currentActionMode);

                startActivity(intent);
            }

        } else {

            /*
             * TODO missing error message
             */

            this.finish();
        }

    }

    public void goOut(View loginView) {
        this.finish();
    }

}
