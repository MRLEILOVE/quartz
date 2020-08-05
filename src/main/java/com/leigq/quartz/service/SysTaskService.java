package com.leigq.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leigq.quartz.bean.dto.AddQuartzJobDTO;
import com.leigq.quartz.bean.vo.AddSysTaskVO;
import com.leigq.quartz.bean.vo.JobAndTriggerVO;
import com.leigq.quartz.bean.vo.SysTaskDetailVO;
import com.leigq.quartz.domain.entity.SysTask;
import com.leigq.quartz.domain.mapper.SysTaskMapper;
import com.leigq.quartz.web.exception.ServiceException;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 系统自己创建的任务表服务
 */
@Transactional
@Service
public class SysTaskService extends ServiceImpl<SysTaskMapper, SysTask> {

    /**
     * Quartz自带表的任务服务
     */
    private final QuartzJobService quartzJobService;

    public SysTaskService(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    /**
     * 添加任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:20 <br>
     *
     * @param addSysTaskVO 添加任务接受参数 VO
     */
    public void addJob(AddSysTaskVO addSysTaskVO) {
        try {
            // 先添加一条任务记录到自己的任务表，应该后面任务日志需要任务id
            SysTask sysTask = SysTask.builder().build();
            BeanUtils.copyProperties(addSysTaskVO, sysTask);
            sysTask.setCreateTime(new Date());
            // 创建人根据自己的系统确定，这里默认写死
            sysTask.setCreator(1L);
            this.save(sysTask);

            // 添加 Quartz 任务表
            AddQuartzJobDTO addQuartzJobDTO = AddQuartzJobDTO.builder().build();
            BeanUtils.copyProperties(addSysTaskVO, addQuartzJobDTO);
            // 转换执行参数为 Map
            addQuartzJobDTO.setDataMap(addSysTaskVO.transExecParams(addSysTaskVO.getExecParams()));
            addQuartzJobDTO.setTaskId(sysTask.getId());
            quartzJobService.addJob(addQuartzJobDTO);
        } catch (SchedulerException e) {
            throw new ServiceException("添加任务失败", e);
        }
    }


    /**
     * 执行任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 2:57 <br>
     *
     * @param cls 任务类，可用 类名.class 获得
     */
    public void executeJob(Class<?> cls) {
        try {
            quartzJobService.executeJob(cls);
        } catch (SchedulerException e) {
            throw new ServiceException("执行任务失败", e);
        }
    }

    /**
     * 执行任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 2:57 <br>
     *
     * @param jobSimpleName 类名
     * @param jobGroupName  类组名
     */
    public void executeJob(String jobSimpleName, String jobGroupName) {
        try {
            quartzJobService.executeJob(jobSimpleName, jobGroupName);
        } catch (SchedulerException e) {
            throw new ServiceException("执行任务失败", e);
        }
    }

    /**
     * 暂停任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 2:57 <br>
     *
     * @param cls 任务类，可用 类名.class 获得
     */
    public void pauseJob(Class<?> cls) {
        try {
            quartzJobService.pauseJob(cls);
        } catch (SchedulerException e) {
            throw new ServiceException("暂停任务失败", e);
        }
    }

    /**
     * 暂停任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 2:57 <br>
     *
     * @param jobSimpleName 类名
     * @param jobGroupName  类组名
     */
    public void pauseJob(String jobSimpleName, String jobGroupName) {
        try {
            quartzJobService.pauseJob(jobSimpleName, jobGroupName);
        } catch (SchedulerException e) {
            throw new ServiceException("暂停任务失败", e);
        }
    }

    /**
     * 恢复任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:00 <br>
     *
     * @param cls 任务类，可用 类名.class 获得
     */
    public void resumeJob(Class<?> cls) {
        try {
            quartzJobService.resumeJob(cls);
        } catch (SchedulerException e) {
            throw new ServiceException("恢复任务失败", e);
        }
    }


    /**
     * 恢复任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:00 <br>
     *
     * @param jobSimpleName 类名
     * @param jobGroupName  类组名
     */
    public void resumeJob(String jobSimpleName, String jobGroupName) {
        try {
            quartzJobService.resumeJob(jobSimpleName, jobGroupName);
        } catch (SchedulerException e) {
            throw new ServiceException("恢复任务失败", e);
        }
    }

    /**
     * 更新任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:50 <br>
     *
     * @param jobClassName   任务全类名
     * @param jobGroupName   类组名
     * @param cronExpression 任务表达式
     */
    public void rescheduleJob(SysTaskDetailVO sysTaskDetailVO) {
        try {
            SysTask sysTask = new SysTask();
            BeanUtils.copyProperties(sysTaskDetailVO, sysTask);
            this.update(Wrappers.update(sysTask));


            this.update(Wrappers.<SysTask>update().set("cron", "111"));

            AddQuartzJobDTO addQuartzJobDTO = new AddQuartzJobDTO();
            // TODO
//            quartzJobService.rescheduleJob(jobClassName, jobGroupName, cronExpression);
            quartzJobService.rescheduleJob(null, null, null);
        } catch (SchedulerException e) {
            throw new ServiceException("更新任务失败", e);
        }
    }

    /**
     * 更新任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:50 <br>
     *
     * @param cls            任务类，可用 类名.class 获得
     * @param cronExpression 表达式
     */
    public void rescheduleJob(Class<?> cls, String cronExpression) {
        try {
            quartzJobService.rescheduleJob(cls, cronExpression);
        } catch (SchedulerException e) {
            throw new ServiceException("更新任务失败", e);
        }
    }


    /**
     * 删除任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:53 <br>
     *
     * @param jobSimpleName 任务类名
     * @param jobGroupName  类组名
     */
    public void deleteJob(String jobSimpleName, String jobGroupName) {
        try {
            quartzJobService.deleteJob(jobSimpleName, jobGroupName);
        } catch (SchedulerException e) {
            throw new ServiceException("删除任务失败", e);
        }
    }

    /**
     * 删除任务
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/28 3:53 <br>
     *
     * @param cls 任务类，可用 类名.class 获得
     */
    public void deleteJob(Class<?> cls) {
        try {
            quartzJobService.deleteJob(cls);
        } catch (SchedulerException e) {
            throw new ServiceException("删除任务失败", e);
        }
    }


    /**
     * 获取任务与触发器详细信息
     * <p>
     * 创建人：LeiGQ <br>
     * 创建时间：2019/5/19 1:18 <br>
     */
    public IPage<JobAndTriggerVO> getJobAndTriggerDetails(int pageNum, int pageSize) {
        return quartzJobService.getJobAndTriggerDetails(pageNum, pageSize);
    }

}
