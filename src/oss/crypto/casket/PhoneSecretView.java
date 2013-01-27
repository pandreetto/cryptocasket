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
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

public class PhoneSecretView
    extends PropertySecretView {

    public PhoneSecretView(Context ctx) {
        super(ctx);
    }

    public PhoneSecretView(Context ctx, Secret secret) {
        super(ctx, secret);
    }

    public Secret buildSecret() {
        PhoneSecret pSecret = new PhoneSecret();
        TextView key = (TextView) this.getChildAt(0);
        TextView value = (TextView) this.getChildAt(1);
        pSecret.setId(key.getText().toString());
        pSecret.setValue(value.getText().toString());
        return pSecret;
    }

    public boolean onLongClick(View tView) {

        PopupMenu popMenu = new PopupMenu(context, tView);
        popMenu.setOnMenuItemClickListener(this);
        Menu menu = popMenu.getMenu();
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.sel_item);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.mod_item);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.call_item);
        popMenu.show();

        return true;
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            setSelected(!isSelected());
            return true;
        case 2:
            EditText eText = (EditText) this.getChildAt(1);
            eText.setInputType(this.getInputType());
            eText.requestFocus();
            return true;
        case 3:
            TextView vText = (TextView) this.getChildAt(1);
            Uri number = Uri.parse("tel:" + vText.getText());
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            context.startActivity(callIntent);
            return true;
        }
        return false;
    }

    protected int getInputType() {
        return InputType.TYPE_CLASS_PHONE;
    }

}