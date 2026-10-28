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
 * 候选用户学习案例：查询、拾取、审批、归还及指派。
 */
@SpringBootTest
class CandidateUsersTest {

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
                .addClasspathResource("process/CandidateUsers.bpmn20.xml")
                .name("候选人案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 启动流程实例，并设置人事审批的候选人。
     */
    @Test
    void startFlow() {
        String processId = "HolidayDemo2:1:db715fc8-d2e5-11f1-92ab-eefa2ff34bf2";
        Map<String, Object> variables = new HashMap<>();
        variables.put("candidate1", "zhangsan");
        variables.put("candidate2", "lisi");
        variables.put("candidate3", "wangwu");

        // candidate4、candidate5 需要在进入经理审批节点前设置。
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId, variables);
        System.out.println("流程实例 ID = " + processInstance.getId());
    }

    /**
     * 根据候选人查询待办信息。
     * 候选人还不是审批人。
     * 候选人需要通过拾取的操作，把候选人变更为审批人。
     * 多个候选人，只有一个可以变为审批人，即拾取的操作。
     * 审批人如果不想审批了，可以归还，从审批人变回候选人。
     */
    @Test
    void findFlow() {
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan")
                .list();
        for (Task task : list) {
            System.out.println("task.getId() = " + task.getId());
        }
    }
    
    /**
     * 拾取任务，将 zhangsan 从候选人变为审批人。
     */
    @Test
    void claimTask() {
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan")
                .list();
        for (Task task : list) {
            taskService.claim(task.getId(), "zhangsan");
        }
    }


    /**
     * 完成 zhangsan 的任务审批，并设置经理审批节点的候选人。
     */
    @Test
    void completeTask() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .list();
        Map<String, Object> variables = new HashMap<>();
        variables.put("candidate4", "kerwim1");
        variables.put("candidate5", "kerwim2");
        for (Task task : list) {
            taskService.complete(task.getId(), variables);
        }
    }

    /**
     * 拾取任务，将 kerwim1 从候选人变为审批人。
     */
    @Test
    void claimTask1() {
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("kerwim1")
                .list();
        for (Task task : list) {
            taskService.claim(task.getId(), "kerwim1");
        }
    }
    
    /**
     * 归还任务，将 kerwim1 已拾取的任务放回候选人待办池。
     */
    @Test
    void unclaimTask() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("kerwim1")
                .list();
        for (Task task : list) {
            taskService.unclaim(task.getId());
        }
    }
    
    /**
     * 指派操作：将 kerwim1 的待办任务指派给 kerwim2 审批。
     */
    @Test
    void setAssignee() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("kerwim1")
                .list();
        for (Task task : list) {
            System.out.println("--指派--");
            taskService.setAssignee(task.getId(), "kerwim2");
        }
    }
    /**
     * 完成经理审批，填入 ACT_RU_TASK 表中的任务 ID 后再运行。
     */
    @Test
    void completeTask1() {
        taskService.complete("5c9e7eec-d2e9-11f1-9ae8-fac29bd0eec8");
    }
}
