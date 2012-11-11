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
import android.widget.LinearLayout;
import android.widget.TextView;

public class PropertySecretView
    extends LinearLayout {

    public PropertySecretView(Context ctx, Secret secret) {
        super(ctx);

        this.setOrientation(VERTICAL);

        PropertySecret pSecret = (PropertySecret) secret;
        TextView key = new TextView(ctx);
        key.setText(pSecret.getId());
        this.addView(key);

        TextView value = new TextView(ctx);
        value.setText(pSecret.getValue());
        this.addView(value);

    }

}