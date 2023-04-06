package com.example;

import am.ik.accesslogger.AccessLogger;
import io.micrometer.core.instrument.config.MeterFilter;
import org.mybatis.scripting.thymeleaf.SqlGenerator;
import org.mybatis.scripting.thymeleaf.SqlGeneratorConfig;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mybatis.scripting.thymeleaf.processor.BindVariableRender.BuiltIn.SPRING_NAMED_PARAMETER;

@Configuration
public class AppConfig {
	@Bean
	public SqlGenerator sqlGenerator() {
		final SqlGeneratorConfig config = SqlGeneratorConfig.newInstanceWithCustomizer(c ->
				c.getDialect().setBindVariableRenderInstance(SPRING_NAMED_PARAMETER));
		return new SqlGenerator(config);
	}

	@Bean
	public AccessLogger accessLogger() {
		return new AccessLogger(httpExchange -> {
			final String uri = httpExchange.getRequest().getUri().getPath();
			return uri != null && !(uri.equals("/readyz") || uri.equals("/livez")
									|| uri.startsWith("/actuator") || uri.startsWith("/_static"));
		});
	}

	@Bean
	public MeterRegistryCustomizer<?> meterRegistryCustomizer() {
		return registry -> registry.config() //
				.meterFilter(MeterFilter.deny(id -> {
					final String uri = id.getTag("uri");
					return uri == null || uri.equals("/readyz") || uri.equals("/livez")
						   || uri.startsWith("/actuator") || uri.startsWith("/_static");
				}));
	}
}
