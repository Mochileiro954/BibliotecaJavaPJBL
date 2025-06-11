import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

abstract class Pessoa {
    protected String nome;
    protected int cpf;
    protected String senha;
    protected double saldo;

    public Pessoa(String nome, int cpf, String senha, double saldo) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.saldo = saldo;
    }

    public String getNome() { return nome; }
    public int getCpf() { return cpf; }
    public String getSenha() { return senha; }
    public double getSaldo() { return saldo; }
    public void adicionarSaldo(double valor) { saldo += valor; }
    public abstract String getTipo();
}

class Cliente extends Pessoa {
    public Cliente(String nome, int cpf, String senha, double saldo) {
        super(nome, cpf, senha, saldo);
    }
    @Override
    public String getTipo() { return "Cliente"; }
}

class Funcionario extends Pessoa {
    public Funcionario(String nome, int cpf, String senha) {
        super(nome, cpf, senha, 0.0);
    }
    @Override
    public String getTipo() { return "Funcionario"; }
}

class Livro {
    private String titulo;
    private String autor;
    private boolean disponivel = true;
    private int cpfAlugador = -1;

    public Livro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponivel() { return disponivel; }
    public int getCpfAlugador() { return cpfAlugador; }

    public void alugar(int cpfCliente) {
        disponivel = false;
        cpfAlugador = cpfCliente;
    }

    public void devolver() {
        disponivel = true;
        cpfAlugador = -1;
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Alugado";
        return titulo + " - " + autor + " [" + status + "]";
    }
}

public class Main {
    private static ArrayList<Cliente> clientes = new ArrayList<>();
    private static ArrayList<Funcionario> funcionarios = new ArrayList<>();
    private static ArrayList<Livro> livros = new ArrayList<>();
    private static Pessoa usuarioLogado = null;
    private static final double TAXA_ALUGUEL = 15.0;

    public static void main(String[] args) {
        carregarClientes();
        carregarFuncionarios();
        carregarLivros();
        SwingUtilities.invokeLater(Main::telaEscolhaLogin);
    }

