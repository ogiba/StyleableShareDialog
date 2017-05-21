package ogiba.stylablesharedialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ogiba on 21.05.2017.
 */

public class FileAsync extends AsyncTask<Bitmap, Integer, Boolean> {
    private Context context;
    private String directoryName;
    private String fileName;
    private ResultListener callback;

    public FileAsync(Context context, String direcotryName, String fileName) {
        this.context = context;
        this.directoryName = direcotryName;
        this.fileName = fileName;
    }

    public void setCallback(ResultListener callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        boolean isDone = false;

        for (Bitmap bitmap : params) {
            File directory = new File(context.getFilesDir(), directoryName);

            if (!directory.exists())
                directory.mkdirs();

            File file = new File(directory, fileName);
            if (file.exists())
                file.delete();

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream io = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, io);
                io.close();
                Log.d("TEST", "Done");
                isDone = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TEST", "Error: " + e.getMessage());
            }
        }

        return isDone;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        callback.onExecuted(aBoolean);
    }

    public interface ResultListener{
        void onExecuted(boolean isDone);
    }
}
