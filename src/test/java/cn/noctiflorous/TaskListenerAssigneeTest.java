package cn.noctiflorous;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务监听器分配学习案例：流程变量指定第一个审批人，监听器指定第二个审批人。
 */
@SpringBootTest
class TaskListenerAssigneeTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 第一步：部署流程，每次执行都会创建一次新部署。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/TaskListenerAssignee.bpmn20.xml")
                .name("任务监听器分配案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 第二步：启动最新版本，并为 ${assignee2} 传入审批人。
     * 每次执行都会创建一个新的流程实例。
     */
    @Test
    void startFlow() {
        String processId = "Example02:1:7e83711d-d2db-11f1-a0a7-5ecfa6c95eb6";

        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee2", "zhangsan");

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId, variables);
        System.out.println("流程实例 ID = " + processInstance.getId());
        System.out.println("流程定义 ID = " + processInstance.getProcessDefinitionId());
    }

    /**
     * 第三步：查询待办。第一个节点查 zhangsan，第二个节点改为 kerwim。
     */
    @Test
    void findFlow() {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionId("Example02")
                .taskAssignee("zhangsan")
                .list();
        for (Task task : list) {
            System.out.println(task.getId());
        }
    }

    /**
     * 第四步：填入当前任务 ID 后完成审批。
     * 第一个任务完成后，监听器会把第二个任务分配给 kerwim。
     * 完成第二个任务时，需要换成第二个任务的新 ID。
     */
    @Test
    void completeTask() {
        String taskId = "9e774d5f-d2dd-11f1-924c-5ecfa6c95eb6";
        taskService.complete(taskId);
    }
}
