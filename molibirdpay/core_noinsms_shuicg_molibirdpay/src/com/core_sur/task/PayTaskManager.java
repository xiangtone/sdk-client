package com.core_sur.task;

import java.util.HashMap;
import java.util.Map;

public class PayTaskManager {
	static PayTaskManager payTaskManager = new PayTaskManager();
	static Map<String, PayTask> payTasks = new HashMap<String, PayTask>();

	public static PayTaskManager getInstance() {
		return payTaskManager;
	}

	public static PayTask getTask(String tid) {
		return payTasks.get(tid);
	}

	public PayTask create(String tid) {
		if (payTasks.containsKey(tid)) {
			return payTasks.get(tid);
		} else {
			PayTask payTask = new PayTask();
			payTasks.put(tid, payTask);
			return payTask;
		}
	}
}
