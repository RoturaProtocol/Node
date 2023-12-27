package brs.db.sql;

import brs.*;
import brs.Block;
import brs.Transaction;
import brs.Attachment.CommitmentAdd;
import brs.Attachment.CommitmentRemove;
import brs.crypto.Crypto;
import brs.db.BlockDb;
import brs.db.TransactionDb;
import brs.db.store.BlockchainStore;
import brs.db.store.IndirectIncomingStore;
import brs.fluxcapacitor.FluxValues;


import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



public class SqlBlockchainStore implements BlockchainStore {

  public static String bucketName = "block";
  public static String connectionString = "couchbase://127.0.0.1";
  public static String username = "root";
  public static String password = "123456";
  static Cluster cluster = Cluster.connect(connectionString, username, password);
  public static com.couchbase.client.java.Collection connection = cluster.bucket(bucketName).defaultCollection();


  private final TransactionDb transactionDb = Burst.getDbs().getTransactionDb();
  private final BlockDb blockDb = Burst.getDbs().getBlockDb();
  private final IndirectIncomingStore indirectIncomingStore;

  public SqlBlockchainStore(IndirectIncomingStore indirectIncomingStore) {
    this.indirectIncomingStore = indirectIncomingStore;
  }

  @Override
  public Collection<Block> getBlocks(int from, int to) {

    int blockchainHeight = Burst.getBlockchain().getHeight();
    int startHeight = Math.max(0, blockchainHeight - Math.max(from, 0));
    int endHeight = blockchainHeight - to;
    String query = String.format("SELECT a.* FROM `%s` a WHERE height BETWEEN %s  AND %s  ORDER BY height DESC ", bucketName, startHeight,endHeight);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    return getBlocks(result);


//    return Db.useDSLContext(ctx -> {
//      int blockchainHeight = Burst.getBlockchain().getHeight();
//      return
//        getBlocks(ctx.selectFrom(BLOCK)
//                .where(BLOCK.HEIGHT.between(to > 0 ? blockchainHeight - to : 0).and(blockchainHeight - Math.max(from, 0)))
//                .orderBy(BLOCK.HEIGHT.desc())
//                .fetch());
//    });
  }

  @Override
  public Collection<Block> getBlocks(Account account, int timestamp, int from, int to) {

    String query = String.format("SELECT a.* FROM `%s` a WHERE generatorId = %s ", bucketName, account.getId());
    if (timestamp > 0) {
      query += String.format(" AND timestamp >= %s ", timestamp);
    }
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    return getBlocks(result);


//    return Db.useDSLContext(ctx -> {
//
//      SelectConditionStep<BlockRecord> query = ctx.selectFrom(BLOCK).where(BLOCK.GENERATOR_ID.eq(account.getId()));
//      if (timestamp > 0) {
//        query.and(BLOCK.TIMESTAMP.ge(timestamp));
//      }
//      // TODO: this filter is not working.
//      // Additionally, if we filter the blocks foget counting becomes wrong.
////      if(from > 0 || to > 0) {
////        int blockchainHeight = Burst.getBlockchain().getHeight();
////        query.and(BLOCK.HEIGHT.between(to > 0 ? blockchainHeight - to : 0).and(blockchainHeight - Math.max(from, 0)));
////      }
//      return getBlocks(query.orderBy(BLOCK.HEIGHT.desc()).fetch());
//    });
  }

