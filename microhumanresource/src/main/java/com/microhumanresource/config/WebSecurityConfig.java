package com.microhumanresource.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microhumanresource.bean.CustomFilterInvocationSecurityMetadataSource;
import com.microhumanresource.bean.CustomerUrlDecisionManager;
import com.microhumanresource.dto.RespDto;
import com.microhumanresource.handler.AuthenticationAccessDeniedHandler;
import com.microhumanresource.service.HrService;
import com.microhumanresource.utility.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private HrService hrService;
    @Autowired
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
    @Autowired
    private CustomerUrlDecisionManager customerUrlDecisionManager;
    @Autowired
    private AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 在HttpSecurity中配置拦截规则、表单登录、登录成功或失败的响应等
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //todo 忽略这些路径,下面一行正确吗????
        http.antMatcher("/css/**").antMatcher("/js/**").antMatcher("/index.html").antMatcher("/img/**").antMatcher("/fonts/**").antMatcher("favicon.ico").antMatcher("/verifyCode");
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>(){
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        o.setAccessDecisionManager(customerUrlDecisionManager);
                        return o;
                    }
                }).and()
                .formLogin().loginPage("/login_p").loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password")
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        RespDto respDto = null;
                        if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException){
                            respDto = RespDto.error("帐号或者密码输入错误！");
                        }else if (e instanceof LockedException){
                            respDto = RespDto.error("账户被锁定，请联系管理员！");
                        }else if (e instanceof CredentialsExpiredException){
                            respDto = RespDto.error("密码过期，请联系管理员！");
                        }else if (e instanceof AccountExpiredException){
                            respDto = RespDto.error("账户过期，请联系管理员！");
                        }else if (e instanceof DisabledException){
                            respDto = RespDto.error("账户被禁用，请联系管理员！");
                        }else {
                            respDto = RespDto.error("登录失败！");
                        }
                        response.setStatus(401);
                        ObjectMapper om = new ObjectMapper();
                        PrintWriter out = response.getWriter();
                        out.write(om.writeValueAsString(respDto));
                        out.flush();
                        out.close();
                    }
                }).successHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                RespDto respDto = RespDto.ok("登录成功！", HrUtils.getCurrentHr());
                ObjectMapper om = new ObjectMapper();
                PrintWriter out = response.getWriter();
                out.write(om.writeValueAsString(respDto));
                out.flush();
                out.close();
            }
        }).permitAll().and().logout().permitAll()
                .and().csrf().disable().exceptionHandling().accessDeniedHandler(authenticationAccessDeniedHandler);
        //最后通过accessDeniedHandler配置异常处理
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

}
