package brs.db.sql;

import brs.*;
import brs.db.TransactionDb;
import brs.schema.tables.records.TransactionRecord;
import brs.util.Convert;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.jooq.BatchBindStep;
import org.jooq.SelectConditionStep;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static brs.schema.Tables.TRANSACTION;

public class SqlTransactionDb implements TransactionDb {

  public static String bucketName = "transaction";
  public static String connectionString = "couchbase://127.0.0.1";
  public static String username = "root";
  public static String password = "123456";
  static Cluster cluster = Cluster.connect(connectionString, username, password);
  public static com.couchbase.client.java.Collection connection = cluster.bucket(bucketName).defaultCollection();
  Gson gson = new Gson();

  @Override
  public Transaction findTransaction(long transactionId) {
    // 从Couchbase获取文档
    String jsonString = connection.get(String.valueOf(transactionId)).contentAsObject().toString();
    try {
      return jsonString == null ? null : Transaction.parseTransaction(JsonParser.parseString(jsonString).getAsJsonObject());
    } catch (BurstException.ValidationException e) {
      throw new RuntimeException("Transaction already in database, id = " + transactionId + ", does not pass validation!", e);
    }

//    return Db.useDSLContext(ctx -> {
//      try {
//        TransactionRecord transactionRecord = ctx.selectFrom(TRANSACTION).where(TRANSACTION.ID.eq(transactionId)).fetchOne();
//        return loadTransaction(transactionRecord);
//      } catch (BurstException.ValidationException e) {
//        throw new RuntimeException("Transaction already in database, id = " + transactionId + ", does not pass validation!", e);
//      }
//    });
  }

  @Override
  public Transaction findTransactionByFullHash(String fullHash) {
    String query = String.format("SELECT a.* FROM `%s` a WHERE fullHash = `%s`", bucketName, fullHash);
    JsonObject jsonObject = cluster.query(query).rowsAsObject().get(0);
    try {
      return jsonObject == null ? null : Transaction.parseTransactionAll(JsonParser.parseString(jsonObject.toString()).getAsJsonObject());
    } catch (BurstException.ValidationException e) {
      throw new RuntimeException("Transaction already in database, full_hash = " + fullHash + ", does not pass validation!", e);
    }

//    return Db.useDSLContext(ctx -> {
//      try {
//        TransactionRecord transactionRecord = ctx.selectFrom(TRANSACTION).where(TRANSACTION.FULL_HASH.eq(Convert.parseHexString(fullHash))).fetchOne();
//        return loadTransaction(transactionRecord);
//      } catch (BurstException.ValidationException e) {
//        throw new RuntimeException("Transaction already in database, full_hash = " + fullHash + ", does not pass validation!", e);
//      }
//    });
  }

  @Override
  public boolean hasTransaction(long transactionId) {
    String query = String.format("SELECT id FROM `%s` WHERE id = %d", bucketName, transactionId);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    if(result.isEmpty()){
      return false;
    }
    return true;
//    return Db.useDSLContext(ctx -> {
//      return ctx.fetchExists(ctx.selectFrom(TRANSACTION).where(TRANSACTION.ID.eq(transactionId)));
//    });
  }

  @Override
  public boolean hasTransactionByFullHash(String fullHash) {

    String query = String.format("SELECT id FROM `%s` WHERE fullHash = `%s`", bucketName, fullHash);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    if(result.isEmpty()){
      return false;
    }
    return true;
//    return Db.useDSLContext(ctx -> {
//      return ctx.fetchExists(ctx.selectFrom(TRANSACTION).where(TRANSACTION.FULL_HASH.eq(Convert.parseHexString(fullHash))));
//    });
  }

