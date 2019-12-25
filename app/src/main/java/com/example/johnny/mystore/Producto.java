package com.example.johnny.mystore;
/**
 * Created by Juan José Guzmán Cruz on 5/07/15.
 */

public class Producto {


	int _id;
	String _nombre;
	String _codigo;
	byte[] _imagen;
	int _cantidad;

	// CONSTRUCTOR VACÍO
	public Producto() {

	}

	// CONSTRUCTOR COMPLETO, CONSTRUYE TODOS LOS CAMPOS
	public Producto(int keyId, String nombre, String codigo, byte[] imagen, int cantidad) {
		this._id = keyId;
		this._nombre = nombre;
		this._codigo = codigo;
		this._imagen = imagen;
		this._cantidad = cantidad;
	}

	/*
	CONTRUCTOR PARCIAL. SOLO CONSTRUYE nombre, codigo E imagen
	SE UTILIZA EN agregarProducto() DE LA CLASE Producto PARA EL INGRESO DE UN
	NUEVO PRODUCTO A LA BASE DE DATOS
	 */
	public Producto(String nombre, String codigo, byte[] imagen, int cantidad) {
		this._nombre = nombre;
		this._codigo = codigo;
		this._imagen = imagen;
		this._cantidad = cantidad;
	}

	// CONSTRUCTOR PARA SUMAR Y RESTAR PRODUCTOS
	public Producto(int keyId, int cantidad) {
		this._id = keyId;
		this._cantidad = cantidad;
	}

	/*
	CONTRUCTOR PARCIAL. SOLO CONSTRUYE id
	SE UTILIZA EN eliminarProducto DE LA CLASE Producto
	 */
	public Producto(int keyId) {
		this._id = keyId;
	}

	public int getId() {
		return this._id;
	}

	public void setId(int keyId) {
		this._id = keyId;
	}

	public String getNombre() {
		return this._nombre;
	}

	public void setNombre(String nombre) {
		this._nombre = nombre;
	}

	public String getCodigo() {
		return this._codigo;
	}

	public void setCodigo(String codigo) {
		this._codigo = codigo;
	}

	public byte[] getImagen() {
		return this._imagen;
	}

	public void setImagen(byte[] imagen) {
		this._imagen = imagen;
	}

	public int getCantidad() {
		return this._cantidad;
	}

	public void setCantidad(int cantidad) {
		this._cantidad = cantidad;
	}
}
