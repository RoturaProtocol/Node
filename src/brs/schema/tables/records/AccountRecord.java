/*
 * This file is generated by jOOQ.
 */
package brs.schema.tables.records;


import brs.schema.tables.Account;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccountRecord extends UpdatableRecordImpl<AccountRecord> implements Record15<Long, Long, Integer, byte[], Integer, Long, Long, Long, Long, Double, Double, String, String, Integer, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>DB.account.db_id</code>.
     */
    public void setDbId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>DB.account.db_id</code>.
     */
    public Long getDbId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>DB.account.id</code>.
     */
    public void setId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>DB.account.id</code>.
     */
    public Long getId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>DB.account.creation_height</code>.
     */
    public void setCreationHeight(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>DB.account.creation_height</code>.
     */
    public Integer getCreationHeight() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>DB.account.public_key</code>.
     */
    public void setPublicKey(byte[] value) {
        set(3, value);
    }

    /**
     * Getter for <code>DB.account.public_key</code>.
     */
    public byte[] getPublicKey() {
        return (byte[]) get(3);
    }

    /**
     * Setter for <code>DB.account.key_height</code>.
     */
    public void setKeyHeight(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>DB.account.key_height</code>.
     */
    public Integer getKeyHeight() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>DB.account.balance</code>.
     */
    public void setBalance(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>DB.account.balance</code>.
     */
    public Long getBalance() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>DB.account.unconfirmed_balance</code>.
     */
    public void setUnconfirmedBalance(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>DB.account.unconfirmed_balance</code>.
     */
    public Long getUnconfirmedBalance() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>DB.account.forged_balance</code>.
     */
    public void setForgedBalance(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>DB.account.forged_balance</code>.
     */
    public Long getForgedBalance() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>DB.account.pledge_balance</code>.
     */
    public void setPledgeBalance(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>DB.account.pledge_balance</code>.
     */
    public Long getPledgeBalance() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>DB.account.stablecoin_balance</code>.
     */
    public void setStablecoinBalance(Double value) {
        set(9, value);
    }

    /**
     * Getter for <code>DB.account.stablecoin_balance</code>.
     */
    public Double getStablecoinBalance() {
        return (Double) get(9);
    }

    /**
     * Setter for <code>DB.account.debt_stablecoin_balance</code>.
     */
    public void setDebtStablecoinBalance(Double value) {
        set(10, value);
    }

    /**
     * Getter for <code>DB.account.debt_stablecoin_balance</code>.
     */
    public Double getDebtStablecoinBalance() {
        return (Double) get(10);
    }

    /**
     * Setter for <code>DB.account.name</code>.
     */
    public void setName(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>DB.account.name</code>.
     */
    public String getName() {
        return (String) get(11);
    }

    /**
     * Setter for <code>DB.account.description</code>.
     */
    public void setDescription(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>DB.account.description</code>.
     */
    public String getDescription() {
        return (String) get(12);
    }

    /**
     * Setter for <code>DB.account.height</code>.
     */
    public void setHeight(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>DB.account.height</code>.
     */
    public Integer getHeight() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>DB.account.latest</code>.
     */
    public void setLatest(Boolean value) {
        set(14, value);
    }

    /**
     * Getter for <code>DB.account.latest</code>.
     */
    public Boolean getLatest() {
        return (Boolean) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, Integer, byte[], Integer, Long, Long, Long, Long, Double, Double, String, String, Integer, Boolean> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<Long, Long, Integer, byte[], Integer, Long, Long, Long, Long, Double, Double, String, String, Integer, Boolean> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Account.ACCOUNT.DB_ID;
    }

    @Override
    public Field<Long> field2() {
        return Account.ACCOUNT.ID;
    }

    @Override
    public Field<Integer> field3() {
        return Account.ACCOUNT.CREATION_HEIGHT;
    }

    @Override
    public Field<byte[]> field4() {
        return Account.ACCOUNT.PUBLIC_KEY;
    }

    @Override
    public Field<Integer> field5() {
        return Account.ACCOUNT.KEY_HEIGHT;
    }

    @Override
    public Field<Long> field6() {
        return Account.ACCOUNT.BALANCE;
    }

    @Override
    public Field<Long> field7() {
        return Account.ACCOUNT.UNCONFIRMED_BALANCE;
    }

    @Override
    public Field<Long> field8() {
        return Account.ACCOUNT.FORGED_BALANCE;
    }

    @Override
    public Field<Long> field9() {
        return Account.ACCOUNT.PLEDGE_BALANCE;
    }

    @Override
    public Field<Double> field10() {
        return Account.ACCOUNT.STABLECOIN_BALANCE;
    }

    @Override
    public Field<Double> field11() {
        return Account.ACCOUNT.DEBT_STABLECOIN_BALANCE;
    }

    @Override
    public Field<String> field12() {
        return Account.ACCOUNT.NAME;
    }

    @Override
    public Field<String> field13() {
        return Account.ACCOUNT.DESCRIPTION;
    }

    @Override
    public Field<Integer> field14() {
        return Account.ACCOUNT.HEIGHT;
    }

    @Override
    public Field<Boolean> field15() {
        return Account.ACCOUNT.LATEST;
    }

    @Override
    public Long component1() {
        return getDbId();
    }

    @Override
    public Long component2() {
        return getId();
    }

    @Override
    public Integer component3() {
        return getCreationHeight();
    }

    @Override
    public byte[] component4() {
        return getPublicKey();
    }

    @Override
    public Integer component5() {
        return getKeyHeight();
    }

    @Override
    public Long component6() {
        return getBalance();
    }

    @Override
    public Long component7() {
        return getUnconfirmedBalance();
    }

    @Override
    public Long component8() {
        return getForgedBalance();
    }

    @Override
    public Long component9() {
        return getPledgeBalance();
    }

    @Override
    public Double component10() {
        return getStablecoinBalance();
    }

    @Override
    public Double component11() {
        return getDebtStablecoinBalance();
    }

    @Override
    public String component12() {
        return getName();
    }

    @Override
    public String component13() {
        return getDescription();
    }

    @Override
    public Integer component14() {
        return getHeight();
    }

    @Override
    public Boolean component15() {
        return getLatest();
    }

    @Override
    public Long value1() {
        return getDbId();
    }

    @Override
    public Long value2() {
        return getId();
    }

    @Override
    public Integer value3() {
        return getCreationHeight();
    }

    @Override
    public byte[] value4() {
        return getPublicKey();
    }

    @Override
    public Integer value5() {
        return getKeyHeight();
    }

    @Override
    public Long value6() {
        return getBalance();
    }

    @Override
    public Long value7() {
        return getUnconfirmedBalance();
    }

    @Override
    public Long value8() {
        return getForgedBalance();
    }

    @Override
    public Long value9() {
        return getPledgeBalance();
    }

    @Override
    public Double value10() {
        return getStablecoinBalance();
    }

    @Override
    public Double value11() {
        return getDebtStablecoinBalance();
    }

    @Override
    public String value12() {
        return getName();
    }

    @Override
    public String value13() {
        return getDescription();
    }

    @Override
    public Integer value14() {
        return getHeight();
    }

    @Override
    public Boolean value15() {
        return getLatest();
    }

    @Override
    public AccountRecord value1(Long value) {
        setDbId(value);
        return this;
    }

    @Override
    public AccountRecord value2(Long value) {
        setId(value);
        return this;
    }

    @Override
    public AccountRecord value3(Integer value) {
        setCreationHeight(value);
        return this;
    }

    @Override
    public AccountRecord value4(byte[] value) {
        setPublicKey(value);
        return this;
    }

    @Override
    public AccountRecord value5(Integer value) {
        setKeyHeight(value);
        return this;
    }

    @Override
    public AccountRecord value6(Long value) {
        setBalance(value);
        return this;
    }

    @Override
    public AccountRecord value7(Long value) {
        setUnconfirmedBalance(value);
        return this;
    }

    @Override
    public AccountRecord value8(Long value) {
        setForgedBalance(value);
        return this;
    }

    @Override
    public AccountRecord value9(Long value) {
        setPledgeBalance(value);
        return this;
    }

    @Override
    public AccountRecord value10(Double value) {
        setStablecoinBalance(value);
        return this;
    }

    @Override
    public AccountRecord value11(Double value) {
        setDebtStablecoinBalance(value);
        return this;
    }

    @Override
    public AccountRecord value12(String value) {
        setName(value);
        return this;
    }

    @Override
    public AccountRecord value13(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public AccountRecord value14(Integer value) {
        setHeight(value);
        return this;
    }

    @Override
    public AccountRecord value15(Boolean value) {
        setLatest(value);
        return this;
    }

    @Override
    public AccountRecord values(Long value1, Long value2, Integer value3, byte[] value4, Integer value5, Long value6, Long value7, Long value8, Long value9, Double value10, Double value11, String value12, String value13, Integer value14, Boolean value15) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AccountRecord
     */
    public AccountRecord() {
        super(Account.ACCOUNT);
    }

    /**
     * Create a detached, initialised AccountRecord
     */
    public AccountRecord(Long dbId, Long id, Integer creationHeight, byte[] publicKey, Integer keyHeight, Long balance, Long unconfirmedBalance, Long forgedBalance, Long pledgeBalance, Double stablecoinBalance, Double debtStablecoinBalance, String name, String description, Integer height, Boolean latest) {
        super(Account.ACCOUNT);

        setDbId(dbId);
        setId(id);
        setCreationHeight(creationHeight);
        setPublicKey(publicKey);
        setKeyHeight(keyHeight);
        setBalance(balance);
        setUnconfirmedBalance(unconfirmedBalance);
        setForgedBalance(forgedBalance);
        setPledgeBalance(pledgeBalance);
        setStablecoinBalance(stablecoinBalance);
        setDebtStablecoinBalance(debtStablecoinBalance);
        setName(name);
        setDescription(description);
        setHeight(height);
        setLatest(latest);
    }
}
