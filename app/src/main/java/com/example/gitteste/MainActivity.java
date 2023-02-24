package com.example.gitteste;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private String dataAtual;
    private String dataColetaAnterior;
    private int nUsers = 1;
    private int nTimesStamps = 1000;
    private int nDim = 200;
    private float inputTensor[][] = new float[nTimesStamps][nDim];
    private int TimeStamp = 0; //ir incrementando a medida que novos eventos forem marcados

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
        //coletar data da sessão
        Calendar calendarStart = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        dataAtual = simpleDateFormat.format(calendarStart.getTime());
        dataAtual = dataAtual.substring(0,10);
        System.out.printf("\nstart = %s", dataAtual);

        //coletar lista de apps e salvar em arquivo txt no armazenamento interno se for a primeira coleta do dia:
        String nomeArquivo = (dataAtual.substring(0,10))+".csv";
        boolean jaColetetouNoDia =  (new File(getFilesDir() + "/" + nomeArquivo)).exists();

        if (!jaColetetouNoDia){ //se é a primeira coleta do dia
            coletarListaAppsESalvar(); //coleta e salva lista de aplicativos

            //conferir se coletou uma antes:
            boolean existeArquivoUltimaColeta = (new File(getFilesDir() + "/ultimaColeta.csv" )).exists();
            if(!existeArquivoUltimaColeta){
                System.out.print("\nPrimeira vez que coleta lista de app => criar Tensor marcando instalações");
                salvarDataColeta(); //salva a data da coleta
                //comparar aplicativos da lista com aplicativos usados no treinamento
                //um evento de instalação para cada  aplicativo
            }
            else{ //já coletou lista de app em pelo menos um dia anterior => comparar
                //ler data da ultima coleta
                lerDataUltimaColeta();
                System.out.print("\nUltima coleta = "+ dataColetaAnterior);
                // comparar com a última
                //compararListas(dataAtual, dataColetaAnterior);
                //atualizar data coleta
                salvarDataColeta();
                System.out.print("\nColeta de hoje CHECK! Ultima coleta ("+ dataColetaAnterior +") == Data Hoje ("+dataAtual+")");
                salvarDatasColetas ();

                //comparar com todas as últimas se pedir para prever
                //=> salvar todas as datas de coletas
                //=> comparar primeira com segunda até penultima com útima
                //=> criar eventos para instalações e desistalações de apps (apenas dos usados no treinamento)
            }
        }
        else{
            // ja fez oq tinha que fazer
            lerDataUltimaColeta();
            System.out.print("\nJá coletou HOJE! Ultima coleta ("+ dataColetaAnterior +") == Data Hoje ("+dataAtual+")");
        }

        ArrayList<String> datasColetas = lerDatasColetas();
        System.out.print("\nDatas de Coletas: \n"+datasColetas);

        compararListas("24-02-2023", "23-02-2023");

        System.out.print("\n");
    }

    //metodo chamado antes da atividade ser destruída"
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //coletar lista de aplicativos e salvar no armazenamento interno
    private void coletarListaAppsESalvar(){
        System.out.print("\nColetou Lista Apps");
        //coletar lista de aplicativos:
        List<ApplicationInfo> applicationInfoList = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        String[] stringsArray = new String[applicationInfoList.size()];
        int i = 0;
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            stringsArray[i] = applicationInfo.packageName;
            i++;
        }

        //salvar lista no armazenamento interno:
        String FILE_NAME = (dataAtual+".csv");
        FileOutputStream fos = null;
        //checar se já existe arquivo com data do dia:
        //boolean exists = (new File(getFilesDir() + "/" + FILE_NAME)).exists();
        //if(!exists){//se ainda não existir, ou seja, se existis = False então é a primeira coleta do dia,
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

    private void salvarDataColeta (){
        String FILE_NAME = "ultimaColeta.csv";
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write((dataAtual).getBytes());
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

    private void lerDataUltimaColeta(){
        FileInputStream fis = null;
        try {
            fis = openFileInput("ultimaColeta.csv");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                dataColetaAnterior = br.readLine(); // lê a primeira linha = data da última coleta
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

    private void salvarDatasColetas (){
        String FILE_NAME = "datasColetas.csv";
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write((dataAtual + "\n").getBytes());

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

    public ArrayList<String> lerDatasColetas() {
        FileInputStream fis = null;
        ArrayList<String> dados = new ArrayList<>(); //= array com tudo lido do arquivo
        try {
            fis = openFileInput("datasColetas.csv");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String lido;

            while ((lido = br.readLine()) != null) {
                dados.add(lido);
            }
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
        return dados;
    }

    private void compararListas (String atual, String anterior){
        HashSet<String> listaAtual = lerListaApps(atual);
        HashSet<String> listaAnterior = lerListaApps(anterior);

        System.out.print("\nLista Atual: \n"+ listaAtual);
        System.out.print("\nLista Anterior: \n"+ listaAnterior);

        //comparar
        //para cada evento salvar: data, nome aplicativo, tipo em arquivo .csv
        //quando for prever: ler esse arquivo para criar tensor
    }

    public HashSet<String> lerListaApps(String data) {
        FileInputStream fis = null;
        ArrayList<String> dados = new ArrayList<>();
        HashSet<String> listaApps = new HashSet<>();
        try {
            fis = openFileInput(data+".csv");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String lido;

            while ((lido = br.readLine()) != null) {
                dados.add(lido);
                listaApps.add(lido);
            }
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

        return listaApps;
    }


    private void createTensor(){
        int nUsers = 1;
        int nTimesStamps = 1000;
        int nDim = 200;

        /*
        try{
            FloatBuffer fb = FloatBuffer.allocate(784); //nao sei se teria que ser 784

            float[][][] aux;
            Tensor<Float> inputTensor = Tensor.create(aux);
        }

         */




        //tensor = [USER = tam 1, TIMESTAMP = tam a definir, APPS = tam +- 200 ] - definir tamanhos como variáveis
        //tensor = [FLOAT, FLOAT, ARRAY DE ZEROS E UNS ]
        //Tensor inputTensor = Tensor.create(new long[] {1,1024}, FloatBuffer.wrap());

        /*
         * coletar lista apps
           - se for a primeira: crear tensor e marcar apps como instalados

         * comparar com a última lista coletada usando hashset
           - nova - ultimaColetada => [instalados]
                                      - se não estiver vazio => novo evento
                                        - marcar timestamp no inidice 2
                                        - marcar aplicativos
                                        ( um pra cada app instalado)

           - ultimaColetada - nova => [desistalados]
        */
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
