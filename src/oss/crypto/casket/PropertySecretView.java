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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PropertySecretView
    extends LinearLayout
    implements SecretViewFactory.SecretViewHelper {

    public PropertySecretView(Context ctx) {
        super(ctx);

        this.setOrientation(HORIZONTAL);

        ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        EditText keyField = new EditText(ctx);
        keyField.setLayoutParams(lParams);
        this.addView(keyField);

        EditText valueField = new EditText(ctx);
        valueField.setLayoutParams(lParams);
        this.addView(valueField);

    }

    public PropertySecretView(Context ctx, Secret secret) {
        super(ctx);

        this.setOrientation(HORIZONTAL);

        PropertySecret pSecret = (PropertySecret) secret;
        TextView key = new TextView(ctx);
        key.setText(pSecret.getId());
        key.setEnabled(false);
        this.addView(key);

        TextView value = new TextView(ctx);
        value.setText(pSecret.getValue());
        this.addView(value);

    }

    public Secret buildSecret() {
        PropertySecret pSecret = new PropertySecret();
        TextView key = (TextView) this.getChildAt(0);
        TextView value = (TextView) this.getChildAt(1);
        pSecret.setId(key.getText().toString());
        pSecret.setValue(value.getText().toString());
        return pSecret;
    }

}