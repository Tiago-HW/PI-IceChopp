package com.icechopp;

import com.icechopp.util.UITheme;
import com.icechopp.view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UITheme.aplicarTemaGlobal();

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
