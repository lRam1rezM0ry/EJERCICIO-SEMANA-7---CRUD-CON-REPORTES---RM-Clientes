package com.example.clientes.model;
    
import jakarta.persistence.*;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    private String idCliente;

    private String nombre;

    private String tipoDocumento;

    private String numeroDocumento;

    private String telefono;

    @Lob
    private byte[] imagen;
    
    @Transient
    private MultipartFile archivoImagen;

    private String direccion;

    private String correo;

    // Getters y Setters

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
public MultipartFile getArchivoImagen() {
    return archivoImagen;
}

public void setArchivoImagen(MultipartFile archivoImagen) {
    this.archivoImagen = archivoImagen;
}

    // Método para convertir la imagen a Base64
    public String getImagenBase64() {
        if (imagen != null) {
            return Base64.getEncoder().encodeToString(imagen);
        }
        return null;
    }
}
