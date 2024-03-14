package tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Controller.ControllerProfessor;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.DTOS.ProfessorDTO;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Errors.ResourceNotFoundException;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.AtualizarProfessorRequest;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.Professor;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.ProfessorRequest;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Repository.RepositorioProfessor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceProfessorTest {

    @InjectMocks //injetar a classe controllerProfessor para os nossos testes
    ServiceProfessorImpl serviceProfessor;

    @Mock //mockar classes que usamos na controller
    RepositorioProfessor repositorioProfessor;
    @Mock
    ModelMapper modelMapper;
    //RepositorioTurma turmaRepositorio;


    //instanciar a classe que usamos como retorno em alguns metodos
    Professor professor;
    ProfessorDTO professorDTO;

    AtualizarProfessorRequest professorRequest;
    List<Professor> listaProfessores;

    String nomeProfessor = "Brunno Nogueira";
    String email = "brunno@ada.com.br";
    String disciplinaLecionada = "Programacao Web";
    Boolean estaAtivo = true;

    @BeforeEach //annotation para dados que podem ser usados em todos os testes
    public void setup() {
        professor = new Professor(nomeProfessor, email, disciplinaLecionada, estaAtivo);
        professorDTO = new ProfessorDTO(nomeProfessor, email, disciplinaLecionada, estaAtivo);
        professorRequest = new AtualizarProfessorRequest(nomeProfessor, email, disciplinaLecionada, estaAtivo);

    }

    @Test
    void deveCadastrarProfessorComSucesso(){
        when(repositorioProfessor.save(any())).thenReturn(professorDTO.paraEntidade());

        Professor professorCadastrado = serviceProfessor.cadastrarProfessor(professorDTO);

        assertNotNull(professorCadastrado);
        assertEquals(nomeProfessor, professorCadastrado.getNomeProfessor());
        assertEquals(email, professorCadastrado.getEmail());
        assertEquals(disciplinaLecionada, professorCadastrado.getDisciplinaLecionada());
        assertEquals(estaAtivo, professorCadastrado.getEstaAtivo());

        verifyNoMoreInteractions(repositorioProfessor);
    }

    @Test
    void deveListarProfessoresComSucesso() {
        //when: config do mock, metodo que é chamado dentro do metodo de teste
        //thenReturn: o que esperamos ser retornado quando o metodo é chamado
        when(repositorioProfessor.findAll()).thenReturn(List.of(professor));

        //chamada do metodo da service
        List<Professor> listaProfessores = serviceProfessor.listarTodos();

        //assertions
        assertNotNull(listaProfessores); //assegurar que a lista nao é nula
        assertEquals("Professor", listaProfessores.get(0).getClass().getSimpleName());// Verifica o tipo do primeiro elemento da lista
        assertEquals(1, listaProfessores.size()); //assegurar que tamanho da lista esperado com o obtido/atual
        assertEquals(Professor.class, listaProfessores.get(0).getClass()); //assegurar que o objeto esperado é do mesmo tipo que o obj obtido

        // Verifica se o método findAll foi chamado apenas uma vez
        verify(repositorioProfessor).findAll();
        // Verifica se não houve mais interações com o mock repositorioProfessor
        verifyNoMoreInteractions(repositorioProfessor);

    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoListarProfessores(){
        //Config do comportamento do mock
        //lancar uma exception quando chamar o metodo findAll
        when(repositorioProfessor.findAll()).thenThrow(new ResourceNotFoundException("lista de professores"));

        try {
            serviceProfessor.listarTodos();
        } catch (Exception ex) {
            assertEquals(ResourceNotFoundException.class, ex.getClass()); //comparar tipo da exceptio lancada
            assertEquals("Não há registros de lista de professores no sistema.", ex.getMessage()); //comparar msg lancada
        }

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceProfessor.listarTodos();}); //Verificacar se a excecao é lançada quando o metodo retorna uma lista vazia
        verifyNoMoreInteractions(repositorioProfessor); //verificar se nao houve mais interacao com o repository

    }

    @Test
    void editarParcialProfessor() {
    }

    @Test
    void atualizarProfessorComSuceso() throws Exception {
        when(repositorioProfessor.findById(anyLong())).thenReturn(Optional.of(professor));
        when(repositorioProfessor.save(any())).thenReturn(professor);

        Professor professorAtualizado = serviceProfessor.atualizarProfessor(1L, professorRequest);

        // Verificações
        assertNotNull(professorAtualizado);
        assertEquals(nomeProfessor, professorAtualizado.getNomeProfessor());
        assertEquals(email, professorAtualizado.getEmail());
        assertEquals(disciplinaLecionada, professorAtualizado.getDisciplinaLecionada());
        assertEquals(estaAtivo, professorAtualizado.getEstaAtivo());


        verify(repositorioProfessor, times(1)).findById(1L);
        verify(repositorioProfessor, times(1)).save(any());
        verifyNoMoreInteractions(repositorioProfessor);
    }

    @Test
    void atualizarProfessorComException() {
        when(repositorioProfessor.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceProfessor.atualizarProfessor(1L, professorRequest);
        });

        verify(repositorioProfessor, times(1)).findById(1L);
        verifyNoMoreInteractions(repositorioProfessor);
    }


    @Test
    void filtrarProfessorPorNomeComSuceso() {
        when(repositorioProfessor.findProfessorsByNomeProfessor(anyString())).thenReturn(List.of(professor));
        List<Professor> professoresFiltrados = serviceProfessor.filtrarProfessorPorNome("Brunno Nogueira");

        assertNotNull(professoresFiltrados);
        assertEquals(1, professoresFiltrados.size());

        assertEquals("Brunno Nogueira", professoresFiltrados.get(0).getNomeProfessor());

        verify(repositorioProfessor, times(1)).findProfessorsByNomeProfessor("Brunno Nogueira");

        verifyNoMoreInteractions(repositorioProfessor);
    }

    @Test
    void filtrarProfessorPorNomeComException() {
        when(repositorioProfessor.findProfessorsByNomeProfessor(anyString())).thenThrow(new ResourceNotFoundException("professor por nome"));

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceProfessor.filtrarProfessorPorNome("Pepito Perez");
        });

        verify(repositorioProfessor, times(1)).findProfessorsByNomeProfessor("Pepito Perez");
        verifyNoMoreInteractions(repositorioProfessor);
    }


}