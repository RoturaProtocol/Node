/*
 * This file is generated by jOOQ.
 */
package brs.schema.tables.records;


import brs.schema.tables.At;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AtRecord extends UpdatableRecordImpl<AtRecord> implements Record15<Long, Long, Long, String, String, Short, Integer, Integer, Integer, Integer, Integer, byte[], Integer, Boolean, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>DB.at.db_id</code>.
     */
    public void setDbId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>DB.at.db_id</code>.
     */
    public Long getDbId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>DB.at.id</code>.
     */
    public void setId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>DB.at.id</code>.
     */
    public Long getId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>DB.at.creator_id</code>.
     */
    public void setCreatorId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>DB.at.creator_id</code>.
     */
    public Long getCreatorId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>DB.at.name</code>.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>DB.at.name</code>.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>DB.at.description</code>.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>DB.at.description</code>.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>DB.at.version</code>.
     */
    public void setVersion(Short value) {
        set(5, value);
    }

    /**
     * Getter for <code>DB.at.version</code>.
     */
    public Short getVersion() {
        return (Short) get(5);
    }

    /**
     * Setter for <code>DB.at.csize</code>.
     */
    public void setCsize(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>DB.at.csize</code>.
     */
    public Integer getCsize() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>DB.at.dsize</code>.
     */
    public void setDsize(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>DB.at.dsize</code>.
     */
    public Integer getDsize() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>DB.at.c_user_stack_bytes</code>.
     */
    public void setCUserStackBytes(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>DB.at.c_user_stack_bytes</code>.
     */
    public Integer getCUserStackBytes() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>DB.at.c_call_stack_bytes</code>.
     */
    public void setCCallStackBytes(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>DB.at.c_call_stack_bytes</code>.
     */
    public Integer getCCallStackBytes() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>DB.at.creation_height</code>.
     */
    public void setCreationHeight(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>DB.at.creation_height</code>.
     */
    public Integer getCreationHeight() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>DB.at.ap_code</code>.
     */
    public void setApCode(byte[] value) {
        set(11, value);
    }

    /**
     * Getter for <code>DB.at.ap_code</code>.
     */
    public byte[] getApCode() {
        return (byte[]) get(11);
    }

    /**
     * Setter for <code>DB.at.height</code>.
     */
    public void setHeight(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>DB.at.height</code>.
     */
    public Integer getHeight() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>DB.at.latest</code>.
     */
    public void setLatest(Boolean value) {
        set(13, value);
    }

    /**
     * Getter for <code>DB.at.latest</code>.
     */
    public Boolean getLatest() {
        return (Boolean) get(13);
    }

    /**
     * Setter for <code>DB.at.ap_code_hash_id</code>.
     */
    public void setApCodeHashId(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>DB.at.ap_code_hash_id</code>.
     */
    public Long getApCodeHashId() {
        return (Long) get(14);
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
    public Row15<Long, Long, Long, String, String, Short, Integer, Integer, Integer, Integer, Integer, byte[], Integer, Boolean, Long> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<Long, Long, Long, String, String, Short, Integer, Integer, Integer, Integer, Integer, byte[], Integer, Boolean, Long> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return At.AT.DB_ID;
    }

    @Override
    public Field<Long> field2() {
        return At.AT.ID;
    }

    @Override
    public Field<Long> field3() {
        return At.AT.CREATOR_ID;
    }

    @Override
    public Field<String> field4() {
        return At.AT.NAME;
    }

    @Override
    public Field<String> field5() {
        return At.AT.DESCRIPTION;
    }

    @Override
    public Field<Short> field6() {
        return At.AT.VERSION;
    }

    @Override
    public Field<Integer> field7() {
        return At.AT.CSIZE;
    }

    @Override
    public Field<Integer> field8() {
        return At.AT.DSIZE;
    }

    @Override
    public Field<Integer> field9() {
        return At.AT.C_USER_STACK_BYTES;
    }

    @Override
    public Field<Integer> field10() {
        return At.AT.C_CALL_STACK_BYTES;
    }

    @Override
    public Field<Integer> field11() {
        return At.AT.CREATION_HEIGHT;
    }

    @Override
    public Field<byte[]> field12() {
        return At.AT.AP_CODE;
    }

    @Override
    public Field<Integer> field13() {
        return At.AT.HEIGHT;
    }

    @Override
    public Field<Boolean> field14() {
        return At.AT.LATEST;
    }

    @Override
    public Field<Long> field15() {
        return At.AT.AP_CODE_HASH_ID;
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
        return getCreatorId();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getDescription();
    }

    @Override
    public Short component6() {
        return getVersion();
    }

    @Override
    public Integer component7() {
        return getCsize();
    }

    @Override
    public Integer component8() {
        return getDsize();
    }

    @Override
    public Integer component9() {
        return getCUserStackBytes();
    }

    @Override
    public Integer component10() {
        return getCCallStackBytes();
    }

    @Override
    public Integer component11() {
        return getCreationHeight();
    }

    @Override
    public byte[] component12() {
        return getApCode();
    }

    @Override
    public Integer component13() {
        return getHeight();
    }

    @Override
    public Boolean component14() {
        return getLatest();
    }

    @Override
    public Long component15() {
        return getApCodeHashId();
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
        return getCreatorId();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getDescription();
    }

    @Override
    public Short value6() {
        return getVersion();
    }

    @Override
    public Integer value7() {
        return getCsize();
    }

    @Override
    public Integer value8() {
        return getDsize();
    }

    @Override
    public Integer value9() {
        return getCUserStackBytes();
    }

    @Override
    public Integer value10() {
        return getCCallStackBytes();
    }

    @Override
    public Integer value11() {
        return getCreationHeight();
    }

    @Override
    public byte[] value12() {
        return getApCode();
    }

    @Override
    public Integer value13() {
        return getHeight();
    }

    @Override
    public Boolean value14() {
        return getLatest();
    }

    @Override
    public Long value15() {
        return getApCodeHashId();
    }

    @Override
    public AtRecord value1(Long value) {
        setDbId(value);
        return this;
    }

    @Override
    public AtRecord value2(Long value) {
        setId(value);
        return this;
    }

    @Override
    public AtRecord value3(Long value) {
        setCreatorId(value);
        return this;
    }

    @Override
    public AtRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public AtRecord value5(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public AtRecord value6(Short value) {
        setVersion(value);
        return this;
    }

    @Override
    public AtRecord value7(Integer value) {
        setCsize(value);
        return this;
    }

    @Override
    public AtRecord value8(Integer value) {
        setDsize(value);
        return this;
    }

    @Override
    public AtRecord value9(Integer value) {
        setCUserStackBytes(value);
        return this;
    }

    @Override
    public AtRecord value10(Integer value) {
        setCCallStackBytes(value);
        return this;
    }

    @Override
    public AtRecord value11(Integer value) {
        setCreationHeight(value);
        return this;
    }

    @Override
    public AtRecord value12(byte[] value) {
        setApCode(value);
        return this;
    }

    @Override
    public AtRecord value13(Integer value) {
        setHeight(value);
        return this;
    }

    @Override
    public AtRecord value14(Boolean value) {
        setLatest(value);
        return this;
    }

    @Override
    public AtRecord value15(Long value) {
        setApCodeHashId(value);
        return this;
    }

    @Override
    public AtRecord values(Long value1, Long value2, Long value3, String value4, String value5, Short value6, Integer value7, Integer value8, Integer value9, Integer value10, Integer value11, byte[] value12, Integer value13, Boolean value14, Long value15) {
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
     * Create a detached AtRecord
     */
    public AtRecord() {
        super(At.AT);
    }

    /**
     * Create a detached, initialised AtRecord
     */
    public AtRecord(Long dbId, Long id, Long creatorId, String name, String description, Short version, Integer csize, Integer dsize, Integer cUserStackBytes, Integer cCallStackBytes, Integer creationHeight, byte[] apCode, Integer height, Boolean latest, Long apCodeHashId) {
        super(At.AT);

        setDbId(dbId);
        setId(id);
        setCreatorId(creatorId);
        setName(name);
        setDescription(description);
        setVersion(version);
        setCsize(csize);
        setDsize(dsize);
        setCUserStackBytes(cUserStackBytes);
        setCCallStackBytes(cCallStackBytes);
        setCreationHeight(creationHeight);
        setApCode(apCode);
        setHeight(height);
        setLatest(latest);
        setApCodeHashId(apCodeHashId);
    }
}
