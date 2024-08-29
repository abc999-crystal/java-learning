package springboot.apiDocGenerator;

/**
 * @Description TODO
 * @Date 2024/8/27 15:49
 * @Version V1.0.0
 * @Author zdd55
 */

public class Request {
    /**
     * 编号
     */
    private String code;
    /**
     * cpu 配额
     */
    private Long cpuQuota;
    /**
     * 延迟时间
     */
    private String delayTime;
    /**
     * 描述
     */
    private String description;
    /**
     * 环境code
     */
    private String environmentCode;
    /**
     * 重试间隔
     */
    private String failRetryInterval;
    /**
     * 重试次数
     */
    private String failRetryTimes;
    /**
     * 运行标志 ，NO:禁止执行 、YES:正常， 默认正常
     */
    private String flag;
    private String id;
    /**
     * 缓存执行，NO:否， 默认不缓存执行、YES:是
     */
    private String isCache;
    /**
     * 最大内存 MB
     */
    private String memoryMax;
    /**
     * 名称
     */
    private String name;
    /**
     * 项目code
     */
    private String projectCode;
    /**
     * 入参
     */
    private String reqParams;
    /**
     * 出参
     */
    private String respParams;
    /**
     * 任务执行类型  ，BATCH、STREAM
     */
    private String taskExecuteType;
    /**
     * 任务组id
     */
    private String taskGroupId;
    /**
     * 任务组优先级
     */
    private String taskGroupPriority;
    /**
     * 任务参数
     */
    private String taskParams;
    /**
     * 任务优先级，HIGHEST、HIGH、MEDIUM 、LOW、LOWEST
     */
    private String taskPriority;
    /**
     * 任务类型，SHELL、JAVA、PYTHON、SQL
     */
    private String taskType;
    private Long timeout;
    /**
     * 超时告警
     */
    private String timeoutFlag;
    /**
     * 超时通知策略
     */
    private String timeoutNotifyStrategy;
    /**
     * 版本
     */
    private String version;
    /**
     * work分组
     */
    private String wokerGroup;

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public Long getcpuQuota() { return cpuQuota; }
    public void setcpuQuota(Long value) { this.cpuQuota = value; }

    public String getDelayTime() { return delayTime; }
    public void setDelayTime(String value) { this.delayTime = value; }

    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }

    public String getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(String value) { this.environmentCode = value; }

    public String getFailRetryInterval() { return failRetryInterval; }
    public void setFailRetryInterval(String value) { this.failRetryInterval = value; }

    public String getFailRetryTimes() { return failRetryTimes; }
    public void setFailRetryTimes(String value) { this.failRetryTimes = value; }

    public String getFlag() { return flag; }
    public void setFlag(String value) { this.flag = value; }

    public String getid() { return id; }
    public void setid(String value) { this.id = value; }

    public String getIsCache() { return isCache; }
    public void setIsCache(String value) { this.isCache = value; }

    public String getMemoryMax() { return memoryMax; }
    public void setMemoryMax(String value) { this.memoryMax = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String value) { this.projectCode = value; }

    public String getReqParams() { return reqParams; }
    public void setReqParams(String value) { this.reqParams = value; }

    public String getRespParams() { return respParams; }
    public void setRespParams(String value) { this.respParams = value; }

    public String getTaskExecuteType() { return taskExecuteType; }
    public void setTaskExecuteType(String value) { this.taskExecuteType = value; }

    public String getTaskGroupId() { return taskGroupId; }
    public void setTaskGroupId(String value) { this.taskGroupId = value; }

    public String getTaskGroupPriority() { return taskGroupPriority; }
    public void setTaskGroupPriority(String value) { this.taskGroupPriority = value; }

    public String getTaskParams() { return taskParams; }
    public void setTaskParams(String value) { this.taskParams = value; }

    public String getTaskPriority() { return taskPriority; }
    public void setTaskPriority(String value) { this.taskPriority = value; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String value) { this.taskType = value; }

    public Long getTimeout() { return timeout; }
    public void setTimeout(Long value) { this.timeout = value; }

    public String getTimeoutFlag() { return timeoutFlag; }
    public void setTimeoutFlag(String value) { this.timeoutFlag = value; }

    public String getTimeoutNotifyStrategy() { return timeoutNotifyStrategy; }
    public void setTimeoutNotifyStrategy(String value) { this.timeoutNotifyStrategy = value; }

    public String getVersion() { return version; }
    public void setVersion(String value) { this.version = value; }

    public String getWokerGroup() { return wokerGroup; }
    public void setWokerGroup(String value) { this.wokerGroup = value; }
}
