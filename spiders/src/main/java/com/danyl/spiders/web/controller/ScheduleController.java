package com.danyl.spiders.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/index")
    @ResponseBody
    public Set<ScheduledTask> index() {
        ThreadPoolTaskScheduler bean = applicationContext.getBean(ThreadPoolTaskScheduler.class);
        // 此方法关不掉那些trycatch ignore 装聋作哑的任务
        bean.shutdown();
        Set<ScheduledTask> scheduledTasks = applicationContext.getBean(ScheduledTaskHolder.class).getScheduledTasks();
        scheduledTasks.stream().forEach(task -> {
            try {
                task.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return scheduledTasks;
    }
}