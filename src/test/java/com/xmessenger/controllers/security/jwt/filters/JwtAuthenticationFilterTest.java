package com.xmessenger.controllers.security.jwt.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.UserService;
import com.xmessenger.model.services.user.security.RawCredentials;
import data.factories.UserDataFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class JwtAuthenticationFilterTest {
    private final String CONTROLLER_PATH = WebSecurityConfig.API_BASE_PATH + "/login";

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(this.springSecurityFilterChain).build();
        AppUser testUser = UserDataFactory.generateSuccessUser();
        testUser.setPassword(this.encoder.encode(testUser.getPassword()));
        Mockito.when(this.userService.lookupUser(testUser.getUsername())).thenReturn(testUser);
    }

    @Test
    public void successfulAuthentication() throws Exception {
        AppUser testUser = UserDataFactory.generateSuccessUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(testUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(this.CONTROLLER_PATH)
                .content(new ObjectMapper().writeValueAsString(rawCredentials));
        MockHttpServletResponse response = this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
        assertTrue(response.containsHeader(TokenProvider.HEADER_NAME));
        assertTrue(response.getHeader(TokenProvider.HEADER_NAME).startsWith(TokenProvider.HEADER_PREFIX));
    }

    @Test
    public void unsuccessfulAuthentication() throws Exception {
        AppUser failureUser = UserDataFactory.generateFailureUser();
        RawCredentials rawCredentials = UserDataFactory.composeRawCredentials(failureUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(this.CONTROLLER_PATH)
                .content(new ObjectMapper().writeValueAsString(rawCredentials));
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void unsuccessfulAuthentication_invalidPayload() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(this.CONTROLLER_PATH)
                .content("[invalid payload]");
        this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}