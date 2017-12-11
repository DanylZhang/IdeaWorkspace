package anndi;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2017-5-15.
 */
public class SomeBean {
    @Autowired
    private OtherBean otherBean;

    public void setOtherBean(OtherBean otherBean) {
        this.otherBean = otherBean;
    }

    @Override
    public String toString() {
        return "SomeBean{" +
                "otherBean=" + otherBean +
                '}';
    }
}
