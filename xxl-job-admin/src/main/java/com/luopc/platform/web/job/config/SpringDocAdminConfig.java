package com.luopc.platform.web.job.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SpringDocAdminConfig {

    @Bean
    public OpenAPI serviceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XXL-JOB 分布式任务调度平台")
                        .description("XXL-JOB是一个分布式任务调度平台，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。")
                        .version("v3.0.0")
                        .license(new License()
                                .name("Licensed under the GNU General Public License (GPL) v3.")
                                .url("https://github.com/xuxueli/xxl-job/blob/master/LICENSE"))
                        .contact(new Contact()
                                .name("xuxueli")
                                .email("https://www.xuxueli.com/page/community.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("中文文档")
                        .url("https://www.xuxueli.com/xxl-job/"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties p = new Properties();
        p.setProperty("Oracle", "oracle");
        p.setProperty("TiDB", "tidb");
        p.setProperty("PostgreSQL", "postgresql");
        p.setProperty("DB2", "db2");
        p.setProperty("SQL Server", "sqlserver");
        p.setProperty("MySQL", "MySQL");
        p.setProperty("H2", "H2");
        databaseIdProvider.setProperties(p);
        return databaseIdProvider;
    }

}
