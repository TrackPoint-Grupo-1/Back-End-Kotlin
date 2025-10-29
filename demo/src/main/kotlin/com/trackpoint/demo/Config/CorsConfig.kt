package com.trackpoint.demo.Config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class CorsConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: String
) {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        val origins = allowedOrigins.split(",").map { it.trim() }.toTypedArray()
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns(*origins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
            }
        }
    }
}
