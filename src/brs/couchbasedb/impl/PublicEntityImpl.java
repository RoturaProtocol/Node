package brs.couchbasedb.impl;

import brs.Account;
import brs.couchbasedb.PublicEntity;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;



import java.util.ArrayList;


public class PublicEntityImpl<T> implements PublicEntity {
  private final Class<T> clazz;

  public final String bucketName;
  public static String connectionString = "couchbase://127.0.0.1";
  public static String username = "root";
  public static String password = "123456";
  Cluster cluster = Cluster.connect(connectionString, username, password);
  public static Collection connection = null;


  public PublicEntityImpl(Class<T> clazz, String bucketName){
    this.clazz = clazz;
    this.bucketName = bucketName;
    connection =  cluster.bucket(bucketName).defaultCollection();
  }




  @Override
  public <T> void insert(T t) {
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = null;
    try {
      if (t != null) {
        jsonString = objectMapper.writeValueAsString(t);
      }

      JsonObject jsonObject = JsonObject.fromJson(jsonString);
      long id = jsonObject.getLong("id");
      connection.upsert(String.valueOf(id), jsonObject);
    } catch (JsonProcessingException e) {
      // 在内部处理异常，可以记录日志或采取其他措施
      System.err.println("JSON processing failed: " + e.getMessage());
    }
  }

  @Override
  public <T> boolean delete(T t) throws NoSuchFieldException, IllegalAccessException {
    if (clazz.isInstance(t)) {
      long id = (long) t.getClass().getDeclaredField("id").get(t);
      connection.remove(String.valueOf(id));
      return true;
    } else {

      return false;
    }
  }

  @Override
  public <T> T get(long id) {
//    JsonObject jsonObject = connection.get(String.valueOf("-23771864678999650")).contentAsObject();

    T t;
    try {
      t = (T) connection.get(String.valueOf(id)).contentAs(clazz);
    }
    catch (DocumentNotFoundException e) {
      System.out.println("**********");
      System.out.println(id);
      System.out.println(e);
       t = null;
    }

    return t;
  }

//  @Override
//  public Account get(long id) {
////    JsonObject jsonObject = connection.get(String.valueOf("-23771864678999650")).contentAsObject();
//    System.out.println("*************");
//    System.out.println(id);
//
//    Account account;
//    try {
//      System.out.println(connection.get(String.valueOf(id)));
//       account = connection.get(String.valueOf(id)).contentAs(Account.class);
//      System.out.println(account.toJson());
//    }
//    catch (DocumentNotFoundException e) {
//      System.out.println(e);
//      account = null;
//    }
//
//    return account;
//
//  }


  @Override
  public <T> T get(long id, int height) {
    String n1qlQuery = String.format("SELECT a.* FROM `%s` a WHERE height = %s limit 1", bucketName, height);
    QueryResult result = cluster.query(n1qlQuery);
    ObjectMapper objectMapper = new ObjectMapper();
    if (!result.rowsAsObject().isEmpty()) {
      JsonObject firstRow = result.rowsAsObject().get(0);
      JsonObject bucketContent = firstRow.getObject(bucketName);
      try {
        T t = (T) objectMapper.readValue(bucketContent.toString(), clazz);
        return t;
        // 使用 account 对象...
      } catch (JsonProcessingException e) {

      }
    }
    return null;
  }

  @Override
  public int getCount() {
    String query = "SELECT COUNT(*) AS count FROM `account`";

    QueryResult result = cluster.query(query);
    if (!result.rowsAsObject().isEmpty()) {
      return result.rowsAsObject().get(0).getInt("count");
    }
    return 0;
  }


  @Override
  public long getAllAccountsBalance() {
    String query = "SELECT sum(balanceNQT) as totalbalanceNQT FROM `account`";

    QueryResult result = cluster.query(query);
    if (!result.rowsAsObject().isEmpty()) {
      System.out.println(result.rowsAsObject());
      System.out.println(result.rowsAsObject().get(0));
      System.out.println(result.rowsAsObject().get(0).getLong("totalbalanceNQT"));
      return result.rowsAsObject().get(0).getLong("totalbalanceNQT");
    }
    return 0;
  }


  @Override
  public <T> java.util.Collection<T> getManyBy(String name, int from, int to) {
    int limit = to - from + 1;
    String n1qlQuery = String.format("SELECT `%s`.* FROM `%s` WHERE name = '%s' LIMIT %d OFFSET %d",
      bucketName, bucketName,name, limit, from);
    QueryResult result = cluster.query(n1qlQuery);
    java.util.Collection<T> entities = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    result.rowsAsObject().forEach(row -> {
      try {
        T entity = (T) objectMapper.readValue(row.toString(), clazz);
        entities.add(entity);
      } catch (JsonProcessingException e) {
        // Handle JSON parsing exception
      }
    });

    return entities;
  }

}
