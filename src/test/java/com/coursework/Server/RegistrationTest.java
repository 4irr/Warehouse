package com.coursework.Server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-info-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-info-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegistrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegister() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/register")
                .param("username", "admin")
                .param("password", "admin123")
                .param("name", "name")
                .param("surname", "surname")
                .param("email", "email@email.ru")
                .param("telNum", "+375291234567")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Пользователь с таким логином уже зарегистрирован!")));
    }
    @Test
    public void testValidation() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/register")
                .param("username", "admin")
                .param("password", "admin123")
                .param("name", "name")
                .param("surname", "surname")
                .param("email", "email")
                .param("telNum", "+375")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@id='reg-box']/form/span").exists());
    }
}
