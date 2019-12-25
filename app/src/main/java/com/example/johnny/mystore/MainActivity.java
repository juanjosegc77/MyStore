package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import zxing.IntentIntegrator;
import zxing.IntentResult;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAdministrar, buttonEntradas, buttonSalidas;

    DataBaseHandler db;

    private static final int SCANNER_REQUEST = 2;

    int opcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonEntradas = (Button) findViewById(R.id.buttonEntradas);
        buttonSalidas = (Button) findViewById(R.id.buttonSalidas);
        buttonAdministrar = (Button) findViewById(R.id.buttonAdministrar);

        db = new DataBaseHandler(this);

        buttonEntradas.setOnClickListener(this);
        buttonSalidas.setOnClickListener(this);
        buttonAdministrar.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonEntradas:
                opcion = 1;
                escanearCodigo();
                break;

            case R.id.buttonSalidas:
                opcion = 2;
                escanearCodigo();
                break;

            case R.id.buttonAdministrar:
                Intent i = new Intent(this, Administrar.class);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANNER_REQUEST:
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();

                    String query = "SELECT * FROM productos WHERE Codigo = '" + scanContent + "'";
                    List<Producto> productos = db.getProductos(query);
                    if(!productos.isEmpty()){

                        int id = productos.get(0).getId();
                        String codigo = productos.get(0).getCodigo();
                        int cantidad = productos.get(0).getCantidad();

                        if (codigo.equals(scanContent)) {
                            int mod;
                            switch (opcion) {

                                case 1:
                                    cantidad = cantidad + 1;
                                    mod = db.actualizarCantidades(new Producto(id, cantidad));

                                    if (mod == 1)
                                        Toast.makeText(this, "Entrada OK", Toast.LENGTH_SHORT)
                                                .show();
                                    else
                                        Toast.makeText(this, "Error en entradas",
                                                Toast.LENGTH_SHORT).show();
                                    break;

                                case 2:
                                    if (cantidad > 0) {
                                        cantidad = cantidad - 1;
                                        mod = db.actualizarCantidades(new Producto(id, cantidad));

                                        if (mod == 1)
                                            Toast.makeText(this, "Salida OK ", Toast.LENGTH_SHORT)
                                                    .show();
                                        else
                                            Toast.makeText(this, "Error en salidas",
                                                    Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(this, "No hay productos en existencia",
                                                Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                        Toast.makeText(this, "No existe el producto",
                                Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No se recibieron datos", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    private void escanearCodigo(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

}
