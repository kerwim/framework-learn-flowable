package cn.noctiflorous.listener;

import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;

public class MyListener01 implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("MyListener01.notify() 执行了，事件：" + delegateTask.getEventName());
        if (EVENTNAME_CREATE.equals(delegateTask.getEventName())) {
            // 任务创建时指定审批人。
            delegateTask.setAssignee("kerwim");
        }
    }
}