    private static void carregarClientes() {
        clientes.clear();
        File file = new File("clientes.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length == 4) {
                    clientes.add(new Cliente(p[0], Integer.parseInt(p[1]), p[2], Double.parseDouble(p[3])));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes.");
        }
    }
    private static void salvarClientes() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("clientes.csv"), StandardCharsets.UTF_8))) {
            for (Cliente c : clientes) {
                pw.println(c.getNome() + ";" + c.getCpf() + ";" + c.getSenha() + ";" + c.getSaldo());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar clientes.");
        }
    }
    private static void carregarFuncionarios() {
        funcionarios.clear();
        File file = new File("funcionarios.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length == 3) {
                    funcionarios.add(new Funcionario(p[0], Integer.parseInt(p[1]), p[2]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar funcionários.");
        }
    }
    private static void salvarFuncionarios() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("funcionarios.csv"), StandardCharsets.UTF_8))) {
            for (Funcionario f : funcionarios) {
                pw.println(f.getNome() + ";" + f.getCpf() + ";" + f.getSenha());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar funcionários.");
        }
    }
    private static void carregarLivros() {
        livros.clear();
        File file = new File("livros.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length >= 2) {
                    Livro l = new Livro(p[0], p[1]);
                    if (p.length == 4) {
                        if (p[2].equals("false")) {
                            l.alugar(Integer.parseInt(p[3]));
                        }
                        if (p[2].equals("true")) {
                        }
                    }
                    livros.add(l);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar livros.");
        }
    }
    private static void salvarLivros() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("livros.csv"), StandardCharsets.UTF_8))) {
            for (Livro l : livros) {
                pw.println(l.getTitulo() + ";" + l.getAutor() + ";" + l.isDisponivel() + ";" + l.getCpfAlugador());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar livros.");
        }
    }

    private static void telaEscolhaLogin() {
        JFrame frame = new JFrame("Biblioteca - Escolha Login");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JButton btnCliente = new JButton("Entrar como Cliente");
        JButton btnFuncionario = new JButton("Entrar como Funcionário");
        panel.add(btnCliente);
        panel.add(btnFuncionario);

        btnCliente.addActionListener(e -> {
            frame.dispose();
            telaLoginCliente();
        });
        btnFuncionario.addActionListener(e -> {
            frame.dispose();
            telaLoginFuncionario();
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void telaLoginCliente() {
        JFrame frame = new JFrame("Login Cliente");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nomeField = new JTextField();
        JTextField cpfField = new JTextField();
        JPasswordField senhaField = new JPasswordField();
        JButton btnLogin = new JButton("Entrar");
        JButton btnCadastro = new JButton("Cadastrar");
        JButton btnVoltar = new JButton("Voltar"); 
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        panel.add(btnLogin);
        panel.add(btnCadastro);
        panel.add(btnVoltar); 

        btnLogin.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());
            int cpf;
            try { cpf = Integer.parseInt(cpfField.getText().trim()); } catch (Exception ex) { cpf = -1; }
            for (Cliente c : clientes) {
                if (c.getNome().equals(nome) && c.getSenha().equals(senha) && c.getCpf() == cpf) {
                    usuarioLogado = c;
                    frame.dispose();
                    telaPrincipalCliente();
                    return;
                }
            }
            JOptionPane.showMessageDialog(frame, "Nome, CPF ou senha inválidos.");
        });

        btnCadastro.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());
            int cpf;
            try { cpf = Integer.parseInt(cpfField.getText().trim()); } catch (Exception ex) { cpf = -1; }
            if (nome.isEmpty() || senha.isEmpty() || cpf == -1) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos corretamente.");
                return;
            }
            for (Cliente c : clientes) {
                if (c.getCpf() == cpf) {
                    JOptionPane.showMessageDialog(frame, "CPF já cadastrado.");
                    return;
                }
            }
            clientes.add(new Cliente(nome, cpf, senha, 0.0));
            salvarClientes();
            JOptionPane.showMessageDialog(frame, "Cadastro realizado! Faça login.");
        });

        btnVoltar.addActionListener(e -> {
            frame.dispose();
            telaEscolhaLogin(); 
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void telaLoginFuncionario() {
        JFrame frame = new JFrame("Login Funcionário");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nomeField = new JTextField();
        JTextField cpfField = new JTextField();
        JPasswordField senhaField = new JPasswordField();
        JButton btnLogin = new JButton("Entrar");
        JButton btnCadastro = new JButton("Cadastrar");
        JButton btnVoltar = new JButton("Voltar"); 
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        panel.add(btnLogin);
        panel.add(btnCadastro);
        panel.add(btnVoltar); 

        btnLogin.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());
            int cpf;
            try { cpf = Integer.parseInt(cpfField.getText().trim()); } catch (Exception ex) { cpf = -1; }
            for (Funcionario f : funcionarios) {
                if (f.getNome().equals(nome) && f.getSenha().equals(senha) && f.getCpf() == cpf) {
                    usuarioLogado = f;
                    frame.dispose();
                    telaPrincipalFuncionario();
                    return;
                }
            }
            JOptionPane.showMessageDialog(frame, "Nome, CPF ou senha inválidos.");
        });

        btnCadastro.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());
            int cpf;
            try { cpf = Integer.parseInt(cpfField.getText().trim()); } catch (Exception ex) { cpf = -1; }
            if (nome.isEmpty() || senha.isEmpty() || cpf == -1) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos corretamente.");
                return;
            }
            for (Funcionario f : funcionarios) {
                if (f.getCpf() == cpf) {
                    JOptionPane.showMessageDialog(frame, "CPF já cadastrado.");
                    return;
                }
            }
            funcionarios.add(new Funcionario(nome, cpf, senha));
            salvarFuncionarios();
            JOptionPane.showMessageDialog(frame, "Cadastro realizado! Faça login.");
        });

        btnVoltar.addActionListener(e -> {
            frame.dispose();
            telaEscolhaLogin(); 
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void telaPrincipalCliente() {
        Cliente cliente = (Cliente) usuarioLogado;
        JFrame frame = new JFrame("Biblioteca - Cliente: " + cliente.getNome());
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultListModel<Livro> livrosModel = new DefaultListModel<>();
        for (Livro l : livros) livrosModel.addElement(l);
        JList<Livro> listaLivros = new JList<>(livrosModel);

        JLabel saldoLabel = new JLabel("Saldo: R$" + String.format("%.2f", cliente.getSaldo()));

        JButton btnAlugar = new JButton("Alugar Livro (R$15)");
        JButton btnDevolver = new JButton("Devolver Livro");
        JButton btnSaldo = new JButton("Adicionar Saldo");
        JButton btnLogout = new JButton("Logout");
        JButton btnVoltar = new JButton("Voltar"); 

        btnAlugar.addActionListener(e -> {
            Livro livro = listaLivros.getSelectedValue();
            if (livro == null) {
                JOptionPane.showMessageDialog(frame, "Selecione um livro.");
                return;
            }
            if (!livro.isDisponivel()) {
                JOptionPane.showMessageDialog(frame, "Livro já está alugado.");
                return;
            }
            if (cliente.getSaldo() < TAXA_ALUGUEL) {
                JOptionPane.showMessageDialog(frame, "Saldo insuficiente.");
                return;
            }
            livro.alugar(cliente.getCpf());
            cliente.adicionarSaldo(-TAXA_ALUGUEL);
            salvarLivros();
            salvarClientes();
            livrosModel.setElementAt(livro, listaLivros.getSelectedIndex());
            saldoLabel.setText("Saldo: R$" + String.format("%.2f", cliente.getSaldo()));
            JOptionPane.showMessageDialog(frame, "Livro alugado com sucesso!");
        });

        btnDevolver.addActionListener(e -> {
            Livro livro = listaLivros.getSelectedValue();
            if (livro == null) {
                JOptionPane.showMessageDialog(frame, "Selecione um livro.");
                return;
            }
            if (livro.isDisponivel() || livro.getCpfAlugador() != cliente.getCpf()) {
                JOptionPane.showMessageDialog(frame, "Você não alugou este livro.");
                return;
            }
            livro.devolver();
            salvarLivros();
            livrosModel.setElementAt(livro, listaLivros.getSelectedIndex());
            JOptionPane.showMessageDialog(frame, "Livro devolvido!");
        });

        btnSaldo.addActionListener(e -> {
            String valorStr = JOptionPane.showInputDialog(frame, "Valor para adicionar:");
            try {
                double valor = Double.parseDouble(valorStr);
                if (valor <= 0) throw new Exception();
                cliente.adicionarSaldo(valor);
                salvarClientes();
                saldoLabel.setText("Saldo: R$" + String.format("%.2f", cliente.getSaldo()));
                JOptionPane.showMessageDialog(frame, "Saldo adicionado! Saldo atual: R$" + cliente.getSaldo());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Valor inválido.");
            }
        });

        btnLogout.addActionListener(e -> {
            usuarioLogado = null;
            frame.dispose();
            telaEscolhaLogin();
        });

        btnVoltar.addActionListener(e -> {
            usuarioLogado = null;
            frame.dispose();
            telaEscolhaLogin();
        });

        JPanel botoes = new JPanel();
        botoes.add(btnAlugar);
        botoes.add(btnDevolver);
        botoes.add(btnSaldo);
        botoes.add(btnLogout);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(saldoLabel, BorderLayout.WEST);

        frame.add(topo, BorderLayout.NORTH);
        frame.add(new JScrollPane(listaLivros), BorderLayout.CENTER);
        frame.add(botoes, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void telaPrincipalFuncionario() {
        JFrame frame = new JFrame("Biblioteca - Funcionário: " + usuarioLogado.getNome());
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultListModel<Livro> livrosModel = new DefaultListModel<>();
        for (Livro l : livros) livrosModel.addElement(l);
        JList<Livro> listaLivros = new JList<>(livrosModel);

        JButton btnAdicionarLivro = new JButton("Cadastrar Livro");
        JButton btnVerClientes = new JButton("Ver Clientes");
        JButton btnLogout = new JButton("Logout");

        btnAdicionarLivro.addActionListener(e -> {
            JTextField tituloField = new JTextField();
            JTextField autorField = new JTextField();
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Título:"));
            panel.add(tituloField);
            panel.add(new JLabel("Autor:"));
            panel.add(autorField);
            int result = JOptionPane.showConfirmDialog(frame, panel, "Cadastrar Livro", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String titulo = tituloField.getText();
                String autor = autorField.getText();
                if (titulo.isEmpty() || autor.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Preencha todos os campos.");
                    return;
                }
                Livro novo = new Livro(titulo, autor);
                livros.add(novo);
                livrosModel.addElement(novo);
                salvarLivros();
                JOptionPane.showMessageDialog(frame, "Livro cadastrado!");
            }
        });

        btnVerClientes.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Cliente c : clientes) {
                sb.append("Nome: ").append(c.getNome())
                  .append(" | CPF: ").append(c.getCpf())
                  .append(" | Saldo: R$").append(c.getSaldo()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.length() == 0 ? "Nenhum cliente cadastrado." : sb.toString());
        });

        btnLogout.addActionListener(e -> {
            usuarioLogado = null;
            frame.dispose();
            telaEscolhaLogin();
        });

        JPanel botoes = new JPanel();
        botoes.add(btnAdicionarLivro);
        botoes.add(btnVerClientes);
        botoes.add(btnLogout);

        frame.add(new JScrollPane(listaLivros), BorderLayout.CENTER);
        frame.add(botoes, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
