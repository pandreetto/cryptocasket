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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Log;

public class SecretManager {

    private static String PBE_ALGO = "PBEWITHSHAANDTWOFISH-CBC";

    private static String MAIN_ALGO = "AES";

    private static String CIPHER_PARAMS = "AES/CBC/PKCS7Padding";

    private static byte[] SALT = { 0, 0xF, 1, 0xE, 2, 0xD, 3, 0xC, 4, 0xB, 5, 0xA, 6, 9, 7, 8 };

    private static int INT_COUNT = 1000;

    private static int KEY_LENGTH = 256;

    private static byte[] INITVECT = { 9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF, 0, 1, 2, 3, 4, 5, 6, 7, 8 };

    private Context context;

    private String login;

    private String pwd;

    private ArrayList<Secret> secretCache;

    private Cipher setupCipher(int type, String pwd)
        throws SecretException {
        try {
            PBEKeySpec pbeKey = new PBEKeySpec(pwd.toCharArray(), SALT, INT_COUNT, KEY_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGO);
            SecretKey sKey = factory.generateSecret(pbeKey);
            SecretKey aesKey = new SecretKeySpec(sKey.getEncoded(), MAIN_ALGO);

            Cipher cipher = Cipher.getInstance(CIPHER_PARAMS);
            cipher.init(type, aesKey, new IvParameterSpec(INITVECT));

            return cipher;
        } catch (Exception ex) {
            Log.e(SecretManager.class.getName(), ex.getMessage(), ex);
            throw new SecretException(R.string.nosetupchiper);
        }
    }

    private ArrayList<Secret> readSecrets()
        throws SecretException {

        if (secretCache != null) {
            return secretCache;
        }

        ArrayList<Secret> result = null;
        BufferedReader reader = null;
        FileInputStream fIn = null;

        SecretParser parser = null;

        try {
            Cipher cipher = setupCipher(Cipher.DECRYPT_MODE, this.pwd);
            fIn = context.openFileInput(this.login + ".crypto");
            CipherInputStream cIn = new CipherInputStream(fIn, cipher);
            reader = new BufferedReader(new InputStreamReader(cIn));

            parser = new SecretParser(reader);
            parser.parse();
            result = parser.getSecrets();

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
            } else if (fIn != null) {
                try {
                    fIn.close();
                } catch (IOException ioEx) {
                    Log.e(SecretManager.class.getName(), ioEx.getMessage(), ioEx);
                }
            }
        }
        return result;
    }

    private void writeSecrets(ArrayList<Secret> sList)
        throws SecretException {
        FileOutputStream fOut = null;
        SecretWriter writer = null;

        try {
            Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE, this.pwd);

            fOut = context.openFileOutput(login + ".crypto.new", Context.MODE_PRIVATE);
            CipherOutputStream cOut = new CipherOutputStream(fOut, cipher);
            writer = new SecretWriter(cOut);

            for (Secret secItem : sList) {
                Log.d(SecretManager.class.getName(), secItem.toXML());
                writer.write(secItem);
            }

        } catch (FileNotFoundException fEx) {

            Log.e(SecretManager.class.getName(), fEx.getMessage(), fEx);
            throw new SecretException(R.string.nocryptofile);

        } finally {
            if (writer != null) {
                writer.close();
            } else if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException ioEx) {
                    Log.e(SecretManager.class.getName(), ioEx.getMessage(), ioEx);
                }
            }
        }

        File srcFile = new File(context.getFilesDir(), login + ".crypto.new");
        File tgtFile = new File(context.getFilesDir(), login + ".crypto");

        if (!srcFile.renameTo(tgtFile)) {
            throw new SecretException(R.string.noupdatecrypto);
        }

        secretCache = sList;
    }

    protected SecretManager(Context ctx, String login, String pwd) throws SecretException {

        this.context = ctx;
        this.login = login;
        this.pwd = pwd;
        secretCache = null;

        if (login == null || login.trim().length() == 0 || pwd == null || pwd.trim().length() == 0) {
            throw new SecretException(R.string.nologorpwd);
        }

        File workDir = context.getFilesDir();
        File cryptoFile = new File(workDir, login + ".crypto");

        Log.d(SecretManager.class.getName(), "Cryptofile: " + cryptoFile.getAbsolutePath());

    }

    public void create()
        throws SecretException {
        File workDir = context.getFilesDir();
        File cryptoFile = new File(workDir, login + ".crypto");

        if (cryptoFile.exists()) {
            throw new SecretException(R.string.existscrypto);
        }

        ArrayList<Secret> sList = new ArrayList<Secret>(0);
        writeSecrets(sList);
    }

    public void destroy()
        throws SecretException {
        File srcFile = new File(login + ".crypto");
        if (!srcFile.delete()) {
            throw new SecretException(R.string.noupdatecrypto);
        }
    }

    public Secret[] getSecrets()
        throws SecretException {

        ArrayList<Secret> resList = readSecrets();
        Secret[] result = new Secret[resList.size()];
        resList.toArray(result);
        return result;

    }

    public Secret getSecret(String secretId)
        throws SecretException {
        ArrayList<Secret> resList = readSecrets();
        for (Secret tmpsec : resList) {
            if (tmpsec.getId().equals(secretId)) {
                return tmpsec;
            }
        }

        throw new SecretException(R.string.unknwsecret);
    }

    public void putSecret(Secret secret)
        throws SecretException {
        int idx = 0;
        ArrayList<Secret> resList = readSecrets();
        for (Secret secItem : resList) {
            if (secret.getId().equals(secItem.getId())) {
                break;
            }
            idx++;
        }

        if (idx == resList.size()) {
            resList.add(secret);
        } else {
            resList.set(idx, secret);
        }
        writeSecrets(resList);

    }

    public void removeSecret(String secretId)
        throws SecretException {
        ArrayList<Secret> resList = readSecrets();
        int idx = 0;
        boolean found = false;
        for (Secret secItem : resList) {
            if (secretId.equals(secItem.getId())) {
                found = true;
                break;
            } else {
                idx++;
            }
        }

        if (found) {
            resList.remove(idx);
            writeSecrets(resList);
        }

    }

    public void removeSecret(Secret secret)
        throws SecretException {
        removeSecret(secret.getId());
    }

    private static SecretManager theManager = null;

    public static SecretManager getManager(Context ctx, String login, String pwd)
        throws SecretException {

        if (theManager == null || (theManager.login != login && theManager.pwd != pwd)) {
            theManager = new SecretManager(ctx, login, pwd);
        }

        return theManager;
    }

}