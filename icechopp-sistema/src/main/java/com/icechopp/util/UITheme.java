package com.icechopp.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Identidade visual do sistema icechopp.
 * Paleta e tipografia extraídas do manual de marca (Montserrat + azuis/dourado).
 * A classe também sabe "pintar" recursivamente uma tela inteira (estilizar),
 * para que cada janela/aba não precise repetir código de estilo.
 */
public final class UITheme {

    // Paleta oficial icechopp
    public static final Color NAVY       = new Color(0x0B, 0x1D, 0x33); // #0B1D33
    public static final Color BLUE       = new Color(0x11, 0x67, 0xB1); // #1167B1
    public static final Color LIGHT_BLUE = new Color(0x4F, 0xC3, 0xF7); // #4FC3F7
    public static final Color PALE_BLUE  = new Color(0xE6, 0xF4, 0xFF); // #E6F4FF
    public static final Color GOLD       = new Color(0xFF, 0xC2, 0x3C); // #FFC23C
    public static final Color GRAY_BG    = new Color(0xF2, 0xF4, 0xF7); // #F2F4F7
    public static final Color WHITE      = Color.WHITE;
    public static final Color RED_DANGER = new Color(0xC6, 0x38, 0x38);

    /** Chave de client property usada para marcar painéis com identidade visual própria (não sobrescrever em estilizar()). */
    private static final String MARCA_FIXA = "icechopp.marcaFixa";

    private static final String FONT_FAMILY = fontDisponivel("Montserrat") ? "Montserrat" : "SansSerif";

    public static final Font FONTE_TITULO     = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONTE_SUBTITULO  = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONTE_LABEL      = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONTE_BOTAO      = new Font(FONT_FAMILY, Font.BOLD, 13);

    private UITheme() {
    }

    private static boolean fontDisponivel(String nome) {
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (f.equalsIgnoreCase(nome)) return true;
        }
        return false;
    }

    /** Aplica as cores padrão do Swing (UIManager) antes de qualquer janela ser criada. */
    public static void aplicarTemaGlobal() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("control", GRAY_BG);
        UIManager.put("info", PALE_BLUE);
        UIManager.put("nimbusBase", BLUE);
        UIManager.put("Panel.background", GRAY_BG);
        UIManager.put("OptionPane.background", GRAY_BG);
        UIManager.put("OptionPane.messageFont", FONTE_LABEL);
        UIManager.put("ToolTip.background", NAVY);
        UIManager.put("ToolTip.foreground", WHITE);
        UIManager.put("TabbedPane.selected", PALE_BLUE);
        UIManager.put("TabbedPane.font", FONTE_SUBTITULO);
    }

    /**
     * Percorre recursivamente a árvore de componentes aplicando o estilo visual
     * icechopp (cores, fontes, bordas). Chame uma vez ao final de montarTela().
     */
    public static void estilizar(Component c) {
        if (c instanceof JButton b) {
            estilizarBotao(b);
            return;
        }
        if (c instanceof JTable t) {
            estilizarTabela(t);
            return;
        }
        if (c instanceof JComboBox<?> combo) {
            estilizarCampo(combo);
            return;
        }
        if (c instanceof JTextField || c instanceof JSpinner) {
            estilizarCampo((JComponent) c);
            return;
        }
        if (c instanceof JLabel lbl) {
            if (lbl.getFont() == null || !lbl.getFont().isBold()) {
                lbl.setFont(FONTE_LABEL);
            }
            if (lbl.getForeground() == null || lbl.getForeground().equals(Color.BLACK)) {
                lbl.setForeground(NAVY);
            }
            return;
        }
        if (c instanceof JScrollPane sp) {
            sp.setBorder(BorderFactory.createLineBorder(PALE_BLUE, 2));
        }
        if (c instanceof JPanel p) {
            if (Boolean.TRUE.equals(p.getClientProperty(MARCA_FIXA))) {
                // painel com identidade visual própria (ex.: faixa de título navy) — não sobrescrever
                return;
            }
            if (p.isOpaque()) {
                p.setBackground(GRAY_BG);
            }
            Border border = p.getBorder();
            if (border instanceof TitledBorder tb) {
                tb.setTitleColor(BLUE);
                tb.setTitleFont(FONTE_SUBTITULO);
                tb.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
            }
        }
        if (c instanceof Container cont) {
            for (Component filho : cont.getComponents()) {
                estilizar(filho);
            }
        }
    }

    public static void estilizarBotao(JButton b) {
        boolean destrutivo = b.getText() != null &&
                (b.getText().toLowerCase().contains("excluir") || b.getText().toLowerCase().contains("remover"));
        Color base = destrutivo ? RED_DANGER : BLUE;
        b.setFont(FONTE_BOTAO);
        b.setBackground(base);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        for (java.awt.event.MouseListener m : b.getMouseListeners()) {
            if (m instanceof HoverIceChopp) b.removeMouseListener(m);
        }
        b.addMouseListener(new HoverIceChopp(b, base));
    }

    private static class HoverIceChopp extends MouseAdapter {
        private final JButton botao;
        private final Color corBase;

        HoverIceChopp(JButton botao, Color corBase) {
            this.botao = botao;
            this.corBase = corBase;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            botao.setBackground(corBase.equals(RED_DANGER) ? RED_DANGER.darker() : LIGHT_BLUE);
            botao.setForeground(corBase.equals(RED_DANGER) ? WHITE : NAVY);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            botao.setBackground(corBase);
            botao.setForeground(WHITE);
        }
    }

    public static void estilizarCampo(JComponent campo) {
        campo.setFont(FONTE_LABEL);
        campo.setBackground(WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setFont(FONTE_LABEL);
        tabela.setRowHeight(26);
        tabela.setGridColor(PALE_BLUE);
        tabela.setShowGrid(true);
        tabela.setIntercellSpacing(new Dimension(1, 1));
        tabela.setSelectionBackground(LIGHT_BLUE);
        tabela.setSelectionForeground(NAVY);
        tabela.setFillsViewportHeight(true);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(FONTE_SUBTITULO);
        header.setBackground(NAVY);
        header.setForeground(NAVY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    comp.setBackground(row % 2 == 0 ? WHITE : PALE_BLUE);
                    comp.setForeground(NAVY);
                }
                return comp;
            }
        };
        tabela.setDefaultRenderer(Object.class, renderer);
    }

    /** Rótulo de título de tela/aba, no padrão da marca. */
    public static JLabel criarTitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONTE_TITULO);
        l.setForeground(NAVY);
        l.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return l;
    }

    /** Rótulo de destaque (usado para totais, saldo, etc.). */
    public static JLabel criarLabelDestaque(String texto, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        l.setForeground(cor);
        return l;
    }

    /** Painel de cabeçalho de tela (faixa navy com título branco). */
    public static JPanel criarFaixaTitulo(String texto) {
        JPanel faixa = new JPanel(new BorderLayout());
        faixa.setBackground(NAVY);
        faixa.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        faixa.putClientProperty(MARCA_FIXA, Boolean.TRUE);
        JLabel titulo = new JLabel(texto);
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(WHITE);
        faixa.add(titulo, BorderLayout.WEST);
        return faixa;
    }
}
