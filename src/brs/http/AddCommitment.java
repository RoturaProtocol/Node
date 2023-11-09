package brs.http;

import brs.Account;
import brs.Attachment;
import brs.Blockchain;
import brs.Burst;
import brs.BurstException;
import brs.fluxcapacitor.FluxValues;
import brs.services.AccountService;
import brs.services.ParameterService;
import brs.util.Convert;

import com.google.gson.JsonElement;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.INCORRECT_FEE;
import static brs.http.JSONResponses.NOT_ENOUGH_FUNDS;
import static brs.http.common.Parameters.AMOUNT_NQT_PARAMETER;

public final class AddCommitment extends CreateTransaction {

  private final ParameterService parameterService;
  private final Blockchain blockchain;

  public AddCommitment(ParameterService parameterService, Blockchain blockchain, AccountService accountService, APITransactionManager apiTransactionManager) {
    super(new APITag[] {APITag.ACCOUNTS, APITag.MINING, APITag.CREATE_TRANSACTION}, apiTransactionManager, AMOUNT_NQT_PARAMETER);
    this.parameterService = parameterService;
    this.blockchain = blockchain;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) throws BurstException {
    final Account account = parameterService.getSenderAccount(req);
    long amountNQT = ParameterParser.getAmountNQT(req);

    long minimumFeeNQT = Burst.getFluxCapacitor().getValue(FluxValues.FEE_QUANT);
    long feeNQT = ParameterParser.getFeeNQT(req);
    if (feeNQT < minimumFeeNQT) {
      return INCORRECT_FEE;
    }

    try {
      if (Convert.safeAdd(amountNQT, feeNQT) > account.getUnconfirmedBalanceNQT()) {
        return NOT_ENOUGH_FUNDS;
      }
    } catch (ArithmeticException e) {
      return NOT_ENOUGH_FUNDS;
    }

    Attachment attachment = new Attachment.CommitmentAdd(amountNQT, blockchain.getHeight());
    System.out.println("AddCommitment_account");
    System.out.println(account);
    System.out.println(amountNQT);
    System.out.println(feeNQT);
    return createTransaction(req, account, attachment);
  }

}
