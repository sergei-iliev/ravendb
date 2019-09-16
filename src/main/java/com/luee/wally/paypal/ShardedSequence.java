package com.luee.wally.paypal;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.collect.ImmutableMap;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sequencer which generates long integers in rough sequence.  That is, all the integers
 * from 0 upwards are generated, but not in strict sequence as the generator is sharded.
 * There is a main entity which distributes number ranges to shards.  When a new sequence
 * member is requested a shard is chosen at random and it returns the next integer in its range.
 * If the range is exhausted it asks for a new one.
 *
 * <p>The distribution process uses a transaction.  Ths maximum limit is a few  (up to 5 perhaps)
 * per second, so we need to make sure that this is the maximum distribution rate.  Let's assume
 * that we are getting <code>N</code> requests per second.  We need to make sure that the shards
 * can cope with this rate, to be safe let's say that we need <code>N</code> shards, and that we
 * the distribution is limited to 1 per second, which means that we need to distribute <code>N</code>
 * integers each time. So, we need the number of shards and the size of the distribution to be roughly the same
 * </p>
 *
 * <p>All this is based on the assumption that the randomizing process will provide a reasonably uniform
 * distribution between the shards. A completely uniform distribution would be a problem, as all the
 * shards would be exhausted at the same time, and we'd get clumping.  A solution to this, which will do
 * fine is to randomly choose between <code>N</code> and <code>2N</code> as the distribution size.
 * This should avoid clumping.</p>
 *
 *https://gist.github.com/cilogi/3f1e789c5ab83ffaedf5
 */
public class ShardedSequence  {
    private static final Logger LOG = Logger.getLogger(ShardedSequence.class.getName());

    private static final long CONCURRENT_EXCEPTION_RETURN = -1;

    public static final boolean isConcurrentException(long val) {
        return val == CONCURRENT_EXCEPTION_RETURN;
    }

    private static final class Sequence {
        private static final String KIND = "Sequence";
        private static final String SHARD_COUNT = "shard_count";
        private static String memcacheShardCountKey(String sequenceName) {
            return KIND + SHARD_COUNT + sequenceName;
        }
    }

    private static final class SequenceShard {
        private static final String KIND_PREFIX = "SequenceShard_";
    }

    private static final DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

    private static final int NUM_INITIAL_SHARDS =50;
    private static final int MAX_SHARDS = 1000;
    private static final long FIRST_INDEX = 17576L;

    private final String sequenceName;
    private final Random generator;
    private String kind;
    private final MemcacheService memcache;

    /**
     * Constructor which creates a sharded counter using the provided counter
     * name.
     *
     * @param name name of the sharded counter
     */
    public ShardedSequence(final String name) {
        sequenceName = name;
        generator = new Random();
        kind = SequenceShard.KIND_PREFIX + sequenceName;
        memcache = MemcacheServiceFactory.getMemcacheService();
    }

    /**
     * Increase the number of shards for a given sharded counter. Will never
     * decrease the number of shards.
     *
     * @param count Number of new shards to build and store
     */
    public final void addShards(final int count) {
        if (getShardCount() < MAX_SHARDS) {
            Key sequenceKay = KeyFactory.createKey(Sequence.KIND, sequenceName);
            incrementProperty(sequenceKay, Sequence.SHARD_COUNT, count, NUM_INITIAL_SHARDS + count);
        }
    }


    // Get the shard count.  We use memcache as the count will be unchanged for long periods
    // It doesn't matter too much if memcache return the wrong answer for a while, as we'll
    // still be able to increment.
    private int getShardCount() {
        String memcacheKey = Sequence.memcacheShardCountKey(sequenceName);
        Long value = (Long)memcache.get(memcacheKey);
        if (value != null) {
            return value.intValue();
        }
        try {
            Key sequenceKey = KeyFactory.createKey(Sequence.KIND, sequenceName);
            Entity counter = dataStore.get(sequenceKey);
            Long shardCount = (Long) counter.getProperty(Sequence.SHARD_COUNT);
            Long out = shardCount.longValue();
            memcache.put(memcacheKey, out);
            return out.intValue();
        } catch (EntityNotFoundException ignore) {
            memcache.put(memcacheKey, (long)NUM_INITIAL_SHARDS);
            return NUM_INITIAL_SHARDS;
        }
    }

    /**
     * Increment datastore property value inside a transaction. If the entity
     * with the provided key does not exist, instead create an entity with the
     * supplied initial property value.
     *
     * @param key          the entity key to update or create
     * @param prop         the property name to be incremented
     * @param increment    the amount by which to increment
     * @param initialValue the value to use if the entity does not exist
     */
    private void incrementProperty(final Key key, final String prop, final long increment, final long initialValue) {
        Transaction tx = dataStore.beginTransaction();
        Entity thing;
        long value;
        try {
            try {
                thing = dataStore.get(tx, key);
                value = (Long) thing.getProperty(prop) + increment;
            } catch (EntityNotFoundException e) {
                thing = new Entity(key);
                value = initialValue;
            }
            thing.setUnindexedProperty(prop, value);
            dataStore.put(tx, thing);
            tx.commit();
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING, "You may need more shards. Consider adding more shards.");
            LOG.log(Level.WARNING, e.toString(), e);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString(), e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    /** Get the next long int in the sequence.  Remember that the sequence will not be generated
     *  exactly, but assuming that the random number generator is fair, all the numbers will eventually
     *  appear
     * @return The next number in sequence, or null if there is some sort of error.
     */
    public Long next() {
        int numShards = getShardCount();
        long shardNum = generator.nextInt(numShards);
        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction tx = dataStore.beginTransaction(options);
        try {
            Entity shard = load(shardKey, tx, ImmutableMap.<String,Object>of("current", 0L, "stop", 0L));
            long current = (Long)shard.getProperty("current");
            long stop = (Long)shard.getProperty("stop");
            if (current < stop) {
                shard.setProperty("current", (long)(current+1L));
                dataStore.put(tx, shard);
                tx.commit();
                return current;
            } else {
                // we need to replenish this shard and then get a number
                Key sequenceKey = KeyFactory.createKey(Sequence.KIND, sequenceName);
                Entity sequence = load(sequenceKey, tx,
                        ImmutableMap.<String,Object>of(Sequence.SHARD_COUNT, NUM_INITIAL_SHARDS, "limit", FIRST_INDEX));
                long limit = (Long)sequence.getProperty("limit");
                int allocationSize = allocationSize(numShards);
                sequence.setUnindexedProperty("limit", (long)(limit + allocationSize));
                shard.setUnindexedProperty("current", limit+1);
                shard.setUnindexedProperty("stop", (long)(limit + allocationSize));
                dataStore.put(tx, shard);
                dataStore.put(tx, sequence);
                tx.commit();
                return limit;
            }
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING, "You may need more shards. Consider adding more shards.");
            LOG.log(Level.WARNING, e.toString(), e);
            return CONCURRENT_EXCEPTION_RETURN;
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString(), e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return null;
    }

    private int allocationSize(int numShards) {
        return numShards + generator.nextInt(numShards);
    }

    private Entity load(Key key, Transaction tx, Map<String,Object> initialProperties) {
        try {
            return dataStore.get(tx, key);
        }  catch (EntityNotFoundException e) {
            Entity entity = new Entity(key);
            if (initialProperties != null) {
                for (String property : initialProperties.keySet()) {
                    entity.setUnindexedProperty(property, initialProperties.get(property));
                }
            }
            return entity;
        }

    }
}
