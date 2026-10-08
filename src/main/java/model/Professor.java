package model;

/** Professor responsável por palestras. Um professor pode ter várias palestras. */
public class Professor {

    private int idProfessor;
    private String nome;
    private String email;

    public Professor() {
    }

    public Professor(int idProfessor, String nome, String email) {
        this.idProfessor = idProfessor;
        this.nome = nome;
        this.email = email;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nome;
    }
}
