package eitc.pucmm.controladores;

import io.javalin.Javalin;

public abstract class BaseControladora {
    protected Javalin app;

    public BaseControladora(Javalin app){
        this.app = app;
    }

    public void aplicarRutas() {

    }
}
