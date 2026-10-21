package com.app.estacionamento.service;

import com.app.estacionamento.controller.ProdutoDTO;
import com.app.estacionamento.dto.ProdutoRequestDTO;
import com.app.estacionamento.model.Produto;
import com.app.estacionamento.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> buscarNome(String nome) {
        if (nome != null && !nome.isBlank()) {
            return produtoRepository.findByNomeContainingIgnoreCase(nome.trim());
        }
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto cadastrarProduto(ProdutoRequestDTO produtoDTO) {
        if (produtoDTO == null) {
            throw new IllegalArgumentException("Os dados do produto não podem ser nulos.");
        }

        if (produtoDTO.getPreco() == null || produtoDTO.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que R$0");
        }

        if (produtoDTO.getNome() == null || produtoDTO.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("O produto deve ter no mínimo 3 letras.");
        }

        Produto novoProduto = new Produto(null, produtoDTO.getNome().trim(), produtoDTO.getPreco());
        return produtoRepository.save(novoProduto);
    }

    // Sobrecarga para compatibilidade com ProdutoDTO legado
    public Produto cadastrarProduto(ProdutoDTO produtoDTO) {
        if (produtoDTO == null) {
            throw new IllegalArgumentException("Os dados do produto não podem ser nulos.");
        }
        return cadastrarProduto(new ProdutoRequestDTO(produtoDTO.getNome(), produtoDTO.getPreco()));
    }

    public Optional<Produto> atualizarProduto(Long id, ProdutoRequestDTO produtoDTO) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            if (produtoDTO.getPreco() != null) {
                if (produtoDTO.getPreco() <= 0) {
                    throw new IllegalArgumentException("O preço do produto deve ser maior que R$0");
                }
                produtoExistente.setPreco(produtoDTO.getPreco());
            }

            if (produtoDTO.getNome() != null) {
                if (produtoDTO.getNome().trim().length() < 3) {
                    throw new IllegalArgumentException("O produto deve ter no mínimo 3 letras.");
                }
                produtoExistente.setNome(produtoDTO.getNome().trim());
            }

            return produtoRepository.save(produtoExistente);
        });
    }

    public boolean excluirProduto(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
