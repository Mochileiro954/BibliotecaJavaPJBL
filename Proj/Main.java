package Proj;

import java.time.LocalDate;

abstract class  Pessoa{
    private String nome;
    private int cpf;
    private int idade;
    private String sexo;
    private String email;
    private String senha;

    public Pessoa(String nome,int cpf, int idade, String sexo, String email, String senha){
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }
    public int getCpf() {
        return cpf;
    }
    public String getEmail() {
        return email;
    }
    public int getIdade() {
        return idade;
    }
    public String getNome() {
        return nome;
    }
    public String getSenha() {
        return senha;
    }
    public String getSexo() {
        return sexo;
    }
}

class Cliente extends Pessoa {
    private double saldo;
    private LocalDate criacao;
    
    public Cliente(String nome,int cpf, int idade, String sexo, String email, String senha, double saldo){
        super(nome,cpf,idade,sexo,email,senha);
        this.saldo = saldo;
        this.criacao = LocalDate.now();
    }

    public LocalDate getCriacao() {
        return criacao;

    }
    public double getSaldo() {
        return saldo;
    }
    @Override
    public String toString() {
        return "Cliente{" +
            "nome='" + getNome() + '\'' +
            ", cpf=" + getCpf() +
            ", idade=" + getIdade() +
            ", sexo='" + getSexo() + '\'' +
            ", email='" + getEmail() + '\'' +
            ", saldo=" + saldo +
            ", criacao=" + criacao +
            '}';
}
}
public class Main{
    public static void main(String[] args) {
        Cliente c = new Cliente("pedro", 123, 18, "Masculino", "Pedro78446@", "oiteste", 1500.60);

        System.out.println(c);
    }
}
