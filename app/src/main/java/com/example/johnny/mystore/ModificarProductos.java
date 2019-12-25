package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import zxing.IntentIntegrator;
import zxing.IntentResult;

public class ModificarProductos extends Activity implements OnClickListener{

    private EditText editTextModNombre;
    private TextView textViewModId, textViewModCodigo;
    private ImageView imageViewModImagen;
    private NumberPicker numberPickerCantidad;

    private Button buttonModImagen, buttonModCodigo;
    private Button buttonActualizar, buttonEliminar;

    DataBaseHandler db;
    private static final int CAMERA_REQUEST = 1;
    private static final int SCANNER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Modificar productos");

        setContentView(R.layout.modificar_productos);

        textViewModId = (TextView) findViewById(R.id.textViewModId);
        editTextModNombre = (EditText) findViewById(R.id.editTextModNombre);
        textViewModCodigo = (TextView) findViewById(R.id.textViewModCodigo);
        imageViewModImagen = (ImageView) findViewById(R.id.imageViewModImagen);
        numberPickerCantidad = (NumberPicker) findViewById(R.id.numberPickerCantidad);

        buttonModImagen = (Button) findViewById(R.id.buttonModImagen);
        buttonModCodigo = (Button) findViewById(R.id.buttonModCodigo);
        buttonActualizar = (Button) findViewById(R.id.buttonActualizar);
        buttonEliminar = (Button) findViewById(R.id.buttonEliminar);

        db = new DataBaseHandler(this);

        // OBTENER LOS VALORES DE LA CLASE Administrar EXTRAÍDOS DEL ProductoAdapter
        Intent intent = getIntent();

        String id = intent.getStringExtra("Id");
        String nombre = intent.getStringExtra("Nombre");
        String codigo = intent.getStringExtra("Codigo");
        Bitmap imagen = intent.getParcelableExtra("Imagen");
        String c = intent.getStringExtra("Cantidad");

        // COLOCAR LOS VALORES OBTENIDOS
        textViewModId.setText(id);
        editTextModNombre.setText(nombre);
        textViewModCodigo.setText(codigo);
        imageViewModImagen.setImageBitmap(imagen);

        numberPickerCantidad.setMinValue(0);
        numberPickerCantidad.setMaxValue(100);
        numberPickerCantidad.setWrapSelectorWheel(true);
        numberPickerCantidad.setValue(Integer.parseInt(c));

        // ESCUCHAS PARA LOS BOTONES
        buttonModImagen.setOnClickListener(this);
        buttonModCodigo.setOnClickListener(this);
        buttonActualizar.setOnClickListener(this);
        buttonEliminar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonModImagen:
                tomarFoto();
                break;

            case R.id.buttonModCodigo:
                escanearCodigo();
                break;

            case R.id.buttonActualizar:
                actualizar();
                break;

            case R.id.buttonEliminar:
                eliminar();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case CAMERA_REQUEST:

                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap takenImage = extras.getParcelable("data");

                    int rotate = 0;
                    try {

                        ExifInterface exif = new ExifInterface(data.getData().getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    takenImage = Bitmap.createBitmap(takenImage , 0, 0, takenImage.getWidth(), takenImage.getHeight(), matrix, true);

                    if (takenImage.getWidth() > takenImage.getHeight()) {
                        Matrix matrix2 = new Matrix();
                        matrix2.postRotate(90);
                        takenImage = Bitmap.createBitmap(takenImage , 0, 0, takenImage.getWidth(), takenImage.getHeight(), matrix2, true);
                    }
                    imageViewModImagen.setImageBitmap(takenImage);
                }
                break;

            case SCANNER_REQUEST:
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    textViewModCodigo.setText(scanContent);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No se recibieron datos", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    public void tomarFoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void escanearCodigo(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void actualizar() {

        int id = Integer.parseInt(textViewModId.getText().toString());
        String nombre = editTextModNombre.getText().toString();
        String codigo = textViewModCodigo.getText().toString();

        Bitmap bmp = ((BitmapDrawable)imageViewModImagen.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imagen = stream.toByteArray();

        int cantidad = numberPickerCantidad.getValue();

        int mod = db.actualizarProducto(new Producto(id, nombre, codigo, imagen, cantidad));

        if (mod == 1) {
            Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT)
                    .show();
            this.returnHome();
        }
        else
            Toast.makeText(this, "Error al actalizar",
                    Toast.LENGTH_SHORT).show();
    }

    public void eliminar(){
        int id = Integer.parseInt(textViewModId.getText().toString());

        int e = db.eliminarProducto(new Producto(id));

        if (e == 1) {
            Toast.makeText(this, "Se eliminó el registro", Toast.LENGTH_SHORT)
                    .show();
            this.returnHome();
        }
        else
            Toast.makeText(this, "Error al eliminar",
                    Toast.LENGTH_SHORT).show();
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), Administrar.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
}
