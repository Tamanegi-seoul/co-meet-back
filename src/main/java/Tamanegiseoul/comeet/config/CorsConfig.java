package Tamanegiseoul.comeet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 서버가 응답할때 json을 자바스크립트에서 처리할 수 있도록
        config.addAllowedOrigin("http://localhost:3030"); // 모든 ip에 응답을 허용하겠다
        config.addAllowedOrigin("http://15.165.76.96:3000");
        config.addAllowedOrigin("https://co-meet.netlify.app");

        //config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*"); // 모든 Header에 대해 응답을 허용
        config.addAllowedMethod("*"); // 모든 post,get,put,delete, .. 요청을 허용하겠다
        source.registerCorsConfiguration("/**", config); //
        return new CorsFilter(source);
    }
}