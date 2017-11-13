package cir

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class Configs : WebMvcConfigurer {

  override fun configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer) {
    configurer.enable()
  }

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/api/**")
  }
}