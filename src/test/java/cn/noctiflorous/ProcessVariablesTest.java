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
 * 流程变量学习案例，使用固定由 zhangsan、lisi 审批的流程。
 */
@SpringBootTest
class ProcessVariablesTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 流程部署操作。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/FixedAssignee.bpmn20.xml")
                .name("流程变量案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 启动流程实例，并设置流程变量。
     */
    @Test
    void startFlow() {
        // 填入 ACT_RE_PROCDEF 表中的 ID_ 后再运行。
        String processId = "firstFlow:3:3335cdf6-d2df-11f1-b793-5ecfa6c95eb6";
        Map<String, Object> variables = new HashMap<>();
        variables.put("var1", "test1");
        variables.put("var2", "test2");
        variables.put("var3", "test3");

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId, variables);
        System.out.println("流程实例 ID = " + processInstance.getId());
    }

    /**
     * 获取流程变量。
     */
    @Test
    void getVariables() {
        // 填入 startFlow() 打印的流程实例 ID 后再运行。
        String executionId = "cc4b9007-d2df-11f1-b948-5ecfa6c95eb6";
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        System.out.println(variables);
    }

    /**
     * 在流程启动后设置流程变量。
     */
    @Test
    void setVariables() {
        String executionId = "cc4b9007-d2df-11f1-b948-5ecfa6c95eb6";
        runtimeService.setVariable(executionId, "var4", "test4");
        runtimeService.setVariableLocal(executionId, "localVar1", "localTest1");

        // 填入 ACT_RU_TASK 表中的 ID_ 后再运行。
        String taskId = "cc4e281f-d2df-11f1-b948-5ecfa6c95eb6";
        taskService.setVariable(taskId, "taskVar1", "taskVar1");
        taskService.setVariableLocal(taskId, "taskLocalVar1", "taskLocalVar1");
    }

    /**
     * 完成任务审批，并传入流程变量。
     */
    @Test
    void completeTask() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("taskVar1", "taskVar1");
        String taskId = "8c750448-d2d2-11f1-8f53-5e9a4f16fd50";
        taskService.complete(taskId, variables);
    }
}
