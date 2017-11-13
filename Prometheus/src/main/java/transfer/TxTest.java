package transfer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017-6-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/TxTest-context.xml")
public class TxTest {
    @Autowired
    private IAccountService service;

    @Test
    public void testService(){
        service.transfer(1L,2L,100);
    }
}