  @Override
  public int getBlocksCount(long accountId, int from, int to) {
    if (from > to) {
      return 0;
    }
    try {
      String query = String.format("SELECT COUNT(*) AS blockCount FROM `%s` WHERE generatorId = %s AND height BETWEEN %s AND %s", bucketName, accountId, from, to);
      List<JsonObject> result = cluster.query(query).rowsAsObject();
      if (!result.isEmpty()) {
        JsonObject firstRow = result.get(0);
        if (firstRow.containsKey("blockCount")) {
          int blockCount = firstRow.getInt("blockCount");
          return blockCount;
        }
      }
      return 0;
//    return Db.useDSLContext(ctx -> {
//      SelectConditionStep<BlockRecord> query = ctx.selectFrom(BLOCK).where(BLOCK.GENERATOR_ID.eq(accountId))
//    		  .and(BLOCK.HEIGHT.between(from).and(to));
//
//      return ctx.fetchCount(query);
//    });
    } catch (Exception e) {

      e.printStackTrace();
      return 0; // 出现异常时返回默认值
    }
  }

//  @Override
//  public Collection<Block> getBlocks(Result<BlockRecord> blockRecords) {
//    return blockRecords.map(blockRecord -> {
//      try {
//        return blockDb.loadBlock(blockRecord);
//      } catch (BurstException.ValidationException e) {
//        throw new RuntimeException(e);
//      }
//    });
//  }
  @Override
  public Collection<Block> getBlocks(List<JsonObject> result) {
    List<Block> blocks = new ArrayList<>();
    result.forEach(row -> {
      try {
        Block block = Block.parseBlock(JsonParser.parseString(row.toString()).getAsJsonObject());
        blocks.add(block);
      } catch (BurstException.ValidationException e) {
        throw new RuntimeException("getBlocks", e);
      }

    });

    return blocks;
  }



  @Override
  public Collection<Long> getBlockIdsAfter(long blockId, int limit) {
    if (limit > 1440) {
      throw new IllegalArgumentException("Can't get more than 1440 blocks at a time");
    }
    String query = String.format("SELECT id FROM `%s` WHERE height > (SELECT height FROM `%s` WHERE id = %d) ORDER BY height ASC LIMIT %d", bucketName, bucketName, blockId, limit);

    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Long> blockIds = new ArrayList<>();

    for (JsonObject row : result) {
      if (row.containsKey("id")) {
        blockIds.add(row.getLong("id"));
      }
    }

    return blockIds;
//    return Db.useDSLContext(ctx -> {
//      return
//        ctx.selectFrom(BLOCK).where(
//          BLOCK.HEIGHT.gt( ctx.select(BLOCK.HEIGHT).from(BLOCK).where(BLOCK.ID.eq(blockId) ) )
//        ).orderBy(BLOCK.HEIGHT.asc()).limit(limit).fetch(BLOCK.ID, Long.class);
//    });
  }

  @Override
  public Collection<Block> getBlocksAfter(long blockId, int limit) {
    if (limit > 1440) {
      throw new IllegalArgumentException("Can't get more than 1440 blocks at a time");
    }
    String query = String.format(
      "SELECT a.* FROM `%s` a WHERE height > (SELECT height FROM `%s` WHERE id = %d) ORDER BY height ASC LIMIT %d",
      bucketName, bucketName, blockId, limit);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Block> blocks = new ArrayList<>();

    for (JsonObject row : result) {
      Block block = null; // 请替换 height 为适当的高度值
      try {
        block = Block.parseBlock(JsonParser.parseString(row.toString()).getAsJsonObject());
        blocks.add(block);
      } catch (BurstException.ValidationException e) {

        throw new RuntimeException("getBlocksAfter", e);
      }
    }

    return blocks;
//    return Db.useDSLContext(ctx -> {
//      return ctx.selectFrom(BLOCK)
//              .where(BLOCK.HEIGHT.gt(ctx.select(BLOCK.HEIGHT)
//                      .from(BLOCK)
//                      .where(BLOCK.ID.eq(blockId))))
//              .orderBy(BLOCK.HEIGHT.asc())
//              .limit(limit)
//              .fetch(result -> {
//                try {
//                  return blockDb.loadBlock(result);
//                } catch (BurstException.ValidationException e) {
//                  throw new RuntimeException(e.toString(), e);
//                }
//              });
//    });
  }

