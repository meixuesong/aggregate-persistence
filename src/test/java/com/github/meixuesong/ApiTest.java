package com.github.meixuesong;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class ApiTest {
    protected String baseUrl;

    @Autowired
    protected Environment environment;

    @Autowired
    protected TestRestTemplate restTemplate;


    @Before
    public void setUp() throws Exception {
        String port = environment.getProperty("local.server.port");
        String contextPath = environment.getProperty("server.servlet.context-path");
        baseUrl = "http://localhost:" + port + contextPath;
    }

}
