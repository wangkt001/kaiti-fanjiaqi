package com.campus.yujianhaowu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 配置
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("豫见好物 - API 文档")
                        .version("1.0.0")
                        .description("河南文创产品销售平台后端 API 接口文档")
                        .contact(new Contact()
                                .name("豫见好物团队")
                                .email("support@yujianhaowu.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .schemaRequirement("Authorization", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"));
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {
                    // 为所有接口添加 JWT Token 参数
                    List<Parameter> parameters = new ArrayList<>();
                    Parameter authHeader = new Parameter()
                            .in("header")
                            .name("Authorization")
                            .description("JWT Token")
                            .required(false);
                    parameters.add(authHeader);

                    if (pathItem.getGet() != null) {
                        pathItem.getGet().setParameters(parameters);
                    }
                    if (pathItem.getPost() != null) {
                        pathItem.getPost().setParameters(parameters);
                    }
                    if (pathItem.getPut() != null) {
                        pathItem.getPut().setParameters(parameters);
                    }
                    if (pathItem.getDelete() != null) {
                        pathItem.getDelete().setParameters(parameters);
                    }
                });
            }
        };
    }
}
