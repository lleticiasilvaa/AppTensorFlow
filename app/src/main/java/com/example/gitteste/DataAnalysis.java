package com.example.gitteste;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DataAnalysis extends AppCompatActivity {

    private ListView listView3;
    private TextView fileName;
    private String startData;

    //metodo acionado assim que atividade é criada
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);
        listView3 = findViewById(R.id.listView3);
        fileName = findViewById(R.id.fileName);
    }
    //metodo para quando a atividade insere o estado "Iniciado"
    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendarStart = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        startData= simpleDateFormat.format(calendarStart.getTime());

        //ler informações nos arquivos .cvs
        readApps();
    }

    public void readApps() {
        String data = startData.substring(0,10);
        FileInputStream fis = null;
        ArrayList<String> dados = new ArrayList<>();
        try {
            fis = openFileInput(data+".cvs");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String lido;

            fileName.setText(data+".cvs");

            while ((lido = br.readLine()) != null) {
                sb.append(lido).append("\n");
                dados.add(lido);
            }
            //System.out.print("\nLido em:"+data+".txt\n"+sb);
            listView3.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,  dados));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}