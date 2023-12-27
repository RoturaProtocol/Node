package brs;

import brs.crypto.Crypto;
import brs.db.TransactionDb;
import brs.fluxcapacitor.FluxValues;
import brs.peer.Peer;
import brs.util.Convert;
import brs.util.JSON;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Block {

  private static final Logger logger = LoggerFactory.getLogger(Block.class);
  private final int version;
  private final int timestamp;
  private long previousBlockId;
  private final byte[] generatorPublicKey;
  private final byte[] previousBlockHash;
  private final long totalAmountNQT;
  private final long totalFeeNQT;
  private final int payloadLength;
  private final byte[] generationSignature;
  private final byte[] payloadHash;
//  private final AtomicReference<List<Transaction>> blockTransactions = new AtomicReference<>();

  private   List<Transaction> blockTransactions;
  private byte[] blockSignature;

  private BigInteger cumulativeDifficulty = BigInteger.ZERO;

  private long baseTarget = Constants.INITIAL_BASE_TARGET;
  private AtomicLong nextBlockId = new AtomicLong();
  private int height = -1;
  private final AtomicLong id = new AtomicLong();
  private final AtomicReference<String> stringId = new AtomicReference<>();
  private final AtomicLong generatorId = new AtomicLong();
  private long nonce;

  private BigInteger pocTime = null;
  private long commitment = 0L;

  private final byte[] blockATs;

  private Peer downloadedFrom = null;
  private int byteLength = 0;

  Block(int version, int timestamp, long previousBlockId, long totalAmountNQT, long totalFeeNQT,
      int payloadLength, byte[] payloadHash, byte[] generatorPublicKey, byte[] generationSignature,
      byte[] blockSignature, byte[] previousBlockHash, List<Transaction> transactions,
      long nonce, byte[] blockATs, int height, long baseTarget) throws BurstException.ValidationException {

    if (payloadLength > Burst.getFluxCapacitor().getValue(FluxValues.MAX_PAYLOAD_LENGTH, height) || payloadLength < 0) {
      throw new BurstException.NotValidException(
          "attempted to create a block with payloadLength " + payloadLength + " height " + height + "previd " + previousBlockId);
    }

    this.version = version;
    this.timestamp = timestamp;
    this.previousBlockId = previousBlockId;
    this.totalAmountNQT = totalAmountNQT;
    this.totalFeeNQT = totalFeeNQT;
    this.payloadLength = payloadLength;
    this.payloadHash = payloadHash;
    this.generatorPublicKey = generatorPublicKey;
    this.generationSignature = generationSignature;
    this.blockSignature = blockSignature;

    this.previousBlockHash = previousBlockHash;
    this.blockTransactions=transactions;
//    if (transactions != null) {
////      this.blockTransactions.set(Collections.unmodifiableList(transactions));
//      this.blockTransactions=transactions;
//      if (blockTransactions.size() > (Burst.getFluxCapacitor().getValue(FluxValues.MAX_NUMBER_TRANSACTIONS, height))) {
//        throw new BurstException.NotValidException(
//            "attempted to create a block with " + blockTransactions.size() + " transactions");
//      }
//      long previousId = 0;
//      for (Transaction transaction : this.blockTransactions) {
//        if (transaction.getId() <= previousId && previousId != 0) {
//          throw new BurstException.NotValidException("Block transactions are not sorted!");
//        }
//        previousId = transaction.getId();
//      }
//    }
    this.nonce = nonce;
    this.blockATs = blockATs;
    this.baseTarget = baseTarget;
  }

  public  Block(int version, int timestamp, long previousBlockId, long totalAmountNQT, long totalFeeNQT, int payloadLength, byte[] payloadHash, byte[] generatorPublicKey, byte[] generationSignature, byte[] blockSignature, byte[] previousBlockHash, BigInteger cumulativeDifficulty, long baseTarget,
      long nextBlockId, int height, Long id, long nonce, byte[] blockATs) throws BurstException.ValidationException {

    this(version, timestamp, previousBlockId, totalAmountNQT, totalFeeNQT, payloadLength, payloadHash, generatorPublicKey, generationSignature, blockSignature, previousBlockHash, null, nonce, blockATs, height, baseTarget);

    this.cumulativeDifficulty = cumulativeDifficulty == null ? BigInteger.ZERO : cumulativeDifficulty;
    this.nextBlockId.set(nextBlockId);
    this.height = height;
    this.id.set(id);
  }

  public Block(int version, int timestamp, long previousBlockId, long totalAmountNQT, long totalFeeNQT, int payloadLength, byte[] payloadHash, byte[] generatorPublicKey
    , byte[] generationSignature, byte[] blockSignature, byte[] previousBlockHash, BigInteger cumulativeDifficulty, long baseTarget, long nextBlockId, int height, long id
    , long nonce, byte[] blockATs
    , String stringId, long generatorId, BigInteger pocTime, long commitment, int byteLength) throws BurstException.ValidationException {
    this(version, timestamp, previousBlockId, totalAmountNQT, totalFeeNQT, payloadLength, payloadHash, generatorPublicKey, generationSignature, blockSignature, previousBlockHash, null, nonce, blockATs, height, baseTarget);

    this.cumulativeDifficulty = cumulativeDifficulty == null ? BigInteger.ZERO : cumulativeDifficulty;
    this.nextBlockId.set(nextBlockId);
    this.height = height;
    this.id.set(id);
    this.stringId.set(stringId);
    this.generatorId.set(generatorId);
    this.pocTime = pocTime;
    this.commitment = commitment;
    this.byteLength = byteLength;
  }


  public String toJson() {
    Gson gson = new GsonBuilder().serializeNulls().create();
    JsonObject json = getJsonObject(); // 调用原始的 getJsonObject 方法生成 JSON
    try {
      return gson.toJson(json);
    } catch (Exception e) {
      e.printStackTrace();
      return "{\"error\":\"Unable to serialize to JSON\"}";
    }
  }

  private TransactionDb transactionDb() {
    return Burst.getDbs().getTransactionDb();
  }

  public boolean isVerified() {
    return pocTime != null;
  }

  public void setPeer(Peer peer) {
    this.downloadedFrom = peer;
  }

  public Peer getPeer() {
    return this.downloadedFrom;
  }

  public void setByteLength(int length) {
    this.byteLength = length;
  }

  public int getByteLength() {
    return this.byteLength;
  }

  public int getVersion() {
    return version;
  }

  public int getTimestamp() {
    return timestamp;
  }

  public long getPreviousBlockId() {
    return previousBlockId;
  }
  public void setPreviousBlockId(long previousBlockId) {
    this.previousBlockId = previousBlockId;
  }


  public byte[] getGeneratorPublicKey() {
    return generatorPublicKey;
  }

  public byte[] getBlockHash() {
    return Crypto.sha256().digest(getBytes());
  }

  public byte[] getPreviousBlockHash() {
    return previousBlockHash;
  }

  public long getTotalAmountNQT() {
    return totalAmountNQT;
  }

  public long getTotalFeeNQT() {
    return totalFeeNQT;
  }

  public int getPayloadLength() {
    return payloadLength;
  }

  public byte[] getPayloadHash() {
    return payloadHash;
  }

  public byte[] getGenerationSignature() {
    return generationSignature;
  }

  public byte[] getBlockSignature() {
    return blockSignature;
  }

  public List<Transaction> getTransactions() {
    if (blockTransactions == null) {
      this.blockTransactions=transactionDb().findBlockTransactions(getId(), true);
      this.blockTransactions.forEach(transaction -> transaction.setBlock(this));
    }
    return blockTransactions;
  }

  public List<Transaction> getAllTransactions() {
    return Collections.unmodifiableList(transactionDb().findBlockTransactions(getId(), false));
  }

  public long getBaseTarget() {
    return baseTarget;
  }

  public long getCapacityBaseTarget() {
    long capacityBaseTarget = baseTarget;
    if(Burst.getFluxCapacitor().getValue(FluxValues.POC_PLUS, height)) {
      // Base target encoded as two floats, one for the commitment and the other the classical base target
      float capacityBaseTargetFloat = Float.intBitsToFloat((int)(baseTarget & 0xFFFFFFFFL));
      capacityBaseTarget = (long)capacityBaseTargetFloat;
    }
    return capacityBaseTarget;
  }

  public long getAverageCommitment() {
    if(Burst.getFluxCapacitor().getValue(FluxValues.POC_PLUS, height)) {
      // Base target encoded as two floats, one for the commitment and the other the classical base target
      float commitmentBaseTargetFloat = Float.intBitsToFloat((int)((baseTarget) >> 32));
      return (long)commitmentBaseTargetFloat;
    }
    return Constants.INITIAL_COMMITMENT;
  }

  public void setBaseTarget(long baseTargetCapacity, long averageCommitment) {
    this.baseTarget = ((long)Float.floatToIntBits((float)averageCommitment)) << 32 |
        ((long)Float.floatToIntBits((float)baseTargetCapacity));
  }

  public BigInteger getCumulativeDifficulty() {
    return cumulativeDifficulty;
  }

  public long getNextBlockId() {
    return nextBlockId.get();
  }

  public void setNextBlockId(long nextblockId) {
    this.nextBlockId.set(nextblockId);
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public long getId() {

    if (id.get() == 0) {
//      if (blockSignature == null) {
//        throw new IllegalStateException("Block is not signed yet");
//      }
//      byte[] hash = Crypto.sha256().digest(getBytes());
//      long longId = Convert.fullHashToId(hash);
//      id.set(longId);
//      stringId.set(Convert.toUnsignedLong(longId));
      id.set(this.height);
    }
    return id.get();
  }

  public String getStringId() {
    if (stringId.get() == null) {
      getId();
      if (stringId.get() == null) {
        stringId.set(Convert.toUnsignedLong(id.get()));
      }
    }
    return stringId.get();
  }

  public long getGeneratorId() {
    if (generatorId.get() == 0) {
      generatorId.set(Account.getId(generatorPublicKey));
    }
    return generatorId.get();
  }

  public Long getNonce() {
    return nonce;
  }

  public boolean equals(Object o) {
    return o instanceof Block && this.getId() == ((Block) o).getId();
  }

  public int hashCode() {
    return (int) (getId() ^ (getId() >>> 32));
  }

  public JsonObject getJsonObject() {

//  private final AtomicReference<List<Transaction>> blockTransactions = new AtomicReference<>();
    JsonObject json = new JsonObject();
    json.addProperty("version", version);
    json.addProperty("timestamp", timestamp);
    json.addProperty("previousBlock", Convert.toUnsignedLong(previousBlockId));
    json.addProperty("totalAmountNQT", totalAmountNQT);
    json.addProperty("totalFeeNQT", totalFeeNQT);
    json.addProperty("payloadLength", payloadLength);
    json.addProperty("payloadHash", Convert.toHexString(payloadHash));
    json.addProperty("generatorPublicKey", Convert.toHexString(generatorPublicKey));
    json.addProperty("generationSignature", Convert.toHexString(generationSignature));
    if (version > 1) {
      json.addProperty("previousBlockHash", Convert.toHexString(previousBlockHash));
    }
    json.addProperty("blockSignature", Convert.toHexString(blockSignature));
    JsonArray transactionsData = new JsonArray();
    getTransactions().forEach(transaction -> transactionsData.add(transaction.getJsonObject()));
    json.add("transactions", transactionsData);
    json.addProperty("nonce", Convert.toUnsignedLong(nonce));
    json.addProperty("baseTarget", Convert.toUnsignedLong(baseTarget));
    json.addProperty("blockATs", Convert.toHexString(blockATs));

    json.addProperty("nextBlockId", nextBlockId.get());
    json.addProperty("height", height);
    json.addProperty("id", id.get());
    json.addProperty("stringId", stringId.get());
    json.addProperty("generatorId", generatorId.get());
    json.addProperty("pocTime", pocTime);
    json.addProperty("commitment", commitment);
    json.addProperty("byteLength", byteLength);
    json.addProperty("cumulativeDifficulty", cumulativeDifficulty);
    return json;
  }

//  public com.couchbase.client.java.json.JsonObject getCouchBaseJsonObject() {
//    com.couchbase.client.java.json.JsonObject json = com.couchbase.client.java.json.JsonObject.create();
//    json.put("version", version);
//    json.put("timestamp", timestamp);
//    json.put("previousBlock", Convert.toUnsignedLong(previousBlockId));
//    json.put("totalAmountNQT", totalAmountNQT);
//    json.put("totalFeeNQT", totalFeeNQT);
//    json.put("payloadLength", payloadLength);
//    json.put("payloadHash", Convert.toHexString(payloadHash));
//    json.put("generatorPublicKey", Convert.toHexString(generatorPublicKey));
//    json.put("generationSignature", Convert.toHexString(generationSignature));
//    if (version > 1) {
//      json.put("previousBlockHash", Convert.toHexString(previousBlockHash));
//    }
//    json.put("blockSignature", Convert.toHexString(blockSignature));
//    JsonArray transactionsData = new JsonArray();
//    getTransactions().forEach(transaction -> transactionsData.add(transaction.getJsonObject()));
//    json.put("transactions", transactionsData);
//    json.put("nonce", Convert.toUnsignedLong(nonce));
//    json.put("baseTarget", Convert.toUnsignedLong(baseTarget));
//    json.put("blockATs", Convert.toHexString(blockATs));
//    return json;
//  }



  static Block parseBlock(JsonObject blockData, int height) throws BurstException.ValidationException {
    try {
      int version = JSON.getAsInt(blockData.get("version"));
      int timestamp = JSON.getAsInt(blockData.get("timestamp"));
      long previousBlock = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("previousBlock")));
      long totalAmountNQT = JSON.getAsLong(blockData.get("totalAmountNQT"));
      long totalFeeNQT = JSON.getAsLong(blockData.get("totalFeeNQT"));
      int payloadLength = JSON.getAsInt(blockData.get("payloadLength"));
      byte[] payloadHash = Convert.parseHexString(JSON.getAsString(blockData.get("payloadHash")));
      byte[] generatorPublicKey = Convert.parseHexString(JSON.getAsString(blockData.get("generatorPublicKey")));
      byte[] generationSignature = Convert.parseHexString(JSON.getAsString(blockData.get("generationSignature")));
      byte[] blockSignature = Convert.parseHexString(JSON.getAsString(blockData.get("blockSignature")));
      byte[] previousBlockHash = version == 1 ? null : Convert.parseHexString(JSON.getAsString(blockData.get("previousBlockHash")));
      long nonce = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("nonce")));
      long baseTarget = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("baseTarget")));

      if(Burst.getFluxCapacitor().getValue(FluxValues.POC_PLUS, height) && baseTarget == 0L) {
        throw new BurstException.NotValidException("Block received without a baseTarget");
      }

      SortedMap<Long, Transaction> blockTransactions = new TreeMap<>();
      JsonArray transactionsData = JSON.getAsJsonArray(blockData.get("transactions"));

      for (JsonElement transactionData : transactionsData) {
        Transaction transaction = Transaction.parseTransaction(JSON.getAsJsonObject(transactionData), height);
        if (transaction.getSignature() != null && blockTransactions.put(transaction.getId(), transaction) != null) {
          throw new BurstException.NotValidException("Block contains duplicate transactions: " + transaction.getStringId());
        }
      }

      byte[] blockATs = Convert.parseHexString(JSON.getAsString(blockData.get("blockATs")));
      return new Block(version, timestamp, previousBlock, totalAmountNQT, totalFeeNQT,
          payloadLength, payloadHash, generatorPublicKey, generationSignature, blockSignature,
          previousBlockHash, new ArrayList<>(blockTransactions.values()), nonce, blockATs, height, baseTarget);
    } catch (BurstException.ValidationException | RuntimeException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to parse block: {}", JSON.toJsonString(blockData));
      }
      throw e;
    }
  }


  public static Block parseBlock(JsonObject blockData) throws BurstException.ValidationException {
    try {

      int version = JSON.getAsInt(blockData.get("version"));
      int timestamp = JSON.getAsInt(blockData.get("timestamp"));
      int height = JSON.getAsInt(blockData.get("height"));
      long previousBlock = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("previousBlock")));
      long totalAmountNQT = JSON.getAsLong(blockData.get("totalAmountNQT"));
      long totalFeeNQT = JSON.getAsLong(blockData.get("totalFeeNQT"));
      int payloadLength = JSON.getAsInt(blockData.get("payloadLength"));
      byte[] payloadHash = Convert.parseHexString(JSON.getAsString(blockData.get("payloadHash")));
      byte[] generatorPublicKey = Convert.parseHexString(JSON.getAsString(blockData.get("generatorPublicKey")));
      byte[] generationSignature = Convert.parseHexString(JSON.getAsString(blockData.get("generationSignature")));
      byte[] blockSignature = Convert.parseHexString(JSON.getAsString(blockData.get("blockSignature")));
      byte[] previousBlockHash = version == 1 ? null : Convert.parseHexString(JSON.getAsString(blockData.get("previousBlockHash")));
      long nonce = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("nonce")));
      long baseTarget = Convert.parseUnsignedLong(JSON.getAsString(blockData.get("baseTarget")));


      long nextBlockId = JSON.getAsLong(blockData.get("nextBlockId"));
      long id = JSON.getAsLong(blockData.get("id"));
      String stringId = JSON.getAsString(blockData.get("stringId"));
      long generatorId = JSON.getAsLong(blockData.get("generatorId"));
      BigInteger pocTime = BigInteger.valueOf(JSON.getAsInt(blockData.get("pocTime")));
      BigInteger cumulativeDifficulty = BigInteger.valueOf(JSON.getAsInt(blockData.get("cumulativeDifficulty")));
      long commitment = JSON.getAsLong(blockData.get("commitment"));
      int byteLength = JSON.getAsInt(blockData.get("byteLength"));





