package brs.db.store;

import brs.Account;
import brs.Block;
import brs.Transaction;
import brs.schema.tables.records.BlockRecord;
import brs.schema.tables.records.TransactionRecord;
import com.couchbase.client.java.json.JsonObject;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.Collection;
import java.util.List;

/**
 * Store for both BlockchainImpl and BlockchainProcessorImpl
 */

public interface BlockchainStore {


  Collection<Block> getBlocks(int from, int to);

  Collection<Block> getBlocks(Account account, int timestamp, int from, int to);

  int getBlocksCount(long accountId, int from, int to);

//  Collection<Block> getBlocks(Result<BlockRecord> blockRecords);
  Collection<Block> getBlocks(List<JsonObject> result);


  Collection<Long> getBlockIdsAfter(long blockId, int limit);

  Collection<Block> getBlocksAfter(long blockId, int limit);

  int getTransactionCount();

  Collection<Transaction> getAllTransactions();

  long getAtBurnTotal();

  Collection<Transaction> getTransactions(Account account, int numberOfConfirmations, byte type, byte subtype,
                                                 int blockTimestamp, int from, int to, boolean includeIndirectIncoming);

  Collection<Long> getTransactionIds(Long sender, Long recipient, int numberOfConfirmations, byte type, byte subtype,
      int blockTimestamp, int from, int to, boolean includeIndirectIncoming);

//  Collection<Transaction> getTransactions(DSLContext ctx, Result<TransactionRecord> rs);

  void addBlock(Block block);
  void addGenesisAccount();

  Collection<Block> getLatestBlocks(int amountBlocks);

  long getCommittedAmount(long accountId, int height, int endHeight, Transaction skipTransaction);
}
