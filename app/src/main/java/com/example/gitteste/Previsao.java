package com.example.gitteste;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Previsao extends AppCompatActivity {

    EditText editText;
    TextView textView;
    Button button;
    Interpreter interpreter;
    String entrada;
    int i = 0;

    private int nUsers = 1;
    private int nTimesStamps = 1000;
    private int nDim = 200;
    private float inputTensor[][] = new float[nTimesStamps][nDim];
    private int TimeStamp = 0; //ir incrementando a medida que novos eventos forem marcados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previsao);

        //setTitle("Predição da Função y = 5x-2");
        editText = findViewById(R.id.inputX);
        button = findViewById(R.id.predictbtn);
        textView = findViewById(R.id.outputY);

        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrada = editText.getText().toString();

                float f =  doInference(entrada);
                textView.setText("Resultado: "+f);
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("linear.tflite");
        FileInputStream fileInputStream = new FileInputStream(((AssetFileDescriptor) assetFileDescriptor).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);
    }

    public float doInference(String val)
    {
        float[] input = new float[1];
        input[0] = Float.parseFloat(val);
        float[][] output = new float[1][1];

        interpreter.run(input,output);
        return  output[0][0];
    }
}

