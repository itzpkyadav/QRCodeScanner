package com.pramod.qrcodescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private Button btn_generate_qr, btn_generate_bar, btn_scan_qr;
    private EditText editText;
    private String EditTextValue;
    public final static int QRCodeWidth = 350;
    private Bitmap bitmap;
    private TextView tv_qr_readTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        btn_generate_qr = findViewById(R.id.btn_generate_qr);
        btn_generate_bar = findViewById(R.id.btn_generate_bar);
        btn_scan_qr = findViewById(R.id.btn_scan_qr);
        tv_qr_readTxt = findViewById(R.id.tv_qr_readTxt);

        btn_generate_qr.setOnClickListener(this);
        btn_generate_bar.setOnClickListener(this);
        btn_scan_qr.setOnClickListener(this);
    }

    Bitmap TextToImageEncode(String Value, int method) throws WriterException {
        BitMatrix bitMatrix = null;
        try {
            if (method == 1) {
                bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeWidth, null);
            } else if (method == 2) {
                bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.CODABAR, QRCodeWidth, QRCodeWidth, null);
            }
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");
            } else {
                Log.e("Scan", "Scanned");
                tv_qr_readTxt.setText(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_generate_qr:
                generateCode(1);
                break;
            case R.id.btn_generate_bar:
                generateCode(2);
                break;
            case R.id.btn_scan_qr:
                scanCode();
                break;
        }
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void generateCode(int method) {
        if (!editText.getText().toString().isEmpty()) {
            EditTextValue = editText.getText().toString();
            try {
                bitmap = TextToImageEncode(EditTextValue, method);
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            editText.setError("Enter Scanned Text or Number");
            editText.requestFocus();
        }
    }
}