  @Override
  public int getTransactionCount() {

    String query = "SELECT COUNT(*) AS transactionCount FROM `transaction`";
    JsonObject result = cluster.query(query).rowsAsObject().get(0);
    return result.getInt("transactionCount");

//    return Db.useDSLContext(ctx -> {
//      return ctx.selectCount().from(TRANSACTION).fetchOne(0, int.class);
//    });
  }

  @Override
  public Collection<Transaction> getAllTransactions() {

    String query = "SELECT a.* FROM `transaction` a ORDER BY timestamp ASC";
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Transaction> resultList = new ArrayList<>();

    for (JsonObject row : result) {

      try {
        com.google.gson.JsonObject asJsonObject = JsonParser.parseString(row.toString()).getAsJsonObject();
        Transaction transaction = Transaction.parseTransaction(asJsonObject);
        resultList.add(transaction);
      } catch (BurstException.ValidationException e) {
        throw new RuntimeException("getAllTransactions", e);
      }
    }

    return resultList;

//    return Db.useDSLContext(ctx -> {
//      return getTransactions(ctx, ctx.selectFrom(TRANSACTION).orderBy(TRANSACTION.DB_ID.asc()).fetch());
//    });
  }

  @Override
  public long getAtBurnTotal() {

    String query = String.format("SELECT SUM(AMOUNT) AS totalAmount FROM `transaction` WHERE recipientId IS NULL AND amountNQT > 0 AND type_one = %s" , TransactionType.TYPE_AUTOMATED_TRANSACTIONS.getType());
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    for (JsonObject row : result) {
      if (row.containsKey("totalAmount")) {
        return row.getLong("totalAmount");
      }
    }
    return 0L;
//    return Db.useDSLContext(ctx -> {
//      return ctx.select(DSL.sum(TRANSACTION.AMOUNT)).from(TRANSACTION)
//          .where(TRANSACTION.RECIPIENT_ID.isNull())
//          .and(TRANSACTION.AMOUNT.gt(0L))
//          .and(TRANSACTION.TYPE.equal(TransactionType.TYPE_AUTOMATED_TRANSACTIONS.getType()))
//          .fetchOneInto(long.class);
//    });
  }


