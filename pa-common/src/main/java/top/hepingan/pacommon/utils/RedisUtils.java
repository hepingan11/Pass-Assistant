package top.hepingan.pacommon.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * redis工具类
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public final class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 设置指定键的过期时间（以秒为单位）
     *
     * @param key     键
     * @param timeout 过期时间，单位：秒
     * @return 是否成功设置过期时间
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取指定键的剩余生存时间
     *
     * @param key 键
     * @return 剩余生存时间（秒）
     */
    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置指定键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否成功设置过期时间
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        Boolean ret = redisTemplate.expire(key, timeout, unit);
        return ret != null && ret;
    }

    /**
     * 检查给定键是否存在
     *
     * @param key 键
     * @return 键是否存在
     */
    public boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 对指定键的数字值进行增量操作
     *
     * @param key   键
     * @param delta 增量值
     * @return 增量操作后的新值
     */
    public long increment(final String key, final long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 删除指定的键
     *
     * @param key 键
     * @return 是否成功删除
     */
    public boolean delKey(final String key) {
        Boolean ret = redisTemplate.delete(key);
        return ret != null && ret;
    }

    /**
     * 批量删除指定的键
     *
     * @param keys 键集合
     * @return 删除的键数量
     */
    public long delKeys(final Collection<String> keys) {
        Long ret = redisTemplate.delete(keys);
        return ret == null ? 0 : ret;
    }

    /**
     * 将对象存储到Redis中，使用指定的键
     *
     * @param key   键
     * @param value 值
     */
    public void setValue(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将对象存储到Redis中，并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public void setValueTimeout(final String key, final Object value, final long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 根据键获取对应的值
     *
     * @param key 键
     * @return 值
     */
    public Object getValue(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 检查哈希表中是否存在指定的字段
     *
     * @param key  哈希表键
     * @param hkey 字段名
     * @return 是否存在
     */
    public boolean hasHashKey(final String key, String hkey) {
        Boolean ret = redisTemplate.opsForHash().hasKey(key, hkey);
        return ret != null && ret;
    }

    /**
     * 向哈希表中添加一个字段-值对
     *
     * @param key   哈希表键
     * @param hKey  字段名
     * @param value 值
     */
    public void hashPut(final String key, final String hKey, final Object value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 向哈希表中批量添加字段-值对
     *
     * @param key    哈希表键
     * @param values 多个字段-值对
     */
    public void hashPutAll(final String key, final Map<String, Object> values) {
        redisTemplate.opsForHash().putAll(key, values);
    }

    /**
     * 获取哈希表中指定字段的值
     *
     * @param key  哈希表键
     * @param hKey 字段名
     * @return 字段对应的值
     */
    public Object hashGet(final String key, final String hKey) {
        return redisTemplate.opsForHash().get(key, hKey);
    }

    /**
     * 获取哈希表中所有的字段和值
     *
     * @param key 哈希表键
     * @return 所有字段和值的映射表
     */
    public Map<Object, Object> hashGetAll(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取哈希表中多个字段的值
     *
     * @param key    哈希表键
     * @param hKeys  多个字段名
     * @return 多个字段对应的值列表
     */
    public List<Object> hashMultiGet(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 检查哈希表中是否存在指定的字段（功能同 hasHashKey）
     *
     * @param key      哈希表键
     * @param hashKey  字段名
     * @return 是否存在
     */
    public boolean hashExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 删除哈希表中的一个或多个字段
     *
     * @param key    哈希表键
     * @param hKeys  多个字段名
     * @return 被删除的字段数量
     */
    public long hashDeleteKeys(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().delete(key, hKeys);
    }

    /**
     * 删除哈希表中的一个或多个字段（可变参数形式）
     *
     * @param key     哈希表键
     * @param hashKey 多个字段名
     * @return 被删除的字段数量
     */
    public Long hashDelete(final String key, final Object... hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 向集合中添加多个元素
     *
     * @param key     集合键
     * @param values  元素数组
     * @return 添加成功的元素数量
     */
    public long setSet(final String key, final Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 从集合中移除指定的元素
     *
     * @param key     集合键
     * @param values  要移除的元素数组
     * @return 成功移除的元素数量
     */
    public long setDel(final String key, final Object... values) {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 获取集合中的所有成员
     *
     * @param key 集合键
     * @return 集合的所有成员
     */
    public Set<Object> getSetAll(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 向有序集合中添加多个带分数的元素
     *
     * @param key     有序集合键
     * @param values  包含元素及其分数的集合
     * @return 添加成功的元素数量
     */
    public long zsetSetAll(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().add(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 获取有序集合中某个元素的分数
     *
     * @param key   有序集合键
     * @param value 元素值
     * @return 元素对应的分数
     */
    public Double zsetSetGetSource(final String key, final Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 对有序集合中的某个元素的分数进行增量操作
     *
     * @param key       有序集合键
     * @param value     元素值
     * @param increment 分数增量
     * @return 更新后的分数
     */
    public Double zsetIncrementScore(final String key, final Object value, final Double increment) {
        return redisTemplate.opsForZSet().incrementScore(key, value, increment);
    }

    /**
     * 向有序集合中添加一个带分数的元素
     *
     * @param key     有序集合键
     * @param values  元素值
     * @param source  分数值
     * @return 是否成功添加
     */
    public Boolean zsetSet(final String key, final Object values, final Double source) {
        final Boolean add = redisTemplate.opsForZSet().add(key, values, source);
        return add;
    }

    /**
     * 获取有序集合中排名在指定范围内的元素及其分数（按分数由高到低排序）
     *
     * @param key   有序集合键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素及其分数的集合
     */
    public Set<ZSetOperations.TypedTuple<Object>> zsetReverseRangeWithScores(final String key, final Long start, final Long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 获取有序集合中排名在指定范围内的元素（按分数由高到低排序）
     *
     * @param key   有序集合键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<Object> zsetReverseRange(final String key, final Long start, final Long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 原子性地增加指定键的值并返回新值（适用于计数器场景）
     *
     * @param key 键
     * @return 自增后的新值
     */
    public long selfIncrease(final String key) {
        return redisTemplate.execute(new SessionCallback<Long>() {
            @Override
            public Long execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Long count = operations.opsForValue().increment(key);
                operations.exec();
                return count;
            }
        });
    }

    /**
     * 原子性地增加有序集合中某个元素的分数并返回新分数
     *
     * @param key   有序集合键
     * @param value 元素值
     * @return 自增后的新分数
     */
    public Double selfIncreaseSource(final String key, final Object value) {
        return redisTemplate.execute(new SessionCallback<Double>() {
            @Override
            public Double execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Double count = operations.opsForZSet().incrementScore(key, value, 1);
                operations.exec();
                return count;
            }
        });
    }

    /**
     * 从有序集合中移除多个指定的元素
     *
     * @param key     有序集合键
     * @param values  要移除的元素集合
     * @return 成功移除的元素数量
     */
    public long zsetDelAll(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 从有序集合中移除指定的元素
     *
     * @param key     有序集合键
     * @param values  要移除的元素
     * @return 成功移除的元素数量
     */
    public long zsetDel(final String key, Object values) {
        Long count = redisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 向列表尾部追加一个元素
     *
     * @param key   列表键
     * @param value 元素值
     * @return 列表在添加元素后的长度
     */
    public long listPush(final String key, final Object value) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 检查指定键是否存在（功能同 hasKey）
     *
     * @param key 键
     * @return 键是否存在
     */
    public Boolean doesItExist(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 向列表尾部追加多个元素（集合形式）
     *
     * @param key     列表键
     * @param values  要追加的元素集合
     * @return 列表在添加元素后的长度
     */
    public long listPushAll(final String key, final Collection<Object> values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 向列表尾部追加多个元素（可变参数形式）
     *
     * @param key     列表键
     * @param values  要追加的元素数组
     * @return 列表在添加元素后的长度
     */
    public long listPushAll(final String key, final Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 获取列表中指定范围的元素
     *
     * @param key   列表键
     * @param start 起始索引
     * @param end   结束索引
     * @return 指定范围的元素列表
     */
    public List<Object> listGet(final String key, final int start, final int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表的长度
     *
     * @param key 列表键
     * @return 列表的长度
     */
    public Long keySize(final String key) {
        return redisTemplate.opsForList().size(key);
    }
}
