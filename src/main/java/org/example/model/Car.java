package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String marque;
    public String modele;
    public String couleur;
    public int annee;

    private Double prix;
    private String typeOffre;
    private boolean disponible = true;


    @ManyToOne
    @JoinColumn(name = "user_pseudo")
    private User owner;

}
