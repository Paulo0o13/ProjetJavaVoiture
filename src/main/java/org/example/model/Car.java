package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Getter
@Setter
public class Car {

    public String marque;
    public String modele;
    public String couleur;
    public int annee;

    public Car(String marque, String modele, String couleur, int annee) {
        this.marque = marque;
        this.modele = modele;
        this.couleur = couleur;
        this.annee = annee;
    }
}
