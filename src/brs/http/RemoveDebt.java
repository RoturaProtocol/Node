package brs.http;

import brs.*;
import brs.services.AccountService;
import brs.services.ParameterService;
import com.google.gson.JsonElement;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.ERROR_NOT_ALLOWED;
import static brs.http.JSONResponses.NOT_ENOUGH_FUNDS;

import static brs.http.JSONResponses.STABLECOIN_NOT_ENOUGH_FUNDS;
import static brs.http.JSONResponses.REPAY_ALL_DEBT;
import static brs.http.common.Parameters.AMOUNT_NQT_PARAMETER;

public final class RemoveDebt extends CreateTransaction {

  private final ParameterService parameterService;
  private final Blockchain blockchain;

  public RemoveDebt(ParameterService parameterService, Blockchain blockchain, AccountService accountService, APITransactionManager apiTransactionManager) {
    super(new APITag[] {APITag.ACCOUNTS, APITag.MINING, APITag.CREATE_TRANSACTION}, apiTransactionManager, AMOUNT_NQT_PARAMETER);
    this.parameterService = parameterService;
    this.blockchain = blockchain;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) throws BurstException {
    final Account account = parameterService.getSenderAccount(req);
    //amountNQT是需要赎回的tusd
    long amountNQT = ParameterParser.getAmountNQT(req);


    if (amountNQT <= 0){
      return NOT_ENOUGH_FUNDS;
    }

//    int nBlocksMined = blockchain.getBlocksCount(account.getId(), blockchain.getHeight() - Constants.MAX_ROLLBACK, blockchain.getHeight());
//    if(nBlocksMined > 0) {
//      // need to wait since the last block mined to remove any commitment
//      return ERROR_NOT_ALLOWED;
//    }

    double stablecoinBalance = account.getStablecoinBalance();



//    if (stablecoinBalance < amountNQT/100000000.0){
//      return STABLECOIN_NOT_ENOUGH_FUNDS;
//    }
    if (stablecoinBalance < amountNQT/10000.0){
      return STABLECOIN_NOT_ENOUGH_FUNDS;
    }
//    if(account.getDebtStablecoinBalance() != amountNQT/100000000.0){
//      return REPAY_ALL_DEBT;
//    }
    if(account.getDebtStablecoinBalance() != amountNQT/10000.0){
      return REPAY_ALL_DEBT;
    }
    double turaPrice = 0.005;
    double tusdPrice = 1;
    double turaTotusdRate = 0.5;
//    long committedAmountNQT = blockchain.getCommittedAmount(account.getId(), blockchain.getHeight(), blockchain.getHeight(), null);
//    if (committedAmountNQT < tusdCount) {
//      return NOT_ENOUGH_FUNDS;
//    }
    Attachment attachment = new Attachment.DebtRemove(amountNQT,turaPrice,tusdPrice,turaTotusdRate, blockchain.getHeight());
    return createTransaction(req, account, attachment);
  }

}
