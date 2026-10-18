CREATE TABLE producto(idProducto INT AUTO_INCREMENT PRIMARY KEY,nombre VARCHAR(255),codigo VARCHAR(255) NOT NULL,imagen TEXT,precio DOUBLE PRECISION);
CREATE TABLE shopping_cart(idShoppingCart INT AUTO_INCREMENT PRIMARY KEY,idProducto INT,codUsuario VARCHAR(128),cantidad INT,FOREIGN KEY(idProducto) REFERENCES producto(idProducto));
