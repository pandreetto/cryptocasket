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

import java.lang.reflect.Constructor;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class SecretViewFactory {

    public interface SecretViewHelper {

        public Secret buildSecret();

    }

    public static View getSecretView(Context ctx, Secret secret) {
        try {

            String sClassName = secret.getClass().getName() + "View";
            Class<?> sClass = Class.forName(sClassName);
            Constructor<?> cTor = sClass.getConstructor(Context.class, Secret.class);
            return (View) cTor.newInstance(ctx, secret);

        } catch (Exception ex) {
            Log.e(SecretViewFactory.class.getName(), ex.getMessage(), ex);
        }
        return null;

    }

    public static Secret getSecret(View sView) {
        if (sView instanceof SecretViewHelper) {
            return ((SecretViewHelper) sView).buildSecret();
        }
        return null;
    }
}