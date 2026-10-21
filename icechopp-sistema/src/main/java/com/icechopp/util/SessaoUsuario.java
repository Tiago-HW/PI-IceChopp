package com.icechopp.util;

import com.icechopp.model.Usuario;

/**
 * Mantém em memória o usuário atualmente logado no sistema,
 * permitindo que qualquer tela consulte quem está operando
 * e qual o seu time (perfil de acesso).
 */
public class SessaoUsuario {

    private static Usuario usuarioLogado;

    private SessaoUsuario() {
    }

    public static void login(Usuario usuario) {usuarioLogado = usuario;}

    public static void logout() {usuarioLogado = null;}

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static boolean isAdmin() {
        return usuarioLogado != null && "admin".equalsIgnoreCase(usuarioLogado.getTime());
    }

    public static boolean isVendas() {
        return usuarioLogado != null && "vendas".equalsIgnoreCase(usuarioLogado.getTime());
    }

    public static boolean isCompras() {
        return usuarioLogado != null && "compras".equalsIgnoreCase(usuarioLogado.getTime());
    }
}
