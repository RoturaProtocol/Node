package brs.http;

import brs.Account;
import brs.BurstException;
import brs.services.ParameterService;
import com.google.gson.JsonElement;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.STABLECOIN_NOT_ENOUGH_FUNDS;
import static brs.http.common.Parameters.AMOUNT_NQT_PARAMETER;
import static brs.http.common.Parameters.RECIPIENT_PARAMETER;

final class SendUSDMoney extends CreateTransaction {

  private final ParameterService parameterService;

  SendUSDMoney(ParameterService parameterService, APITransactionManager apiTransactionManager) {
    super(new APITag[]{APITag.ACCOUNTS, APITag.TRANSACTIONS, APITag.CREATE_TRANSACTION}, apiTransactionManager, RECIPIENT_PARAMETER, AMOUNT_NQT_PARAMETER);
    this.parameterService = parameterService;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) throws BurstException {
    long recipient = ParameterParser.getRecipientId(req);
    long amountNQT = ParameterParser.getAmountNQT(req);
    Account account = parameterService.getSenderAccount(req);

//    if(amountNQT/100000000.0 > account.getStablecoinBalance()){
//      return STABLECOIN_NOT_ENOUGH_FUNDS;
//    }
    if(amountNQT/10000.0 > account.getStablecoinBalance()){
      return STABLECOIN_NOT_ENOUGH_FUNDS;
    }

    return createTransactionTUSD(req, account, recipient, amountNQT);
  }

}
