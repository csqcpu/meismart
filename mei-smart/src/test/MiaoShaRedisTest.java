import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
import redis.clients.jedis.Jedis;
 
/**
 * redis
 *
 * @author 10255_000
 *
 */
 
public class MiaoShaRedisTest {
     public static void main(String[] args) {
            final String watchkeys = "watchkeys";
            ExecutorService executor = Executors.newFixedThreadPool(2);  //20个线程池并发数
            final int sellNum=999;
            final int threadNum=1000;
            final Jedis jedis = new Jedis("127.0.0.1", 6379);
            jedis.set(watchkeys, ""+sellNum);//设置起始的抢购数
           // jedis.del("setsucc", "setfail");
            jedis.close();
            CountDownLatch countDownLatch = new CountDownLatch(threadNum);
            long start = System.currentTimeMillis(); 
            for (int i = 0; i < threadNum; i++) {//设置1000个人来发起抢购
                executor.execute(new MiaoShaRunnable1("user"+getRandomString(6),sellNum,countDownLatch));
            }
    		try {
    			countDownLatch.await();
    			long end = System.currentTimeMillis();
    			System.out.println("运行耗时："+(end - start));
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
            executor.shutdown();
        }
 
      
     public static String getRandomString(int length) { //length是随机字符串长度
            String base = "abcdefghijklmnopqrstuvwxyz0123456789";  
            Random random = new Random();  
            StringBuffer sb = new StringBuffer();  
            for (int i = 0; i < length; i++) {  
                int number = random.nextInt(base.length());  
                sb.append(base.charAt(number));  
            }  
            return sb.toString();  
         } 
}