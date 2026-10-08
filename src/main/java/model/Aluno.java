package model;

/** Aluno do evento. Cada aluno pertence a um Curso. */
public class Aluno {

    private int idAluno;
    private String nome;
    private String ra;
    private String email;
    private Curso curso;

    public Aluno() {
    }

    public Aluno(int idAluno, String nome, String ra, String email, Curso curso) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.ra = ra;
        this.email = email;
        this.curso = curso;
    }

    public int getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(int idAluno) {
        this.idAluno = idAluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return nome;
    }
}
