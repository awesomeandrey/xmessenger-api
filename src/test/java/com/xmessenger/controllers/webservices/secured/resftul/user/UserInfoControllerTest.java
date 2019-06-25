package com.xmessenger.controllers.webservices.secured.resftul.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import data.factories.UserDataFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserInfoControllerTest {
    private final String CONTROLLER_PATH = WebSecurityConfig.API_BASE_PATH + "/user";
    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private UserService flowExecutor;

    @MockBean
    private UserDAO userDAO;

    private MockMvc mockMvc;

    @Before
    public void before() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(this.springSecurityFilterChain).build();
        AppUser testUser = UserDataFactory.generateSuccessUser();
        Mockito.when(this.userDAO.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        Mockito.when(this.flowExecutor.changeProfileInfo(testUser)).thenReturn(testUser);
    }

    @Test
    public void getCurrentUser() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String token = this.generateToken(testUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(this.CONTROLLER_PATH + "/info")
                .header(TokenProvider.HEADER_NAME, TokenProvider.HEADER_PREFIX + token);
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(testUser.getId()));
    }

    @Test
    public void changeProfileInfo() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String token = this.generateToken(testUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(this.CONTROLLER_PATH + "/info")
                .header(TokenProvider.HEADER_NAME, TokenProvider.HEADER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUser));
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(testUser.getId()));
    }

    @Test
    public void findPeople() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String token = this.generateToken(testUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(this.CONTROLLER_PATH + "/search")
                .header(TokenProvider.HEADER_NAME, TokenProvider.HEADER_PREFIX + token)
                .param("nameOrLogin", testUser.getUsername());
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void setProfilePicture() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        String token = this.generateToken(testUser);
        MockMultipartFile sampleFile = new MockMultipartFile("file", "New user picture".getBytes());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload(this.CONTROLLER_PATH + "/picture")
                .file(sampleFile)
                .header(TokenProvider.HEADER_NAME, TokenProvider.HEADER_PREFIX + token);
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(testUser.getId()));
    }

//    @Test
//    public void changePassword() throws Exception {
//        AppUser testUser = UserDataFactory.generateSuccessUser();
//        String token = this.generateToken(testUser);
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(this.CONTROLLER_PATH + "/password")
//                .header(TokenProvider.HEADER_NAME, TokenProvider.HEADER_PREFIX + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(UserDataFactory.composeRawCredentials(testUser)));
//        this.mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }

    private String generateToken(AppUser appUser) {
        String token = this.tokenProvider.generateToken(appUser);
        assertNotNull(token);
        return token;
    }
}