package com.example.application.persistencia;

import com.example.application.entidade.Aluno;
import com.example.application.entidade.Eletivas;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RegistrationServiceA {

    @Autowired
    private AlunoRepository repositoryA;

    @Autowired
    private EletivasRepository eletivasRepository;


    public List<String> getSerie() {
        return Arrays.asList("1 ANO", "2 ANO", "3 ANO"); // setar os valores do combox serie
    }

    public List<String> getTurma() {
        return Arrays.asList("A", "B", "C"); // setar os valores do combox turma
    }

    public void register(Aluno aluno) {
        repositoryA.save(aluno);
    }

    public void delete(Aluno aluno) {
        repositoryA.delete(aluno);
    }

    public List<Aluno> getAllAlunos() {
        return repositoryA.findAll();
    }

    @Transactional
    public void matricularAlunoEmEletiva(Long alunoId, Long eletivaId) {
        Aluno aluno = repositoryA.findById(alunoId).orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        Eletivas eletiva = eletivasRepository.findById(eletivaId).orElseThrow(() -> new EntityNotFoundException("Eletiva não encontrada"));

        if (eletiva.getVagasDisponiveis() > 0) {
            if (aluno.getEletivas().contains(eletiva)) {
                throw new IllegalStateException("O aluno já está matriculado nesta eletiva");
            }

            aluno.getEletivas().add(eletiva);
            eletiva.getAlunos().add(aluno);
            eletiva.setVagasDisponiveis(eletiva.getVagasDisponiveis() - 1);

            repositoryA.save(aluno);
            eletivasRepository.save(eletiva);
        } else {
            throw new IllegalStateException("Não há vagas disponíveis para esta eletiva");
        }
    }

}
