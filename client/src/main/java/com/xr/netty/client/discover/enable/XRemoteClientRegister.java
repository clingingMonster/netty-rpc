package com.xr.netty.client.discover.enable;

import com.xr.netty.client.discover.Discover;
import com.xr.netty.client.discover.model.enums.EnableXRemoteClient;
import com.xr.netty.client.discover.model.enums.XRemoteClient;
import com.xr.netty.core.exception.XException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * @author XR
 * Created  on 2020/12/7.
 */
public class XRemoteClientRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

    private ClassLoader classLoader;

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableXRemoteClient.class.getCanonicalName());

        //注册服务发现
        Class discover = (Class<Discover>) attributes.get("discover");
        registerClass(discover, beanDefinitionRegistry);

        // 注册路由
        Class loadBalance = (Class<Discover>) attributes.get("loadBalance");
        registerClass(loadBalance, beanDefinitionRegistry);

        // 注册客户端
        String[] basePackages = (String[]) attributes.get("basePackages");

        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(XRemoteClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = beanDefinition.getMetadata();
                    Assert.isTrue(metadata.isInterface(), "@XRemoteClient can only be specified on an interface");

                    Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(XRemoteClient.class.getCanonicalName());
                    registerFeignClient(beanDefinitionRegistry, metadata, annotationAttributes);
                }
            }
        }
    }

    private void registerClass(Class cla, BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cla);
        beanDefinitionRegistry.registerBeanDefinition(cla.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());

    }


    private void registerFeignClient(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(XRemoteClientFactoryBean.class);
        String name = getName(attributes);
        definition.addPropertyValue("serverName", name);
        definition.addPropertyValue("type", className);
        definition.addPropertyValue("fallback", attributes.get("fallback"));
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        boolean primary = (Boolean) attributes.get("primary");
        beanDefinition.setPrimary(primary);
        String alias = name + "RemoteClient";
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private String getName(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");

        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        name = this.environment.resolvePlaceholders(name);
        if (!StringUtils.hasText(name)) {
            throw new XException("服务名称为空");
        }
        return name;
    }


    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    XRemoteClientRegister.this.classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: "
                                            + beanDefinition.getMetadata().getClassName(),
                                    ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }


}
