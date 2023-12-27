package brs.couchbasedb;



import brs.Account;
import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;
import org.jooq.Condition;


import java.util.Collection;

public interface PublicEntity {


  <T> void insert(T t)  ;

  <T> boolean delete(T t) throws NoSuchFieldException, IllegalAccessException;

  <T> T get(long id);


//  Account get(long id);

  <T> T get(long id, int height);

  int getCount();

  long getAllAccountsBalance();

  <T> Collection<T> getManyBy(String name, int from, int to);


//  int getCount();
//
//  int getRowCount();
//
//  void insert(T t);


//  void rollback(int height);
//  void truncate();

}
