package brs.test;

public class AccountSpark {


  public  long id;
  public  int creationHeight;
  public byte[] publicKey;
  public int keyHeight;
  public long balanceNQT;
  public long unconfirmedBalanceNQT;
  public long forgedBalanceNQT;

  public long pledgeBalanceNQT;
  public double stablecoinBalance;
  public double debtStablecoinBalance;


  public String name;
  public String description;

  public AccountSpark() {
  }

  public AccountSpark(long id, int creationHeight, byte[] publicKey, int keyHeight, long balanceNQT
    , long unconfirmedBalanceNQT, long forgedBalanceNQT
    , long pledgeBalanceNQT, double stablecoinBalance, double debtStablecoinBalance, String name, String description) {
    this.id = id;
    this.creationHeight = creationHeight;
    this.publicKey = publicKey;
    this.keyHeight = keyHeight;
    this.balanceNQT = balanceNQT;
    this.unconfirmedBalanceNQT = unconfirmedBalanceNQT;
    this.forgedBalanceNQT = forgedBalanceNQT;
    this.pledgeBalanceNQT = pledgeBalanceNQT;
    this.stablecoinBalance = stablecoinBalance;
    this.debtStablecoinBalance = debtStablecoinBalance;
    this.name = name;
    this.description = description;

  }



  public void setPledgeBalanceNQT(long pledgeBalanceNQT) {
    this.pledgeBalanceNQT = pledgeBalanceNQT;
  }

  public void setStablecoinBalance(double stablecoinBalance) {
    this.stablecoinBalance = stablecoinBalance;
  }

  public void setDebtStablecoinBalance(double debtStablecoinBalance) {
    this.debtStablecoinBalance = debtStablecoinBalance;
  }

  public void setForgedBalanceNQT(long forgedBalanceNQT) {
    this.forgedBalanceNQT = forgedBalanceNQT;
  }

  public long getPledgeBalanceNQT() {
    return pledgeBalanceNQT;
  }

  public double getStablecoinBalance() {
    return stablecoinBalance;
  }

  public double getDebtStablecoinBalance() {
    return debtStablecoinBalance;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUnconfirmedBalanceNQT(long unconfirmedBalanceNQT) {
    this.unconfirmedBalanceNQT = unconfirmedBalanceNQT;
  }

  public void setBalanceNQT(long balanceNQT) {
    this.balanceNQT = balanceNQT;
  }

  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey;
  }

  public void setKeyHeight(int keyHeight) {
    this.keyHeight = keyHeight;
  }

  public enum Event {
    BALANCE, UNCONFIRMED_BALANCE, ASSET_BALANCE, UNCONFIRMED_ASSET_BALANCE,
    LEASE_SCHEDULED, LEASE_STARTED, LEASE_ENDED

  }




  static class DoubleSpendingException extends RuntimeException {

    DoubleSpendingException(String message) {
      super(message);
    }

  }


  public AccountSpark(long id, int creationHeight) {

    this.id = id;
//    this.nxtKey = burstKey;
    this.creationHeight = creationHeight;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public byte[] getPublicKey() {
    if (this.keyHeight == -1) {
      return null;
    }
    return publicKey;
  }

  public int getCreationHeight() {
    return creationHeight;
  }

  public int getKeyHeight() {
    return keyHeight;
  }







  public long getBalanceNQT() {
    return balanceNQT;
  }

  public long getUnconfirmedBalanceNQT() {
    return unconfirmedBalanceNQT;
  }

  public long getForgedBalanceNQT() {
    return forgedBalanceNQT;
  }



}
