import com.lottery.service.ad.AdUserService;

import net.sourceforge.groboutils.junit.v1.TestRunnable;

public class UpdateRunnable extends TestRunnable {

    AdUserService adUserService;
    
    public UpdateRunnable( AdUserService adUserService) {
        this.adUserService = adUserService;
        System.out.println(adUserService);
    }

    @Override
    public void runTest() {
    	int updateNum=adUserService.updateAccountByName("aa");
    	System.out.println(updateNum);
    }

}