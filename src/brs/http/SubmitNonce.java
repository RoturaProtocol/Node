package brs.http;

import brs.*;
import brs.crypto.Crypto;
import brs.props.PropertyService;
import brs.props.Props;
import brs.services.AccountService;
import brs.util.Convert;
import burst.kit.crypto.BurstCrypto;
import burst.kit.entity.BurstAddress;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static brs.http.common.Parameters.*;
import brs.seconddb.Public;



public final class SubmitNonce extends APIServlet.JsonRequestHandler {

  private final Map<Long, String> passphrases;
  private final boolean allowOtherSoloMiners;
  private final AccountService accountService;
  private final Blockchain blockchain;
  private final Generator generator;
  private final int checkPointHeight;


  SubmitNonce(PropertyService propertyService, AccountService accountService, Blockchain blockchain, Generator generator) {
    super(new APITag[] {APITag.MINING}, SECRET_PHRASE_PARAMETER, NONCE_PARAMETER, ACCOUNT_ID_PARAMETER, BLOCK_HEIGHT_PARAMETER, DEADLINE_PARAMETER);
    BurstCrypto burstCrypto = BurstCrypto.getInstance();
    this.passphrases = propertyService.getStringList(Props.SOLO_MINING_PASSPHRASES)
      .stream()
      .collect(Collectors.toMap(passphrase -> burstCrypto.getBurstAddressFromPassphrase(passphrase).getBurstID().getSignedLongId(), Function.identity()));
    this.allowOtherSoloMiners = propertyService.getBoolean(Props.ALLOW_OTHER_SOLO_MINERS);
    this.checkPointHeight = propertyService.getInt(Props.BRS_CHECKPOINT_HEIGHT);

    this.accountService = accountService;
    this.blockchain = blockchain;
    this.generator = generator;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) {

    String secret = req.getParameter(SECRET_PHRASE_PARAMETER);
    long nonce = Convert.parseUnsignedLong(req.getParameter(NONCE_PARAMETER));

    String accountId = req.getParameter(ACCOUNT_ID_PARAMETER);

    String submissionHeight = Convert.emptyToNull(req.getParameter(BLOCK_HEIGHT_PARAMETER));

    JsonObject response = new JsonObject();

    if (submissionHeight != null) {
      try {
        int height = Integer.parseInt(submissionHeight);
        if (height < checkPointHeight) {
          response.addProperty("result", "Given block height smaller than the check point height");
          return response;
        }
        if (height != blockchain.getHeight() + 1) {
          response.addProperty("result", "Given block height does not match current blockchain height");
          return response;
        }
      } catch (NumberFormatException e) {
        response.addProperty("result", "Given block height is not a number");
        return response;
      }
    }

    if (secret == null || Objects.equals(secret, "")) {
      long accountIdLong;
      try {
        accountIdLong = BurstAddress.fromEither(accountId).getBurstID().getSignedLongId();
      } catch (Exception e) {
        response.addProperty("result", "Missing Passphrase and Account ID is malformed");
        return response;
      }
      if (passphrases.containsKey(accountIdLong)) {
        secret = passphrases.get(accountIdLong);
      } else {
        response.addProperty("result", "Missing Passphrase and account passphrase not in solo mining config");
        return response;
      }
    }

    if (!allowOtherSoloMiners && !passphrases.containsValue(secret)) {
      response.addProperty("result", "This account is not allowed to mine on this node as the whitelist is enabled and it is not whitelisted.");
      return response;
    }

    byte[] secretPublicKey = Crypto.getPublicKey(secret);

    Account secretAccount = accountService.getAccount(secretPublicKey);

    if(secretAccount != null) {
      try {

        /*
        ln 220817
        增加verifySecretAccount验证白名单，address唯一标识
        * */
//        verifySecretAccount(accountService, blockchain, secretAccount, Convert.parseUnsignedLong(accountId));
        verifySecretAccount(accountService, blockchain, secretAccount, Convert.parseUnsignedLong(accountId),Convert.parseAddress(accountId));

      } catch (Exception e) {

        response.addProperty("result", e.getMessage());

        return response;
      }
    }

    Generator.GeneratorState generatorState = null;
    if(accountId == null || secretAccount == null) {
      generatorState = generator.addNonce(secret, nonce);
    }
    else {
      Account genAccount = accountService.getAccount(Convert.parseUnsignedLong(accountId));
      if(genAccount == null || genAccount.getPublicKey() == null) {
        response.addProperty("result", "Passthrough mining requires public key in blockchain");
      }
      else {
        byte[] publicKey = genAccount.getPublicKey();
        generatorState = generator.addNonce(secret, nonce, publicKey);
      }
    }

    if(generatorState == null) {
      response.addProperty("result", "failed to create generator");
      return response;
    }

    response.addProperty("result", "success");
    response.addProperty("deadline", generatorState.getDeadlineLegacy());

    return response;
  }

  public static void verifySecretAccount(AccountService accountService, Blockchain blockchain, Account secretAccount, long accountId ,BurstAddress address) throws Exception {
    Account genAccount;
    if (accountId != 0) {
      genAccount = accountService.getAccount(accountId);
    }
    else {
      genAccount = secretAccount;
    }

//    Committed Balance=BalanceNQT-UnconfirmedBalanceNQT
//    100000000000 = 1000
    long minerLicence = secretAccount.getBalanceNQT()-secretAccount.getUnconfirmedBalanceNQT();

//    Public p = new Public();
//    Boolean whiteLicence = p.verifyWhiteList(address.toString());

//    if (minerLicence < 100000000000L ){
//      throw new Exception("No mining licence");
//    }
//    int height = blockchain.getHeight();
//    long committedAmountNOT = Burst.getBlockchain().getCommittedAmount(genAccount.getId(), height+ Constants.COMMITMENT_WAIT, height, null);

//    long astrict = 1000000000000000L;
//    long astrict = 100000000000L;
//    if (committedAmountNOT < astrict){
//      throw new Exception("No mining licence");
//    }

    if (genAccount != null) {
      Account.RewardRecipientAssignment assignment = accountService.getRewardRecipientAssignment(genAccount);
      long rewardId;
      if (assignment == null) {
        rewardId = genAccount.getId();
      } else if (assignment.getFromHeight() > blockchain.getLastBlock().getHeight() + 1) {
        rewardId = assignment.getPrevRecipientId();
      } else {
        rewardId = assignment.getRecipientId();
      }
      if (rewardId != secretAccount.getId()) {
        throw new Exception("Passphrase does not match reward recipient");
      }
    }
    else {
      throw new Exception("Passphrase is for a different account");
    }

  }

  @Override
  boolean requirePost() {
    return true;
  }
}
