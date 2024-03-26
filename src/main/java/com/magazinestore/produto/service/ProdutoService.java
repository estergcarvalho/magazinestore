package com.magazinestore.produto.service;

import com.magazinestore.produto.dto.CaracteristicaResponse;
import com.magazinestore.produto.dto.ProdutoRequest;
import com.magazinestore.produto.dto.ProdutoResponse;
import com.magazinestore.produto.exception.ProdutoNaoEncontradoException;
import com.magazinestore.produto.model.Caracteristica;
import com.magazinestore.produto.model.Produto;
import com.magazinestore.produto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public ProdutoResponse cadastrar(ProdutoRequest produtoRequest, MultipartFile imagem) throws IOException {
        String file = Base64.getEncoder().encodeToString(imagem.getBytes());

        Produto produto = Produto.builder()
            .nome(produtoRequest.getNome())
            .descricao(produtoRequest.getDescricao())
            .preco(produtoRequest.getPreco())
            .marca(produtoRequest.getMarca())
            .caracteristica(new ArrayList<>())
            .imagem(file)
            .build();

        if (!CollectionUtils.isEmpty(produtoRequest.getCaracteristicas())) {
            produtoRequest.getCaracteristicas().forEach(caracteristicaRequest -> {
                Caracteristica caracteristica = Caracteristica.builder()
                    .nome(caracteristicaRequest.getNome())
                    .descricao(caracteristicaRequest.getDescricao())
                    .produto(produto)
                    .build();

                produto.getCaracteristica().add(caracteristica);
            });
        }

        Produto produtoSalvo = produtoRepository.save(produto);

        List<CaracteristicaResponse> caracteristicasResponse = new ArrayList<>();
        produtoSalvo.getCaracteristica().forEach(caracteristica -> {
            CaracteristicaResponse caracteristicaResponse = new CaracteristicaResponse(
                caracteristica.getId(),
                caracteristica.getNome(),
                caracteristica.getDescricao()
            );
            caracteristicasResponse.add(caracteristicaResponse);
        });

        return ProdutoResponse.builder()
            .id(produtoSalvo.getId())
            .nome(produtoSalvo.getNome())
            .descricao(produtoSalvo.getDescricao())
            .preco(produtoSalvo.getPreco())
            .marca(produtoSalvo.getMarca())
            .caracteristicas(caracteristicasResponse)
            .build();
    }

    public List<ProdutoResponse> listar() {
        List<Produto> produtos = produtoRepository.findAll();

        List<ProdutoResponse> produtosResponse = new ArrayList<>();

        produtos.forEach(produto -> {
            List<CaracteristicaResponse> caracteristicas = new ArrayList<>();

            if (!CollectionUtils.isEmpty(produto.getCaracteristica())) {
                produto.getCaracteristica().forEach(caracteristica ->
                    caracteristicas.add(
                        CaracteristicaResponse.builder()
                            .id(caracteristica.getId())
                            .nome(caracteristica.getNome())
                            .descricao(caracteristica.getDescricao())
                            .build()
                    )
                );
            }

            ProdutoResponse produtoResponse = ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .marca(produto.getMarca())
                .preco(produto.getPreco())
                .caracteristicas(caracteristicas)
                .build();

            produtosResponse.add(produtoResponse);
        });

        return produtosResponse;
    }

    public ProdutoResponse buscarPorId(Long id) throws ProdutoNaoEncontradoException {
        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isEmpty()) {
            throw new ProdutoNaoEncontradoException();
        }

        Produto produtoExistente = produto.get();

        List<CaracteristicaResponse> caracteristicas = new ArrayList<>();
        produtoExistente.getCaracteristica().forEach(caracteristica -> {
            caracteristicas.add(
                CaracteristicaResponse.builder()
                    .id(caracteristica.getId())
                    .nome(caracteristica.getNome())
                    .descricao(caracteristica.getDescricao())
                    .build()
            );
        });

        return ProdutoResponse.builder()
            .id(produtoExistente.getId())
            .nome(produtoExistente.getNome())
            .descricao(produtoExistente.getDescricao())
            .preco(produtoExistente.getPreco())
            .marca(produtoExistente.getMarca())
            .caracteristicas(caracteristicas)
            .build();
    }

    public ProdutoResponse atualizar(Long produtoId, ProdutoRequest produtoRequest) {
        Optional<Produto> produtos = produtoRepository.findById(produtoId);

        if (produtos.isEmpty()) {
            throw new ProdutoNaoEncontradoException();
        }

        Produto produtoExistente = produtos.get();

        if (!CollectionUtils.isEmpty(produtoRequest.getCaracteristicas())) {
            produtoRequest.getCaracteristicas().forEach(caracteristicaRequest -> {
                Caracteristica caracteristica = Caracteristica.builder()
                    .nome(caracteristicaRequest.getNome())
                    .descricao(caracteristicaRequest.getDescricao())
                    .produto(produtoExistente)
                    .build();

                produtoExistente.getCaracteristica().add(caracteristica);
            });
        }

        produtoExistente.setNome(produtoRequest.getNome());
        produtoExistente.setDescricao(produtoRequest.getDescricao());
        produtoExistente.setMarca(produtoRequest.getMarca());
        produtoExistente.setPreco(produtoRequest.getPreco());

        produtoRepository.save(produtoExistente);

        return ProdutoResponse.builder()
            .id(produtoExistente.getId())
            .nome(produtoExistente.getNome())
            .descricao(produtoExistente.getDescricao())
            .preco(produtoExistente.getPreco())
            .marca(produtoExistente.getMarca())
            .build();
    }

    public List<Produto> buscarProdutosPorTexto(String nome, String descricao) {
        List<Produto> produtos = produtoRepository
            .findByNomeIgnoreCaseContainingOrDescricaoIgnoreCaseContaining(nome, descricao);

        if (produtos.isEmpty()) {
            throw new ProdutoNaoEncontradoException();
        }

        return produtos;
    }

}