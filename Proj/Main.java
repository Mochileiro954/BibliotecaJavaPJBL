// Importações
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.swing.*;

class HashUtil {
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criar hash", e);
        }
    }
}

abstract class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String nome;
    protected String cpf;
    protected String senha;
    protected double saldo;

    public Pessoa(String nome, String cpf, String senha, double saldo) {
        this.nome = nome;
        this.cpf = HashUtil.hash(cpf);
        this.senha = HashUtil.hash(senha);
        this.saldo = saldo;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getSenha() { return senha; }
    public double getSaldo() { return saldo; }
    public void adicionarSaldo(double valor) { saldo += valor; }
    public abstract String getTipo();
}

class UsuarioInvalidoException extends Exception {
    public UsuarioInvalidoException() {
        super("Nome, CPF ou senha inválidos.");
    }
}

class DadosInvalidosException extends Exception {
    public DadosInvalidosException() {
        super("Preencha todos os campos corretamente.");
    }
}

class CpfJaCadastradoException extends Exception {
    public CpfJaCadastradoException() {
        super("CPF já cadastrado.");
    }
}

class LivroIndisponivelException extends Exception {
    public LivroIndisponivelException() {
        super("Livro já está alugado.");
    }
}

class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente.");
    }
}

class LivroNaoAlugadoPorClienteException extends Exception {
    public LivroNaoAlugadoPorClienteException() {
        super("Você não alugou este livro.");
    }
}

class ValorInvalidoException extends Exception {
    public ValorInvalidoException() {
        super("Valor inválido.");
    }
}

class CampoVazioException extends Exception {
    public CampoVazioException() {
        super("Operação cancelada ou campo vazio.");
    }
}

class Cliente extends Pessoa {
    private static final long serialVersionUID = 1L;
    public Cliente(String nome, String cpf, String senha, double saldo) {
        super(nome, cpf, senha, saldo);
    }
    @Override
    public String getTipo() { return "Cliente"; }
}

class Funcionario extends Pessoa {
    private static final long serialVersionUID = 1L;
    public Funcionario(String nome, String cpf, String senha) {
        super(nome, cpf, senha, 0.0);
    }
    @Override
    public String getTipo() { return "Funcionario"; }
}

