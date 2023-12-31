/*
 * This file is generated by jOOQ.
 */
package brs.schema.tables.records;


import brs.schema.tables.AskOrder;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AskOrderRecord extends UpdatableRecordImpl<AskOrderRecord> implements Record9<Long, Long, Long, Long, Long, Long, Integer, Integer, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>DB.ask_order.db_id</code>.
     */
    public void setDbId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>DB.ask_order.db_id</code>.
     */
    public Long getDbId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>DB.ask_order.id</code>.
     */
    public void setId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>DB.ask_order.id</code>.
     */
    public Long getId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>DB.ask_order.account_id</code>.
     */
    public void setAccountId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>DB.ask_order.account_id</code>.
     */
    public Long getAccountId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>DB.ask_order.asset_id</code>.
     */
    public void setAssetId(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>DB.ask_order.asset_id</code>.
     */
    public Long getAssetId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>DB.ask_order.price</code>.
     */
    public void setPrice(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>DB.ask_order.price</code>.
     */
    public Long getPrice() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>DB.ask_order.quantity</code>.
     */
    public void setQuantity(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>DB.ask_order.quantity</code>.
     */
    public Long getQuantity() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>DB.ask_order.creation_height</code>.
     */
    public void setCreationHeight(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>DB.ask_order.creation_height</code>.
     */
    public Integer getCreationHeight() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>DB.ask_order.height</code>.
     */
    public void setHeight(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>DB.ask_order.height</code>.
     */
    public Integer getHeight() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>DB.ask_order.latest</code>.
     */
    public void setLatest(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>DB.ask_order.latest</code>.
     */
    public Boolean getLatest() {
        return (Boolean) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, Long, Long, Long, Long, Integer, Integer, Boolean> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Long, Long, Long, Long, Long, Long, Integer, Integer, Boolean> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return AskOrder.ASK_ORDER.DB_ID;
    }

    @Override
    public Field<Long> field2() {
        return AskOrder.ASK_ORDER.ID;
    }

    @Override
    public Field<Long> field3() {
        return AskOrder.ASK_ORDER.ACCOUNT_ID;
    }

    @Override
    public Field<Long> field4() {
        return AskOrder.ASK_ORDER.ASSET_ID;
    }

    @Override
    public Field<Long> field5() {
        return AskOrder.ASK_ORDER.PRICE;
    }

    @Override
    public Field<Long> field6() {
        return AskOrder.ASK_ORDER.QUANTITY;
    }

    @Override
    public Field<Integer> field7() {
        return AskOrder.ASK_ORDER.CREATION_HEIGHT;
    }

    @Override
    public Field<Integer> field8() {
        return AskOrder.ASK_ORDER.HEIGHT;
    }

    @Override
    public Field<Boolean> field9() {
        return AskOrder.ASK_ORDER.LATEST;
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
    public Long component3() {
        return getAccountId();
    }

    @Override
    public Long component4() {
        return getAssetId();
    }

    @Override
    public Long component5() {
        return getPrice();
    }

    @Override
    public Long component6() {
        return getQuantity();
    }

    @Override
    public Integer component7() {
        return getCreationHeight();
    }

    @Override
    public Integer component8() {
        return getHeight();
    }

    @Override
    public Boolean component9() {
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
    public Long value3() {
        return getAccountId();
    }

    @Override
    public Long value4() {
        return getAssetId();
    }

    @Override
    public Long value5() {
        return getPrice();
    }

    @Override
    public Long value6() {
        return getQuantity();
    }

    @Override
    public Integer value7() {
        return getCreationHeight();
    }

    @Override
    public Integer value8() {
        return getHeight();
    }

    @Override
    public Boolean value9() {
        return getLatest();
    }

    @Override
    public AskOrderRecord value1(Long value) {
        setDbId(value);
        return this;
    }

    @Override
    public AskOrderRecord value2(Long value) {
        setId(value);
        return this;
    }

    @Override
    public AskOrderRecord value3(Long value) {
        setAccountId(value);
        return this;
    }

    @Override
    public AskOrderRecord value4(Long value) {
        setAssetId(value);
        return this;
    }

    @Override
    public AskOrderRecord value5(Long value) {
        setPrice(value);
        return this;
    }

    @Override
    public AskOrderRecord value6(Long value) {
        setQuantity(value);
        return this;
    }

    @Override
    public AskOrderRecord value7(Integer value) {
        setCreationHeight(value);
        return this;
    }

    @Override
    public AskOrderRecord value8(Integer value) {
        setHeight(value);
        return this;
    }

    @Override
    public AskOrderRecord value9(Boolean value) {
        setLatest(value);
        return this;
    }

    @Override
    public AskOrderRecord values(Long value1, Long value2, Long value3, Long value4, Long value5, Long value6, Integer value7, Integer value8, Boolean value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AskOrderRecord
     */
    public AskOrderRecord() {
        super(AskOrder.ASK_ORDER);
    }

    /**
     * Create a detached, initialised AskOrderRecord
     */
    public AskOrderRecord(Long dbId, Long id, Long accountId, Long assetId, Long price, Long quantity, Integer creationHeight, Integer height, Boolean latest) {
        super(AskOrder.ASK_ORDER);

        setDbId(dbId);
        setId(id);
        setAccountId(accountId);
        setAssetId(assetId);
        setPrice(price);
        setQuantity(quantity);
        setCreationHeight(creationHeight);
        setHeight(height);
        setLatest(latest);
    }
}
