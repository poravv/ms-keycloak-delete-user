package com.ms.keycloak.service;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminClientService {

    Date fechaActual = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final String SERVER_URL = "http://localhost:5001";
    private static final String REALM_NAME = "realm-spring-boot-dev";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";//Se crea por defecto en el keycloak
    private static final String USER_CONSOLE = "admin"; //Usuario root
    private static final String PASSWORD_CONSOLE = ""; //Password root
    private static final String CLIENT_SECRET = "";

    public void searchUsersByDeletionDate() {

        // Configura el cliente Keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();

        // Accede a los usuarios y busca por fecha de eliminación
        UsersResource usersResource = keycloak.realm(REALM_NAME).users();
        List<UserRepresentation> allUsers = usersResource.list();
        List<UserRepresentation> usuariosEliminacionVencida = new ArrayList<>();

        for (UserRepresentation user : allUsers) {
            String fechaEliminacion="";
            Map<String,List<String>> atributos = user.getAttributes();

            try {
                //System.out.println(atributos.get("expiration_date").get(0));
                fechaEliminacion = atributos.get("expiration_date").get(0);
                //System.out.println("Fecha de eliminacion: "+ sdf.parse(fechaEliminacion));
                //System.out.println("Fecha actual: "+fechaActual);

                if (fechaEliminacion != null && (fechaActual.after(sdf.parse(fechaEliminacion))||fechaActual.equals(sdf.parse(fechaEliminacion)))) {
                    //System.out.println("Entra para su eliminacion "+user.getUsername());
                    usuariosEliminacionVencida.add(user);
                }
            }catch(NullPointerException e) {
                System.out.println("El atributo no existe o está vacío. "+user);
            } catch (ParseException e) {
                System.out.println("No se pudo parsear el registro. "+user);
            }
        }

        // Procesa los usuarios encontrados
        for (UserRepresentation user : usuariosEliminacionVencida) {
            System.out.println("Usuario encontrado: " + user.getUsername());
            // Elimina el usuario si es necesario
            usersResource.delete(user.getId());
        }
    }
}