//      if(Burst.getFluxCapacitor().getValue(FluxValues.POC_PLUS, height) && baseTarget == 0L) {
//        throw new BurstException.NotValidException("Block received without a baseTarget");
//      }

      SortedMap<Long, Transaction> blockTransactions = new TreeMap<>();
      JsonArray transactionsData = JSON.getAsJsonArray(blockData.get("transactions"));

      for (JsonElement transactionData : transactionsData) {
        Transaction transaction = Transaction.parseTransaction(JSON.getAsJsonObject(transactionData), height);
        if (transaction.getSignature() != null && blockTransactions.put(transaction.getId(), transaction) != null) {
          throw new BurstException.NotValidException("Block contains duplicate transactions: " + transaction.getStringId());
        }
      }

      byte[] blockATs = Convert.parseHexString(JSON.getAsString(blockData.get("blockATs")));
//      return new Block(version, timestamp, previousBlock, totalAmountNQT, totalFeeNQT,
//        payloadLength, payloadHash, generatorPublicKey, generationSignature, blockSignature,
//        previousBlockHash, new ArrayList<>(blockTransactions.values()), nonce, blockATs, height, baseTarget);

      return new Block(version, timestamp, previousBlock, totalAmountNQT, totalFeeNQT, payloadLength, payloadHash,
        generatorPublicKey, generationSignature, blockSignature, previousBlockHash,
        cumulativeDifficulty, baseTarget, nextBlockId, height, id, nonce, blockATs
        ,stringId,generatorId,pocTime,commitment,byteLength);

    } catch (BurstException.ValidationException | RuntimeException e) {

      if (logger.isDebugEnabled()) {
        logger.debug("Failed to parse block: {}", JSON.toJsonString(blockData));
      }
      throw e;

    }
  }


