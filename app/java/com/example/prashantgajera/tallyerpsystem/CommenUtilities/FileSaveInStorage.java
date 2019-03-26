package com.example.prashantgajera.tallyerpsystem.CommenUtilities;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Prashant Gajera on 09-Feb-19.
 */

public class FileSaveInStorage {

    private static  String FILENAME =null;
    private static final String DNAME = ".com.android.tallyData1";
    private byte[] filedata=null;
    private byte[] Descrypt=null;
    private byte[] encrypted = null;
    private byte[] key = null;
    AESEncryption aesEncryption = new AESEncryption();
    public File rootPath=null;
    public File dataFile=null;
    public FileSaveInStorage(String filename,  byte[] filedata)
    {
        FILENAME= filename;
        this.filedata=filedata;
    }

    public void SaveFile()
    {
        aesEncryption = new AESEncryption();
        key = aesEncryption.generateAesSecretKey();
        encrypted = aesEncryption.encodeFile(key,filedata);

        rootPath = new File(Environment.getExternalStorageDirectory(), DNAME);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        dataFile = new File(rootPath, FILENAME);

        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, false);
            mOutput.write(encrypted);
            mOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JSONObject RetriveFile(String datafile)
    {
        JSONObject jsonObject=null;
        try {
            Log.i("datafile",dataFile+"");
            File dataFile = new File(datafile);
            FileInputStream mInput = new FileInputStream(dataFile);
            byte[] data = new byte[(int)dataFile.length()];
            mInput.read(data);
            key = aesEncryption.generateAesSecretKey();
            Descrypt = aesEncryption.decodeFile(key,data);
            String DecryptString = new String(Descrypt);

            mInput.close();
            Log.i("Display",DecryptString);
            jsonObject = new JSONObject(DecryptString);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  jsonObject;
    }

}


