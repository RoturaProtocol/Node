package brs.test;
import brs.Transaction;
//import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.json.JsonObject;
import com.google.gson.*;
import com.couchbase.client.java.*;
//import com.couchbase.client.java.json.*;


public class test2 {
  // Update these variables to point to your Couchbase Server instance and credentials.



  public static void main(String... args) {
//    String bucketName = "transaction";
//    String connectionString = "couchbase://127.0.0.1";
//    String username = "root";
//    String password = "123456";
//    Cluster cluster = Cluster.connect(connectionString, username, password);
//    Collection collection = cluster.bucket(bucketName).defaultCollection();
//
////    JsonObject jsonObject = collection.get("-23771864678999650").contentAsObject();
//    System.out.println("***************");
//    System.out.println(collection.get("1111"));
//    JsonObject jsonObject = collection.get("1111").contentAsObject();
//    Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Transaction.class, new AttachmentDeserializer())
//                .serializeNulls()
//                .create();
//    System.out.println(jsonObject.toString());
//    Transaction transaction11 = gson.fromJson(jsonObject.toString(), Transaction.class);
//    System.out.println(transaction11.toJson());

//    PublicEntityImpl<Transaction> publicEntity = new PublicEntityImpl<>(Transaction.class, "transaction");
//    System.out.println("***");
//    System.out.println(transaction11.getId());

//    Transaction o = publicEntity.get(transaction11.getId());
//    System.out.println(o.toJson());

//    int height = 2147483647;
////    String n1qlQuery = String.format("SELECT * FROM transaction.default.default WHERE height = %s limit 1", bucketName, height);
//    String n1qlQuery = String.format("SELECT * FROM `%s` WHERE height = %s limit 1", bucketName, height);
//    System.out.println(n1qlQuery);
//    QueryResult result = cluster.query(n1qlQuery);
////    QueryResult result = cluster.query("select * from `transaction` where height = 2147483647 limit 10");
//
//    System.out.println("**********");
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    if (!result.rowsAsObject().isEmpty()) {
//      JsonObject firstRow = result.rowsAsObject().get(0);
//      JsonObject bucketContent = firstRow.getObject(bucketName);
//      try {
//        Account account = objectMapper.readValue(bucketContent.toString(), Account.class);
//        System.out.println(account.getId());
//        // 使用 account 对象...
//      } catch (JsonProcessingException e) {
//        // 处理解析错误
//      }
//    }


  }
}
