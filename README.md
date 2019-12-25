# MyStore
Proyecto de control de almacen o alacena.

## Descripción

Proyecto android para administrar los artículos de un pequeño almacén o alacena, donde se utilizan los códigos de barras para incrementar o decrementar los artículos en existencia.
Se utiliza el paquete "zxing" y se debe instalar la aplicación "Barcode Scanner" para hacer uso de la lectura por códigos de barras.
La administración de la base de datos de lleva a cabo con SQLite.

Cuenta con tres opciones: Entradas, Salidas y Administrar.

* Entradas - Activa la cámara y el lector de códigos para incrementar en uno el número de elementos disponibles del artículo registrado.
* Salidas - Activa la cámara y el lector de códigos para decrementar en uno el número de elementos disponibles del artículo registrado.
* Administrar - Permite agregar, modificar y eliminar registros.
