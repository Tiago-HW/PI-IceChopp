package com.icechopp.view.components;

import com.icechopp.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Campo de data com botão de calendário (popup) para seleção visual,
 * sem depender de bibliotecas externas. Também aceita digitação manual
 * no formato aaaa-mm-dd.
 */
public class DatePickerField extends JPanel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Locale PT_BR = new Locale("pt", "BR");

    private final JTextField campoTexto = new JTextField(10);
    private final JButton botaoCalendario = new JButton("\uD83D\uDCC5");
    private final JPopupMenu popup = new JPopupMenu();
    private JPanel painelDias;
    private JLabel labelMesAno;
    private YearMonth mesExibido = YearMonth.now();

    public DatePickerField() {
        super(new BorderLayout(4, 0));
        setOpaque(false);
        campoTexto.setToolTipText("Formato: aaaa-mm-dd");
        campoTexto.setColumns(9);
        botaoCalendario.setToolTipText("Escolher no calendário");
        botaoCalendario.setMargin(new Insets(2, 8, 2, 8));
        botaoCalendario.setFocusable(false);
        botaoCalendario.addActionListener(this::abrirCalendario);

        add(campoTexto, BorderLayout.CENTER);
        add(botaoCalendario, BorderLayout.EAST);
        montarPopup();
    }

    private void montarPopup() {
        JPanel raiz = new JPanel(new BorderLayout(0, 4));
        raiz.setBorder(BorderFactory.createLineBorder(UITheme.NAVY, 1));
        raiz.setBackground(Color.WHITE);
        raiz.setPreferredSize(new Dimension(230, 220));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UITheme.NAVY);
        topo.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JButton anterior = new JButton("<");
        JButton proximo = new JButton(">");
        labelMesAno = new JLabel("", SwingConstants.CENTER);
        labelMesAno.setForeground(Color.WHITE);
        labelMesAno.setFont(UITheme.FONTE_SUBTITULO);

        for (JButton nav : new JButton[]{anterior, proximo}) {
            nav.setFocusable(false);
            nav.setBackground(UITheme.NAVY);
            nav.setForeground(Color.WHITE);
            nav.setBorderPainted(false);
            nav.setFont(UITheme.FONTE_BOTAO);
        }
        anterior.addActionListener(e -> {
            mesExibido = mesExibido.minusMonths(1);
            atualizarCalendario();
        });
        proximo.addActionListener(e -> {
            mesExibido = mesExibido.plusMonths(1);
            atualizarCalendario();
        });

        topo.add(anterior, BorderLayout.WEST);
        topo.add(labelMesAno, BorderLayout.CENTER);
        topo.add(proximo, BorderLayout.EAST);

        painelDias = new JPanel(new GridLayout(0, 7, 2, 2));
        painelDias.setBackground(Color.WHITE);
        painelDias.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JButton hoje = new JButton("Hoje");
        hoje.setFocusable(false);
        UITheme.estilizarBotao(hoje);
        hoje.addActionListener(e -> {
            setDate(LocalDate.now());
            popup.setVisible(false);
        });
        JButton limpar = new JButton("Limpar");
        limpar.setFocusable(false);
        UITheme.estilizarBotao(limpar);
        limpar.addActionListener(e -> {
            campoTexto.setText("");
            popup.setVisible(false);
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        rodape.setBackground(Color.WHITE);
        rodape.add(hoje);
        rodape.add(limpar);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(painelDias, BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);

        popup.setBorder(BorderFactory.createEmptyBorder());
        popup.add(raiz);
    }

    private void abrirCalendario(ActionEvent e) {
        LocalDate atual = getDate();
        mesExibido = atual != null ? YearMonth.from(atual) : YearMonth.now();
        atualizarCalendario();
        popup.show(botaoCalendario, botaoCalendario.getWidth() - 230, botaoCalendario.getHeight());
    }

    private void atualizarCalendario() {
        painelDias.removeAll();
        String nomeMes = mesExibido.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
        labelMesAno.setText(capitalizar(nomeMes) + " " + mesExibido.getYear());

        String[] diasSemana = {"D", "S", "T", "Q", "Q", "S", "S"};
        for (String d : diasSemana) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(UITheme.FONTE_BOTAO);
            l.setForeground(UITheme.BLUE);
            painelDias.add(l);
        }

        LocalDate primeiroDia = mesExibido.atDay(1);
        int deslocamento = primeiroDia.getDayOfWeek().getValue() % 7; // getDayOfWeek: 1=segunda..7=domingo -> %7 = 0 p/ domingo
        for (int i = 0; i < deslocamento; i++) painelDias.add(new JLabel(""));

        LocalDate selecionada = getDate();
        for (int dia = 1; dia <= mesExibido.lengthOfMonth(); dia++) {
            LocalDate data = mesExibido.atDay(dia);
            JButton botaoDia = new JButton(String.valueOf(dia));
            botaoDia.setMargin(new Insets(1, 1, 1, 1));
            botaoDia.setFocusable(false);
            botaoDia.setFont(UITheme.FONTE_LABEL);
            boolean isHoje = data.equals(LocalDate.now());
            boolean isSelecionado = data.equals(selecionada);
            if (isSelecionado) {
                botaoDia.setBackground(UITheme.BLUE);
                botaoDia.setForeground(Color.WHITE);
            } else if (isHoje) {
                botaoDia.setBackground(UITheme.GOLD);
                botaoDia.setForeground(UITheme.NAVY);
            } else {
                botaoDia.setBackground(Color.WHITE);
                botaoDia.setForeground(UITheme.NAVY);
            }
            botaoDia.setBorder(BorderFactory.createLineBorder(UITheme.PALE_BLUE));
            final LocalDate dataFinal = data;
            botaoDia.addActionListener(e -> {
                setDate(dataFinal);
                popup.setVisible(false);
            });
            painelDias.add(botaoDia);
        }
        popup.revalidate();
        popup.repaint();
    }

    private String capitalizar(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** @return a data digitada/selecionada, ou {@code null} se vazio ou inválido. */
    public LocalDate getDate() {
        String texto = campoTexto.getText().trim();
        if (texto.isEmpty()) return null;
        try {
            return LocalDate.parse(texto, FORMATO);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    public void setDate(LocalDate data) {
        campoTexto.setText(data == null ? "" : data.format(FORMATO));
    }

    /** @return true se o texto digitado não está vazio, mas não é uma data válida. */
    public boolean isTextoInvalido() {
        String texto = campoTexto.getText().trim();
        if (texto.isEmpty()) return false;
        return getDate() == null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        campoTexto.setEnabled(enabled);
        botaoCalendario.setEnabled(enabled);
    }

    public JTextField getCampoTexto() {
        return campoTexto;
    }
}
