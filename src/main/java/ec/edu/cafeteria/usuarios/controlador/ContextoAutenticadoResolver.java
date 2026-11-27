package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import ec.edu.cafeteria.usuarios.servicio.SesionService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Inyecta el {@link ContextoAutenticado} en los métodos de los controladores a
 * partir de la
 * cabecera "Authorization: Bearer &lt;token&gt;". Sin token válido responde
 * 401.
 */
@Component
public class ContextoAutenticadoResolver implements HandlerMethodArgumentResolver {

    private static final String PREFIJO = "Bearer ";

    private final SesionService sesionService;

    public ContextoAutenticadoResolver(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ContextoAutenticado.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String cabecera = webRequest.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith(PREFIJO)) {
            throw new NoAutenticadoException("Debe iniciar sesión");
        }
        return sesionService.obtener(cabecera.substring(PREFIJO.length()).trim())
                .orElseThrow(() -> new NoAutenticadoException("Sesión no válida"));
    }
}