  @Override
  public Collection<Transaction> getTransactions(Account account, int numberOfConfirmations, byte type, byte subtype, int blockTimestamp, int from, int to, boolean includeIndirectIncoming) {
    int height = numberOfConfirmations > 0 ? Burst.getBlockchain().getHeight() - numberOfConfirmations : Integer.MAX_VALUE;
    if (height < 0) {
      throw new IllegalArgumentException("Number of confirmations required " + numberOfConfirmations + " exceeds current blockchain height " + Burst.getBlockchain().getHeight());
    }

//    StringBuilder queryBuilder = new StringBuilder(String.format("SELECT * FROM `%s` WHERE ", bucketName));
    StringBuilder queryBuilder = new StringBuilder(String.format("  WHERE "));


    if (blockTimestamp > 0) {
      queryBuilder.append(String.format("blockTimestamp >= %d AND ", blockTimestamp));
    }
    if (type >= 0) {
      queryBuilder.append(String.format("type = %d ", type));
      if (subtype >= 0) {
        queryBuilder.append(String.format("AND subtype = %d ", subtype));
      }
    }
    if (height < Integer.MAX_VALUE) {
      queryBuilder.append(String.format("AND height <= %d ", height));
    }

    String whereString = queryBuilder.toString();
    String selectString = "SELECT *,block_timestamp,id FROM `transaction` ";

    String query1 = null;
    if (account != null) {
      query1 = selectString + whereString+ String.format("AND recipientId = %d AND senderId != %d ", account.getId(), account.getId());
    }else {
      query1 = selectString + whereString+ "AND recipientId IS NULL ";
    }


    String query2 = selectString + whereString+ String.format("AND senderId = %d" , account.getId());
    String query3 = selectString + whereString+ String.format("AND id IN [%s]" , indirectIncomingStore.getIndirectIncomings(account.getId(), -1, -1).stream()
      .map(String::valueOf)
      .collect(Collectors.joining(", ")));

    String query = query1 +" union "+ query2 + " union " + query3 + " order by blockTimestamp id desc";

    int limit = to >= 0 && to >= from && to < Integer.MAX_VALUE ? to - from + 1 : 0;
    int offset = from > 0 ? from : 0;


    String limitClause = limit > 0 ? " LIMIT " + limit : "";
    String offsetClause = offset > 0 ? " OFFSET " + offset : "";

    query += (limitClause + offsetClause);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Transaction> resultList = new ArrayList<>();
    for (JsonObject row : result) {
      try {
        com.google.gson.JsonObject asJsonObject = JsonParser.parseString(row.toString()).getAsJsonObject();
        Transaction transaction = Transaction.parseTransaction(asJsonObject);
        resultList.add(transaction);
      } catch (BurstException.ValidationException e) {
        throw new RuntimeException("getTransactions", e);
      }
    }

    return resultList;
//    return Db.useDSLContext(ctx -> {
//      ArrayList<Condition> conditions = new ArrayList<>();
//      if (blockTimestamp > 0) {
//        conditions.add(TRANSACTION.BLOCK_TIMESTAMP.ge(blockTimestamp));
//      }
//      if (type >= 0) {
//        conditions.add(TRANSACTION.TYPE.eq(type));
//        if (subtype >= 0) {
//          conditions.add(TRANSACTION.SUBTYPE.eq(subtype));
//        }
//      }
//      if (height < Integer.MAX_VALUE) {
//        conditions.add(TRANSACTION.HEIGHT.le(height));
//      }
//
//      SelectOrderByStep<TransactionRecord> select = ctx.selectFrom(TRANSACTION).where(conditions).and(
//          account == null ? TRANSACTION.RECIPIENT_ID.isNull() :
//            TRANSACTION.RECIPIENT_ID.eq(account.getId()).and(
//                      TRANSACTION.SENDER_ID.ne(account.getId())
//              )
//      ).unionAll(
//          account == null ? null :
//              ctx.selectFrom(TRANSACTION).where(conditions).and(
//                      TRANSACTION.SENDER_ID.eq(account.getId())
//              )
//      );
//
//      if (includeIndirectIncoming) {
//        select = select.unionAll(ctx.selectFrom(TRANSACTION)
//                .where(conditions)
//                .and(TRANSACTION.ID.in(indirectIncomingStore.getIndirectIncomings(account.getId(), -1, -1))));
//      }
//
//      SelectQuery<TransactionRecord> selectQuery = select
//              .orderBy(TRANSACTION.BLOCK_TIMESTAMP.desc(), TRANSACTION.ID.desc())
//              .getQuery();
//
//      DbUtils.applyLimits(selectQuery, from, to);
//
//      return getTransactions(ctx, selectQuery.fetch());
//    });
  }

//  @Override
//  public Collection<Transaction> getTransactions(DSLContext ctx, Result<TransactionRecord> rs) {
//    return rs.map(r -> {
//      try {
//        return transactionDb.loadTransaction(r);
//      } catch (BurstException.ValidationException e) {
//        throw new RuntimeException(e);
//      }
//    });
//  }

  @Override
  public void addBlock(Block block) {


    blockDb.saveBlock(block);


//    Db.useDSLContext(ctx -> {
//      blockDb.saveBlock(ctx, block);
//    });

  }

