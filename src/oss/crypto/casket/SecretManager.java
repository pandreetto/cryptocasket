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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

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

    Cipher setupCipher(int type, String pwd)
        throws IOException {
        try {
            PBEKeySpec pbeKey = new PBEKeySpec(pwd.toCharArray(), SALT, INT_COUNT, KEY_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGO);
            SecretKey sKey = factory.generateSecret(pbeKey);
            SecretKey aesKey = new SecretKeySpec(sKey.getEncoded(), MAIN_ALGO);

            Cipher cipher = Cipher.getInstance(CIPHER_PARAMS);
            cipher.init(type, aesKey, new IvParameterSpec(INITVECT));

            return cipher;
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    protected SecretManager(Context ctx, String login, String pwd, boolean create) throws IOException {

        this.context = ctx;
        this.login = login;
        this.pwd = pwd;

        File workDir = context.getFilesDir();
        File cryptoFile = new File(workDir, login + ".crypto");

        Log.d(SecretManager.class.getName(), "Cryptofile: " + cryptoFile.getAbsolutePath());

        if (create) {

            if (cryptoFile.exists()) {
                // throw new IOException("Cryptofile already exists");
            }

            try {

                Cipher cipher = this.setupCipher(Cipher.ENCRYPT_MODE, this.pwd);

                FileOutputStream fOut = context.openFileOutput(login + ".crypto", Context.MODE_PRIVATE);
                CipherOutputStream cOut = new CipherOutputStream(fOut, cipher);
                PrintWriter writer = new PrintWriter(cOut);

                for (int k = 0; k < 20; k++) {
                    String tmps = "Secret for " + login + ": " + k;
                    writer.println(tmps);
                }
                writer.close();
            } catch (Exception ex) {
                Log.e(SecretManager.class.getName(), ex.getMessage(), ex);
                throw new IOException(ex.getMessage());
            }

        } else if (!cryptoFile.exists()) {
            throw new IOException("Missing cryptofile");
        }

    }

    public String[] getSecrets() {
        Log.d(SecretManager.class.getName(), "Login: " + login + " pwd: " + pwd);

        ArrayList<String> resList = new ArrayList<String>();
        try {

            Cipher cipher = this.setupCipher(Cipher.DECRYPT_MODE, this.pwd);
            FileInputStream fIn = context.openFileInput(this.login + ".crypto");
            CipherInputStream cIn = new CipherInputStream(fIn, cipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cIn));
            String tmps = reader.readLine();
            while (tmps != null) {
                resList.add(tmps.trim());
                tmps = reader.readLine();
            }
            reader.close();
        } catch (Exception ex) {
            Log.e(SecretManager.class.getName(), ex.getMessage());
        }
        String[] result = new String[resList.size()];
        resList.toArray(result);
        return result;
    }

    public void addSecret(String secret) {
    }

    private static HashMap<String, SecretManager> managers = new HashMap<String, SecretManager>();

    public static SecretManager getManager(Context ctx, String login, String pwd, boolean create)
        throws IOException {

        SecretManager result = managers.get(login);

        if (result == null) {
            result = new SecretManager(ctx, login, pwd, create);
            managers.put(login, result);
        }
        return result;
    }
}