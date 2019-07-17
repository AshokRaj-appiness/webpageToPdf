package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private TextView textView;
    private int PERMISSION_REQUEST = 0;
    private boolean allowSave = true;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        webView.loadUrl("https://pspdfkit.com/blog/2019/how-to-convert-html-to-pdf-with-swift/");
        webView.setWebViewClient(new WebViewClient());

        textView = findViewById(R.id.textView);


        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                Toast.makeText(MainActivity.this, "Web page loaded..start downloading..", Toast.LENGTH_SHORT).show();
                savePdf();
            }
        });


        savePdf();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void savePdf() {
        if(!allowSave)
            return;
        allowSave = false;
        textView.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PERMISSION_GRANTED) {
            String fileName = String.format("%s.pdf", new SimpleDateFormat("dd_mm_yyyy").format(new Date()));
            final PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);
            PrintAttributes printAttributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            final File file = Environment.getExternalStorageDirectory();
            new PdfPrint(printAttributes).print(
                    printAdapter,
                    file,
                    fileName,
                    new PdfPrint.CallbackPrint() {
                        @Override
                        public void onSuccess(String path) {
                            textView.setVisibility(View.GONE);
                            allowSave = true;
                            Toast.makeText(getApplicationContext(),
                                    String.format("Your file is saved in %s", path),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            textView.setVisibility(View.GONE);
                            allowSave = true;
                            Toast.makeText(getApplicationContext(),
                                    String.format("Exception while saving the file and the exception is %s", ex.getMessage()),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[Arrays.asList(permissions).indexOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)] == PERMISSION_GRANTED) {
                savePdf();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}