  @Override
  public void addGenesisAccount() {
    byte[] b = Crypto.getPublicKey("some teach felicity people reflect cage task state ship study admit shove");
//    Db.useDSLContext(ctx -> {
//
//      ctx.insertInto(ACCOUNT,ACCOUNT.ID,ACCOUNT.PUBLIC_KEY,ACCOUNT.KEY_HEIGHT,ACCOUNT.CREATION_HEIGHT,
//        ACCOUNT.BALANCE,ACCOUNT.UNCONFIRMED_BALANCE,ACCOUNT.FORGED_BALANCE,
//        ACCOUNT.HEIGHT).values(2802355793221220719L,b, 0,0, 12798000000000000L,12798000000000000L,12798000000000000L, 0).execute();
//    });


    Account sqlAccount = new SqlAccountStore.SqlAccount();
    String bucketName = "account";
    String connectionString = "couchbase://127.0.0.1";
    String username = "root";
    String password = "123456";
    Cluster cluster = Cluster.connect(connectionString, username, password);
    com.couchbase.client.java.Collection collection = cluster.bucket(bucketName).defaultCollection();
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = null;
    try {
      jsonString = objectMapper.writeValueAsString(sqlAccount);
      System.out.println("addGenesisAccount");
      System.out.println(jsonString);
    } catch (JsonProcessingException e) {

      throw new RuntimeException("addGenesisAccount", e);
    }
    JsonObject jsonObject = JsonObject.fromJson(jsonString);
    long id = jsonObject.getLong("id");
    collection.upsert(String.valueOf(id), jsonObject);

//    Db.useDSLContext(ctx -> {
//      byte[] b = Crypto.getPublicKey("some teach felicity people reflect cage task state ship study admit shove");
//      ctx.insertInto(ACCOUNT_STABLECOIN
//        ,ACCOUNT_STABLECOIN.ID
//        ,ACCOUNT_STABLECOIN.CREATION_HEIGHT
//        ,ACCOUNT_STABLECOIN.PUBLIC_KEY
//        ,ACCOUNT_STABLECOIN.PLEDGE_BALANCE
//        ,ACCOUNT_STABLECOIN.STABLECOIN_BALANCE
//        ,ACCOUNT_STABLECOIN.DEBT_STABLECOIN_BALANCE
//        ,ACCOUNT_STABLECOIN.HEIGHT
//        ,ACCOUNT_STABLECOIN.LATEST
//      ).values(
//        2802355793221220719L,
//        0,
//        b,
//        0L,
//        0.0d,
//        0.0d,
//        0,
//        true
//      ).execute();
//    });

  }
  @Override
  public Collection<Block> getLatestBlocks(int amountBlocks) {
    final int latestBlockHeight = blockDb.findLastBlock().getHeight();

    final int firstLatestBlockHeight = Math.max(0, latestBlockHeight - amountBlocks);


    String query = String.format("SELECT a.* FROM `%s` a WHERE height BETWEEN %d AND %d ORDER BY height ASC", bucketName, firstLatestBlockHeight, latestBlockHeight);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    return getBlocks(result);


//    return Db.useDSLContext(ctx -> {
//      return getBlocks(ctx.selectFrom(BLOCK)
//                      .where(BLOCK.HEIGHT.between(firstLatestBlockHeight).and(latestBlockHeight))
//                      .orderBy(BLOCK.HEIGHT.asc())
//                      .fetch());
//    });
  }

