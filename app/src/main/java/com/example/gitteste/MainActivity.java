package com.example.gitteste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private String startData;


    //metodo acionado assim que atividade é criada
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    //metodo para quando a atividade insere o estado "Iniciado"
    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendarStart = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        startData= simpleDateFormat.format(calendarStart.getTime());
        System.out.printf("\nstart = %s", startData);

        //coletar lista de apps e salvar em arquivo txt no armazenamento interno:
        apps();
    }

    //metodo chamado antes da atividade ser destruída"
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //coletar lista de aplicativos e salvar no armazenamento interno
    private void apps(){
        //coletar lista de aplicativos:
        List<ApplicationInfo> applicationInfoList = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        String[] stringsArray = new String[applicationInfoList.size()];
        int i = 0;
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            stringsArray[i] = applicationInfo.packageName;
            i++;
        }

        //salvar lista no armazenamento interno:
        String data = startData.substring(0,10);
        String FILE_NAME = (data+".cvs");
        FileOutputStream fos = null;
        //checar se já existe arquivo com data do dia:
        boolean exists = (new File(getFilesDir() + "/" + FILE_NAME)).exists();
        if(!exists){//se ainda não existir:
            try {
                fos = openFileOutput(FILE_NAME, MODE_APPEND);
                for (i = 0; i < applicationInfoList.size(); i++) {
                    fos.write((stringsArray[i] + "\n").getBytes());
                }
                //mostrar caminho para arquivo onde foi salvo:
                System.out.print("\nSaved to " + getFilesDir() + "/" + FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //metodo para quando clicar no botal "FAZER LOGIN"
    public void telaPrevisao(View view) {
        Intent intent = new Intent(getApplicationContext(), Previsao.class);
        startActivity(intent);
    }

    //metodo para quando clicar no botal "ANALISAR DADOS COLETADOS"
    public void telaDados(View view) {
        Intent intent = new Intent(getApplicationContext(), DataAnalysis.class);
        startActivity(intent);
    }
}
