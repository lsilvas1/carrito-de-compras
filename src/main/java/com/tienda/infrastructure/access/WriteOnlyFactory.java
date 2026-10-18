package com.tienda.infrastructure.access;
import javax.sql.DataSource;
public final class WriteOnlyFactory extends DatabaseAccessFactory {
 public WriteOnlyFactory(Credentials credentials) {super(credentials);}
 @Override public DataSource createDataSource() {return build();}
 public boolean canRead() {return false;}
 public boolean canWrite() {return true;}
}
