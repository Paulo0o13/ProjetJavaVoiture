#  Présentation et documentation du Projet 

## Présentation Générale
L'application permet à des utilisateurs de se créer un compte, de s'authentifier, et de gérer une collection personnelle de voitures.


---

## Fonctionnalités Principales

* **Gestion des utilisateurs  :**
    * Inscription d'un nouvel utilisateur (Pseudo, Mot de passe).
    * Connexion sécurisée par session (Login).
    * Déconnexion .
* **Gestion des voitures :**
    * Ajout d'une nouvelle voiture au catalogue (Marque, Modèle, Couleur, Année).
    * Liaison automatique de la voiture à l'utilisateur actuellement connecté.
    * Consultation de la liste des voitures appartenant **exclusivement** à l'utilisateur connecté.

---

## Architecture de l'Application

L'application respecte globalement le standard **MVC (Model-View-Controller)** couplé à une **architecture en couches (N-Tiers)**, très classique dans l'écosystème Spring.

###  Modèle de Données 
La base de données relationnelle s'articule autour de deux tables principales, avec une relation **One-to-Many** (Un utilisateur peut posséder plusieurs voitures) :

* **Entité `User` (Table `users`)** :
    * `pseudo` (String) : Clé primaire (`@Id`). Sert d'identifiant unique.
    * `password` (String) : Mot de passe de l'utilisateur.
* **Entité `Car` (Table `cars`)** :
    * `id` (Long) : Clé primaire auto-générée (`GenerationType.IDENTITY`).
    * Attributs descriptifs : `marque`, `modele`, `couleur`, `annee`.
    * `owner` (User) : Clé étrangère (`user_pseudo`) gérée via une relation `@ManyToOne`.

### Flux de données 
Les responsabilités sont divisées de la manière suivante :
1.  **Couche Web (Controllers)** : `UserController` et `CarsController` interceptent les requêtes HTTP, valident l'état de la session, appellent la logique métier et retournent le nom de la vue à afficher.
2.  **Couche Métier (Services)** : `CarService` et son implémentation `CarsServiceImpl`. Cette couche est censée encapsuler la logique métier complexe.
3.  **Couche Données (Repositories)** : Interfaces `UserRepository` et `CarRepository` étendant `JpaRepository`. Elles fournissent toutes les méthodes CRUD de base sans avoir à écrire de requêtes SQL.

---

## Documentation des Composants 

### Contrôleurs 

#### `UserController`
Gère tout le cycle de vie de l'authentification.
* Il utilise directement le `UserRepository` pour vérifier l'existence de l'utilisateur lors du login et pour sauvegarder un nouvel utilisateur lors de l'inscription.
* **Mécanisme de session** : Lors d'un login réussi, l'objet `User` complet est injecté dans la session HTTP sous l'attribut `"loggedUser"`.

#### `CarsController`
Gère les actions liées aux véhicules.
* **Sécurité** : Chaque méthode commence par vérifier si `"loggedUser"` est présent dans la session. Si ce n'est pas le cas, l'utilisateur est redirigé vers `/login`.
* **Particularité architecturale** : Le contrôleur utilise le `CarService` pour la sauvegarde (`saveCar`), mais fait appel directement au `CarRepository` pour la lecture (`findByOwnerPseudo`). *Voir la section "Pistes d'amélioration".*

### Repositories

* **`UserRepository`** : Méthodes CRUD standards. La recherche par pseudo se fait naturellement via `findById(pseudo)` puisque le pseudo est la clé primaire.
* **`CarRepository`** : Introduction d'une méthode dérivée de Spring Data JPA : `findByOwnerPseudo(String pseudo)`. Cette méthode permet de générer automatiquement une requête SQL filtrant les voitures en fonction de la clé étrangère de l'utilisateur.

---

## Référentiel des Routes 

| Méthode HTTP | Endpoint | Description | Vue retournée ou redirection        | 
| :--- | :--- | :--- |:------------------------------------| 
| **GET** | `/register` | Affiche le formulaire d'inscription. | `formRegister`                      |
| **POST** | `/register` | Enregistre un nouvel utilisateur. | `redirect:/login`                   | 
| **GET** | `/login` | Affiche le formulaire de connexion. | `formLog`                           | 
| **POST** | `/login` | Authentifie l'utilisateur. | `redirect:/cars` ou `/login?error`  |
| **GET** | `/logout` | Déconnecte l'utilisateur. | `redirect:/login`                   | 
| **GET** | `/car` | Affiche le formulaire d'ajout de voiture. | `formCar`                           |
| **POST** | `/car` | Ajoute une voiture au compte connecté. | `redirect:/cars`                    | 
| **GET** | `/cars` | Liste les voitures de l'utilisateur connecté. | `listCar`                           | 

---
