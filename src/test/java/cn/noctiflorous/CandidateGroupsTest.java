package cn.noctiflorous;

import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户与候选组学习案例：用户、组、成员关系及候选任务操作。
 */
@SpringBootTest
class CandidateGroupsTest {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 维护用户。
     */
    @Test
    void createUser() {
        User user = identityService.newUser("lisi");
        user.setEmail("lisi@qq.com");
        user.setFirstName("li");
        user.setLastName("si");
        user.setPassword("123");
        identityService.saveUser(user);
    }

    /**
     * 用户组的维护。
     */
    @Test
    void createGroup() {
        Group group = identityService.newGroup("xzb");
        group.setName("行政部");
        group.setType("type1");
        identityService.saveGroup(group);
    }

    /**
     * 维护用户和用户组的关联关系。
     */
    @Test
    void createMemberShip() {
        // 查询对应的用户组。
        Group group = identityService.createGroupQuery().groupId("xsb").singleResult();
        List<User> users = identityService.createUserQuery().list();
        for (User user : users) {
            // 用户和组的绑定。
            identityService.createMembership(user.getId(), group.getId());
        }
    }

    /**
     * 第一步：部署流程，每次执行都会创建一次新部署。
     */
    @Test
    void deployFlow() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/CandidateGroups.bpmn20.xml")
                .name("候选组案例")
                .deploy();
        System.out.println("部署 ID = " + deployment.getId());
    }

    /**
     * 第二步：启动最新版本，并为 ${assignee2} 传入审批人。
     * 每次执行都会创建一个新的流程实例。
     */
    @Test
    void startFlow() {
        String processId = "HolidayDemo3:1:698caeb9-d2ef-11f1-a948-869b57319429";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
        System.out.println("流程实例 ID = " + processInstance.getId());
        System.out.println("流程定义 ID = " + processInstance.getProcessDefinitionId());
    }


    /**
     * 根据用户所属的候选组查询待办任务。
     */
    @Test
    void findCandidateTask() {
        // 根据当前登录的用户查询所属用户组。
        Group group = identityService.createGroupQuery()
                .groupMember("zhangsan")
                .singleResult();
        System.out.println("group.getId() = " + group.getId());

        List<Task> list = taskService.createTaskQuery()
                .taskCandidateGroup(group.getId())
                .list();
        for (Task task : list) {
            System.out.println("task.getId() = " + task.getId());
        }
    }

    
    /**
     * 根据用户所属的候选组查询任务，并由 zhangsan 拾取。
     */
    @Test
    void claimTask() {
        // 根据当前登录的用户查询对应的用户组。
        Group group = identityService.createGroupQuery()
                .groupMember("zhangsan")
                .singleResult();
        System.out.println("group.getId() = " + group.getId());

        List<Task> list = taskService.createTaskQuery()
                .taskCandidateGroup(group.getId())
                .list();
        for (Task task : list) {
            taskService.claim(task.getId(), "zhangsan");
        }
    }
    /**
     * 根据任务 ID 完成审批。
     */
    @Test
    void completeTask() {
        taskService.complete("2b7c94e8-d2e3-11f1-b3c0-eefa2ff34bf2");
    }
}
