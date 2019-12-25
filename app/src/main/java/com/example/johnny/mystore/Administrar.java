package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import zxing.IntentIntegrator;
import zxing.IntentResult;

public class Administrar extends AppCompatActivity {

	// DEFINICIÓN DE VARIABLES
	EditText editTextFiltrar;
	ListView listViewProductos;

	DataBaseHandler db;
	ArrayList<Producto> arrayProductos = new ArrayList<Producto>();
	ProductoAdapter adapter;

	private static final int CAMERA_REQUEST = 1;
	private static final int SCANNER_REQUEST = 2;

	String nombre, codigo;
	byte imageInByte[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.administrar);

		// INICIALIZACIÓN VARIABLES
		editTextFiltrar = (EditText) findViewById(R.id.editTextFiltrar);
		editTextFiltrar.addTextChangedListener(filterTextWatcher);
		listViewProductos = (ListView) findViewById(R.id.listViewProductos);
		db = new DataBaseHandler(this);

		mostrarTodo();

		/* SE OBTIENEN LOS DATOS DEL ITEM SELECCIONADO EN EL listViewProductos Y SE PASAN
		 * A LA CLASE ModificarProductos. ESTA ÚLTIMA ES ACTIVADA.
		 */
		listViewProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
				TextView textViewId = (TextView) view.findViewById(R.id.textViewId);
				TextView textViewNombre = (TextView) view.findViewById(R.id.textViewNombre);
				TextView textViewCodigo = (TextView) view.findViewById(R.id.textViewCodigo);
				ImageView imageViewImagen = (ImageView) view.findViewById(R.id.imageViewImagen);
				TextView textViewCantidad = (TextView) view.findViewById(R.id.textViewCantidad);

				String id = textViewId.getText().toString();
				String nombre = textViewNombre.getText().toString();
				String codigo = textViewCodigo.getText().toString();
				Bitmap imagen = ((BitmapDrawable) imageViewImagen.getDrawable()).getBitmap();
				String cantidad = textViewCantidad.getText().toString();

				Intent modify_intent = new Intent(getApplicationContext(), ModificarProductos.class);
				modify_intent.putExtra("Id", id);
				modify_intent.putExtra("Nombre", nombre);
				modify_intent.putExtra("Codigo", codigo);
				modify_intent.putExtra("Imagen", imagen);
				modify_intent.putExtra("Cantidad", cantidad);

				startActivity(modify_intent);
			}
		});

	}

	/* RESPONDE A CAMBIOS EN EL editTextFiltrar
	 * CADA VEZ QUE SE ESCRIBE UN NUEVO CARACTER SE CONSULTA EN LA BASE DE DATOS Y SE MUESTRAN
	 * TODOS LOS REGISTROS QUE INICIAN CON DICHAS LETRAS
	 */
	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {

			adapter.clear();
			String query = "SELECT * FROM productos WHERE Nombre LIKE '" + s + "%' ORDER BY Nombre";
			List<Producto> productos = db.getProductos(query);
			for (Producto prod : productos) {
				arrayProductos.add(prod);
			}

			listViewProductos.setAdapter(listViewProductos.getAdapter());
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		editTextFiltrar.removeTextChangedListener(filterTextWatcher);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_administrar, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// SI SE HA ELEGIDO EL ICONO DE + EN EL OptionsMenu, SE LLAMA AL METODO nuevoProducto()
		int id = item.getItemId();
		if (id == R.id.agregar_nuevo)
			nuevoProducto();

		return super.onOptionsItemSelected(item);
	}

	// RESULTADO DE ACTIVIDAD POR ALGÚN Intent
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		// SI SE HA ACTIVADO LA CÁMARA A PARTIR DEL MÉTODO tomarFoto()
		case CAMERA_REQUEST:

				Bundle extras = data.getExtras();

				if (extras != null) {
					Bitmap takenImage = extras.getParcelable("data");

					// PARA CORREGIR ERRORES DE ROTACIÓN EN LA IMAGEN
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

					// CONVERTIR LA IMAGEN DE Bitmap a Byte
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					takenImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					imageInByte = stream.toByteArray();

					insertarProducto();
				}
			break;

			// SI SE HA ACTIVADO LA CÁMARA A PARTIR DEL MÉTODO escanearCodigo()
			case SCANNER_REQUEST:
				IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
				if (scanningResult != null) {
					String scanContent = scanningResult.getContents();

					String query = "SELECT Nombre FROM productos WHERE Codigo = '" + scanContent + "'";
					String nombre = db.getByCodigo(query);

					if(!nombre.equals("")) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"ERROR Código asignado al artículo: " + nombre, Toast.LENGTH_LONG);
						toast.show();
					} else{
						codigo = scanContent;
						adapter.notifyDataSetChanged();

						tomarFoto();
					}


				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"No se recibieron datos", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
		}
	}

	// OBTIENE TODOS LOS REGISTROS CONTENIDOS EN LA BASE DE DATOS Y LOS MUESTRA UTILIZANDO UN ADAPTADOR
	public void mostrarTodo(){
		//LEER LA BASE DE DATOS Y COLOCAR LOS REGISTROS EN UN ARREGLO
		String query = "SELECT * FROM productos ORDER BY Nombre";
		List<Producto> productos = db.getProductos(query);
		for (Producto prod : productos) {
			arrayProductos.add(prod);
		}

		/**
		 * MOSTRAR LOS DATOS EN EL ListView UTILIZANDO EL ADAPTADOR ProductoAdapter
		 * Y EL Layout administrar.xml
		 */
		adapter = new ProductoAdapter(this, R.layout.producto_adapter, arrayProductos);
		listViewProductos.setAdapter(adapter);
	}

	/* INGRESAR NUEVO PRODUCTO UTILIZANDO UN AlertDialog QUE SOLICITA EL NOMBRE DEL PRODUCTO
	 * SI EL DATO ES INGRESADO Y SE PRESIONA "OK", SE GUARDA EL DATO EN UNA VARIABLE Y
	 * SE LLAMA AL METODO escanearCodigo()
	*/
	public void nuevoProducto(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Producto nuevo");
		alert.setMessage("Escriba el nombre del producto");

		// COLOCAR UN EditText PARA ESCRIBIR EL NOMBRE DEL PRODUCTO
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (!value.equals("")) {
					nombre = value;

					escanearCodigo();

				} else {
					Toast.makeText(getApplicationContext(), "Escriba el nombre",
							Toast.LENGTH_SHORT).show();
					nuevoProducto();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		alert.show();
	}

	// A PARTIR DE UN Intent SE ACTIVA LA CÁMARA PARA TOMAR UNA FOTO
	public void tomarFoto() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	// UTILIZANDO zxing SE ACTIVA LA CÁMARA PARA ESCANEAR UN CODIGO
	public void escanearCodigo(){
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}

	// CREA UN NUEVO REGISTRO EN LA BASE DE DATOS
	public void insertarProducto(){
		db.agregarProducto(new Producto(nombre, codigo , imageInByte, 0));
		Intent i = new Intent(getApplicationContext(), Administrar.class);
		startActivity(i);
		finish();
	}
}