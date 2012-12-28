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
import android.content.res.ColorStateList;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class PropertySecretView
    extends LinearLayout
    implements SecretView, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    private final static LinearLayout.LayoutParams col1Params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.7);

    private final static LinearLayout.LayoutParams col2Params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.3);

    private Context context;

    public PropertySecretView(Context ctx) {
        super(ctx);

        context = ctx;

        this.setOrientation(HORIZONTAL);

        EditText keyField = new EditText(ctx);
        keyField.setHint(R.string.key_hint);
        keyField.setLayoutParams(col1Params);
        this.addView(keyField);

        EditText valueField = new EditText(ctx);
        valueField.setHint(R.string.value_hint);
        valueField.setLayoutParams(col2Params);
        this.addView(valueField);

    }

    public PropertySecretView(Context ctx, Secret secret) {
        super(ctx);

        context = ctx;

        this.setOrientation(HORIZONTAL);

        PropertySecret pSecret = (PropertySecret) secret;
        TextView keyField = new TextView(ctx);
        keyField.setText(pSecret.getId());
        keyField.setLayoutParams(col1Params);
        ColorStateList cList = context.getResources().getColorStateList(R.color.secretviewitem);
        keyField.setTextColor(cList);
        keyField.setOnLongClickListener(this);
        this.addView(keyField);

        EditText valueField = new EditText(ctx);
        valueField.setText(pSecret.getValue());
        valueField.setLayoutParams(col2Params);
        this.addView(valueField);

    }

    public Secret buildSecret() {
        PropertySecret pSecret = new PropertySecret();
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
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.canc_pop);
        popMenu.show();

        return true;
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            setSelected(!isSelected());
            return true;
        }
        return false;
    }

    public void setSelected(boolean sel) {
        TextView keyField = (TextView) this.getChildAt(0);
        keyField.setSelected(sel);
    }

    public boolean isSelected() {
        TextView tmpv = (TextView) this.getChildAt(0);
        return tmpv.isSelected();
    }

}