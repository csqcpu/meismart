import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lottery.service.ad.AdUserService;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import redis.clients.jedis.Jedis;

public  class UpdateTest extends BaseJunit4Test {
    @Autowired    
    AdUserService adUserService;
    @Test
    public void test() throws Throwable {
//         ExecutorService threadPool = Executors.newFixedThreadPool(20);
//        for (int i =0; i < 100; i++) {
////          new Run("user"+i,key,num)
//            threadPool.execute(new UpdateRunnable(adUserService));
//        }
//        threadPool.shutdown();
        TestRunnable[] trs = new TestRunnable [10000];  
        for(int i=0;i<10000;i++){  
            trs[i]=new UpdateRunnable(adUserService);  
        }  

        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入 
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);  
        // 开发并发执行数组里定义的内容 
        mttr.runTestRunnables();  	
    	
    }
}