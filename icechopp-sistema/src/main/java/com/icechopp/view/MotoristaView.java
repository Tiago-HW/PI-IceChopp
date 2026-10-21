package com.icechopp.view;

import com.icechopp.dao.CaminhaoDAO;
import com.icechopp.util.UITheme;
import com.icechopp.dao.MotoristaDAO;
import com.icechopp.model.Caminhao;
import com.icechopp.model.Motorista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MotoristaView extends JPanel {

    private final MotoristaDAO dao = new MotoristaDAO();
    private final CaminhaoDAO caminhaoDAO = new CaminhaoDAO();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "CPF", "Telefone", "Endereço", "Placa Caminhão", "Salário"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoCpf = new JTextField(15);
    private final JTextField campoTelefone = new JTextField(15);
    private final JTextField campoEndereco = new JTextField(20);
    private final JComboBox<Caminhao> comboCaminhao = new JComboBox<>();
    private final JTextField campoSalario = new JTextField(10);
    private Integer idSelecionado = null;

    public MotoristaView() {
        montarTela();
        carregarCaminhoes();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Motoristas"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:")); form.add(campoNome);
        form.add(new JLabel("CPF:")); form.add(campoCpf);
        form.add(new JLabel("Telefone:")); form.add(campoTelefone);
        form.add(new JLabel("Endereço:")); form.add(campoEndereco);
        form.add(new JLabel("Caminhão:")); form.add(comboCaminhao);
        form.add(new JLabel("Salário:")); form.add(campoSalario);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnNovo); botoes.add(btnSalvar); botoes.add(btnExcluir);

        JPanel sul = new JPanel(new BorderLayout());
        sul.add(form, BorderLayout.CENTER);
        sul.add(botoes, BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    private void carregarCaminhoes() {
        comboCaminhao.removeAllItems();
        try {
            List<Caminhao> lista = caminhaoDAO.listarTodos();
            for (Caminhao c : lista) comboCaminhao.addItem(c);
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum caminhão cadastrado. Cadastre um caminhão antes de cadastrar motoristas.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar caminhões:\n" + e.getMessage());
        }
    }

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Motorista m : dao.listarTodos()) {
                modelo.addRow(new Object[]{m.getIdMotorista(), m.getNome(), m.getCpf(), m.getTelefone(),
                        m.getEndereco(), m.getPlacaCaminhao(), m.getSalario()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar motoristas:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        campoCpf.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        campoTelefone.setText(String.valueOf(modelo.getValueAt(linha, 3)));
        campoEndereco.setText(String.valueOf(modelo.getValueAt(linha, 4)));
        String placa = String.valueOf(modelo.getValueAt(linha, 5));
        for (int i = 0; i < comboCaminhao.getItemCount(); i++) {
            if (comboCaminhao.getItemAt(i).getPlaca().equals(placa)) {
                comboCaminhao.setSelectedIndex(i);
                break;
            }
        }
        campoSalario.setText(String.valueOf(modelo.getValueAt(linha, 6)));
    }

    private void limpar() {
        idSelecionado = null;
        campoNome.setText(""); campoCpf.setText(""); campoTelefone.setText("");
        campoEndereco.setText(""); campoSalario.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        if (campoNome.getText().isBlank() || campoCpf.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe nome e CPF.");
            return;
        }
        Caminhao caminhao = (Caminhao) comboCaminhao.getSelectedItem();
        if (caminhao == null) {
            JOptionPane.showMessageDialog(this, "Cadastre e selecione um caminhão.");
            return;
        }
        long telefone; double salario;
        try {
            telefone = Long.parseLong(campoTelefone.getText().trim());
            salario = Double.parseDouble(campoSalario.getText().trim().replace(",", "."));
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Telefone e salário devem ser numéricos.");
            return;
        }
        try {
            Motorista m = new Motorista();
            m.setNome(campoNome.getText().trim());
            m.setCpf(campoCpf.getText().trim());
            m.setTelefone(telefone);
            m.setEndereco(campoEndereco.getText().trim());
            m.setPlacaCaminhao(caminhao.getPlaca());
            m.setSalario(salario);

            if (idSelecionado == null) {
                dao.inserir(m);
            } else {
                m.setIdMotorista(idSelecionado);
                dao.atualizar(m);
            }
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um motorista.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir motorista selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.deletar(idSelecionado);
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir:\n" + e.getMessage());
        }
    }
}
