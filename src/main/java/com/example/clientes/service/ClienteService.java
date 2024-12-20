package com.example.clientes.service;

import com.example.clientes.model.Cliente;
import com.example.clientes.repository.ClienteRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public void guardarCliente(Cliente cliente) {
        repository.save(cliente);  // Guarda el cliente en la base de datos
    }

    public Iterable<Cliente> listarClientes() {
        return repository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(String id) {
        return repository.findById(id);
    }

    public void eliminarCliente(String id) {
        repository.deleteById(id);
    }
}