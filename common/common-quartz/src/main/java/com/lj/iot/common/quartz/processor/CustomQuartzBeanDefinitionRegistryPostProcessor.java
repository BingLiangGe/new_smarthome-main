package com.lj.iot.common.quartz.processor;

import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.quartz.anno.QuartzComponent;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.utils.Key;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class CustomQuartzBeanDefinitionRegistryPostProcessor implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry beanDefinitionRegistry;
    private ApplicationContext applicationContext;
    private static String GROUP = "execute";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] names = configurableListableBeanFactory.getBeanNamesForAnnotation(QuartzComponent.class);

        for (String name : names) {
            try {
                Object object = applicationContext.getBean(name);
                if (!(object instanceof Job)) {
                    continue;
                }
                QuartzComponent quartzComponent = object.getClass().getAnnotation(QuartzComponent.class);

                JobKey jobKey = new JobKey(name, GROUP);
                BeanDefinition jobDetailImpl = new RootBeanDefinition(JobDetailImpl.class);
                MutablePropertyValues mpv = jobDetailImpl.getPropertyValues();
                mpv.add("jobClass", object.getClass());
                mpv.add("description", name);
                mpv.add("key", jobKey);
                mpv.add("requestsRecovery", false);
                mpv.add("durability", true);
                beanDefinitionRegistry.registerBeanDefinition(name + "JobDetail", jobDetailImpl);

                JobDetailImpl jobDetail = applicationContext.getBean(name + "JobDetail", JobDetailImpl.class);

                Calendar cl = Calendar.getInstance();
                cl.setTime(new Date());
                cl.set(Calendar.MILLISECOND, 0);

                //cron
                if (StringUtils.hasText(quartzComponent.cron())) {

                    BeanDefinition trigger = new RootBeanDefinition(CronTriggerImpl.class);
                    MutablePropertyValues triggerPropertys = trigger.getPropertyValues();
                    triggerPropertys.add("cronExpression", quartzComponent.cron());
                    //triggerPropertys.add("timeZone", cronExpression.getTimeZone());
                    triggerPropertys.add("misfireInstruction", CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
                    triggerPropertys.add("description", GROUP);
                    triggerPropertys.add("key", new TriggerKey(name, GROUP));
                    triggerPropertys.add("jobKey", jobDetail.getKey());
                    //triggerPropertys.add("startTime", cl.getTime());

                    beanDefinitionRegistry.registerBeanDefinition(name + "JobTrigger", trigger);

                    //间隔执行
                } else if (quartzComponent.fixedRate() != -1L) {

                    BeanDefinition trigger = new RootBeanDefinition(SimpleTriggerImpl.class);
                    MutablePropertyValues triggerPropertys = trigger.getPropertyValues();
                    triggerPropertys.add("repeatInterval", quartzComponent.fixedRate());
                    triggerPropertys.add("repeatCount", SimpleTrigger.REPEAT_INDEFINITELY);
                    triggerPropertys.add("misfireInstruction", SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                    triggerPropertys.add("description", GROUP);
                    triggerPropertys.add("key", new TriggerKey(Key.createUniqueName(null), null));
                    triggerPropertys.add("jobKey", jobDetail.getKey());
                    triggerPropertys.add("startTime", cl.getTime());

                    beanDefinitionRegistry.registerBeanDefinition(name + "JobTrigger", trigger);
                }
            } catch (Exception e) {
                log.error("CustomQuartzBeanDefinitionRegistryPostProcessor.postProcessBeanFactory", e);
                throw CommonException.FAILURE("注册JOB异常");
            }
        }
    }
}
