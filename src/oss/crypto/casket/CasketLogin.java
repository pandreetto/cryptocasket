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

    public final static String LOGIN_TAG = "oss.crypto.casket.LOGIN";

    public final static String PWD_TAG = "oss.crypto.casket.PWD";

    public final static String ACT_TAG = "oss.crypto.casket.REGISTER";

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
        intent.putExtra(LOGIN_TAG, loginName);
        intent.putExtra(PWD_TAG, pwd);
        intent.putExtra(ACT_TAG, false);

        startActivity(intent);

    }

    public void confirmPwd(View loginView) {
        TextView confirmText = (TextView) findViewById(R.id.repwd_text);
        EditText repwdText = (EditText) findViewById(R.id.repwd_message);
        Button regBtn = (Button) findViewById(R.id.register_btn);
        Button logBtn = (Button) findViewById(R.id.login_btn);
        Button cancBtn = (Button) findViewById(R.id.canc_btn);
        Button createBtn = (Button) findViewById(R.id.create_btn);
        Button discBtn = (Button) findViewById(R.id.discard_btn);

        regBtn.setVisibility(Button.GONE);
        logBtn.setVisibility(Button.GONE);
        cancBtn.setVisibility(Button.GONE);
        confirmText.setVisibility(TextView.VISIBLE);
        repwdText.setVisibility(EditText.VISIBLE);
        createBtn.setVisibility(Button.VISIBLE);
        discBtn.setVisibility(Button.VISIBLE);

    }

    public void register(View loginView) {
        EditText loginText = (EditText) findViewById(R.id.login_message);
        String loginName = loginText.getText().toString();

        EditText pwdText = (EditText) findViewById(R.id.pwd_message);
        String pwd = pwdText.getText().toString();

        EditText rePwdText = (EditText) findViewById(R.id.repwd_message);
        String rePwd = rePwdText.getText().toString();

        if (pwd.equals(rePwd)) {
            Intent intent = new Intent(this, SecretList.class);
            intent.putExtra(LOGIN_TAG, loginName);
            intent.putExtra(PWD_TAG, pwd);
            intent.putExtra(ACT_TAG, true);

            startActivity(intent);
        } else {
            this.finish();
        }

    }

    public void goOut(View loginView) {
        this.finish();
    }

}
