/*
 * Copyright(c) YummySDK 2014
 */

package org.yummysdk.lib;

import android.app.Activity;
import android.content.Context;

public class YMBillingInterface
{
    // Call within activity
    public static native int init (Activity activity, String ppidset, int options, YMBillingCallback callback);
    public static native int makePayment (Activity activity, String chargepoint, String name, int price, String cpparam, int options, YMBillingCallback callback);

    // Call within service
    public static native int sinit (Context context, String ppidset, int options, YMBillingCallback callback);

    public static void loadSO(String path) {
		System.load(path);
	}
}


