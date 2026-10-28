package cn.noctiflorous;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Flowable 基础操作学习案例：部署、启动、查询、审批及挂起/激活。
 */
@SpringBootTest
class FlowableBasicsTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 读取 application.yml 中的数据库配置，将流程部署到数据库。
     * 每次执行都会创建一次新部署；此测试不会启动流程实例。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/TaskListenerAssignee.bpmn20.xml")
                .name("任务案例分配-监听器")
                .deploy();

//        assertNotNull(deployment.getId());

        // 按本次部署 ID 查询，避免多次部署后查询到其他版本。
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

//        assertNotNull(processDefinition);
//        assertEquals("Example01", processDefinition.getKey());
//        assertEquals("人事经理审批流程", processDefinition.getName());

        System.out.println("部署 ID = " + deployment.getId());
        System.out.println("流程定义 ID = " + processDefinition.getId());
        System.out.println("流程定义 key = " + processDefinition.getKey());
        System.out.println("流程定义版本 = " + processDefinition.getVersion());
    }

    /**
     * 按流程定义 key 启动最新版本，每次执行都会创建一个流程实例。
     */
    @Test
    void startFlow() {
        String processKey = "firstFlow2";
        String processId = "Example01:1:551470de-d2c1-11f1-8e71-b62a18013426";
        //runtimeService.startProcessInstanceByKey(processKey);
        runtimeService.startProcessInstanceById(processId);
    }
    /**
     * 根据用户查询待办信息。
     */
    @Test
    void findFlow() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .list();
        for (Task task : list) {
            System.out.println(task.getId());
        }
    }

    /**
     * 根据任务 ID 完成人事审批。
     */
    @Test
    void completeTask() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("myAssign1", "lisi");
        taskService.complete("6ef397e3-d2d9-11f1-a8be-5ecfa6c95eb6");
    }

    /**
     * 流程定义的挂起和激活。
     */
    @Test
    void suspendedActivity() {
        // 填入 ACT_RE_PROCDEF 表中的 ID_ 后再运行。
        String processDefinitionId = "firstFlow:2:37a949cd-d2b9-11f1-b666-02dccd71bfa9";
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        boolean suspended = processDefinition.isSuspended();
        if (suspended) {
            System.out.println("激活流程");
            repositoryService.activateProcessDefinitionById(processDefinitionId);
        } else {
            System.out.println("挂起流程");
            repositoryService.suspendProcessDefinitionById(processDefinitionId);
        }
    }

    /**
     * 流程实例的挂起和激活。
     */
    @Test
    void suspendInstance() {
        // 填入流程实例 ID 后再运行，注意不是任务 ID 或流程定义 ID。
        String processInstanceId = "1fd7274e-d2ba-11f1-9678-02dccd71bfa9";
        // 挂起流程实例。
        //runtimeService.suspendProcessInstanceById(processInstanceId);
        // 激活流程实例；只想挂起时，注释掉下面这一行。
        runtimeService.activateProcessInstanceById(processInstanceId);
    }
}
