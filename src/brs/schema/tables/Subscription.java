/*
 * This file is generated by jOOQ.
 */
package brs.schema.tables;


import brs.schema.Db;
import brs.schema.Indexes;
import brs.schema.Keys;
import brs.schema.tables.records.SubscriptionRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Subscription extends TableImpl<SubscriptionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DB.subscription</code>
     */
    public static final Subscription SUBSCRIPTION = new Subscription();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SubscriptionRecord> getRecordType() {
        return SubscriptionRecord.class;
    }

    /**
     * The column <code>DB.subscription.db_id</code>.
     */
    public final TableField<SubscriptionRecord, Long> DB_ID = createField(DSL.name("db_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>DB.subscription.id</code>.
     */
    public final TableField<SubscriptionRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.sender_id</code>.
     */
    public final TableField<SubscriptionRecord, Long> SENDER_ID = createField(DSL.name("sender_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.recipient_id</code>.
     */
    public final TableField<SubscriptionRecord, Long> RECIPIENT_ID = createField(DSL.name("recipient_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.amount</code>.
     */
    public final TableField<SubscriptionRecord, Long> AMOUNT = createField(DSL.name("amount"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.frequency</code>.
     */
    public final TableField<SubscriptionRecord, Integer> FREQUENCY = createField(DSL.name("frequency"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.time_next</code>.
     */
    public final TableField<SubscriptionRecord, Integer> TIME_NEXT = createField(DSL.name("time_next"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.height</code>.
     */
    public final TableField<SubscriptionRecord, Integer> HEIGHT = createField(DSL.name("height"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>DB.subscription.latest</code>.
     */
    public final TableField<SubscriptionRecord, Boolean> LATEST = createField(DSL.name("latest"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("1", SQLDataType.BOOLEAN)), this, "");

    private Subscription(Name alias, Table<SubscriptionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Subscription(Name alias, Table<SubscriptionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>DB.subscription</code> table reference
     */
    public Subscription(String alias) {
        this(DSL.name(alias), SUBSCRIPTION);
    }

    /**
     * Create an aliased <code>DB.subscription</code> table reference
     */
    public Subscription(Name alias) {
        this(alias, SUBSCRIPTION);
    }

    /**
     * Create a <code>DB.subscription</code> table reference
     */
    public Subscription() {
        this(DSL.name("subscription"), null);
    }

    public <O extends Record> Subscription(Table<O> child, ForeignKey<O, SubscriptionRecord> key) {
        super(child, key, SUBSCRIPTION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Db.DB;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.SUBSCRIPTION_SUBSCRIPTION_LATEST_INDEX, Indexes.SUBSCRIPTION_SUBSCRIPTION_RECIPIENT_ID_HEIGHT_IDX, Indexes.SUBSCRIPTION_SUBSCRIPTION_SENDER_ID_HEIGHT_IDX, Indexes.SUBSCRIPTION_SUBSCRIPTION_TIME_NEXT_INDEX);
    }

    @Override
    public Identity<SubscriptionRecord, Long> getIdentity() {
        return (Identity<SubscriptionRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SubscriptionRecord> getPrimaryKey() {
        return Keys.KEY_SUBSCRIPTION_PRIMARY;
    }

    @Override
    public List<UniqueKey<SubscriptionRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_SUBSCRIPTION_SUBSCRIPTION_ID_HEIGHT_IDX);
    }

    @Override
    public Subscription as(String alias) {
        return new Subscription(DSL.name(alias), this);
    }

    @Override
    public Subscription as(Name alias) {
        return new Subscription(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Subscription rename(String name) {
        return new Subscription(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Subscription rename(Name name) {
        return new Subscription(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, Long, Long, Long, Integer, Integer, Integer, Boolean> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
