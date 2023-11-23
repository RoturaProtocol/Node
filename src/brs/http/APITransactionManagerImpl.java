package brs.http;

import brs.*;
import brs.Appendix.EncryptToSelfMessage;
import brs.Appendix.EncryptedMessage;
import brs.Appendix.Message;
import brs.Appendix.PublicKeyAnnouncement;
import brs.Transaction.Builder;
import brs.crypto.Crypto;
import brs.crypto.EncryptedData;
import brs.fluxcapacitor.FluxValues;
import brs.http.common.Parameters;
import brs.services.AccountService;
import brs.services.ParameterService;
import brs.services.TransactionService;
import brs.util.Convert;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.*;
import static brs.http.common.Parameters.*;
import static brs.http.common.ResultFields.*;
import static brs.http.common.ResultFields.FULL_HASH_RESPONSE;



public class APITransactionManagerImpl implements APITransactionManager {

  private final ParameterService parameterService;
  private final TransactionProcessor transactionProcessor;
  private final Blockchain blockchain;
  private final AccountService accountService;
  private final TransactionService transactionService;


  public APITransactionManagerImpl(ParameterService parameterService, TransactionProcessor transactionProcessor, Blockchain blockchain, AccountService accountService,
      TransactionService transactionService) {
    this.parameterService = parameterService;
    this.transactionProcessor = transactionProcessor;
    this.blockchain = blockchain;
    this.accountService = accountService;
    this.transactionService = transactionService;
  }

