import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

// MODELOS

class Livro {
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
        if (disponivel) disponivel = false;
    }

    public void devolver() {
        if (!disponivel) disponivel = true;
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

    public int getCpf() { return cpf; }
    public String getEmail() { return email; }
    public int getIdade() { return idade; }
    public String getNome() { return nome; }
    public String getSenha() { return senha; }
    public String getSexo() { return sexo; }
}

class Cliente extends Pessoa {
    private double saldo;
    private LocalDate criacao;

    public Cliente(String nome, int cpf, int idade, String sexo, String email, String senha, double saldo) {
        super(nome, cpf, idade, sexo, email, senha);
        this.saldo = saldo;
        this.criacao = LocalDate.now();
    }

    public LocalDate getCriacao() { return criacao; }

    public double getSaldo() { return saldo; }

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

    public String getCargo() { return cargo; }

    public double getSalario() { return salario; }

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

    public double getValor(){
        return valor;
    }

    public double calcularValorFinal() {
        return valor * Math.pow(1 + taxaJuros, prazoMeses);
    }

    public String exibirInfo() {
        return "Empréstimo para o cliente: " + cliente.getNome() + "\n" +
                "Valor: R$ " + valor + "\n" +
                "Juros: " + (taxaJuros * 100) + "% ao mês\n" +
                "Prazo: " + prazoMeses + " meses\n" +
                "Total a pagar: R$ " + String.format("%.2f", calcularValorFinal()) + "\n";
    }
}

class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String msg) {
        super(msg);
    }
}


// Interface

public class Main {
    private static ArrayList<Cliente> clientes = new ArrayList<>();
    private static ArrayList<Funcionario> funcionarios = new ArrayList<>();
    private static JTextArea outputArea;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Biblioteca - GUI");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);

        JButton btnAddCliente = new JButton("Cadastrar Cliente");
        JButton btnAddFuncionario = new JButton("Cadastrar Funcionário");
        JButton btnEmprestimo = new JButton("Fazer Empréstimo");
        JButton btnMostrarClientes = new JButton("Mostrar Clientes");

        btnAddCliente.addActionListener(e -> cadastrarCliente());
        btnAddFuncionario.addActionListener(e -> cadastrarFuncionario());
        btnEmprestimo.addActionListener(e -> fazerEmprestimo());
        btnMostrarClientes.addActionListener(e -> mostrarClientes());

        panel.add(btnAddCliente);
        panel.add(btnAddFuncionario);
        panel.add(btnEmprestimo);
        panel.add(btnMostrarClientes);
        panel.add(new JScrollPane(outputArea));

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void cadastrarCliente() {
        try {
            String nome = JOptionPane.showInputDialog("Nome:");
            int cpf = Integer.parseInt(JOptionPane.showInputDialog("CPF (números):"));
            int idade = Integer.parseInt(JOptionPane.showInputDialog("Idade:"));
            String sexo = JOptionPane.showInputDialog("Sexo:");
            String email = JOptionPane.showInputDialog("Email:");
            String senha = JOptionPane.showInputDialog("Senha:");
            double saldo = Double.parseDouble(JOptionPane.showInputDialog("Saldo:"));

            Cliente c = new Cliente(nome, cpf, idade, sexo, email, senha, saldo);
            clientes.add(c);
            outputArea.append("Cliente cadastrado: " + c.getNome() + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar cliente.");
        }
    }

    private static void cadastrarFuncionario() {
        try {
            String nome = JOptionPane.showInputDialog("Nome:");
            int cpf = Integer.parseInt(JOptionPane.showInputDialog("CPF (números):"));
            int idade = Integer.parseInt(JOptionPane.showInputDialog("Idade:"));
            String sexo = JOptionPane.showInputDialog("Sexo:");
            String email = JOptionPane.showInputDialog("Email:");
            String senha = JOptionPane.showInputDialog("Senha:");
            String cargo = JOptionPane.showInputDialog("Cargo:");
            double salario = Double.parseDouble(JOptionPane.showInputDialog("Salário:"));

            Funcionario f = new Funcionario(nome, cpf, idade, sexo, email, senha, cargo, salario);
            funcionarios.add(f);
            outputArea.append("Funcionário cadastrado: " + f.getNome() + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar funcionário.");
        }
    }

    private static void fazerEmprestimo() {
        try {
            if (clientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
                return;
            }

            String cpfStr = JOptionPane.showInputDialog("CPF do cliente:");
            int cpfBusca = Integer.parseInt(cpfStr);
            Cliente cliente = null;

            for (Cliente c : clientes) {
                if (c.getCpf() == cpfBusca) {
                    cliente = c;
                    break;
                }
            }

            if (cliente == null) {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
                return;
            }

            double valor = Double.parseDouble(JOptionPane.showInputDialog("Valor do empréstimo:"));


            if (cliente.getSaldo() < valor) {
                throw new SaldoInsuficienteException("Saldo insuficiente para o empréstimo.");
            }

            double taxa = Double.parseDouble(JOptionPane.showInputDialog("Taxa de juros (ex: 0.03 para 3%):"));
            int prazo = Integer.parseInt(JOptionPane.showInputDialog("Prazo em meses:"));

            Emprestimo emprestimo = new Emprestimo(valor, taxa, prazo, cliente);
            outputArea.append(emprestimo.exibirInfo());


        } catch (SaldoInsuficienteException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao fazer empréstimo.");
        }
    }


    private static void mostrarClientes() {
        if (clientes.isEmpty()) {
            outputArea.append("Nenhum cliente cadastrado.\n");
            return;
        }
        for (Cliente c : clientes) {
            outputArea.append(c.toString() + "\n");
        }
    }
}
