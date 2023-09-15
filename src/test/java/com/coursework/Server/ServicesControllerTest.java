package com.coursework.Server;

import com.coursework.Server.Controllers.ServicesController;
import com.coursework.Server.Model.Batch;
import com.coursework.Server.Model.Product;
import com.coursework.Server.Repositories.BatchRepository;
import com.coursework.Server.Repositories.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-info-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-info-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("admin")
public class ServicesControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ServicesController servicesController;
    @Test
    public void testBatches() throws Exception {
        this.mockMvc.perform(get("/services/batches"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@class='item']").nodeCount(3));
    }
    @Test
    public void testBatchRepository() throws Exception {
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);
        int expectedResult = 3;
        assertEquals(expectedResult, batches.size());
    }
    @Test
    public void testFilter() throws Exception {
        this.mockMvc.perform(get("/services/batches/sort").param("type", "Ноутбуки"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@class='item']").nodeCount(1));
    }
    @Test
    public void validBatchTest() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/services/batches/add")
                .param("type", "Электроника")
                .param("amount", "-30")
                .param("weight", "100")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='main-container']/form/span").exists());
    }
    @Test
    public void addBatchTest() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/services/batches/add")
                .param("type", "Электроника")
                .param("amount", "30")
                .param("weight", "100")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='item']").nodeCount(4));
    }
    @Test
    public void removeConnectedBatch() throws Exception {
        this.mockMvc.perform(post("/services/batches/1/remove").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(xpath("//span[@id='error']").exists());
    }
    @Test
    public void removeBatch() throws Exception {
        this.mockMvc.perform(post("/services/products/1/remove").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='item']").nodeCount(0));
    }
    @Test
    public void testProductRepository() throws Exception {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        int expectedResult = 1;
        assertEquals(expectedResult, products.size());
    }
    @Test
    public void validEditProductTest() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/services/products/1/edit")
                .param("batch", "1")
                .param("name", "Планшет")
                .param("cell", "A2")
                .param("price", "-100")
                .param("shelfLife", "2023.20.05")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='main-container']/form/span").exists());
    }
}
