/*
 * Copyright(c) YummySDK 2014
 */

package org.yummysdk.lib;

public abstract class YMBillingCallback
{
        public abstract void onInitSuccess (String extra);
        public abstract void onInitFail (String extra, int code);

        public abstract void onSuccess (String chargepoint);
        public abstract void onCancel (String chargepoint);
        public abstract void onFail (String chargepoint, int code);
}