class Livro implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titulo;
    private String autor;
    private boolean disponivel = true;
    private String cpfAlugador = "-1";

    public Livro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponivel() { return disponivel; }
    public String getCpfAlugador() { return cpfAlugador; }

    public void alugar(String cpfCliente) {
        disponivel = false;
        cpfAlugador = cpfCliente;
    }

    public void devolver() {
        disponivel = true;
        cpfAlugador = "-1";
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

    public static boolean validarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }

        for (int i = 0; i < cpf.length(); i++) {
            if (!Character.isDigit(cpf.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean validarSenha(String senha) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

        return senha.matches(regex);
    }


    private static void carregarClientes() {
        clientes.clear();
        File file = new File("clientes.cleitin");
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            clientes = (ArrayList<Cliente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes.");
        }
    }

    private static void salvarClientes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("clientes.cleitin"))) {
            oos.writeObject(clientes);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar clientes.");
        }
    }


    private static void carregarFuncionarios() {
        funcionarios.clear();
        File file = new File("funcionarios.cleitin");
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            funcionarios = (ArrayList<Funcionario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar funcionários.");
        }
    }

    private static void salvarFuncionarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("funcionarios.cleitin"))) {
            oos.writeObject(funcionarios);
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
                    if (p.length == 4 && p[2].equals("false")) {
                        l.alugar(p[3]);
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
            try {
                String nome = nomeField.getText().trim();
                String senha = new String(senhaField.getPassword());
                String cpf = cpfField.getText().trim();

                if (!validarSenha(senha)) {
                    throw new UsuarioInvalidoException();
                }

                if (!validarCpf(cpf)) {
                    throw new UsuarioInvalidoException();
                }

                String senhaHash = HashUtil.hash(senha);
                String cpfHash = HashUtil.hash(cpf);

                for (Cliente c : clientes) {
                    if (c.getNome().equals(nome) && c.getSenha().equals(senhaHash) && c.getCpf().equals(cpfHash)) {
                        usuarioLogado = c;
                        frame.dispose();
                        telaPrincipalCliente();
                        return;
                    }
                }
                throw new UsuarioInvalidoException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });


        btnCadastro.addActionListener(e -> {
            try {
                String nome = nomeField.getText().trim();
                String senha = new String(senhaField.getPassword());
                String cpf = cpfField.getText().trim();

                if (!validarSenha(senha)) {
                    throw new DadosInvalidosException();
                }

                if (!validarCpf(cpf)) {
                    throw new DadosInvalidosException();
                }

                if (nome.isEmpty() || senha.isEmpty()) throw new DadosInvalidosException();

                String cpfHash = HashUtil.hash(cpf);
                for (Cliente c : clientes) {
                    if (c.getCpf().equals(cpfHash)) throw new CpfJaCadastradoException();
                }

                clientes.add(new Cliente(nome, cpf, senha, 0.0));
                salvarClientes();
                JOptionPane.showMessageDialog(frame, "Cadastro realizado! Faça login.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }

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
            try {
                String nome = nomeField.getText().trim();
                String senha = new String(senhaField.getPassword());
                String cpf = cpfField.getText().trim();
                String senhaHash = HashUtil.hash(senha);
                String cpfHash = HashUtil.hash(cpf);
                for (Funcionario f : funcionarios) {
                    if (f.getNome().equals(nome) && f.getSenha().equals(senhaHash) && f.getCpf().equals(cpfHash)) {
                        usuarioLogado = f;
                        frame.dispose();
                        telaPrincipalFuncionario();
                        return;
                    }
                }
                throw new UsuarioInvalidoException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        btnCadastro.addActionListener(e -> {
            try {
                String nome = nomeField.getText().trim();
                String senha = new String(senhaField.getPassword());
                String cpf = cpfField.getText().trim();

                if (!validarSenha(senha)) {
                    throw new DadosInvalidosException();
                }

                if (!validarCpf(cpf)) {
                    throw new DadosInvalidosException();
                }

                if (nome.isEmpty() || senha.isEmpty()) throw new DadosInvalidosException();

                String cpfHash = HashUtil.hash(cpf);
                for (Funcionario f : funcionarios) {
                    if (f.getCpf().equals(cpfHash)) throw new CpfJaCadastradoException();
                }

                funcionarios.add(new Funcionario(nome, cpf, senha));
                salvarFuncionarios();
                JOptionPane.showMessageDialog(frame, "Cadastro realizado! Faça login.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
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

        btnAlugar.addActionListener(e -> {
            try {
                Livro livro = listaLivros.getSelectedValue();
                if (livro == null) throw new Exception("Selecione um livro.");
                if (!livro.isDisponivel()) throw new LivroIndisponivelException();
                if (cliente.getSaldo() < TAXA_ALUGUEL) throw new SaldoInsuficienteException();
                livro.alugar(cliente.getCpf());
                cliente.adicionarSaldo(-TAXA_ALUGUEL);
                salvarLivros();
                salvarClientes();
                livrosModel.setElementAt(livro, listaLivros.getSelectedIndex());
                saldoLabel.setText("Saldo: R$" + String.format("%.2f", cliente.getSaldo()));
                JOptionPane.showMessageDialog(frame, "Livro alugado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        btnDevolver.addActionListener(e -> {
            try {
                Livro livro = listaLivros.getSelectedValue();
                if (livro == null) throw new Exception("Selecione um livro.");
                if (livro.isDisponivel() || !livro.getCpfAlugador().equals(cliente.getCpf())) throw new LivroNaoAlugadoPorClienteException();
                livro.devolver();
                salvarLivros();
                livrosModel.setElementAt(livro, listaLivros.getSelectedIndex());
                JOptionPane.showMessageDialog(frame, "Livro devolvido!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        btnSaldo.addActionListener(e -> {
            try {
                String valorStr = JOptionPane.showInputDialog(frame, "Valor para adicionar:");
                if (valorStr == null || valorStr.trim().isEmpty()) {
                    throw new CampoVazioException();
                }
                double valor = Double.parseDouble(valorStr.trim());
                if (valor <= 0) throw new ValorInvalidoException();
                cliente.adicionarSaldo(valor);
                salvarClientes();
                saldoLabel.setText("Saldo: R$" + String.format("%.2f", cliente.getSaldo()));
                JOptionPane.showMessageDialog(frame, "Saldo adicionado! Saldo atual: R$" + cliente.getSaldo());
            } catch (CampoVazioException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Digite apenas números válidos para o saldo!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        btnLogout.addActionListener(e -> {
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