  @Override
  public long getCommittedAmount(long accountId, int height, int endHeight, Transaction skipTransaction) {
    int commitmentWait = Burst.getFluxCapacitor().getValue(FluxValues.COMMITMENT_WAIT, height);
    int commitmentHeight = Math.min(height - commitmentWait, endHeight);

    String commitmmentAddTransactionsQuery = String.format("SELECT a.* FROM `%s` a WHERE type = %d AND subtype = %d and height <= %d", bucketName, TransactionType.TYPE_BURST_MINING.getType(),
      TransactionType.SUBTYPE_BURST_MINING_COMMITMENT_ADD,commitmentHeight);
    List<Transaction> commitmmentAddTransactions = cluster.query(commitmmentAddTransactionsQuery).rowsAsObject().stream().map(x -> {
      Transaction transaction = null;
      try {
        transaction = Transaction.parseTransactionAll(JsonParser.parseString(x.toString()).getAsJsonObject());
      } catch (BurstException.NotValidException e) {
        throw new RuntimeException("getCommittedAmount", e);
      }
      return transaction;
    }).filter(Objects::nonNull).collect(Collectors.toList());

    String commitmmentRemoveTransactionsQuery = String.format("SELECT a.* FROM `%s` a WHERE type = %d AND subtype = %d and height <= %d", bucketName, TransactionType.TYPE_BURST_MINING.getType(),
      TransactionType.SUBTYPE_BURST_MINING_COMMITMENT_REMOVE,endHeight);
    List<Transaction> commitmmentRemoveTransactions = cluster.query(commitmmentRemoveTransactionsQuery).rowsAsObject().stream().map(x -> {
      Transaction transaction = null;
      try {
        transaction = Transaction.parseTransactionAll(JsonParser.parseString(x.toString()).getAsJsonObject());
      } catch (BurstException.NotValidException e) {

        throw new RuntimeException("getCommittedAmount", e);
      }
      return transaction;
    }).filter(Objects::nonNull).collect(Collectors.toList());

//    Collection<Transaction> commitmmentAddTransactions = Db.useDSLContext(ctx -> {
//      SelectConditionStep<TransactionRecord> select = ctx.selectFrom(TRANSACTION).where(TRANSACTION.TYPE.eq(TransactionType.TYPE_BURST_MINING.getType()))
//          .and(TRANSACTION.SUBTYPE.eq(TransactionType.SUBTYPE_BURST_MINING_COMMITMENT_ADD))
//          .and(TRANSACTION.HEIGHT.le(commitmentHeight));
//      if(accountId != 0L)
//        select = select.and(TRANSACTION.SENDER_ID.equal(accountId));
//      return getTransactions(ctx, select.fetch());
//    });
//    Collection<Transaction> commitmmentRemoveTransactions = Db.useDSLContext(ctx -> {
//      SelectConditionStep<TransactionRecord> select = ctx.selectFrom(TRANSACTION).where(TRANSACTION.TYPE.eq(TransactionType.TYPE_BURST_MINING.getType()))
//          .and(TRANSACTION.SUBTYPE.eq(TransactionType.SUBTYPE_BURST_MINING_COMMITMENT_REMOVE))
//          .and(TRANSACTION.HEIGHT.le(endHeight));
//      if(accountId != 0L)
//        select = select.and(TRANSACTION.SENDER_ID.equal(accountId));
//      return getTransactions(ctx, select.fetch());
//    });

    BigInteger amountCommitted = BigInteger.ZERO;
    for(Transaction tx : commitmmentAddTransactions) {
      CommitmentAdd txAttachment = (CommitmentAdd) tx.getAttachment();
      amountCommitted = amountCommitted.add(BigInteger.valueOf(txAttachment.getAmountNQT()));
    }
    for(Transaction tx : commitmmentRemoveTransactions) {
      if(skipTransaction !=null && skipTransaction.getId() == tx.getId())
        continue;
      CommitmentRemove txAttachment = (CommitmentRemove) tx.getAttachment();
      amountCommitted = amountCommitted.subtract(BigInteger.valueOf(txAttachment.getAmountNQT()));
    }
    if(amountCommitted.compareTo(BigInteger.ZERO) < 0) {
      // should never happen
      amountCommitted = BigInteger.ZERO;
    }
    return amountCommitted.longValue();
  }

