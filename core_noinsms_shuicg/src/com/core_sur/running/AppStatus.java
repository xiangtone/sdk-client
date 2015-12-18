package com.core_sur.running;

import com.core_sur.bean.DestroyMessage;
import com.core_sur.bean.StartMessage;
import com.core_sur.finals.ParamFinals;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CActivityManager;
import com.core_sur.tools.CommonUtils;

/**
 * App开启关闭状态
 * tbl_Statistic表操作
 * @author Administrator
 * 
 */
public class AppStatus implements Runnable {
	private int appStatus = -1;

	public AppStatus() {
	}

	@Override
	public void run() {
		boolean isRunningProgress = CActivityManager
				.getRunningProgressIsThis(EPCoreManager.getInstance().getContext());
		if (isRunningProgress) {
			if (appStatus != ParamFinals.STATISTICAL_START) {
				appStatus = ParamFinals.STATISTICAL_START;
				EPCoreManager.getInstance().sendMessenger(
						URLFinals.WEB_STATISTICAL,
						new StartMessage(CommonUtils.getSp(
								EPCoreManager.getInstance().getContext()).getString(
								"version", "0")
								+ ""), null);
			}
		} else {

			if (appStatus != ParamFinals.STATISTICAL_END) {
				appStatus = ParamFinals.STATISTICAL_END;
				EPCoreManager.getInstance().sendMessenger(
						URLFinals.WEB_STATISTICAL, new DestroyMessage(), null);
			}
		}
	}
}
