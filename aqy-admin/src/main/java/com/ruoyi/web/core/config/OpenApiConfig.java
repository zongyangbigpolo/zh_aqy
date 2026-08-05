package com.ruoyi.web.core.config;

import com.ruoyi.common.config.RuoYiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI interface documentation configuration.
 */
@Configuration
public class OpenApiConfig
{
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Bean
    public OpenAPI zhAqyOpenApi()
    {
        String securitySchemeName = "Authorization";
        return new OpenAPI()
                .info(new Info()
                        .title("中瀚安全云平台接口文档")
                        .description("项目、设备、实时数据、阈值告警、短信通知和系统管理接口")
                        .version(ruoyiConfig.getVersion())
                        .contact(new Contact().name(ruoyiConfig.getName())))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .schemaRequirement(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER));
    }
}
