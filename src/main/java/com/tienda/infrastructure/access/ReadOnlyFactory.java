package com.tienda.infrastructure.access;
import javax.sql.DataSource;
public final class ReadOnlyFactory extends DatabaseAccessFactory {
 public ReadOnlyFactory(Credentials credentials) {super(credentials);}
 @Override public DataSource createDataSource() {return build();}
 public boolean canRead() {return true;}
 public boolean canWrite() {return false;}
}
