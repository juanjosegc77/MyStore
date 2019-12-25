package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
	
	// VERSIÓN DE LA BASE DE DATOS
	private static final int DATABASE_VERSION = 1;

	// NOMBRE DE LA BASE DE DATOS
	private static final String DATABASE_NAME = "store";

	
	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// CREAR TABLA productos
	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE productos ("
				+ "Id INTEGER PRIMARY KEY, "
				+ "Nombre TEXT, "
				+ "Codigo TEXT, "
				+ "Imagen BLOB, "
				+ "Cantidad INTEGER"
				+ ")";
		db.execSQL(query);
	}

	// ACTUALIZAR LA TABLA DE DATOS
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS productos");
		onCreate(db);
	}

	// AGREGAR UN NUEVO PRODUCTO
	public void agregarProducto(Producto producto) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Nombre", producto._nombre);
		values.put("Codigo", producto._codigo);
		values.put("Imagen", producto._imagen);
		values.put("Cantidad", producto._cantidad);

		db.insert("productos", null, values);
		db.close();
	}

	// OBTENER PRODUCTOS DADA UNA CONDICIÓN
	public List<Producto> getProductos(String query) {
		List<Producto> productoList = new ArrayList<Producto>();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Producto producto = new Producto();
				producto.setId(Integer.parseInt(cursor.getString(0)));
				producto.setNombre(cursor.getString(1));
				producto.setCodigo(cursor.getString(2));
				producto.setImagen(cursor.getBlob(3));
				producto.setCantidad(cursor.getInt(4));

				productoList.add(producto);
			} while (cursor.moveToNext());
		}
		return productoList;
	}

	public String getByCodigo(String query) {
		String nombre = "";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			nombre = cursor.getString(0);
		}
		return nombre;
	}

	// ACTUALIZAR UN PRODUCTO
	public int actualizarProducto(Producto producto) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Nombre", producto.getNombre());
		values.put("Codigo", producto.getCodigo());
		values.put("Imagen", producto.getImagen());
		values.put("Cantidad", producto.getCantidad());

		return db.update("productos", values, "Id" + " = ?",
				new String[] { String.valueOf(producto.getId()) });

	}

	// ELIMINAR UN PRODUCTO
	public int eliminarProducto(Producto producto) {
		SQLiteDatabase db = this.getWritableDatabase();

		return db.delete("productos", "Id" + " = ?",
				new String[]{String.valueOf(producto.getId())});
	}

	// OBTENER EL TOTAL DE PRODUCTOS ALMACENADOS EN LA BASE DE DATOS
	public int getProductosCount() {
		String countQuery = "SELECT  * FROM " + "productos";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	// ACTUALIZAR CANTIDADES
	public int actualizarCantidades(Producto producto) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Cantidad", producto.getCantidad());

		return db.update("productos", values, "Id" + " = ?",
				new String[]{String.valueOf(producto.getId())});

	}
}
