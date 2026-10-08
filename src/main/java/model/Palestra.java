package model;

import java.time.LocalDate;

/** Palestra do evento. Cada palestra tem um Professor responsável. */
public class Palestra {

    private int idPalestra;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private String horario; // formato HH:mm
    private String local;
    private Professor professor;

    public Palestra() {
    }

    public int getIdPalestra() {
        return idPalestra;
    }

    public void setIdPalestra(int idPalestra) {
        this.idPalestra = idPalestra;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
