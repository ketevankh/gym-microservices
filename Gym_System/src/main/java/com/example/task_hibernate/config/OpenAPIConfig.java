//package com.example.task_hibernate.config;
//
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenAPIConfig {
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                .components(new Components().addSecuritySchemes("bearerAuth",
//                        new SecurityScheme()
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")))
//                .info(new Info().title("Gym Management System API")
//                        .description("This is a Gym Management System Application")
//                        .version("1.0.0"));
//    }
//}
