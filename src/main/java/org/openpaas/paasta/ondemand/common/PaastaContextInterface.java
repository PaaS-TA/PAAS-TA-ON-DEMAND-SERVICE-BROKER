package org.openpaas.paasta.ondemand.common;

import java.util.Date;

public abstract class PaastaContextInterface {

    Date create_time;

    public Date getCreate_time() {
        return create_time;
    }

}
