package com.example.rssi_distance.helpers;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class DataFileWriter {

    public void writeToFiles(String filename, String folderName, String[] header, List<String[]> data){
        data.add(0, header);
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), folderName);
            File datafile = new File(folder, filename);
            if (!folder.exists()){
                if (folder.mkdirs())
                    Log.d(Parameters.LOG_TAG, "Folder created");
            }
            if (datafile.exists()){
                boolean deleted = datafile.delete();
                if (deleted)
                    Log.d(Parameters.LOG_TAG, filename.toString() + " exists! Writing through");
            }
            if (datafile.createNewFile()) {
                FileWriter outputfile = new FileWriter(new File(folder, filename));
                CSVWriter writer = new CSVWriter(outputfile, ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                writer.writeAll(data);
                Log.d(Parameters.LOG_TAG, "File created");
                writer.close();
            }
        } catch (IOException e) {
            Log.d(Parameters.LOG_TAG, e.toString());
            e.printStackTrace();
        }
    }

}
