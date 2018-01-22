package com.zjcds.common.dozer;

import com.google.common.collect.FluentIterable;
import org.dozer.DozerBeanMapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * created date：2017-08-30
 * @author niezhegang
 */
@Configuration
public class DozerConfiguration implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public  DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() throws Exception{
        DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
        dozerBeanMapperFactoryBean.setMappingFiles(applicationContext.getResources("classpath*:/dozer/*mapping.xml"));
        return dozerBeanMapperFactoryBean;
    }

    @Bean
    public  DozerBeanMapper dozerBeanMapper() throws Exception{
        DozerBeanMapper dozerBeanMapper = (DozerBeanMapper) dozerBeanMapperFactoryBean().getObject();
        BeanCopyUtils.init(dozerBeanMapper);
        return dozerBeanMapper;
    }
    /**
     * created date：2017-09-18
     * @author niezhegang
     */
    public static abstract class BeanCopyUtils {

        private static DozerBeanMapper dozerBeanMapper ;

        public static void init(DozerBeanMapper dozerBeanMapper){
            if(BeanCopyUtils.dozerBeanMapper == null)
                BeanCopyUtils.dozerBeanMapper = dozerBeanMapper;
            else
                throw new IllegalArgumentException("BeanCopyUtils不允许重复初始化！");
        }

        public static void valid(){
            Assert.notNull(dozerBeanMapper,"BeanCopyUtils未初始化！");
        }

        public static <S,T> T copy(S source, T target){
            valid();
            if(source == null || target == null)
                return null;
            dozerBeanMapper.map(source,target);
            return target;
        }

        public static <S,T> T copy(S source,Class<T> targetClass){
            valid();
            if(source == null || targetClass == null)
                return null;
            return dozerBeanMapper.map(source,targetClass);
        }

        public static <S,T> List<T> copy(List<S> source, List<T> target){
            valid();
            if(source == null)
                return Collections.emptyList();
            dozerBeanMapper.map(source,target);
            return target;
        }

        public static <S,T> List<T> copy(List<S> sources,Class<T> targetClass){
            valid();
            List<T> targets = new ArrayList<>();
            if(sources != null && sources.size() > 0){
                FluentIterable.from(sources).forEach(new Consumer<S>() {
                    @Override
                    public void accept(S s) {
                        targets.add(copy(s,targetClass));
                    }
                });
            }
            return targets;
        }

    }

}
