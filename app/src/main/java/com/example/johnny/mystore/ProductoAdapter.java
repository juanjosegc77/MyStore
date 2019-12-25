package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class ProductoAdapter extends ArrayAdapter<Producto>{
	Context context;
	int layoutResourceId;

	TextView textViewId, textViewNombre, textViewCodigo, textViewCantidad;
	ImageView imagenViewImagen;

	ArrayList<Producto> data=new ArrayList<Producto>();

	public ProductoAdapter(Context context, int layoutResourceId, ArrayList<Producto> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;


		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		textViewId = (TextView)row.findViewById(R.id.textViewId);
		textViewNombre = (TextView)row.findViewById(R.id.textViewNombre);
		textViewCodigo = (TextView)row.findViewById(R.id.textViewCodigo);
		imagenViewImagen = (ImageView)row.findViewById(R.id.imageViewImagen);
		textViewCantidad = (TextView)row.findViewById(R.id.textViewCantidad);

		Producto prod = data.get(position);
		String id = String.valueOf(prod._id);
		textViewId.setText(id);
		textViewNombre.setText(prod._nombre);
		textViewCodigo.setText(prod._codigo);

		byte[] outImage = prod._imagen;
		ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
		Bitmap theImage = BitmapFactory.decodeStream(imageStream);
		imagenViewImagen.setImageBitmap(theImage);

		String cantidad = String.valueOf(prod._cantidad);
		textViewCantidad.setText(cantidad);

		return row;
	}
}
