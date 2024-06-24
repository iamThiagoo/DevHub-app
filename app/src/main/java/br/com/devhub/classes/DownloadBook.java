package br.com.devhub.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadBook extends AsyncTask<String, Void, String> {
    private Context context;

    public DownloadBook(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String fileName = strings[1] + ".pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
            FileOutputStream outputStream = new FileOutputStream(filePath);

            byte[] data = new byte[1024];
            int count;
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return "Livro \"" + strings[1] + "\" instalado com sucesso!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Download do livro falhou!";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}