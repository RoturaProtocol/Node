package brs.test;


import brs.crypto.Crypto;


import com.couchbase.client.java.*;


import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertOptions;

import java.time.Duration;
import java.util.Map;

public class test1 {
  public static void main(String[] args) {

    String connectionString = "couchbase://127.0.0.1";
    String username = "root";
    String password = "123456";
    String bucketName = "transaction";

//    Cluster cluster = Cluster.connect(connectionString, username, password);
//    Bucket bucket = cluster.bucket(bucketName);
//    Scope scope = bucket.scope("sss");
//    Collection collection = scope.collection("ccc");

    Cluster cluster = Cluster.connect(connectionString, username, password);
    Bucket bucket = cluster.bucket(bucketName);

    com.couchbase.client.java.Collection collection = bucket.defaultCollection();
    save(collection);










    // 关闭连接
    cluster.disconnect();
  }
  public static void save(Collection collection){
    byte[] b = Crypto.getPublicKey("some teach ");


    AccountSpark account = new AccountSpark(2802355793221220719L,0,b,0,12798000000000000L,12798000000000000L,12798000000000000L,0L,0.0,0.0,null,null);



    collection.upsert("1009", account);

    AccountSpark accountSpark = collection.get("1009").contentAs(AccountSpark.class);
    System.out.println(accountSpark.getId());
    System.out.println(accountSpark.getKeyHeight());



  }


}
