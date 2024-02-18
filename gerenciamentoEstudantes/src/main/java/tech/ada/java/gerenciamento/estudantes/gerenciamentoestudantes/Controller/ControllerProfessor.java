package tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.AtualizarProfessorRequest;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.Professor;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Model.ProfessorRequest;
import tech.ada.java.gerenciamento.estudantes.gerenciamentoestudantes.Repository.RepositorioProfessor;

import java.util.List;
import java.util.Optional;

// TODO: 18/02/24 cadastrarProfessor()
// TODO: 18/02/24 listarTodos()
// TODO: 18/02/24 editarProfessor
// TODO: 18/02/24 checar modelMapper - implementacao do Estudante

@RestController
public class ControllerProfessor {

    private final RepositorioProfessor repositorioProfessor;
    private final ModelMapper modelMapper;

    @Autowired
    //injetando a dependencia via construtor com padrão inversao de dependencia
    public ControllerProfessor(RepositorioProfessor repositorioProfessor, ModelMapper modelMapper) {
        this.repositorioProfessor = repositorioProfessor;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/professor")
    public Professor cadastrarProfessor(@RequestBody ProfessorRequest professorRequest) {

        //converter a request que chegou no body para uma entidade Professor
        Professor professorConvertido = modelMapper.map(professorRequest, Professor.class);

        Professor novoProfessor = repositorioProfessor.save(professorConvertido);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoProfessor).getBody();

    }

    @GetMapping("/professor")
    public List<Professor> listarTodos() {
        List<Professor> listarProfessores = repositorioProfessor.findAll();
        return listarProfessores;
    }

    @PutMapping("/professor/{id}")
    public ResponseEntity<Professor> editarProfessor(@PathVariable("id") Long id, @RequestBody AtualizarProfessorRequest atualizarProfessorRequest) throws Exception {
        Optional<Professor> optionalProfessor = repositorioProfessor.findById(id);

        if (optionalProfessor.isPresent()) {
            Professor professorExistente = optionalProfessor.get();

            professorExistente.setNomeProfessor(atualizarProfessorRequest.nomeProfessor());
            professorExistente.setEmail(atualizarProfessorRequest.email());
            professorExistente.setDisciplinaLecionada(atualizarProfessorRequest.disciplinaLecionada());

            Professor professorSalvo = repositorioProfessor.save(professorExistente);

            return ResponseEntity.ok(professorSalvo);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

}
