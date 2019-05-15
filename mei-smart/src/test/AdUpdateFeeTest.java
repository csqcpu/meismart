import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lottery.redis.MybatisRedisCache;
import com.lottery.service.ad.AdUserService;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import redis.clients.jedis.Jedis;

public  class AdUpdateFeeTest extends BaseJunit4Test {
	
    @Test
    public void test() throws Throwable {
//    	MybatisRedisCache mybatisRedisCache=new  MybatisRedisCache(null);
    	CountDownLatch countDownLatch=new CountDownLatch(1);
        TestRunnable[] trs = new TestRunnable [1000];  
        for(int i=0;i<1000;i++){  
            trs[i]=new AdUpdateFeeRunnable(null,countDownLatch);  
        }  
        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入 
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);  
        // 开发并发执行数组里定义的内容 
        mttr.runTestRunnables();  	
    }
}