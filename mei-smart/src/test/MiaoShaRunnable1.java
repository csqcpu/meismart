import java.util.List;
import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class MiaoShaRunnable1 implements Runnable {
	int sellNum;
	CountDownLatch countDownLatch;
	String watchkeys = "watchkeys";// 监视keys
	Jedis jedis = new Jedis("127.0.0.1", 6379);
	String userinfo;

	public MiaoShaRunnable1() {
	}

	public MiaoShaRunnable1(String uinfo, int sellNum, CountDownLatch countDownLatch) {
		this.userinfo = uinfo;
		this.sellNum = sellNum;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		List<Object> list = null;
		int valint = sellNum;
		try {
			while ((list == null || list.size() == 0) && valint >= 1) {
				jedis.watch(watchkeys);// watchkeys
				String val = jedis.get(watchkeys);
				valint = Integer.valueOf(val);

				if (valint <= sellNum && valint >= 1) {
					Transaction tx = jedis.multi();// 开启事务
					// tx.incr("watchkeys");
					tx.incrBy("watchkeys", -1);
					list = tx.exec();// 提交事务，如果此时watchkeys被改动了，则返回null
				}
			}
			if (!(list == null || list.size() == 0)) {
				String succuserifo = "succ" + userinfo;
				String succinfo = "用户：" + succuserifo + "抢购成功，当前抢购成功人数:" + (1 - (valint - sellNum));
				System.out.println(succinfo);
				/* 抢购成功业务逻辑 */
			} else {
				String failuserifo = "fail" + userinfo;
				String failinfo = "用户：" + failuserifo + "商品争抢失败，抢购失败";
				System.out.println(failinfo);
				/* 抢购失败业务逻辑 */
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
			countDownLatch.countDown();
		}

	}

}