package model;

import java.time.LocalDate;

/** Inscrição de um Aluno em uma Palestra (liga as duas classes). */
public class Inscricao {

    private int idInscricao;
    private Aluno aluno;
    private Palestra palestra;
    private LocalDate dataInscricao;

    public Inscricao() {
    }

    public int getIdInscricao() {
        return idInscricao;
    }

    public void setIdInscricao(int idInscricao) {
        this.idInscricao = idInscricao;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Palestra getPalestra() {
        return palestra;
    }

    public void setPalestra(Palestra palestra) {
        this.palestra = palestra;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
}
