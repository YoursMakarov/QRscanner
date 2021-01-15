package ru.makscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //скан кода
    public IntentIntegrator integrator;
    //Кнопка
    Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //фрейм по умолчанию
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация айди кнопки
        scanBtn = findViewById(R.id.scanBtn);
        //Присвоение кнопке проверки для нажатия
        scanBtn.setOnClickListener(this);
    }

    //событие при нажатии кнопки
    @Override
    public void onClick(View v) {
        //процесс скана
        scanCode();
    }

    private void scanCode() {
        //Инициализация скана
        integrator = new IntentIntegrator(this);
        //Установка класса для активности
        integrator.setCaptureActivity(CaptureAct.class);
        //Установка направления
        integrator.setOrientationLocked(false);
        //инициализация для всех типов кода для сканера
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        //Надпись
        integrator.setPrompt("Сканирование кода");
        //Завершение сканирования = завершение и обработка
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int reqestCode, int resultCode, Intent data) {
        //Результат камеры
        IntentResult result = IntentIntegrator.parseActivityResult(reqestCode, resultCode, data);
        //Проврек на его существование
        if (result != null) {
            if (result.getContents() != null) {
                //Окно с сообщением
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //результат
                builder.setTitle("Результат");
                String resultat = result.getContents();

                //Проверка на валидность ссылки
                if (resultat.contains("http")) {
                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultat));
                    if (openUrlIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(openUrlIntent);
                    }
                }

                builder.setMessage(resultat);

                //кнопка повторного сканирования
                builder.setPositiveButton("Сканировать еще раз", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                    //кнопка завершения сканирования
                }).setNegativeButton("завершить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                //показ окон и их обработка при создании
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                //надпись о не найденном результате
                Toast.makeText(this, "Результат не найден", Toast.LENGTH_LONG).show();
            }
        } else {
            //Дефолтный результат - исключение
            super.onActivityResult(reqestCode, resultCode, data);
        }

    }
}