  @Override
  public Collection<Long> getTransactionIds(Long sender, Long recipient, int numberOfConfirmations, byte type,
      byte subtype, int blockTimestamp, int from, int to, boolean includeIndirectIncoming) {

    int height = numberOfConfirmations > 0 ? Burst.getBlockchain().getHeight() - numberOfConfirmations : Integer.MAX_VALUE;
    if (height < 0) {
      throw new IllegalArgumentException("Number of confirmations required " + numberOfConfirmations + " exceeds current blockchain height " + Burst.getBlockchain().getHeight());
    }


    StringBuilder queryBuilder = new StringBuilder(String.format("  WHERE "));


    if (blockTimestamp > 0) {
      queryBuilder.append(String.format("blockTimestamp >= %d AND ", blockTimestamp));
    }
    if (type >= 0) {
      queryBuilder.append(String.format("type = %d ", type));
      if (subtype >= 0) {
        queryBuilder.append(String.format("AND subtype = %d ", subtype));
      }
    }
    if (height < Integer.MAX_VALUE) {
      queryBuilder.append(String.format("AND height <= %d ", height));
    }


    String whereString = queryBuilder.toString();
    String selectString = "SELECT *,block_timestamp,id FROM `transaction` ";

    String query1 = null;



    if (recipient != null) {
      query1 += selectString + whereString+ String.format("AND recipientId = %d  ", recipient);
      }
    if (sender != null) {
      query1 = selectString + whereString+ String.format("AND senderId = %d) ", sender);
      }

    String query3 = selectString + whereString+ String.format("AND id IN [%s]" , indirectIncomingStore.getIndirectIncomings(recipient, -1, -1).stream()
      .map(String::valueOf)
      .collect(Collectors.joining(", ")));

    String query = query1  + " union " + query3 + " order by blockTimestamp id desc";

    int limit = to >= 0 && to >= from && to < Integer.MAX_VALUE ? to - from + 1 : 0;
    int offset = from > 0 ? from : 0;


    String limitClause = limit > 0 ? " LIMIT " + limit : "";
    String offsetClause = offset > 0 ? " OFFSET " + offset : "";

    query += (limitClause + offsetClause);
    List<JsonObject> result = cluster.query(query).rowsAsObject();
    List<Long> resultList = new ArrayList<>();
    for (JsonObject row : result) {
      resultList.add(row.getLong("id"));

    }

    return resultList;




//    return Db.useDSLContext(ctx -> {
//      ArrayList<Condition> conditions = new ArrayList<>();
//      if (blockTimestamp > 0) {
//        conditions.add(TRANSACTION.BLOCK_TIMESTAMP.ge(blockTimestamp));
//      }
//      if (type >= 0) {
//        conditions.add(TRANSACTION.TYPE.eq(type));
//        if (subtype >= 0) {
//          conditions.add(TRANSACTION.SUBTYPE.eq(subtype));
//        }
//      }
//      if (height < Integer.MAX_VALUE) {
//        conditions.add(TRANSACTION.HEIGHT.le(height));
//      }
//
//      SelectConditionStep<TransactionRecord> select = ctx.selectFrom(TRANSACTION).where(conditions);
//
//      if (recipient != null) {
//        select = select.and(TRANSACTION.RECIPIENT_ID.eq(recipient));
//      }
//      if (sender != null) {
//        select = select.and(TRANSACTION.SENDER_ID.eq(sender));
//      }
//
//      SelectOrderByStep<TransactionRecord> selectOrder = select;
//
//      if (includeIndirectIncoming && recipient != null) {
//        selectOrder = selectOrder.unionAll(ctx.selectFrom(TRANSACTION)
//                .where(conditions)
//                .and(TRANSACTION.ID.in(indirectIncomingStore.getIndirectIncomings(recipient, -1, -1))));
//      }
//
//      SelectQuery<TransactionRecord> selectQuery = selectOrder
//              .orderBy(TRANSACTION.BLOCK_TIMESTAMP.desc(), TRANSACTION.ID.desc())
//              .getQuery();
//
//      DbUtils.applyLimits(selectQuery, from, to);
//
//      return selectQuery.fetch(TRANSACTION.ID, Long.class);
//    });
  }
}
