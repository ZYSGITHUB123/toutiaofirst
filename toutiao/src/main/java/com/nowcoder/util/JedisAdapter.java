package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

//Jedis 是redis的包装     一种KV形式的数据库概念
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private static String url = "39.108.173.242";
    private static int port = 6379;
    private static int MAX_ACTIVE = 1024;
    private static int MAX_IDLE = 1024;
    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    private static boolean TEST_ON_BORROW = true;
    //redis线程池配置
    private  static JedisPoolConfig config() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(TEST_ON_BORROW);
        return config;
    }
   private static JedisPool jedisPool = new JedisPool(config(), url, port, TIMEOUT);

    @Override
    public void afterPropertiesSet() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(TEST_ON_BORROW);
        jedisPool = new JedisPool(config, url, port, TIMEOUT);

    }

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    public static void main(String[] argv) {
        Jedis jedis = new Jedis(url);
        jedis.flushAll();
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));

        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));

        jedis.setex("hello2", 15, "world");//设置过期时间为15

        jedis.set("pv", "100");       //浏览量的概念pv
        jedis.incr("pv");   //每次加1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);   //增加步长为5
        print(2, jedis.get("pv"));

        //列表的操作list
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.llen(listName));  //listName的长度
        print(5, jedis.lpop(listName));  //将列表中的第一个内容推出来
        print(6, jedis.llen(listName)); //推出来以后列表的长度
        print(7, jedis.lindex(listName, 3));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(10, jedis.lrange(listName, 0, 12));


        //hash
        String userKey = "user12";
        jedis.hset(userKey, "name", "Jim"); //hash
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");

        print(11, jedis.hget(userKey, "name"));
        print(12, jedis.hgetAll(userKey));
        print(13, jedis.hdel(userKey, "phone"));
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hkeys(userKey));
        print(16, jedis.hvals(userKey));
        print(17, jedis.hexists(userKey, "email"));
        print(18, jedis.hexists(userKey, "age"));

        jedis.hsetnx(userKey, "school", "NJUPT"); //如果没有的话就设置,添加
        jedis.hsetnx(userKey, "name", "ZYS");
        print(19, jedis.hgetAll(userKey));

        //set集合的概念
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
        print(20, jedis.smembers(likeKeys1));
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys1));//求两个集合的交
        print(23, jedis.sunion(likeKeys1, likeKeys1));//求两个集合的并
        print(24, jedis.sdiff(likeKeys1, likeKeys2));//求1中2没有的
        print(25, jedis.sismember(likeKeys1, "5"));//判断5是否是likelist1的成员
        jedis.srem(likeKeys1, "5");                 //把5值从中去掉
        print(26, jedis.smembers(likeKeys1));           //查看整个集合里的元素
        print(27, jedis.scard(likeKeys1));             //查看集合长度
        jedis.smove(likeKeys2, likeKeys1, "14");     //将2中的元素14转移到1中
        print(28, jedis.scard(likeKeys1));            //查看1的长度
        print(29, jedis.smembers(likeKeys1));          //查看1的所有元素

        //优先队列，排行榜 Sortted Set
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 80, "Mei");
        jedis.zadd(rankKey, 75, "Lucy");
        print(30, jedis.zcard(rankKey));//该排行榜的长度
        print(31, jedis.zcount(rankKey, 61, 100));//统计某个区间内的个数
        print(32, jedis.zscore(rankKey, "Lucy"));//查找某个成员的分数

        jedis.zincrby(rankKey, 2, "Lucy");  //某个存在的增加两分
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");   //若该成员不存在则增加后，会自动添加这个分数的变量
        print(34, jedis.zcount(rankKey, 0, 100));
        print(35, jedis.zrange(rankKey, 1, 3));//输出1到3，从小到大
        print(35, jedis.zrange(rankKey, 1, 3));//从大到小1到3

        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }              //按照分数排序的一个全显示
        print(37, jedis.zrangeByScore(rankKey, "0", "100"));
        print(38, jedis.zrank(rankKey, "Ben"));//得出某个成员的正数多少名
        print(39, jedis.zrevrank(rankKey, "Ben"));//得出某个成员的倒数多少名

        //以上为redis中的一些核心数据结构

        //Jedis也有线程池(连接池)的概念


        for (int i = 0; i < 100; i++) {
            Jedis j = jedisPool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();          //如果不关掉难以实现线程的复用
        }

    }



    private Jedis getJedis() {
        return jedisPool.getResource();
    }
       //把以下几个主要的方法给
    public long sadd(String key, String value) {          //某个线程redis执行集合增加方法
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
           return jedis.sadd(key,value);

        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }

    }

    public long srem(String key, String value) {      //某个线程redis执行集合减少方法
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            return jedis.srem(key,value);

        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }

    }

    public boolean sismember(String key, String value) {         //某个线程redis执行判断元素是否在的方法
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            return jedis.sismember(key,value);

        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return false;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }

    }
    public long scard(String key) {             //某个线程redis执行计算集合大小的方法
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            return jedis.scard(key);

        }catch(Exception e){
            logger.error("发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }

    }

}