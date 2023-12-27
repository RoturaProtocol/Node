//package brs;
//
//import brs.Appendix.AbstractAppendix;
//import brs.crypto.Crypto;
//import brs.fluxcapacitor.FluxValues;
//import brs.transactionduplicates.TransactionDuplicationKey;
//import brs.util.Convert;
//import brs.util.JSON;
//import com.google.gson.JsonObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.Serializable;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.security.MessageDigest;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class TransactionSpark implements Comparable<TransactionSpark>, Serializable {
//
//  private static final Logger logger = LoggerFactory.getLogger(TransactionSpark.class);
//
//  private final short deadline;
//  private final byte[] senderPublicKey;
//  private final long recipientId;
//  private final long amountNQT;
//  private final long feeNQT;
//  private final String referencedTransactionFullHash;
//  private final TransactionType type;
//  private final int ecBlockHeight;
//  private final long ecBlockId;
//  private final byte version;
//  private final int timestamp;
//  private final Attachment.AbstractAttachment attachment;
//  private final Appendix.Message message;
//  private final Appendix.EncryptedMessage encryptedMessage;
//  private final Appendix.EncryptToSelfMessage encryptToSelfMessage;
//  private final Appendix.PublicKeyAnnouncement publicKeyAnnouncement;
//
//  private final List<AbstractAppendix> appendages;
//  private final int appendagesSize;
//
//  private final AtomicInteger height = new AtomicInteger();
//  private final AtomicLong blockId = new AtomicLong();
//  private final AtomicReference<Block> block = new AtomicReference<>();
//  private final AtomicReference<byte[]> signature = new AtomicReference<>();
//  private final AtomicInteger blockTimestamp = new AtomicInteger();
//  private final AtomicLong id = new AtomicLong();
//  private final AtomicReference<String> stringId = new AtomicReference<>();
//  private final AtomicLong senderId = new AtomicLong();
//  private final AtomicReference<String> fullHash = new AtomicReference<>();
//
//
//
//  private TransactionSpark(short deadline, byte[] senderPublicKey, long recipientId, long amountNQT, long feeNQT, String referencedTransactionFullHash, TransactionType type
//    , int ecBlockHeight, long ecBlockId, byte version, int timestamp, Attachment.AbstractAttachment attachment, Appendix.Message message, Appendix.EncryptedMessage encryptedMessage
//    , Appendix.EncryptToSelfMessage encryptToSelfMessage, Appendix.PublicKeyAnnouncement publicKeyAnnouncement, List<AbstractAppendix> appendages, int appendagesSize
//  ,) throws BurstException.NotValidException {
//    this.deadline = deadline;
//    this.senderPublicKey = senderPublicKey;
//    this.recipientId = recipientId;
//    this.amountNQT = amountNQT;
//    this.feeNQT = feeNQT;
//    this.referencedTransactionFullHash = referencedTransactionFullHash;
//    this.type = type;
//    this.ecBlockHeight = ecBlockHeight;
//    this.ecBlockId = ecBlockId;
//    this.version = version;
//    this.timestamp = timestamp;
//    this.attachment = attachment;
//    this.message = message;
//    this.encryptedMessage = encryptedMessage;
//    this.encryptToSelfMessage = encryptToSelfMessage;
//    this.publicKeyAnnouncement = publicKeyAnnouncement;
//    this.appendages = appendages;
//    this.appendagesSize = appendagesSize;
//
//
//  }
//
//  public short getDeadline() {
//    return deadline;
//  }
//
//  public byte[] getSenderPublicKey() {
//    return senderPublicKey;
//  }
//
//  public long getRecipientId() {
//    return recipientId;
//  }
//
//  public long getAmountNQT() {
//    return amountNQT;
//  }
//
//  public long getFeeNQT() {
//    return feeNQT;
//  }
//
//  public long getFeeNQTPerByte() {
//    return feeNQT/getSize();
//  }
//
//  public String getReferencedTransactionFullHash() {
//    return referencedTransactionFullHash;
//  }
//
//  public int getHeight() {
//    return height.get();
//  }
//
//  public void setHeight(int height) {
//    this.height.set(height);
//  }
//
//  public byte[] getSignature() {
//    return signature.get();
//  }
//
//  public TransactionType getType() {
//    return type;
//  }
//
//  public byte getVersion() {
//    return version;
//  }
//
//  public long getBlockId() {
//    return blockId.get();
//  }
//
//  public void setBlock(Block block) {
//    this.block.set(block);
//    this.blockId.set(block.getId());
//    this.height.set(block.getHeight());
//    this.blockTimestamp.set(block.getTimestamp());
//  }
//
//  void unsetBlock() {
//    this.block.set(null);
//    this.blockId.set(0);
//    this.blockTimestamp.set(-1);
//    // must keep the height set, as transactions already having been included in a popped-off block before
//    // get priority when sorted for inclusion in a new block
//  }
//
//  public int getTimestamp() {
//    return timestamp;
//  }
//
//  public int getBlockTimestamp() {
//    return blockTimestamp.get();
//  }
//
//  public int getExpiration() {
//    return timestamp + deadline * 60;
//  }
//
//  public Attachment getAttachment() {
//    return attachment;
//  }
//
//  public List<AbstractAppendix> getAppendages() {
//    return appendages;
//  }
//
//  public long getId() {
//    if (id.get() == 0) {
//      if (signature.get() == null && type.isSigned()) {
//        throw new IllegalStateException("Transaction is not signed yet");
//      }
//      byte[] hash;
//      if (useNQT()) {
//        byte[] data = zeroSignature(getBytes());
//        byte[] signatureHash = Crypto.sha256().digest(signature.get() != null ? signature.get() : new byte[64]);
//        MessageDigest digest = Crypto.sha256();
//        digest.update(data);
//        hash = digest.digest(signatureHash);
//      } else {
//        hash = Crypto.sha256().digest(getBytes());
//      }
//      long longId = Convert.fullHashToId(hash);
//      id.set(longId);
//      stringId.set(Convert.toUnsignedLong(longId));
//      fullHash.set(Convert.toHexString(hash));
//    }
//    return id.get();
//  }
//
//  public String getStringId() {
//    if (stringId.get() == null) {
//      getId();
//      if (stringId.get() == null) {
//        stringId.set(Convert.toUnsignedLong(id.get()));
//      }
//    }
//    return stringId.get();
//  }
//
//  public String getFullHash() {
//    if (fullHash.get() == null) {
//      getId();
//    }
//    return fullHash.get();
//  }
//
//  public long getSenderId() {
//    if (senderId.get() == 0 && (type == null || type.isSigned())) {
//      senderId.set(Account.getId(senderPublicKey));
//    }
//    return senderId.get();
//  }
//
//  public Appendix.Message getMessage() {
//    return message;
//  }
//
//  public Appendix.EncryptedMessage getEncryptedMessage() {
//    return encryptedMessage;
//  }
//
//  public Appendix.EncryptToSelfMessage getEncryptToSelfMessage() {
//    return encryptToSelfMessage;
//  }
//
//  public Appendix.PublicKeyAnnouncement getPublicKeyAnnouncement() {
//    return publicKeyAnnouncement;
//  }
//
//  public byte[] getBytes() {
//    try {
//      ByteBuffer buffer = ByteBuffer.allocate(getSize());
//      buffer.order(ByteOrder.LITTLE_ENDIAN);
//      buffer.put(type.getType());
//      buffer.put((byte) ((version << 4) | ( type.getSubtype() & 0xff ) ));
//      buffer.putInt(timestamp);
//      buffer.putShort(deadline);
//      if(type.isSigned() || !Burst.getFluxCapacitor().getValue(FluxValues.AT_FIX_BLOCK_4)) {
//        buffer.put(senderPublicKey);
//      } else {
//        buffer.putLong(senderId.get());
//        buffer.put(new byte[24]);
//      }
//      buffer.putLong(type.hasRecipient() ? recipientId : Genesis.CREATOR_ID);
//      if (useNQT()) {
//        buffer.putLong(amountNQT);
//        buffer.putLong(feeNQT);
//        if (referencedTransactionFullHash != null) {
//          buffer.put(Convert.parseHexString(referencedTransactionFullHash));
//        } else {
//          buffer.put(new byte[32]);
//        }
//      } else {
//        buffer.putInt((int) (amountNQT / Constants.ONE_BURST));
//        buffer.putInt((int) (feeNQT / Constants.ONE_BURST));
//        if (referencedTransactionFullHash != null) {
//          buffer.putLong(Convert.fullHashToId(Convert.parseHexString(referencedTransactionFullHash)));
//        } else {
//          buffer.putLong(0L);
//        }
//      }
//      buffer.put(signature.get() != null ? signature.get() : new byte[64]);
//      if (version > 0) {
//        buffer.putInt(getFlags());
//        buffer.putInt(ecBlockHeight);
//        buffer.putLong(ecBlockId);
//      }
//      appendages.forEach(appendage -> appendage.putBytes(buffer));
//      return buffer.array();
//    } catch (RuntimeException e) {
//      if (logger.isDebugEnabled()) {
//        logger.debug("Failed to get transaction bytes for transaction: {}", JSON.toJsonString(getJsonObject()));
//      }
//      throw e;
//    }
//  }
//
//  public static TransactionSpark parseTransaction(byte[] bytes) throws BurstException.ValidationException {
//    try {
//      ByteBuffer buffer = ByteBuffer.wrap(bytes);
//      buffer.order(ByteOrder.LITTLE_ENDIAN);
//      byte type = buffer.get();
//      byte subtype = buffer.get();
//      byte version = (byte) ((subtype & 0xF0) >> 4);
//      subtype = (byte) (subtype & 0x0F);
//      int timestamp = buffer.getInt();
//      short deadline = buffer.getShort();
//      byte[] senderPublicKey = new byte[32];
//      buffer.get(senderPublicKey);
//      long recipientId = buffer.getLong();
//
////      if (subtype == 101 || subtype == 102){
////        buffer.getDouble();
////      }
//
//
//      long amountNQT = buffer.getLong();
//      long feeNQT = buffer.getLong();
//      String referencedTransactionFullHash = null;
//      byte[] referencedTransactionFullHashBytes = new byte[32];
//      buffer.get(referencedTransactionFullHashBytes);
//      if (Convert.emptyToNull(referencedTransactionFullHashBytes) != null) {
//        referencedTransactionFullHash = Convert.toHexString(referencedTransactionFullHashBytes);
//      }
//      byte[] signature = new byte[64];
//      buffer.get(signature);
//      signature = Convert.emptyToNull(signature);
//      int flags = 0;
//      int ecBlockHeight = 0;
//      long ecBlockId = 0;
//      if (version > 0) {
//        flags = buffer.getInt();
//        ecBlockHeight = buffer.getInt();
//        ecBlockId = buffer.getLong();
//      }
//      TransactionType transactionType = TransactionType.findTransactionType(type, subtype);
//      Attachment.AbstractAttachment abstractAttachment = transactionType.parseAttachment(buffer, version);
////      Transaction.Builder builder = new Transaction.Builder(version, senderPublicKey, amountNQT, feeNQT,
////                                                                            timestamp, deadline, transactionType.parseAttachment(buffer, version))
//      TransactionSpark.Builder builder = new TransactionSpark.Builder(version, senderPublicKey, amountNQT, feeNQT,
//                                                                            timestamp, deadline, abstractAttachment)
//          .referencedTransactionFullHash(referencedTransactionFullHash)
//          .signature(signature)
//          .ecBlockHeight(ecBlockHeight)
//          .ecBlockId(ecBlockId);
//
//      if (transactionType.hasRecipient()) {
//        builder.recipientId(recipientId);
//      }
////      System.out.println("parseTransaction***");
////      System.out.println(type);
////      System.out.println(subtype);
////      System.out.println(recipientId);
////      System.out.println(referencedTransactionFullHashBytes);
////      System.out.println(flags);
////      System.out.println(ecBlockHeight);
////      System.out.println(ecBlockId);
//      transactionType.parseAppendices(builder, flags, version, buffer);
//      return builder.build();
//    } catch (BurstException.NotValidException|RuntimeException e) {
//      if (logger.isDebugEnabled()) {
//        logger.debug("Failed to parse transaction bytes: {}", Convert.toHexString(bytes));
//      }
//      throw e;
//    }
//  }
//
//  public byte[] getUnsignedBytes() {
//    return zeroSignature(getBytes());
//  }
//
//  public JsonObject getJsonObject() {
//    JsonObject json = new JsonObject();
//    json.addProperty("type", type.getType());
//    json.addProperty("subtype", type.getSubtype());
//    json.addProperty("timestamp", timestamp);
//    json.addProperty("deadline", deadline);
//    json.addProperty("senderPublicKey", Convert.toHexString(senderPublicKey));
//    if (type.hasRecipient()) {
//      json.addProperty("recipient", Convert.toUnsignedLong(recipientId));
//    }
//    json.addProperty("amountNQT", amountNQT);
//    json.addProperty("feeNQT", feeNQT);
//    if (referencedTransactionFullHash != null) {
//      json.addProperty("referencedTransactionFullHash", referencedTransactionFullHash);
//    }
//    json.addProperty("ecBlockHeight", ecBlockHeight);
//    json.addProperty("ecBlockId", Convert.toUnsignedLong(ecBlockId));
//    json.addProperty("signature", Convert.toHexString(signature.get()));
//    JsonObject attachmentJSON = new JsonObject();
//    appendages.forEach(appendage -> JSON.addAll(attachmentJSON, appendage.getJsonObject()));
//    json.add("attachment", attachmentJSON);
//    json.addProperty("version", version);
//    return json;
//  }
//
//  static TransactionSpark parseTransaction(JsonObject transactionData, int height) throws BurstException.NotValidException {
//    try {
//      byte type = JSON.getAsByte(transactionData.get("type"));
//      byte subtype = JSON.getAsByte(transactionData.get("subtype"));
//      int timestamp = JSON.getAsInt(transactionData.get("timestamp"));
//      short deadline = JSON.getAsShort(transactionData.get("deadline"));
//      byte[] senderPublicKey = Convert.parseHexString(JSON.getAsString(transactionData.get("senderPublicKey")));
//      long amountNQT = JSON.getAsLong(transactionData.get("amountNQT"));
//      long feeNQT = JSON.getAsLong(transactionData.get("feeNQT"));
//      String referencedTransactionFullHash = JSON.getAsString(transactionData.get("referencedTransactionFullHash"));
//      byte[] signature = Convert.parseHexString(JSON.getAsString(transactionData.get("signature")));
//      byte version = JSON.getAsByte(transactionData.get("version"));
//      JsonObject attachmentData = JSON.getAsJsonObject(transactionData.get("attachment"));
//
//      TransactionType transactionType = TransactionType.findTransactionType(type, subtype);
//      if (transactionType == null) {
//        throw new BurstException.NotValidException("Invalid transaction type: " + type + ", " + subtype);
//      }
//      TransactionSpark.Builder builder = new Builder(version, senderPublicKey,
//                                                                            amountNQT, feeNQT, timestamp, deadline,
//                                                                            transactionType.parseAttachment(attachmentData))
//          .referencedTransactionFullHash(referencedTransactionFullHash)
//          .signature(signature)
//          .height(height);
//      if (transactionType.hasRecipient()) {
//        long recipientId = Convert.parseUnsignedLong(JSON.getAsString(transactionData.get("recipient")));
//        builder.recipientId(recipientId);
//      }
//
//      transactionType.parseAppendices(builder, attachmentData);
//
//      if (version > 0) {
//        builder.ecBlockHeight(JSON.getAsInt(transactionData.get("ecBlockHeight")));
//        builder.ecBlockId(Convert.parseUnsignedLong(JSON.getAsString(transactionData.get("ecBlockId"))));
//      }
//      return builder.build();
//    } catch (BurstException.NotValidException|RuntimeException e) {
//      if (logger.isDebugEnabled()) {
//        logger.debug("Failed to parse transaction: {}", JSON.toJsonString(transactionData));
//      }
//      throw e;
//    }
//  }
//
//
//  public int getECBlockHeight() {
//    return ecBlockHeight;
//  }
//
//  public long getECBlockId() {
//    return ecBlockId;
//  }
//
//  public void sign(String secretPhrase) {
//    if (signature.get() != null) {
//      throw new IllegalStateException("Transaction already signed");
//    }
//    signature.set(Crypto.sign(getBytes(), secretPhrase));
//  }
//
//  @Override
//  public boolean equals(Object o) {
//    return o instanceof TransactionSpark && this.getId() == ((TransactionSpark)o).getId();
//  }
//
//  @Override
//  public int hashCode() {
//    return (int)(getId() ^ (getId() >>> 32));
//  }
//
//  public int compareTo(TransactionSpark other) {
//    return Long.compare(this.getId(), other.getId());
//  }
//
//  public boolean verifySignature() {
//    if(signature.get() == null) {
//      return false;
//    }
//    byte[] data = zeroSignature(getBytes());
//    return Crypto.verify(signature.get(), data, senderPublicKey, useNQT());
//  }
//
//  public int getSize() {
//    return signatureOffset() + 64  + (version > 0 ? 4 + 4 + 8 : 0) + appendagesSize;
//  }
//
//  public int getAppendagesSize() {
//    return appendagesSize;
//  }
//
//  private int signatureOffset() {
////    if (isDebtTransaction()){
////      return 1 + 1 + 4 + 2 + 32 + 8 + (useNQT() ? 8+8+8+ 8 + 8 + 32 : 8+8+8+ 4 + 4 + 8);
////    }
//    return 1 + 1 + 4 + 2 + 32 + 8 + (useNQT() ? 8 + 8 + 32 : 4 + 4 + 8);
//  }
//
//  private boolean useNQT() {
//    return this.height.get() > Constants.NQT_BLOCK
//        && (this.height.get() < Integer.MAX_VALUE
//            || Burst.getBlockchain().getHeight() >= Constants.NQT_BLOCK);
//  }
//
//  private byte[] zeroSignature(byte[] data) {
//    int start = signatureOffset();
//    for (int i = start; i < start + 64; i++) {
//      data[i] = 0;
//    }
//    return data;
//  }
//
//  private boolean isDebtTransaction(){
//      if (this.type.getSubtype() == 101 || type.getSubtype() == 102 ){
//        return true;
//    }
//      return false;
//  }
//
//  private int getFlags() {
//    int flags = 0;
//    int position = 1;
//    if (message != null) {
//      flags |= position;
//    }
//    position <<= 1;
//    if (encryptedMessage != null) {
//      flags |= position;
//    }
//    position <<= 1;
//    if (publicKeyAnnouncement != null) {
//      flags |= position;
//    }
//    position <<= 1;
//    if (encryptToSelfMessage != null) {
//      flags |= position;
//    }
//    return flags;
//  }
//
//  public TransactionDuplicationKey getDuplicationKey() {
//    return type.getDuplicationKey(this);
//  }
//}
