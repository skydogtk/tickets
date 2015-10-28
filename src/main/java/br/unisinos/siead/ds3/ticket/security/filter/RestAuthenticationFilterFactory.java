package br.unisinos.siead.ds3.ticket.security.filter;

import java.util.List;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.unisinos.siead.ds3.ticket.annotation.Authenticated;
import br.unisinos.siead.ds3.ticket.security.service.AuthenticationService;
import br.unisinos.siead.ds3.ticket.dto.Usuario;
import br.unisinos.siead.ds3.ticket.jdbc.ConnectionSettingsFromEnv;
import br.unisinos.siead.ds3.ticket.jdbc.DBConnectionFactory;
import br.unisinos.siead.ds3.ticket.jdbc.DBType;
import br.unisinos.siead.ds3.ticket.security.AuthType;
import static br.unisinos.siead.ds3.ticket.security.AuthType.DENNY_ALL_METHOD;
import static br.unisinos.siead.ds3.ticket.security.AuthType.PERMIT_ALL_CLASS;
import static br.unisinos.siead.ds3.ticket.security.AuthType.PERMIT_ALL_METHOD;
import static br.unisinos.siead.ds3.ticket.security.AuthType.ROLES_ALLOWED_CLASS;
import static br.unisinos.siead.ds3.ticket.security.AuthType.ROLES_ALLOWED_METHOD;
import static br.unisinos.siead.ds3.ticket.security.AuthType.UNAUTHENTICATED;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

public class RestAuthenticationFilterFactory implements ResourceFilterFactory {

    /**
     * @author fcsilva
     *
     */
    public class RestAuthenticationFilter implements ResourceFilter, ContainerRequestFilter {

        public static final String AUTHENTICATION_HEADER = "Authorization";

        private boolean authRequested;
        private boolean denyAll;
        private boolean permitAll;
        private final String[] rolesAllowed;

        protected RestAuthenticationFilter(AuthType authType) {
            this(authType, null);
        }

        protected RestAuthenticationFilter(AuthType authType, String[] rolesAllowed) {
            switch (authType) {
                case DENNY_ALL_METHOD:
                    this.authRequested = false;
                    this.denyAll = true;
                    this.permitAll = false;
                    break;
                case PERMIT_ALL_CLASS:
                case PERMIT_ALL_METHOD:
                    this.authRequested = true;
                    this.denyAll = false;
                    this.permitAll = true;
                    break;
                case ROLES_ALLOWED_CLASS:
                case ROLES_ALLOWED_METHOD:
                    this.authRequested = true;
                    this.denyAll = false;
                    this.permitAll = false;
                    break;
                case UNAUTHENTICATED:
                    this.authRequested = false;
                    this.denyAll = false;
                    this.permitAll = false;
                    break;
            }
            this.rolesAllowed = (rolesAllowed != null) ? rolesAllowed : new String[]{};
        }

        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }

        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }

        @Override
        public ContainerRequest filter(ContainerRequest request) {
            Connection con;
            Usuario usuario = null;

            try {
                con = new DBConnectionFactory(new ConnectionSettingsFromEnv(DBType.POSTGRESQL)).getConnection();
                request.getProperties().put("conexao", con);
            } catch (Exception ex) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            if (authRequested) {
                try {
                    String authCredentials = request.getHeaderValue(AUTHENTICATION_HEADER);
                    usuario = new AuthenticationService(con).authenticate(authCredentials);
                    request.getProperties().put("usuario", usuario);
                } catch (Exception ex) {
                    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                }
            }

            if (!denyAll) {
                if (authRequested) {
                    if (usuario != null) {
                        if (permitAll) {
                            return request;
                        } else {
                            for (String role : rolesAllowed) {
                                if (usuario.getPapel().getDesc().equals(role)) {
                                    return request;
                                }
                            }
                        }
                    }
                } else {
                    return request;
                }
            }
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        // DenyAll on the method take precedence over RolesAllowed and PermitAll
        if (am.isAnnotationPresent(DenyAll.class)) {
//            return Collections.<ResourceFilter>singletonList(new RestAuthenticationFilter());
            return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.DENNY_ALL_METHOD)));
        }

        // RolesAllowed on the method takes precedence over PermitAll
        RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
        if (ra != null) {
//            return Collections.<ResourceFilter>singletonList(new RestAuthenticationFilter(ra.value()));
            return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.ROLES_ALLOWED_METHOD, ra.value())));
        }

        // PermitAll takes precedence over RolesAllowed on the class
        if (am.getResource().isAnnotationPresent(PermitAll.class)) {
            if (am.getResource().isAnnotationPresent(Authenticated.class)) {
//                return Collections.<ResourceFilter>singletonList(new RestAuthenticationFilter(true));
                return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.PERMIT_ALL_CLASS)));
            }
            return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.UNAUTHENTICATED)));
        }

        // RolesAllowed on the class takes precedence over PermitAll
        ra = am.getResource().getAnnotation(RolesAllowed.class);
        if (ra != null) {
//            return Collections.<ResourceFilter>singletonList(new RestAuthenticationFilter(ra.value()));
            return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.ROLES_ALLOWED_CLASS, ra.value())));
        }

        if (am.getResource().isAnnotationPresent(Authenticated.class)
                && !am.isAnnotationPresent(DenyAll.class)
                && !am.isAnnotationPresent(RolesAllowed.class)) {
//            return Collections.<ResourceFilter>singletonList(new RestAuthenticationFilter(ra.value()));
            return new ArrayList<ResourceFilter>(Arrays.asList(new RestAuthenticationFilter(AuthType.PERMIT_ALL_METHOD)));
        }

        // No need to check whether PermitAll is present.
        return null;
    }
}
