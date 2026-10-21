package com.icechopp.view;

import com.icechopp.dao.CaminhaoDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Caminhao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CaminhaoView extends JPanel {

    private final CaminhaoDAO dao = new CaminhaoDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Placa", "Marca", "Ano", "Modelo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoPlaca = new JTextField(10);
    private final JTextField campoMarca = new JTextField(20);
    private final JTextField campoAno = new JTextField(6);
    private final JTextField campoModelo = new JTextField(20);
    private String placaSelecionada = null;

    public CaminhaoView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Caminhões"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Placa:")); form.add(campoPlaca);
        form.add(new JLabel("Marca:")); form.add(campoMarca);
        form.add(new JLabel("Ano:")); form.add(campoAno);
        form.add(new JLabel("Modelo:")); form.add(campoModelo);

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

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Caminhao c : dao.listarTodos()) {
                modelo.addRow(new Object[]{c.getPlaca(), c.getMarca(), c.getAno(), c.getModelo()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar caminhões:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        placaSelecionada = String.valueOf(modelo.getValueAt(linha, 0));
        campoPlaca.setText(placaSelecionada);
        campoPlaca.setEditable(false);
        campoMarca.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        campoAno.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        campoModelo.setText(String.valueOf(modelo.getValueAt(linha, 3)));
    }

    private void limpar() {
        placaSelecionada = null;
        campoPlaca.setText(""); campoPlaca.setEditable(true);
        campoMarca.setText(""); campoAno.setText(""); campoModelo.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        if (campoPlaca.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe a placa.");
            return;
        }
        int ano;
        try {
            ano = Integer.parseInt(campoAno.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Ano inválido.");
            return;
        }
        try {
            Caminhao c = new Caminhao();
            c.setPlaca(campoPlaca.getText().trim().toUpperCase());
            c.setMarca(campoMarca.getText().trim());
            c.setAno(ano);
            c.setModelo(campoModelo.getText().trim());

            if (placaSelecionada == null) {
                dao.inserir(c);
            } else {
                dao.atualizar(c);
            }
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (placaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione um caminhão.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir caminhão selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.deletar(placaSelecionada);
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir:\n" + e.getMessage());
        }
    }
}
