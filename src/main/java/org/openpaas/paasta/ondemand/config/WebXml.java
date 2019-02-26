package org.openpaas.paasta.ondemand.config;

import org.openpaas.paasta.ondemand.OnDemandApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * WebXml 설정 클래스
 *
 * @author sjchoi
 * @since 2018.08.21
 * @version 1.0
 */
public class WebXml extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OnDemandApplication.class);
	}

}