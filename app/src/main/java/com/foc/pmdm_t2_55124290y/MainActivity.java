package com.foc.pmdm_t2_55124290y;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtNombre;
    EditText etMensaje, etNumero;
    WebView webView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = findViewById(R.id.txtNombre);
        etMensaje = findViewById(R.id.etMensaje);
        etNumero = findViewById(R.id.etNumero);
        webView1 = findViewById(R.id.webView1);

        //Apertura de SharedPreferences y Editor
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        //Se almacena el nombre del alumno en SharedPreferences
        // y se guardan los cambios.
        editor.putString("nombre_alumno", "Nicolás Esteban Borquez");
        editor.commit();


        //Se lee el nombre almacenado en SharedPreferences
        String nombreAlumno = sp.getString("nombre_alumno", null);
        //Se carga el nombre obtenido en el TextField
        txtNombre.setText(nombreAlumno);

        //WebView:
        //Se carga el nuevo cliente, para evitar que la web se cargue
        // en el navegador por defecto del dispositivo
        webView1.setWebViewClient(new WebViewClient());
        //Finalmente se carga la URL, en este caso la de FOC:
        webView1.loadUrl("https://www.foc.es");
    }

    @Override
    public void onClick(View v) {
        //Si se pulsa en botón "Enviar SMS"
        if (v.getId() == R.id.btnEnviarSms) {
            //Si los campos "numero" y "mensaje" han sido rellenados, se envía el mensaje
            if (!etMensaje.getText().toString().isEmpty() && !etNumero.getText().toString().isEmpty()) {
                String mensaje = etMensaje.getText().toString();
                String numero = etNumero.getText().toString();
                sendSMSbyCode(numero, mensaje);
                Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_LONG).show();
            } else {
                //Si falta algún campo por rellenar se informa al usuario
                Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
            }
        }
        //Si se pulsa el botón "Conexión"
        if (v.getId() == R.id.btnConexion) {
            this.comprobarConexion(this);
        }
    }


    //Envío de SMS usando SmsManager
    public void sendSMSbyCode(String number, String text) {

        //Se comprueba que se tienen los permisos necesarios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager sms = SmsManager.getDefault();
            //Chequeo de que el largo del mensaje sea mayor a 140 caracteres
            // en caso afirmativo se divide en fargmentos de 140.
            if (text.length() > 140) {
                ArrayList<String> lista = sms.divideMessage(text);
                for (String fragmento : lista) {
                    //Envío de los sms
                    sms.sendTextMessage(number, null, fragmento, null, null);
                }
            } else {
                //Envío del sms
                sms.sendTextMessage(number, null, text, null, null);
            }
        } else {
            //En caso de no tener los permisos se hace una petición (***)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 999);
        }
    }

    // (***) En este método se obtiene la respuesta de la petición de permisos:
    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);

        switch (requestCode) {
            //Comprobamos la respuesta sobre el requestCode 999 (permiso para enviar SMS)
            case 999:
                if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    //En caso de tener los permisos, enviamos el SMS
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show();

                    String numero = etNumero.getText().toString();
                    String mensaje = etMensaje.getText().toString();
                    sendSMSbyCode(numero, mensaje);
                } else {
                    //En caso de NO tener permiso informamos al usuario
                    Toast.makeText(this, "No dispone de los permisos necesarios", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    //En este método se realizar la comprobación del estado de la conectividad usando ConnectivityManager
    public void comprobarConexion(Context context) {

        //Se capta el gestor de conectividad
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            //Se obtiene la red activa del dispositivo
            Network red = cm.getActiveNetwork();

            //En caso se obtener resultado, hay conexion a internet
            if (red != null) {
                Toast.makeText(context, "Hay conexión a internet", Toast.LENGTH_LONG).show();
            }
            // En caso contrario, no hay conexión a internet
              else {
                Toast.makeText(context, "NO hay conexión a internet", Toast.LENGTH_LONG).show();
            }
        }
    }
}