  @Override
  public JsonElement createTransaction(HttpServletRequest req, Account senderAccount, Long recipientId, long amountNQT, Attachment attachment, long minimumFeeNQT) throws BurstException {
    int blockchainHeight = blockchain.getHeight();
    String deadlineValue = req.getParameter(DEADLINE_PARAMETER);
    //无传入
    String referencedTransactionFullHash = Convert.emptyToNull(req.getParameter(REFERENCED_TRANSACTION_FULL_HASH_PARAMETER));
    //无传入
    String referencedTransactionId = Convert.emptyToNull(req.getParameter(REFERENCED_TRANSACTION_PARAMETER));
    //无传入
    String secretPhrase = Convert.emptyToNull(req.getParameter(SECRET_PHRASE_PARAMETER));

    String publicKeyValue = Convert.emptyToNull(req.getParameter(PUBLIC_KEY_PARAMETER));

    String recipientPublicKeyValue = Convert.emptyToNull(ParameterParser.getRecipientPublicKey(req));
    //false
    boolean broadcast = !Parameters.isFalse(req.getParameter(BROADCAST_PARAMETER));

    //encryptedMessage is null
    EncryptedMessage encryptedMessage = null;
    //attachment.getTransactionType().hasRecipient() is false
    if (attachment.getTransactionType().hasRecipient()) {
      EncryptedData encryptedData = parameterService.getEncryptedMessage(req, accountService.getAccount(recipientId), Convert.parseHexString(recipientPublicKeyValue));
      if (encryptedData != null) {
        encryptedMessage = new EncryptedMessage(encryptedData, !Parameters.isFalse(req.getParameter(MESSAGE_TO_ENCRYPT_IS_TEXT_PARAMETER)), blockchainHeight);
      }
    }

    //encryptToSelfMessage is null
    EncryptToSelfMessage encryptToSelfMessage = null;
    EncryptedData encryptedToSelfData = parameterService.getEncryptToSelfMessage(req);
    if (encryptedToSelfData != null) {
      encryptToSelfMessage = new EncryptToSelfMessage(encryptedToSelfData, !Parameters.isFalse(req.getParameter(MESSAGE_TO_ENCRYPT_TO_SELF_IS_TEXT_PARAMETER)), blockchainHeight);
    }
    //message is null
    Message message = null;
    String messageValue = Convert.emptyToNull(req.getParameter(MESSAGE_PARAMETER));
    if (messageValue != null) {
      boolean messageIsText = Burst.getFluxCapacitor().getValue(FluxValues.DIGITAL_GOODS_STORE, blockchainHeight)
          && !Parameters.isFalse(req.getParameter(MESSAGE_IS_TEXT_PARAMETER));
      try {
        message = messageIsText ? new Message(messageValue, blockchainHeight) : new Message(Convert.parseHexString(messageValue), blockchainHeight);
      } catch (RuntimeException e) {
        throw new ParameterException(INCORRECT_ARBITRARY_MESSAGE);
      }
    } else if (attachment instanceof Attachment.ColoredCoinsAssetTransfer && Burst.getFluxCapacitor().getValue(FluxValues.DIGITAL_GOODS_STORE, blockchainHeight)) {
      String commentValue = Convert.emptyToNull(req.getParameter(COMMENT_PARAMETER));
      if (commentValue != null) {
        message = new Message(commentValue, blockchainHeight);
      }
    } else if (attachment == Attachment.ARBITRARY_MESSAGE && ! Burst.getFluxCapacitor().getValue(FluxValues.DIGITAL_GOODS_STORE, blockchainHeight)) {
      message = new Message(new byte[0], blockchainHeight);
    }


    //is null
    PublicKeyAnnouncement publicKeyAnnouncement = null;
    if (recipientPublicKeyValue != null && Burst.getFluxCapacitor().getValue(FluxValues.DIGITAL_GOODS_STORE, blockchainHeight)) {
      publicKeyAnnouncement = new PublicKeyAnnouncement(Convert.parseHexString(recipientPublicKeyValue), blockchainHeight);
    }
//    System.out.println("publicKeyAnnouncement");
//    System.out.println(publicKeyAnnouncement);

    if (secretPhrase == null && publicKeyValue == null) {
      return MISSING_SECRET_PHRASE;
    } else if (deadlineValue == null) {
      return MISSING_DEADLINE;
    }

    short deadline;
    try {
      deadline = Short.parseShort(deadlineValue);
      if (deadline < 1 || deadline > 1440) {
        return INCORRECT_DEADLINE;
      }
    } catch (NumberFormatException e) {
      return INCORRECT_DEADLINE;
    }

    long feeNQT = ParameterParser.getFeeNQT(req);
    if (feeNQT < minimumFeeNQT) {
      return INCORRECT_FEE;
    }

    try {
      String description = attachment.getTransactionType().getDescription();
      if (description == "TUSD Payment") {
        //处理稳定币转账质押的余额和账户费用是否充足
        if (feeNQT  >  senderAccount.getUnconfirmedBalanceNQT() ){
          return NOT_ENOUGH_FUNDS;
        }
      } else {
        if (Convert.safeAdd(amountNQT, feeNQT) > senderAccount.getUnconfirmedBalanceNQT()) {
          return NOT_ENOUGH_FUNDS;
        }
      }
    } catch (ArithmeticException e) {
      return NOT_ENOUGH_FUNDS;
    }

    if (referencedTransactionId != null) {
      return INCORRECT_REFERENCED_TRANSACTION;
    }

    JsonObject response = new JsonObject();

    // shouldn't try to get publicKey from senderAccount as it may have not been set yet
    byte[] publicKey = secretPhrase != null ? Crypto.getPublicKey(secretPhrase) : Convert.parseHexString(publicKeyValue);

    try {

      Builder builder = transactionProcessor.newTransactionBuilder(publicKey, amountNQT, feeNQT, deadline, attachment).referencedTransactionFullHash(referencedTransactionFullHash);
      if (attachment.getTransactionType().hasRecipient()) {
        builder.recipientId(recipientId);
      }
      if (encryptedMessage != null) {
        builder.encryptedMessage(encryptedMessage);
      }
      if (message != null) {
        builder.message(message);
      }
      if (publicKeyAnnouncement != null) {
        builder.publicKeyAnnouncement(publicKeyAnnouncement);
      }
      if (encryptToSelfMessage != null) {
        builder.encryptToSelfMessage(encryptToSelfMessage);
      }
      Transaction transaction = builder.build();
      transactionService.validate(transaction);

      if (secretPhrase != null) {
        transaction.sign(secretPhrase);
        transactionService.validate(transaction); // 2nd validate may be needed if validation requires id to be known
        response.addProperty(TRANSACTION_RESPONSE, transaction.getStringId());
        response.addProperty(FULL_HASH_RESPONSE, transaction.getFullHash());
        response.addProperty(TRANSACTION_BYTES_RESPONSE, Convert.toHexString(transaction.getBytes()));
        response.addProperty(SIGNATURE_HASH_RESPONSE, Convert.toHexString(Crypto.sha256().digest(transaction.getSignature())));
        if (broadcast) {
          response.addProperty(NUMBER_PEERS_SENT_TO_RESPONSE, transactionProcessor.broadcast(transaction));
          response.addProperty(BROADCASTED_RESPONSE, true);
        } else {
          response.addProperty(BROADCASTED_RESPONSE, false);
        }
      } else {
        response.addProperty(BROADCASTED_RESPONSE, false);
      }
      //钱包接受的是这个
      response.addProperty(UNSIGNED_TRANSACTION_BYTES_RESPONSE, Convert.toHexString(transaction.getUnsignedBytes()));

      response.add(TRANSACTION_JSON_RESPONSE, JSONData.unconfirmedTransaction(transaction));

    } catch (BurstException.NotYetEnabledException e) {
      return FEATURE_NOT_AVAILABLE;
    } catch (BurstException.ValidationException e) {
      response.addProperty(ERROR_CODE_RESPONSE, 4);
      response.addProperty(ERROR_DESCRIPTION_RESPONSE, e.getMessage());
    }
    return response;
  }
}
