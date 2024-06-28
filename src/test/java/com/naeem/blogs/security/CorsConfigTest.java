package com.naeem.blogs.security;

import org.assertj.core.api.Assertions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RunWith(SpringJUnit4ClassRunner.class)
public class CorsConfigTest {

	@InjectMocks
	private CorsConfig corsConfig;

	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(corsConfig);
	}

	
	@Test 
	public void corsFilter_IdIsNotNullAndIdDoesNotExist_ReturnCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config); 
        
        Assertions.assertThat(corsConfig.corsFilter()).isEqualToComparingFieldByFieldRecursively(new CorsFilter(source));
	}

}

