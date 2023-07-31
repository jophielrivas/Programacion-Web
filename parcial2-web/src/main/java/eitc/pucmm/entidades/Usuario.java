package eitc.pucmm.entidades;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "Usuario.findAllByUsuario", query = "select u from Usuario u where u.usuario = :user"),
        @NamedQuery(name = "Usuario.autenticarUsuario", query = "select u from Usuario u where u.usuario = :user and u.password = :pass")})

public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;
    @PrimaryKeyJoinColumn
    private String usuario;
    private String nombre;
    private String password;
    private RoleasAPP rol;  //lo estaremos utilizando para los roles.
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER) // La clase "Clase" es la dueña de la relación.
    private Set<Enlace> misEnlaces = new HashSet<Enlace>();

    //Indicando las referencias bidireccional de la entidad Clase.

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleasAPP getRol() {
        return rol;
    }

    public void setRol(RoleasAPP rol) {
        this.rol = rol;
    }

    public Set<Enlace> getMisEnlaces() {
        return misEnlaces;
    }

    public void setMisEnlaces(Set<Enlace> misEnlaces) {
        this.misEnlaces = misEnlaces;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public enum RoleasAPP {
        ROLE_USUARIO, ROLE_ADMIN
    }
}
