package com.magazinestore.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magazinestore.produto.dto.CaracteristicaRequest;
import com.magazinestore.produto.dto.ProdutoRequest;
import com.magazinestore.produto.model.Caracteristica;
import com.magazinestore.produto.model.Produto;
import com.magazinestore.produto.repository.ProdutoRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ProdutoControllerTest {

    @InjectMocks
    private ProdutoController produtoController;

    @MockBean
    private ProdutoRepository produtoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PRODUTO_GUARDA_ROUPA_NOME = "Guarda-Roupa de Madeira Maciça";
    private static final String PRODUTO_GUARDA_ROUPA_DESCRICAO = "Guarda-roupa espaçoso com acabamento em madeira";
    private static final BigDecimal PRODUTO_GUARDA_ROUPA_PRECO = new BigDecimal("1299.99");
    private static final String PRODUTO_GUARDA_ROUPA_MARCA = "Silvia Design";

    private static final String PRODUTO_TELEVISAO_NOME = "Smart TV 55” UHD 4K LED LG";
    private static final String PRODUTO_TELEVISAO_DESCRICAO = "Ela possui resolução UHD 4K com tecnologia LED";
    private static final BigDecimal PRODUTO_TELEVISAO_PRECO = new BigDecimal("2599.0");
    private static final String PRODUTO_TELEVISAO_MARCA = "LG";

    private static final String CARACTERISTICA_NOME = "Dimensao:";
    private static final String CARACTERISTICA_DESCRICAO = "Largura: 1m Altura: 80cm. Profundidade: 60cm";

    @Test
    @DisplayName("Deve cadastrar um produto")
    public void deveCadastrarProduto() throws Exception {
        Long id = 1L;

        byte[] imagemTelevisao = "televisao.jpg".getBytes();
        String imagem = Base64.getEncoder().encodeToString(imagemTelevisao);

        MockMultipartFile formatoImgAceito = new MockMultipartFile(
            "imagem",
            "televisao.jpg",
            "multipart/form-data",
            imagem.getBytes()
        );

        ProdutoRequest televisaoRequest = ProdutoRequest.builder()
            .nome(PRODUTO_TELEVISAO_NOME)
            .descricao(PRODUTO_TELEVISAO_DESCRICAO)
            .preco(PRODUTO_TELEVISAO_PRECO)
            .marca(PRODUTO_TELEVISAO_MARCA)
            .caracteristicas(new ArrayList<>
                (Collections.singletonList(CaracteristicaRequest
                    .builder()
                    .nome(CARACTERISTICA_NOME)
                    .descricao(CARACTERISTICA_DESCRICAO)
                    .build()
                )))
            .build();

        Produto televisao = Produto.builder()
            .id(id)
            .nome(PRODUTO_TELEVISAO_NOME)
            .descricao(PRODUTO_TELEVISAO_DESCRICAO)
            .preco(PRODUTO_TELEVISAO_PRECO)
            .marca(PRODUTO_TELEVISAO_MARCA)
            .imagem(imagem)

            .caracteristica(new ArrayList<>(Collections.singletonList(
                Caracteristica.builder()
                    .id(id)
                    .nome(CARACTERISTICA_NOME)
                    .descricao(CARACTERISTICA_DESCRICAO)
                    .build()
            )))
            .build();

        when(produtoRepository.save(any())).thenReturn(televisao);

        mockMvc.perform(
                multipart("/produtos")
                    .file(formatoImgAceito)
                    .file(new MockMultipartFile(
                        "produtoRequest",
                        "televisao",
                        "application/json",
                        objectMapper.writeValueAsBytes(televisaoRequest)))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.nome").value(PRODUTO_TELEVISAO_NOME))
            .andExpect(jsonPath("$.descricao").value(PRODUTO_TELEVISAO_DESCRICAO))
            .andExpect(jsonPath("$.preco").value(PRODUTO_TELEVISAO_PRECO))
            .andExpect(jsonPath("$.marca").value(PRODUTO_TELEVISAO_MARCA))
            .andExpect(jsonPath("$.caracteristicas[0].id").exists())
            .andExpect(jsonPath("$.caracteristicas[0].nome").value(CARACTERISTICA_NOME))
            .andExpect(jsonPath("$.caracteristicas[0].descricao").value(CARACTERISTICA_DESCRICAO));
    }

    @Test
    @DisplayName("Deve listar produtos")
    public void deveListarProdutos() throws Exception {
        Long idGuardaRoupa = 1L;
        Long idTelevisao = 2L;

        Produto guardaRoupa = Produto.builder()
            .id(idGuardaRoupa)
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .build();

        Produto televisao = Produto.builder()
            .id(idTelevisao)
            .nome(PRODUTO_TELEVISAO_NOME)
            .descricao(PRODUTO_TELEVISAO_DESCRICAO)
            .preco(PRODUTO_TELEVISAO_PRECO)
            .marca(PRODUTO_TELEVISAO_MARCA)
            .build();

        List<Produto> produtos = Arrays.asList(guardaRoupa, televisao);

        when(produtoRepository.findAll()).thenReturn(produtos);

        mockMvc.perform(get("/produtos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(idGuardaRoupa))
            .andExpect(jsonPath("$[0].nome").value(PRODUTO_GUARDA_ROUPA_NOME))
            .andExpect(jsonPath("$[0].descricao").value(PRODUTO_GUARDA_ROUPA_DESCRICAO))
            .andExpect(jsonPath("$[0].preco").value(PRODUTO_GUARDA_ROUPA_PRECO))
            .andExpect(jsonPath("$[0].marca").value(PRODUTO_GUARDA_ROUPA_MARCA))
            .andExpect(jsonPath("$[1].id").value(idTelevisao))
            .andExpect(jsonPath("$[1].nome").value(PRODUTO_TELEVISAO_NOME))
            .andExpect(jsonPath("$[1].descricao").value(PRODUTO_TELEVISAO_DESCRICAO))
            .andExpect(jsonPath("$[1].preco").value(PRODUTO_TELEVISAO_PRECO))
            .andExpect(jsonPath("$[1].marca").value(PRODUTO_TELEVISAO_MARCA));
    }

    @Test
    @DisplayName("Deve buscar produto por id")
    public void deveBuscarProdutoPorId() throws Exception {
        Long idTelevisao = 5L;

        Produto televisao = Produto.builder()
            .id(idTelevisao)
            .nome(PRODUTO_TELEVISAO_NOME)
            .descricao(PRODUTO_TELEVISAO_DESCRICAO)
            .preco(PRODUTO_TELEVISAO_PRECO)
            .marca(PRODUTO_TELEVISAO_MARCA)

            .caracteristica(new ArrayList<>(Collections.singletonList(
                Caracteristica.builder()
                    .id(idTelevisao)
                    .nome(CARACTERISTICA_NOME)
                    .descricao(CARACTERISTICA_DESCRICAO)
                    .build()
            )))
            .build();

        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(televisao));

        mockMvc.perform(get("/produtos/{id}", idTelevisao)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(idTelevisao))
            .andExpect(jsonPath("$.nome").value(PRODUTO_TELEVISAO_NOME))
            .andExpect(jsonPath("$.descricao").value(PRODUTO_TELEVISAO_DESCRICAO))
            .andExpect(jsonPath("$.preco").value(PRODUTO_TELEVISAO_PRECO))
            .andExpect(jsonPath("$.marca").value(PRODUTO_TELEVISAO_MARCA))
            .andExpect(jsonPath("$.caracteristicas[0].nome").value(CARACTERISTICA_NOME))
            .andExpect(jsonPath("$.caracteristicas[0].descricao").value(CARACTERISTICA_DESCRICAO));
    }

    @Test
    @DisplayName("Deve lançar exception para produto não encontrado")
    public void deveLancarExceptionProdutoNaoEncontrado() throws Exception {
        Long idNaoExistente = 999999L;

        when(produtoRepository.findById(idNaoExistente)).thenReturn(Optional.empty());

        mockMvc.perform(get("/produtos/{id}", idNaoExistente)
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar produto por texto e retornar sucesso")
    public void deveBuscarProdutoPorTextoERetornarSucesso() throws Exception {
        String nome = "iPad";
        String descricao = "miNi";

        Produto produto = Produto.builder()
            .nome(nome)
            .descricao(descricao)
            .build();

        when(produtoRepository
            .findByNomeIgnoreCaseContainingOrDescricaoIgnoreCaseContaining(nome, descricao)).thenReturn(List.of(produto));

        mockMvc.perform(get("/produtos/pesquisa")
                .param("nome", nome)
                .param("descricao", descricao)
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value(nome))
            .andExpect(jsonPath("$[0].descricao").value(descricao));
    }

    @Test
    @DisplayName("Deve retornar status 204 quando produto for atualizado")
    public void deveAtualizarUmProduto() throws Exception {
        Long guardaRoupaId = 3L;

        Produto produtoExistente = Produto.builder()
            .id(guardaRoupaId)
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .caracteristica(new ArrayList<>())
            .build();

        ProdutoRequest guardaRoupaRequest = ProdutoRequest.builder()
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .caracteristicas(new ArrayList<>())
            .build();

        CaracteristicaRequest caracteristicaRequest = CaracteristicaRequest.builder()
            .nome(CARACTERISTICA_NOME)
            .descricao(CARACTERISTICA_DESCRICAO)
            .build();
        guardaRoupaRequest.getCaracteristicas().add(caracteristicaRequest);

        when(produtoRepository.findById(guardaRoupaId)).thenReturn(Optional.of(produtoExistente));

        Produto produtoAtualizado = Produto.builder()
            .nome(guardaRoupaRequest.getNome())
            .descricao(guardaRoupaRequest.getDescricao())
            .preco(guardaRoupaRequest.getPreco())
            .marca(guardaRoupaRequest.getMarca())
            .caracteristica(guardaRoupaRequest.getCaracteristicas()
                .stream()
                .map(caracteristica -> Caracteristica
                    .builder()
                    .nome(caracteristica.getNome())
                    .descricao(caracteristica.getDescricao())
                    .build())
                .collect(Collectors.toList()))
            .build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoAtualizado);

        mockMvc.perform(put("/produtos/{produtoId}", guardaRoupaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guardaRoupaRequest)))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve lançar exception para produto não encontrado")
    public void deveLancarExceptionProdutoNotFound() throws Exception {
        Long idNaoExistente = 789L;

        when(produtoRepository.findById(idNaoExistente)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos/{produtoId}", idNaoExistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }

}