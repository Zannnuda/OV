package com.javamentor.qa.platform.webapp.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String HEADER_SIDEBAR_FOOTER = "headerSidebarFooter";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/ask").setViewName("askQuestion");
        registry.addViewController("/chat").setViewName("chat");
        registry.addViewController("/registration").setViewName("registration");
        registry.addViewController("/registration/confirm").setViewName("registrationConfirm");
        registry.addViewController("/chatws").setViewName("chatws");
        registry.addViewController("/test").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/site").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/users").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/tagsAria").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/questionAria").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/unansweredAria").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/question/{questionId}").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/user/profile/{id}").setViewName("userPage");
        registry.addViewController("/search").setViewName(HEADER_SIDEBAR_FOOTER);
        registry.addViewController("/user/edit-profile").setViewName("edit-profile");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");//указываем что ресурсы ищем в дереве файлов
    }
}