  @Override
  public Transaction loadTransaction(TransactionRecord tr) throws BurstException.ValidationException {
    if (tr == null) {
      return null;
    }
    ByteBuffer buffer = null;
    if (tr.getAttachmentBytes() != null) {
      buffer = ByteBuffer.wrap(tr.getAttachmentBytes());
      buffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    TransactionType transactionType = TransactionType.findTransactionType(tr.getType(), tr.getSubtype());
    Transaction.Builder builder = new Transaction.Builder(tr.getVersion(), tr.getSenderPublicKey(),
            tr.getAmount(), tr.getFee(), tr.getTimestamp(), tr.getDeadline(),
            transactionType.parseAttachment(buffer, tr.getVersion()))
            .referencedTransactionFullHash(tr.getReferencedTransactionFullhash())
            .signature(tr.getSignature())
            .blockId(tr.getBlockId())
            .height(tr.getHeight())
            .id(tr.getId())
            .senderId(tr.getSenderId())
            .blockTimestamp(tr.getBlockTimestamp())
            .fullHash(tr.getFullHash());
    if (transactionType.hasRecipient()) {
      builder.recipientId(Optional.ofNullable(tr.getRecipientId()).orElse(0L));
    }
    if (tr.getHasMessage()) {
      builder.message(new Appendix.Message(buffer, tr.getVersion()));
    }
    if (tr.getHasEncryptedMessage()) {
      builder.encryptedMessage(new Appendix.EncryptedMessage(buffer, tr.getVersion()));
    }
    if (tr.getHasPublicKeyAnnouncement()) {
      builder.publicKeyAnnouncement(new Appendix.PublicKeyAnnouncement(buffer, tr.getVersion()));
    }
    if (tr.getHasEncrypttoselfMessage()) {
      builder.encryptToSelfMessage(new Appendix.EncryptToSelfMessage(buffer, tr.getVersion()));
    }
    if (tr.getVersion() > 0) {
      builder.ecBlockHeight(tr.getEcBlockHeight());
      builder.ecBlockId(Optional.ofNullable(tr.getEcBlockId()).orElse(0L));
    }

    return builder.build();
  }

  @Override
  public List<Transaction> findBlockTransactions(long blockId, boolean onlySigned) {

    String query = String.format("SELECT id FROM `%s` WHERE blockId = %d", bucketName,blockId);

    if(onlySigned) {
      query += " AND signature IS NOT NULL";
    }
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Transaction> resultList = new ArrayList<>();
    for (JsonObject row : result) {
      try {
        com.google.gson.JsonObject asJsonObject = JsonParser.parseString(row.toString()).getAsJsonObject();
        Transaction transaction = Transaction.parseTransactionAll(asJsonObject);
        resultList.add(transaction);
      } catch (BurstException.ValidationException e) {

        throw new RuntimeException("findBlockTransactions", e);
      }
    }
    return resultList;
//    return Db.useDSLContext(ctx -> {
//      SelectConditionStep<TransactionRecord> select = ctx.selectFrom(TRANSACTION)
//          .where(TRANSACTION.BLOCK_ID.eq(blockId));
//      if(onlySigned) {
//        select = select.and(TRANSACTION.SIGNATURE.isNotNull());
//      }
//      return select.fetch(record -> {
//                try {
//                  return loadTransaction(record);
//                } catch (BurstException.ValidationException e) {
//                  e.printStackTrace();
//                  throw new RuntimeException("Invalid transaction :" + e.getMessage(), e);
//                }
//              });
//    });
  }

  public static byte[] getAttachmentBytes(Transaction transaction) {
    int bytesLength = 0;
    for (Appendix appendage : transaction.getAppendages()) {
      bytesLength += appendage.getSize();
    }
    if (bytesLength == 0) {
      return null;
    } else {
      ByteBuffer buffer = ByteBuffer.allocate(bytesLength);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      for (Appendix appendage : transaction.getAppendages()) {
        appendage.putBytes(buffer);
      }
      return buffer.array();
    }
  }

  public void saveTransactions(List<Transaction> transactions) {
    for (Transaction transaction : transactions) {
      connection.upsert(String.valueOf(transaction.getId()),transaction.getJsonObjectAll());
    }


//    if (!transactions.isEmpty()) {
//      Db.useDSLContext(ctx -> {
//        BatchBindStep insertBatch = ctx.batch(
//            ctx.insertInto(TRANSACTION, TRANSACTION.ID, TRANSACTION.DEADLINE,
//                TRANSACTION.SENDER_PUBLIC_KEY, TRANSACTION.RECIPIENT_ID, TRANSACTION.AMOUNT,
//                TRANSACTION.FEE, TRANSACTION.REFERENCED_TRANSACTION_FULLHASH, TRANSACTION.HEIGHT,
//                TRANSACTION.BLOCK_ID, TRANSACTION.SIGNATURE, TRANSACTION.TIMESTAMP,
//                TRANSACTION.TYPE,
//                TRANSACTION.SUBTYPE, TRANSACTION.SENDER_ID, TRANSACTION.ATTACHMENT_BYTES,
//                TRANSACTION.BLOCK_TIMESTAMP, TRANSACTION.FULL_HASH, TRANSACTION.VERSION,
//                TRANSACTION.HAS_MESSAGE, TRANSACTION.HAS_ENCRYPTED_MESSAGE,
//                TRANSACTION.HAS_PUBLIC_KEY_ANNOUNCEMENT, TRANSACTION.HAS_ENCRYPTTOSELF_MESSAGE,
//                TRANSACTION.EC_BLOCK_HEIGHT, TRANSACTION.EC_BLOCK_ID)
//                .values((Long) null, null, null, null, null, null, null, null, null, null, null,
//                    null, null,
//                    null, null, null, null, null, null, null, null, null, null, null));
//        for (Transaction transaction : transactions) {
//          insertBatch.bind(
//              transaction.getId(),
//              transaction.getDeadline(),
//              transaction.getSenderPublicKey(),
//              (transaction.getRecipientId() == 0 ? null : transaction.getRecipientId()),
//              transaction.getAmountNQT(),
//              transaction.getFeeNQT(),
//              Convert.parseHexString(transaction.getReferencedTransactionFullHash()),
//              transaction.getHeight(),
//              transaction.getBlockId(),
//              transaction.getSignature(),
//              transaction.getTimestamp(),
//              transaction.getType().getType(),
//              transaction.getType().getSubtype(),
//              transaction.getSenderId(),
//              getAttachmentBytes(transaction),
//              transaction.getBlockTimestamp(),
//              Convert.parseHexString(transaction.getFullHash()),
//              transaction.getVersion(),
//              transaction.getMessage() != null,
//              transaction.getEncryptedMessage() != null,
//              transaction.getPublicKeyAnnouncement() != null,
//              transaction.getEncryptToSelfMessage() != null,
//              transaction.getECBlockHeight(),
//              (transaction.getECBlockId() != 0 ? transaction.getECBlockId() : null)
//          );
//        }
//        insertBatch.execute();
//      });
//    }
  }

//  @Override
//  public void optimize() {
////    Db.optimizeTable(TRANSACTION.getName());
//  }
}
