package com.javamentor.qa.platform.webapp.configs.initializer;

import com.javamentor.qa.platform.service.abstracts.model.SearchQuestionService;
import com.javamentor.qa.platform.service.impl.TestDataInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingClass({"org.springframework.boot.test.context.SpringBootTest"})
public class TestEntityInit implements CommandLineRunner {

    private TestDataInitService testDataInitService;
    private SearchQuestionService searchQuestionService;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Autowired
    public TestEntityInit(TestDataInitService testDataInitService, SearchQuestionService searchQuestionService) {
        this.testDataInitService = testDataInitService;
        this.searchQuestionService = searchQuestionService;
    }

    @Override
    public void run(String... args) {
        if (ddlAuto.contains("create")) {
            testDataInitService.createEntity();
        }
        searchQuestionService.index();
    }
}
