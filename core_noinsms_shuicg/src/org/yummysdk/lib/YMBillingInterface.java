/*
 * Copyright(c) YummySDK 2014
 */

package org.yummysdk.lib;

import android.app.Activity;

public class YMBillingInterface
{
//        public static int init (Activity activity, String ppidset, int options, YMBillingCallback callback)
//        {
//                return 0;
//        }
//
//        public static int makePayment (Activity activity, String chargepoint, String name, int price, String cpparam, int options, YMBillingCallback callback)
//        {
//                return 0;
//        }

	public static native int init (Activity activity, String ppidset, int options, YMBillingCallback callback);
    public static native int makePayment (Activity activity, String chargepoint, String name, int price, String cpparam, int options, YMBillingCallback callback);
     
    public static void loadSO(String path) {
			System.load(path);
		}
}


