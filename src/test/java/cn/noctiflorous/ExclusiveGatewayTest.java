package cn.noctiflorous;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * 排他网关学习案例：根据请假天数选择审批分支。
 */
@SpringBootTest
class ExclusiveGatewayTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署排他网关流程。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/ExclusiveGateway.bpmn20.xml")
                .name("排他网关案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 启动流程实例，请假天数 num 在完成用户申请任务时传入。
     */
    @Test
    void startFlow() {
        String processId = "HolidayDemo4:1:38baf4b1-d2f6-11f1-bc08-76114c79c402";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
        System.out.println("流程实例 ID = " + processInstance.getId());
    }

    /**
     * 任务审批，传入请假天数。
     */
    @Test
    void completeTask() {
        String taskId = "1bc4c04d-d2f7-11f1-8386-22554c7126c8";
        Map<String, Object> map = new HashMap<>();
        map.put("num", 5);
        taskService.complete(taskId, map);
    }
}
