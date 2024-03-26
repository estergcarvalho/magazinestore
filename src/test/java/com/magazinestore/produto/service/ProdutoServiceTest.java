package com.magazinestore.produto.service;

import com.magazinestore.produto.dto.CaracteristicaRequest;
import com.magazinestore.produto.dto.ProdutoRequest;
import com.magazinestore.produto.dto.ProdutoResponse;
import com.magazinestore.produto.exception.ProdutoNaoEncontradoException;
import com.magazinestore.produto.model.Caracteristica;
import com.magazinestore.produto.model.Produto;
import com.magazinestore.produto.repository.ProdutoRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

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
    private static final Long CARACTERISTICA_ID = 1L;
    private static final Long PRODUTO_GUARDA_ROUPA_ID = 10L;

    @Test
    @DisplayName("Deve cadastrar produto")
    public void deveCadastrarProduto() throws IOException {
        byte[] imgGuardaRoupa = "guardaRoupa.jpg".getBytes();

        String img = Base64.getEncoder().encodeToString(imgGuardaRoupa);

        MultipartFile formatoImgAceito = new MockMultipartFile(
            "imagem",
            "guardaRupa.jpg",
            "guardaRoupa/jpeg",
            imgGuardaRoupa);

        CaracteristicaRequest caracteristicaRequest = CaracteristicaRequest.builder()
            .nome(CARACTERISTICA_NOME)
            .descricao(CARACTERISTICA_DESCRICAO)
            .build();

        List<CaracteristicaRequest> caracteristicasRequest = Collections.singletonList(caracteristicaRequest);

        ProdutoRequest produto = ProdutoRequest.builder()
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .caracteristicas(caracteristicasRequest)
            .build();

        Produto guardaRoupa = Produto.builder()
            .id(1L)
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .imagem(img)
            .build();

        List<CaracteristicaRequest> caracteristicasProduto = produto.getCaracteristicas();

        if (caracteristicasProduto != null && !caracteristicasProduto.isEmpty()) {
            List<Caracteristica> caracteristicas = caracteristicasProduto.stream()
                .map(request -> Caracteristica.builder()
                    .nome(request.getNome())
                    .descricao(request.getDescricao())
                    .build())
                .collect(Collectors.toList());

            guardaRoupa.setCaracteristica(caracteristicas);
        }

        when(produtoRepository.save(ArgumentMatchers.any())).thenReturn(guardaRoupa);

        ProdutoResponse guardaRoupaResponse = produtoService.cadastrar(produto, formatoImgAceito);

        assertEquals(1L, guardaRoupaResponse.getId().longValue());
        assertEquals(PRODUTO_GUARDA_ROUPA_NOME, guardaRoupaResponse.getNome());
        assertEquals(PRODUTO_GUARDA_ROUPA_DESCRICAO, guardaRoupaResponse.getDescricao());
        assertEquals(PRODUTO_GUARDA_ROUPA_PRECO, guardaRoupaResponse.getPreco());
        assertEquals(PRODUTO_GUARDA_ROUPA_MARCA, guardaRoupaResponse.getMarca());
        assertEquals(1, guardaRoupaResponse.getCaracteristicas().size());
        assertEquals(CARACTERISTICA_NOME, guardaRoupaResponse.getCaracteristicas().get(0).getNome());
        assertEquals(CARACTERISTICA_DESCRICAO, guardaRoupaResponse.getCaracteristicas().get(0).getDescricao());
        assertArrayEquals(formatoImgAceito.getBytes(), imgGuardaRoupa);
    }

    @Test
    @DisplayName("Deve listar produtos")
    public void deveListarProdutos() {
        List<Produto> produtos = new ArrayList<>();

        List<CaracteristicaRequest> caracteristicasRequest = Collections.singletonList(
            CaracteristicaRequest.builder()
                .nome(CARACTERISTICA_NOME)
                .descricao(CARACTERISTICA_DESCRICAO)
                .build()
        );

        List<Caracteristica> caracteristicas = new ArrayList<>();

        caracteristicasRequest.forEach(caracteristicaRequest -> {
            Caracteristica caracteristica = Caracteristica.builder()
                .nome(caracteristicaRequest.getNome())
                .descricao(caracteristicaRequest.getDescricao())
                .build();
            caracteristicas.add(caracteristica);
        });

        Produto guardaRoupa = Produto.builder()
            .id(1L)
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .caracteristica(caracteristicas)
            .build();
        produtos.add(guardaRoupa);

        Produto televisao = Produto.builder()
            .id(2L)
            .nome(PRODUTO_TELEVISAO_NOME)
            .descricao(PRODUTO_TELEVISAO_DESCRICAO)
            .preco(PRODUTO_TELEVISAO_PRECO)
            .marca(PRODUTO_TELEVISAO_MARCA)
            .caracteristica(new ArrayList<>())
            .build();
        produtos.add(televisao);

        when(produtoRepository.findAll()).thenReturn(produtos);

        List<ProdutoResponse> produtosResponse = produtoService.listar();

        assertEquals(2, produtosResponse.size());

        ProdutoResponse guardaRoupaResponse = produtosResponse.get(0);
        assertEquals(1L, guardaRoupaResponse.getId().longValue());
        assertEquals(PRODUTO_GUARDA_ROUPA_NOME, guardaRoupaResponse.getNome());
        assertEquals(PRODUTO_GUARDA_ROUPA_DESCRICAO, guardaRoupaResponse.getDescricao());
        assertEquals(PRODUTO_GUARDA_ROUPA_MARCA, guardaRoupaResponse.getMarca());
        assertEquals(PRODUTO_GUARDA_ROUPA_PRECO, guardaRoupaResponse.getPreco());
        assertEquals(1, guardaRoupaResponse.getCaracteristicas().size());
        assertEquals(CARACTERISTICA_NOME, guardaRoupaResponse.getCaracteristicas().get(0).getNome());
        assertEquals(CARACTERISTICA_DESCRICAO, guardaRoupaResponse.getCaracteristicas().get(0).getDescricao());

        ProdutoResponse televisaoResponse = produtosResponse.get(1);
        assertEquals(2L, televisaoResponse.getId().longValue());
        assertEquals(PRODUTO_TELEVISAO_NOME, televisaoResponse.getNome());
        assertEquals(PRODUTO_TELEVISAO_DESCRICAO, televisaoResponse.getDescricao());
        assertEquals(PRODUTO_TELEVISAO_MARCA, televisaoResponse.getMarca());
        assertEquals(PRODUTO_TELEVISAO_PRECO, televisaoResponse.getPreco());
        assertTrue(televisaoResponse.getCaracteristicas().isEmpty());
    }

    @Test
    @DisplayName("Deve buscar produto por id")
    public void deveBuscarProdutoPorId() {
        Produto guardaRoupa = Produto.builder()
            .id(10L)
            .nome(PRODUTO_GUARDA_ROUPA_NOME)
            .descricao(PRODUTO_GUARDA_ROUPA_DESCRICAO)
            .preco(PRODUTO_GUARDA_ROUPA_PRECO)
            .marca(PRODUTO_GUARDA_ROUPA_MARCA)
            .caracteristica(Collections.singletonList(
                Caracteristica.builder()
                    .id(CARACTERISTICA_ID)
                    .nome(CARACTERISTICA_NOME)
                    .descricao(CARACTERISTICA_DESCRICAO)
                    .build()
            ))
            .build();

        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(guardaRoupa));

        ProdutoResponse guardaRoupaResponse = produtoService.buscarPorId(10L);

        assertNotNull(guardaRoupaResponse);
        assertEquals(10L, guardaRoupaResponse.getId().longValue());
        assertEquals(PRODUTO_GUARDA_ROUPA_NOME, guardaRoupaResponse.getNome());
        assertEquals(PRODUTO_GUARDA_ROUPA_DESCRICAO, guardaRoupaResponse.getDescricao());
        assertEquals(PRODUTO_GUARDA_ROUPA_PRECO, guardaRoupaResponse.getPreco());
        assertEquals(PRODUTO_GUARDA_ROUPA_MARCA, guardaRoupaResponse.getMarca());
        assertEquals(1, guardaRoupaResponse.getCaracteristicas().size());
        assertEquals(CARACTERISTICA_ID, guardaRoupaResponse.getCaracteristicas().get(0).getId());
        assertEquals(CARACTERISTICA_NOME, guardaRoupaResponse.getCaracteristicas().get(0).getNome());
        assertEquals(CARACTERISTICA_DESCRICAO, guardaRoupaResponse.getCaracteristicas().get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exception para produto não encontrado")
    public void deveLancarExceptionProdutoNaoEncontrado() {
        Long id = 99999999L;

        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve buscar produto por texto e retornar sucesso")
    public void deveBuscarProdutoPorTextoERetornarSucesso() {
        String nome = "ipaD";
        String descricao = "mIni";

        Produto produto = Produto.builder()
            .nome(nome)
            .descricao(descricao)
            .build();

        when(produtoRepository.findByNomeIgnoreCaseContainingOrDescricaoIgnoreCaseContaining(nome, descricao))
            .thenReturn(List.of(produto));

        List<Produto> produtoEncontrado = produtoService.buscarProdutosPorTexto(nome, descricao);

        Produto produtoRetornado = produtoEncontrado.get(0);

        assertEquals(nome, produtoRetornado.getNome());
        assertEquals(descricao, produtoRetornado.getDescricao());
        assertNotNull(produtoRetornado);
    }

    @Test
    @DisplayName("Deve lancar exception para produto não localizado")
    public void deveLancarExceptionParaProdutoNaoLocalizado() {
        String nome = "nome produto inexistente";
        String descricao = "descricao produto inexistente";

        when(produtoRepository.findByNomeIgnoreCaseContainingOrDescricaoIgnoreCaseContaining(nome, descricao))
            .thenReturn(Collections.emptyList());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarProdutosPorTexto(nome, descricao));
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    public void deveAtualizarProdutoExistente() {
        Produto guardaRoupaExistente = Produto.builder()
            .id(PRODUTO_GUARDA_ROUPA_ID)
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
            .caracteristicas(List.of(
                CaracteristicaRequest.builder()
                    .nome(CARACTERISTICA_NOME)
                    .descricao(CARACTERISTICA_DESCRICAO)
                    .build()
            ))
            .build();

        when(produtoRepository.findById(PRODUTO_GUARDA_ROUPA_ID)).thenReturn(Optional.of(guardaRoupaExistente));
        when(produtoRepository.save(ArgumentMatchers.any())).thenReturn(guardaRoupaExistente);

        ProdutoResponse produtoAtualizado = produtoService.atualizar(PRODUTO_GUARDA_ROUPA_ID, guardaRoupaRequest);

        ProdutoResponse guardaRoupaResponse = ProdutoResponse.builder()
            .id(produtoAtualizado.getId())
            .nome(guardaRoupaRequest.getNome())
            .descricao(guardaRoupaRequest.getDescricao())
            .preco(guardaRoupaRequest.getPreco())
            .marca(guardaRoupaRequest.getMarca())
            .caracteristicas(produtoAtualizado.getCaracteristicas())
            .build();

        assertEquals(guardaRoupaResponse.getId(), produtoAtualizado.getId());
        assertEquals(guardaRoupaResponse.getNome(), produtoAtualizado.getNome());
        assertEquals(guardaRoupaResponse.getDescricao(), produtoAtualizado.getDescricao());
        assertEquals(guardaRoupaResponse.getPreco(), produtoAtualizado.getPreco());
        assertEquals(guardaRoupaResponse.getMarca(), produtoAtualizado.getMarca());
        assertEquals(guardaRoupaResponse.getCaracteristicas(), produtoAtualizado.getCaracteristicas());

    }

    @Test
    @DisplayName("Deve lançar exceção para produto não encontrado ao tentar atualizar")
    public void deveLancarExceptionProdutoNaoEncontradoAoAtualizar() {
        Long produtoId = 1L;

        ProdutoRequest guardaRoupaRequest = ProdutoRequest.builder().build();

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            produtoService.atualizar(produtoId, guardaRoupaRequest);
        });
    }

}