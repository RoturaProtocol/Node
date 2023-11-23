package brs.http;

import brs.*;
import brs.fluxcapacitor.FluxValues;
import brs.services.AccountService;
import brs.services.ParameterService;
import brs.util.Convert;
import com.google.gson.JsonElement;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.INCORRECT_FEE;
import static brs.http.JSONResponses.UNABLE_TO_BE_REPESTED_DEBT;
import static brs.http.JSONResponses.COMMIT_NOT_ENOUGH_FUNDS;
import static brs.http.JSONResponses.NOT_ENOUGH_FUNDS;
import static brs.http.common.Parameters.AMOUNT_NQT_PARAMETER;

public final class AddDebt extends CreateTransaction {

  private final ParameterService parameterService;
  private final Blockchain blockchain;

  public AddDebt(ParameterService parameterService, Blockchain blockchain, AccountService accountService, APITransactionManager apiTransactionManager) {
    super(new APITag[] {APITag.ACCOUNTS, APITag.MINING, APITag.CREATE_TRANSACTION}, apiTransactionManager, AMOUNT_NQT_PARAMETER);
    this.parameterService = parameterService;
    this.blockchain = blockchain;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) throws BurstException {
    final Account account = parameterService.getSenderAccount(req);
    //amountNQT抵押多少tura
    long amountNQT = ParameterParser.getAmountNQT(req);

    double turaPrice = 0.005;
    double tusdPrice = 1;
    double turaTotusdRate = 0.5;

    long minimumFeeNQT = Burst.getFluxCapacitor().getValue(FluxValues.FEE_QUANT);

    int height = blockchain.getHeight();
    long committedAmountNOT = Burst.getBlockchain().getCommittedAmount(account.getId(), height+Constants.COMMITMENT_WAIT, height, null);


    //错误处理
    if (committedAmountNOT<amountNQT){
      return COMMIT_NOT_ENOUGH_FUNDS;
    }
    if (account.getPledgeBalanceNQT()>0){
      return UNABLE_TO_BE_REPESTED_DEBT;
    }

    long feeNQT = ParameterParser.getFeeNQT(req);
    if (feeNQT < minimumFeeNQT) {
      return INCORRECT_FEE;
    }
    try {
      if (feeNQT > account.getUnconfirmedBalanceNQT()) {
        return NOT_ENOUGH_FUNDS;
      }
    } catch (ArithmeticException e) {
      return NOT_ENOUGH_FUNDS;
    }

    Attachment attachment = new Attachment.DebtAdd(amountNQT,turaPrice,tusdPrice,turaTotusdRate, blockchain.getHeight());
    return createTransaction(req, account, attachment);
  }

}
