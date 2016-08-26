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

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@Deprecated
public class GroupOfSecretView
    extends LinearLayout
    implements SecretView {

    private TextView secIdText;

    public GroupOfSecretView(Context ctx) {
        super(ctx);

        this.setOrientation(VERTICAL);

        secIdText = new EditText(ctx);
        secIdText.setHint(R.string.sec_id_hint);
        this.addView(secIdText);

    }

    public GroupOfSecretView(Context ctx, Secret secret) {
        super(ctx);

        this.setOrientation(VERTICAL);

        GroupOfSecret gSecret = (GroupOfSecret) secret;

        secIdText = new TextView(ctx);
        secIdText.setText(gSecret.getId());
        secIdText.setEnabled(false);
        secIdText.setGravity(Gravity.CENTER);
        secIdText.setPadding(5, 5, 5, 5);
        secIdText.setTextSize(24);
        this.addView(secIdText);

        for (Secret secItem : gSecret) {
            View viewItem = SecretViewFactory.getSecretView(ctx, secItem);
            this.addView(viewItem);
        }
    }

    public Secret buildSecret() {
        GroupOfSecret gSecret = new GroupOfSecret();
        gSecret.setId(secIdText.getText().toString());

        for (int k = 0; k < this.getChildCount(); k++) {
            View vChild = this.getChildAt(k);
            Secret tmps = SecretViewFactory.getSecret(vChild);
            if (tmps != null) {
                gSecret.add(tmps);
            }
        }
        return gSecret;
    }

}