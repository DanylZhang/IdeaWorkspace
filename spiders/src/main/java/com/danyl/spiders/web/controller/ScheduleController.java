package com.danyl.spiders.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/index")
    public void index() {
        Set<ScheduledTask> scheduledTasks = applicationContext.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks();
        scheduledTasks.stream().forEach(task -> {
            try {
                task.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(scheduledTasks);
    }
}