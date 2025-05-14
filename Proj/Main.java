package Proj;

import java.time.LocalDate;
import java.util.ArrayList;


class Biblioteca{
    private Arraylist<Livro> livros;

    public Biblioteca{
        this.livros = ArrayList<>();
    }

    public void addLivro(Livro livro) {
        livros.add(livro);
    }
}

class Livro{
    private String titulo;
    private String autor;
    private int anoPublicacao;
    private boolean disponivel;

    public Livro(String titulo, String autor, int anoPublicacao, boolean disponivel) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.disponivel = disponivel;
    }

    public void emprestar() {
        if (disponivel) {
            disponivel = false;
        }
    }

    public void devolver() {
        if (!disponivel) {
            disponivel = true;
        }
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Emprestado";
        return "Título: " + titulo + ", Autor: " + autor + ", Ano: " + anoPublicacao + ", Status: " + status;
    }

    public boolean getDisponivel() {
        return disponivel;
    }

    public String getTitulo() {
        return titulo;
    }
}

abstract class Pessoa {
    private String nome;
    private int cpf;
    private int idade;
    private String sexo;
    private String email;
    private String senha;

    public Pessoa(String nome, int cpf, int idade, String sexo, String email, String senha) {
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

    public Cliente(String nome, int cpf, int idade, String sexo, String email, String senha, double saldo) {
        super(nome, cpf, idade, sexo, email, senha);
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

class Funcionario extends Pessoa {
    private String cargo;
    private double salario;

    public Funcionario(String nome, int cpf, int idade, String sexo, String email, String senha, String cargo, double salario) {
        super(nome, cpf, idade, sexo, email, senha);
        this.cargo = cargo;
        this.salario = salario;
    }

    public String getCargo() {
        return cargo;
    }

    public double getSalario() {
        return salario;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
            "nome='" + getNome() + '\'' +
            ", cpf=" + getCpf() +
            ", idade=" + getIdade() +
            ", sexo='" + getSexo() + '\'' +
            ", email='" + getEmail() + '\'' +
            ", cargo='" + cargo + '\'' +
            ", salario=" + salario +
            '}';
    }
}

class Emprestimo {
    private double valor;
    private double taxaJuros;
    private int prazoMeses;
    private Cliente cliente;

    public Emprestimo(double valor, double taxaJuros, int prazoMeses, Cliente cliente) {
        this.valor = valor;
        this.taxaJuros = taxaJuros;
        this.prazoMeses = prazoMeses;
        this.cliente = cliente;
    }

    public double calcularValorFinal() {
        return valor * Math.pow(1 + taxaJuros, prazoMeses);
    }

    public void exibirInfo() {
        System.out.println("Empréstimo para o cliente: " + cliente.getNome());
        System.out.println("Valor do empréstimo: R$ " + valor);
        System.out.println("Taxa de juros: " + (taxaJuros * 100) + "% ao mês");
        System.out.println("Prazo: " + prazoMeses + " meses");
        System.out.println("Valor final do empréstimo: R$ " + calcularValorFinal());
    }
}

public class Main {
    public static void main(String[] args) {
        Cliente c = new Cliente("Pedro", 123, 18, "Masculino", "Pedro78446@", "oiteste", 1500.60);

        System.out.println(c);

        Funcionario funcionario = new Funcionario("Maria", 456, 28, "Feminino", "maria@example.com", "senha123", "Gerente", 5000.00);

        System.out.println(funcionario);

        Emprestimo emprestimo = new Emprestimo(5000.00, 0.03, 12, c);  
        
        emprestimo.exibirInfo();
    }
}