//  public static Block parseBlock(com.couchbase.client.java.json.JsonObject blockData) throws BurstException.ValidationException {
//    try {
//      blockData = blockData.getObject("block");
//      int version = blockData.getInt("version");
//      int timestamp = blockData.getInt("timestamp");
//      int height = blockData.getInt("height");
//      long previousBlock = Convert.parseUnsignedLong(blockData.getString("previousBlock"));
//      long totalAmountNQT = blockData.getLong("totalAmountNQT");
//      long totalFeeNQT = blockData.getLong("totalFeeNQT");
//      int payloadLength = blockData.getInt("payloadLength");
//      byte[] payloadHash = Convert.parseHexString(blockData.getString("payloadHash"));
//      byte[] generatorPublicKey = Convert.parseHexString(blockData.getString("generatorPublicKey"));
//      byte[] generationSignature = Convert.parseHexString(blockData.getString("generationSignature"));
//      byte[] blockSignature = Convert.parseHexString(blockData.getString("blockSignature"));
//
//      // Handle optional fields like "previousBlockHash"
//      byte[] previousBlockHash = null;
//      if (blockData.containsKey("previousBlockHash")) {
//        previousBlockHash = version == 1 ? null :Convert.parseHexString(blockData.getString("previousBlockHash"));
//      }
//
//      long nonce = Convert.parseUnsignedLong(blockData.getString("nonce"));
//      long baseTarget = Convert.parseUnsignedLong(blockData.getString("baseTarget"));
//
//      long nextBlockId = blockData.getLong("nextBlockId");
//      long id = blockData.getLong("id");
//      String stringId = blockData.getString("stringId");
//      long generatorId = blockData.getLong("generatorId");
//      BigInteger pocTime = blockData.getBigInteger("pocTime");
//      BigInteger cumulativeDifficulty = BigInteger.valueOf(blockData.getLong("cumulativeDifficulty"));
//      long commitment = blockData.getLong("commitment");
//      int byteLength = blockData.getInt("byteLength");
//
//      SortedMap<Long, Transaction> blockTransactions = new TreeMap<>();
//      com.couchbase.client.java.json.JsonArray transactionsData = blockData.getArray("transactions");
//
//      for (Object transactionData : transactionsData) {
//        if (transactionData instanceof JsonObject) {
//          JsonObject transactionObject = (JsonObject) transactionData;
//          Transaction transaction = Transaction.parseTransaction(transactionObject);
//          if (transaction.getSignature() != null && blockTransactions.put(transaction.getId(), transaction) != null) {
//            throw new BurstException.NotValidException("Block contains duplicate transactions: " + transaction.getStringId());
//          }
//        }
//      }
//
//      byte[] blockATs = Convert.parseHexString(blockData.getString("blockATs"));
//      return new Block(version, timestamp, previousBlock, totalAmountNQT, totalFeeNQT, payloadLength, payloadHash,
//        generatorPublicKey, generationSignature, blockSignature, previousBlockHash,
//        cumulativeDifficulty, baseTarget, nextBlockId, height, id, nonce, blockATs
//        ,stringId,generatorId,pocTime,commitment,byteLength);
//    } catch (BurstException.ValidationException | RuntimeException e) {
//      if (logger.isDebugEnabled()) {
//        logger.debug("Failed to parse block: {}", blockData.toString());
//      }
//      throw e;
//    }
//  }


  public byte[] getBytes() {
    ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 8 + 4 + (version < 3 ? (4 + 4) : (8 + 8)) + 4
        + 32 + 32 + (32 + 32) + 8 + (blockATs != null ? blockATs.length : 0) + 64);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putInt(version);
    buffer.putInt(timestamp);
    buffer.putLong(previousBlockId);
    buffer.putInt(getTransactions().size());
    if (version < 3) {
      buffer.putInt((int) (totalAmountNQT / Constants.ONE_BURST));
      buffer.putInt((int) (totalFeeNQT / Constants.ONE_BURST));
    } else {
      buffer.putLong(totalAmountNQT);
      buffer.putLong(totalFeeNQT);
    }
    buffer.putInt(payloadLength);
    buffer.put(payloadHash);
    buffer.put(generatorPublicKey);
    buffer.put(generationSignature);
    if (version > 1) {
      buffer.put(previousBlockHash);
    }
    buffer.putLong(nonce);
    if (blockATs != null)
      buffer.put(blockATs);
    if (buffer.limit() - buffer.position() < blockSignature.length)
      logger.error("Something is too large here - buffer should have {} bytes left but only has {}",
                   blockSignature.length,
                   (buffer.limit() - buffer.position()));
    buffer.put(blockSignature);
    return buffer.array();
  }

  void sign(String secretPhrase) {
    if (blockSignature != null) {
      throw new IllegalStateException("Block already signed");
    }
    blockSignature = new byte[64];
    byte[] data = getBytes();
    byte[] data2 = new byte[data.length - 64];
    System.arraycopy(data, 0, data2, 0, data2.length);
    blockSignature = Crypto.sign(data2, secretPhrase);
  }

  public byte[] getBlockATs() {
    return blockATs;
  }

  public BigInteger getPocTime() {
    return pocTime;
  }

  public void setPocTime(BigInteger pocTime) {
    this.pocTime = pocTime;
  }

  public long getCommitment() {
    return this.commitment;
  }

  public void setCommitment(long commitment) {
    this.commitment = commitment;
  }

  public void setBaseTarget(long baseTarget) {
    this.baseTarget = baseTarget;
  }

  public void setCumulativeDifficulty(BigInteger cumulativeDifficulty) {
    this.cumulativeDifficulty = cumulativeDifficulty;
  }
}
