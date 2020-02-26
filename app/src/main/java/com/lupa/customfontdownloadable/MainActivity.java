package com.lupa.customfontdownloadable;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;

//import android.provider.FontRequest;                  //Bez podpory API nižší než 26
//import android.provider.FontsContract;                //Bez podpory API nižší než 26

import android.support.v4.provider.FontRequest;         //Pro podporu API i nižší než 26
import android.support.v4.provider.FontsContractCompat; //Pro podporu API i nižší než 26

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_UNAVAILABLE;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_MALFORMED_QUERY;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_PROVIDER_NOT_FOUND;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_WRONG_CERTIFICATES;


public class MainActivity extends AppCompatActivity {

    TextView label_04;
    Button btn_01;
    Spinner spinner;

    Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        label_04 = findViewById(R.id.label_04);
        btn_01 = findViewById(R.id.btn_01);
        spinner = findViewById(R.id.spinner);

        ArrayList<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.fonts_array)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                setMyFont((String) adapterView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Tato verze metody je i pro podpuru API nižší než 26, protože je použita knihovna Support
    //Library. Pokud je tato knihovna použita, musí být VŽDY v parametru requestu certifikáty. A to
    //i v případě, že používáme předinstalované poskytovatele fontů.
    private void setMyFont(String fontName) {
        FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                fontName,
                R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                label_04.setTypeface(typeface);

                Toast.makeText(MainActivity.this,
                        "Nastavení fontu OK", Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                Toast.makeText(
                        MainActivity.this,
                        requestErrorToString(reason) + reason,
                        Toast.LENGTH_LONG).show();
            }
        };

        FontsContractCompat.requestFont(MainActivity.this, request, callback, getHandlerThreadHandler());

    }

    /*
    //Tato verze metody by byla použita pouze v případě, že by minimální verze API byla nastavena
    //na 26 (Android Oreo 8.0). Pokud je minimální API 26, není k nastavení fontu potřeba knihovna
    //Support Library. Aplikace ale nebude kompatibilní se staršími verzemi Androidu.
    private void setMyFont() {

        //V tomto případě (viz. komentář nad touto metodou) nemusíme do parametrů requestu přidávat
        //žádné certifikáty - přidáme prázdný ArrayList. Ale pozor, pokud bychom získávali fonty
        //od jiného poskytovatele, než od toho, který je předinstalovaný v Android Studiu (aktuálně
        //Google), musíme certifikáty do parametru requestu přidat. Certifikáty by měl poskytnout
        //poskytovatel fontů.
        FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Almendra",
                new ArrayList<List<byte[]>>());

        FontsContract.FontRequestCallback callback = new FontsContract.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                label_04.setTypeface(typeface);

                Toast.makeText(MainActivity.this,
                        "Nastavení fontu OK", Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                Toast.makeText(MainActivity.this,
                        requestErrorToString(reason) + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };

        FontsContract.requestFonts(MainActivity.this, request, getHandlerThreadHandler(), null, callback);

    }
    */

    private Handler getHandlerThreadHandler() {
        if (myHandler == null) {
            HandlerThread handlerThread = new HandlerThread("myFonts");
            handlerThread.start();
            myHandler = new Handler(handlerThread.getLooper());
        }

        return myHandler;
    }

    private String requestErrorToString(int errCode) {
        switch (errCode) {
            case FAIL_REASON_PROVIDER_NOT_FOUND:
                return "FAIL_REASON_PROVIDER_NOT_FOUND - daný poskytovatel nebyl v zařízení nalezen.";
            case FAIL_REASON_WRONG_CERTIFICATES:
                return "FAIL_REASON_WRONG_CERTIFICATES - daný poskytovatel musí být ověřen a dané certifikáty neodpovídají jeho podpisu..";
            case FAIL_REASON_FONT_LOAD_ERROR:
                return "FAIL_REASON_FONT_LOAD_ERROR - vrácený font nebyl správně načten.";
            case FAIL_REASON_SECURITY_VIOLATION:
                return "FAIL_REASON_SECURITY_VIOLATION - písmo nebylo načteno kvůli problémům se zabezpečením.";
            case FAIL_REASON_FONT_NOT_FOUND:
                return "FAIL_REASON_FONT_NOT_FOUND - Poskytovatel fontu nevrátil pro daný dotaz žádné výsledky.";
            case FAIL_REASON_FONT_UNAVAILABLE:
                return "FAIL_REASON_FONT_UNAVAILABLE - poskytovatel písma našel dotazovaný font, ale momentálně není k dispozici.";
            case FAIL_REASON_MALFORMED_QUERY:
                return "FAIL_REASON_MALFORMED_QUERY - daný dotaz není poskytovatelem podporován.";
            default:
                return "Neznámá chyba při získávání fontu.";
        }
    }
}
