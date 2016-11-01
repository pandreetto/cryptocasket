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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import oss.crypto.casket.crypto.CryptoUtils;
import oss.crypto.casket.stego.bitmap.StegoCodec;

public class SecretManager {

    private static final int CACHE_IDLE = 0;

    private static final int CACHE_STABLE = 1;

    private static final int CACHE_TOFLUSH = 2;

    private Context context;

    private Uri pictureURI;

    private String pwd;

    private ArrayList<Secret> secretCache;

    private int cacheStatus;

    public void changePassword(String newPwd)
        throws SecretException {
        /*
         * TODO investigate
         */
    }

    public Secret[] getSecrets()
        throws SecretException {

        if (secretCache == null)
            loadSecrets();

        Secret[] result = new Secret[secretCache.size()];
        secretCache.toArray(result);
        return result;

    }

    public Secret getSecret(String secretId)
        throws SecretException {

        if (secretCache == null)
            loadSecrets();

        for (Secret tmpsec : secretCache) {
            if (tmpsec.getId().equals(secretId)) {
                return tmpsec;
            }
        }

        throw new SecretException(R.string.unknwsecret);
    }

    public void putSecret(Secret secret)
        throws SecretException {

        if (secretCache == null)
            loadSecrets();

        int idx = 0;
        for (Secret secItem : secretCache) {
            if (secret.getId().equals(secItem.getId())) {
                break;
            }
            idx++;
        }

        if (idx == secretCache.size()) {
            secretCache.add(secret);
        } else {
            secretCache.set(idx, secret);
        }
        cacheStatus = CACHE_TOFLUSH;

    }

    public void removeSecret(String secretId)
        throws SecretException {

        if (secretCache == null)
            loadSecrets();

        int idx = 0;
        for (Secret secItem : secretCache) {
            if (secretId.equals(secItem.getId())) {
                break;
            } else {
                idx++;
            }
        }

        if (idx < secretCache.size()) {
            secretCache.remove(idx);
            cacheStatus = CACHE_TOFLUSH;
        }

    }

    public void removeSecret(Secret secret)
        throws SecretException {
        removeSecret(secret.getId());
    }

    public void flushSecrets()
        throws SecretException {
        writeSecrets();
        cacheStatus = CACHE_STABLE;
    }

    private void loadSecrets()
        throws SecretException {

        BufferedReader reader = null;

        SecretParser parser = null;

        try {

            byte[] cryptoData = StegoCodec.decode(context, pictureURI);

            Cipher cipher = CryptoUtils.setupCipher(Cipher.DECRYPT_MODE, this.pwd);

            ByteArrayInputStream binStream = new ByteArrayInputStream(cryptoData);
            CipherInputStream cIn = new CipherInputStream(binStream, cipher);
            reader = new BufferedReader(new InputStreamReader(cIn));

            parser = new SecretParser(reader);
            parser.parse();
            secretCache = parser.getSecrets();
            cacheStatus = CACHE_STABLE;

        } catch (FileNotFoundException fEx) {

            Log.e(SecretManager.class.getName(), fEx.getMessage(), fEx);
            throw new SecretException(R.string.nocryptofile);
        } catch (IOException pEx) {

            Log.e(SecretManager.class.getName(), pEx.getMessage(), pEx);
            throw new SecretException(R.string.errparsecrypto);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioEx) {
                    Log.e(SecretManager.class.getName(), ioEx.getMessage(), ioEx);
                }
            }
        }

    }

    private void writeSecrets()
        throws SecretException {
        SecretWriter writer = null;

        if (cacheStatus < CACHE_TOFLUSH)
            return;

        try {
            Cipher cipher = CryptoUtils.setupCipher(Cipher.ENCRYPT_MODE, this.pwd);

            ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
            CipherOutputStream cOut = new CipherOutputStream(boutStream, cipher);
            writer = new SecretWriter(cOut);

            for (Secret secItem : secretCache) {
                Log.d(SecretManager.class.getName(), secItem.toXML());
                writer.write(secItem);
            }
            writer.close();
            writer = null;

            StegoCodec.encode(context, pictureURI, boutStream.toByteArray());

        } catch (IOException ioEx) {

            Log.e(SecretManager.class.getName(), ioEx.getMessage(), ioEx);
            throw new SecretException(R.string.nocryptofile);

        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

    public void init(Context ctx, Uri pictureURI, String pwd, boolean loadOnInit)
        throws SecretException {

        if (pictureURI == null || pwd == null || pwd.trim().length() == 0) {
            throw new SecretException(R.string.nologorpwd);
        }

        this.context = ctx;
        this.pictureURI = pictureURI;
        this.pwd = pwd;
        if (loadOnInit) {
            loadSecrets();
        } else {
            secretCache = new ArrayList<Secret>();
            cacheStatus = CACHE_STABLE;
        }

    }

    protected SecretManager() {
        cacheStatus = CACHE_IDLE;
    }

    private static SecretManager theManager = null;

    public static SecretManager getManager()
        throws SecretException {

        if (theManager == null) {
            theManager = new SecretManager();
        }

        return theManager;
    }

}