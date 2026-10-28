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
 * 包容网关学习案例，按步骤单独运行各方法。
 */
@SpringBootTest
class InclusiveGatewayTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署包容网关流程。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/InclusiveGateway.bpmn20.xml")
                .name("包容网关案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 按流程定义 ID 启动，完成用户申请前需设置请假天数 num。
     */
    @Test
    void startFlow() {
        // 部署后填入 ACT_RE_PROCDEF 表中的 ID_。
        String processId = "InclusiveGateway:1:50458270-d2fc-11f1-b69a-22554c7126c8";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
        System.out.println("流程实例 ID = " + processInstance.getId());
        System.out.println("流程定义 ID = " + processInstance.getProcessDefinitionId());
    }
    /**
     * 完成用户申请，传入请假天数以选择包容网关分支。
     */
    @Test
    void completeTask() {
        // 填入 ACT_RU_TASK 表中用户申请任务的 ID_。
        String taskId = "a5cad198-d2fc-11f1-8c7b-22554c7126c8";
        Map<String, Object> map = new HashMap<>();
        map.put("num", 5);
        taskService.complete(taskId);
    }
}
