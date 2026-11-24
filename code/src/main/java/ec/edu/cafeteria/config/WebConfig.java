package ec.edu.cafeteria.config;

import ec.edu.cafeteria.usuarios.controlador.ContextoAutenticadoResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ContextoAutenticadoResolver contextoResolver;

    public WebConfig(ContextoAutenticadoResolver contextoResolver) {
        this.contextoResolver = contextoResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(contextoResolver);
    }
}
