package brs.test;



import brs.Transaction;

import brs.services.TransactionService;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;

import java.util.*;
import com.couchbase.client.java.*;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import org.apache.spark.Partitioner;
import scala.Tuple3;

public class test3 {

  public static SparkSession sparkSession;
  //将交易放入couchbase
  public static String connectionString = "couchbase://127.0.0.1";
  public static String username = "root";
  public static String password = "123456";
  public static String bucketName = "transaction";

  public static Cluster cluster = Cluster.connect(connectionString, username, password);
  public static Bucket bucket = cluster.bucket(bucketName);
  public static com.couchbase.client.java.Collection collection = bucket.defaultCollection();

  public static synchronized SparkSession getSparkSession() {
    if (sparkSession == null) {
      sparkSession = SparkSession
        .builder()
        .master("local[*]")
        .appName("Java API")
        .config("spark.couchbase.connectionString", "127.0.0.1")
        .config("spark.couchbase.username", "root")
        .config("spark.couchbase.password", "123456")
        .config("spark.couchbase.implicitBucket", "transaction")  // 指定桶名
        .getOrCreate();
    }
    return sparkSession;
  }


  public static Map<String, Object> applyTransactionSpark(TransactionService transactionService){
    SparkSession spark = getSparkSession();
    Dataset<Row> multiLine = spark.read()
      .format("couchbase.query")
      .option("multiLine", true).load();


    JavaRDD<Transaction> transactionRdd = multiLine.javaRDD().map(Transaction::parseWordParticiple);
    // 使用 accountId 作为键值对的 key
    JavaPairRDD<Long, Transaction> keyedRdd = transactionRdd.mapToPair(
      (PairFunction<Transaction, Long, Transaction>) transaction ->
        new Tuple2<Long, Transaction>(transaction.getSenderId(), transaction)
    );

    // 自定义分区
    int numPartitions = 4; // 你想要的分区数
    JavaPairRDD<Long, Transaction> partitionedRdd = keyedRdd.partitionBy(new CustomPartitioner(numPartitions));

    partitionedRdd.foreach(v->{
      System.out.println(v._2.getSenderId());
    });
    // 使用 mapPartitions 处理所有分区的数据，并返回处理后的 Transaction 对象
    JavaRDD<Transaction> transactionJavaRDD = partitionedRdd.mapPartitions(
      (FlatMapFunction<Iterator<Tuple2<Long, Transaction>>, Transaction>) iterator -> {
        List<Transaction> result = new ArrayList<>();

        // 处理每个分区的数据
        while (iterator.hasNext()) {
          Tuple2<Long, Transaction> tuple = iterator.next();
          Transaction transaction = tuple._2();

          if (transactionService.applyUnconfirmed(transaction)) {
            transactionService.apply(transaction);
            //将couchbase中的交易删除
            collection.remove(String.valueOf(transaction.getId()));
            // 添加处理后的结果到列表中
            result.add(transaction);
          }
        }

        // 返回处理后的结果的迭代器
        return result.iterator();
      }
    );
    // 使用 reduce 操作累加所有交易金额和手续费
    Tuple3<Long, Long,Integer> totalAmounts = transactionJavaRDD
      .map(transaction -> new Tuple3<Long, Long, Integer>(transaction.getAmountNQT(), transaction.getFeeNQT(),transaction.getSize()))
      .reduce((t1, t2) -> new Tuple3<Long, Long, Integer>(t1._1() + t2._1(), t1._2() + t2._2(),t1._3() + t2._3()));

    Long totalAmountNQT = totalAmounts._1();
    Long totalFeeNQT = totalAmounts._2();
    int payloadSize = totalAmounts._3();
    SortedSet<Transaction> orderedBlockTransactions = new TreeSet<>(transactionJavaRDD.collect());
    // 创建包含多个结果的映射
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("totalAmountNQT", totalAmountNQT);
    resultMap.put("totalFeeNQT", totalFeeNQT);
    resultMap.put("payloadSize", payloadSize);
    resultMap.put("orderedBlockTransactions", orderedBlockTransactions);

    // 返回映射
    return resultMap;

  }




  private static void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // 在 JVM 关闭时执行清理工作
      if (sparkSession != null) {
        System.out.println("Closing SparkSession...");
        sparkSession.stop();
      }
    }));
  }


















  public static void main(String[] args) {

    SparkSession spark = getSparkSession();

    Dataset<Row> multiLine = spark.read()
      .format("couchbase.query")
      .option("multiLine", true).load();
    JavaRDD<Row> rowJavaRDD = multiLine.javaRDD();



    JavaRDD<Transaction> transactionRdd = rowJavaRDD.map(Transaction::parseWordParticiple);
    // 使用 accountId 作为键值对的 key
    JavaPairRDD<Long, Transaction> keyedRdd = transactionRdd.mapToPair(
      (PairFunction<Transaction, Long, Transaction>) transaction ->
        new Tuple2<Long, Transaction>(transaction.getSenderId(), transaction)
    );

    // 自定义分区
    int numPartitions = 4; // 你想要的分区数
    JavaPairRDD<Long, Transaction> partitionedRdd = keyedRdd.partitionBy(new CustomPartitioner(numPartitions));
//    partitionedRdd.foreachPartition(new MyForeachPartitionFunction());

    // 使用 mapPartitions 处理所有分区的数据，并返回处理后的 Transaction 对象
    JavaRDD<Transaction> processedRdd = partitionedRdd.mapPartitions(
      (FlatMapFunction<Iterator<Tuple2<Long, Transaction>>, Transaction>) iterator -> {
        List<Transaction> result = new ArrayList<>();

        // 处理每个分区的数据
        while (iterator.hasNext()) {
          Tuple2<Long, Transaction> tuple = iterator.next();
          Transaction transaction = tuple._2();

          // 在这里对每个元素进行处理，例如进行一些修改
          // ...
          System.out.println(String.valueOf(transaction.getId()));
          if (String.valueOf(transaction.getId()).equals("-189127552053357102")){
            System.out.println("********************");
            System.out.println(String.valueOf(transaction.getId()));
            transaction.setAmountNQT(transaction.getAmountNQT()+520);
            collection.upsert(String.valueOf(transaction.getId()),com.couchbase.client.java.json.JsonObject.fromJson(transaction.toJson()));
            // 添加处理后的结果到列表中
            result.add(transaction);
          }

        }

        // 返回处理后的结果的迭代器
        return result.iterator();
      }
    );

    // 打印结果
    processedRdd.foreach(tuple -> System.out.println("Processed Transaction: " + tuple.getType()));


//    Double reduce = map.mapToDouble(Transaction::getAmountNQT).reduce(Double::sum);
//    System.out.println(reduce);




    spark.stop();

  }

}


// 自定义分区器
class CustomPartitioner extends Partitioner {

  private final int numPartitions;

  CustomPartitioner(int numPartitions) {
    this.numPartitions = numPartitions;
  }
  @Override
  public int numPartitions() {
    return numPartitions;
  }
  @Override
  public int getPartition(Object key) {
    // 根据 accountId 来决定分区
    // 这里假设 accountId 是 Long 类型
    Long accountId = (Long) key;
    // 你可以实现自己的分区逻辑，比如简单的 hash 分区
    return Math.abs(accountId.hashCode()) % numPartitions;
  }
}
