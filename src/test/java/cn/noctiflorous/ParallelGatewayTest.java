package cn.noctiflorous;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 并行网关学习案例：人事与部门审批完成后汇合，按步骤单独运行各方法。
 */
@SpringBootTest
class ParallelGatewayTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署并行网关流程。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/ParallelGateway.bpmn20.xml")
                .name("并行网关案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 根据流程定义 key 启动最新版本，每次执行都会创建一个新实例。
     */
    @Test
    void startFlow() {
        String processId = "HolidayDemo5:1:8b63508c-d2f8-11f1-8e19-22554c7126c8";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
        System.out.println("流程实例 ID = " + processInstance.getId());
        System.out.println("流程定义 ID = " + processInstance.getProcessDefinitionId());
    }
    /**
     * 根据任务 ID 完成审批。
     */
    @Test
    void completeTask() {
        String taskId = "3463cc0e-d2f2-11f1-af75-5a5cb8594247";
        taskService.complete(taskId);
    }
}
