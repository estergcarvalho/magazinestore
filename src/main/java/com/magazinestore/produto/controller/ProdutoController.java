package com.magazinestore.produto.controller;

import com.magazinestore.produto.dto.ProdutoRequest;
import com.magazinestore.produto.dto.ProdutoResponse;
import com.magazinestore.produto.model.Produto;
import com.magazinestore.produto.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produto", description = "API para operações relacionadas a produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(
        summary = "Cadastra um produto",
        description = "Cadastra um novo produto com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
        }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoResponse> cadastrar(@RequestPart("produtoRequest") ProdutoRequest produtoRequest,
                                                     @RequestParam(value = "imagem") MultipartFile imagem) throws IOException {
        ProdutoResponse produtoResponse = produtoService.cadastrar(produtoRequest, imagem);

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoResponse);
    }

    @Operation(
        summary = "Retorna uma lista de produtos",
        description = "Retorna uma lista de todos os produtos cadastrados"
    )
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        List<ProdutoResponse> produtos = produtoService.listar();

        return ResponseEntity.ok(produtos);
    }

    @Operation(
        summary = "Retorna um produto dado um id",
        description = "Retorna os detalhes de um produto com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.buscarPorId(id);

        return ResponseEntity.ok(produto);
    }

    @Operation(
        summary = "Retorna produto dado nome ou descrição",
        description = "Retorna o produto com base no nome ou descrição fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/pesquisa")
    public ResponseEntity<List<Produto>> buscarProdutoPorTexto(@RequestParam(required = false) String nome,
                                                               @RequestParam(required = false) String descricao) {
        List<Produto> produtos = produtoService.buscarProdutosPorTexto(nome, descricao);

        return ResponseEntity.ok(produtos);
    }

    @Operation(
        summary = "Atualizar um produto",
        description = "Atualiza os detalhes de um produto com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{produtoId}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long produtoId, @RequestBody ProdutoRequest
        produtoRequest) {
        produtoService.atualizar(produtoId, produtoRequest);

        return ResponseEntity.noContent().build();
    }

}