import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.lottery.model.ad.AdOnline;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.service.ad.AdUserService;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class AdUpdateFeeRunnable extends TestRunnable {
	MybatisRedisCache mybatisRedisCache;
	CountDownLatch countDownLatch;
	byte[] watchkeys = "adOnline:id:1".getBytes();// 监视keys
	Jedis jedis = new Jedis("127.0.0.1", 6379);
	String userinfo;

	public AdUpdateFeeRunnable(MybatisRedisCache mybatisRedisCache,CountDownLatch countDownLatch) {
		this.mybatisRedisCache = mybatisRedisCache;
		this.countDownLatch =countDownLatch;
	}

	@Override
	public void run() {
		try {
			RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
			jedis.watch(watchkeys);
			//String result = jedis.get
			//byte[] result = jedis.get(watchkeys).getBytes();
			AdOnline adOnlie = (AdOnline) serializer.deserialize(jedis.get(watchkeys));
			int valint = adOnlie.getFee_id();
			if (valint >= 1) {
				Transaction tx = jedis.multi();// 开启事务
				valint =valint-1;
				adOnlie.setFee_id(valint);
				tx.set(watchkeys, serializer.serialize(adOnlie));
				List<Object> list = tx.exec();// 提交事务，
				if (list == null || list.size() == 0) {
					String failuserifo = "fail" + userinfo;
					String failinfo = "用户：" + failuserifo + "计算失败";
				} else {
						String succuserifo = "succ" + userinfo;
						String succinfo = "用户：" + succuserifo + "计算成功，当前计算次数:" + (1 - (valint - 5000));
						System.out.println(succinfo);
				}
			} else {
				String failuserifo = "fail" + userinfo;
				String failinfo1 = "用户：" + failuserifo + "费用不足";
				// System.out.println(failinfo1);
				// Thread.sleep(500);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
			countDownLatch.countDown();
		}
	}

	@Override
	public void runTest() throws Throwable {
		// TODO 自动生成的方法存根
		
	}
}