#  Présentation et documentation du Projet 

## Présentation Générale
L'application permet à des utilisateurs de se créer un compte, de s'authentifier, et de gérer une collection personnelle de voitures.

On a mis en place deux entités principales : `User` et `Car`. Chaque utilisateur peut posséder plusieurs voitures, mais chaque voiture est associée à un seul utilisateur.
L'application offre une interface simple pour l'inscription, la connexion, la gestion des voitures, et la consultation de la collection personnelle de véhicules.
On s'est assuré que quelqu'un sans compte ne puisse pas accéder aux fonctionnalités de gestion des voitures, en utilisant une vérification de session dans les contrôleurs.
On s'est aussi assuré que les utilisateurs ne puissent voir que leurs propres voitures, en filtrant les données à partir de la session de l'utilisateur connecté.

Une fois connecté à son compte, l'utilisateur peut ajouter des voitures à sa collection, et consulter la liste de ses voitures. 
Chaque voiture est liée à l'utilisateur qui l'a ajoutée, garantissant ainsi une séparation claire entre les données des différents utilisateurs.

Ce qui était important à comprendre dans ce projet, c'était la gestion endpoints avec PostMapping et le remplissage des données à partir du formulaire.
Dans ce projet, la couche service n'était pas vraiment utilisée, car la logique métier était assez simple et pouvait être gérée directement dans les contrôleurs.
C'est pour ça qu'on a seulement fait CarService et pas UserService.
On pouvait directement injecter les repositories dans les contrôleurs pour gérer les opérations de base sur les données, sans avoir besoin d'une couche de service intermédiaire.

Ensuite il fallait bien comprendre comment remplir les HTML et utiliser les attributs du modèle pour afficher les données dynamiquement dans les vues.

Au niveau de la logique de modélisation des données on a crée dans la table Car une clé étrangère qui référence la table User, pour pouvoir faire le lien entre les voitures et leurs propriétaires.
Chaque User peut avoir une ou plusieurs Car, mais chaque Car appartient à un seul User.

Enfin il a fallu comprendre l'utilisation des sessions pour maintenir l'état de connexion de l'utilisateur à travers les différentes requêtes HTTP, et pour sécuriser l'accès aux fonctionnalités de gestion des voitures.

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

L'application respecte globalement le standard MVC.

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

### Repositories

* **`UserRepository`** : Méthodes CRUD standards. La recherche par pseudo se fait naturellement via `findById(pseudo)` puisque le pseudo est la clé primaire.
* **`CarRepository`** : Méthode : `findByOwnerPseudo(String pseudo)`. Cette méthode permet de générer automatiquement une requête SQL filtrant les voitures en fonction de la clé étrangère de l'utilisateur.

---

## Référentiel des Routes 

| Méthode HTTP | Endpoint | Description | Vue retournée ou redirection                     | 
| :--- | :--- | :--- |:-------------------------------------------------| 
| **GET** | `/register` | Affiche le formulaire d'inscription. | `formRegister`                                   |
| **POST** | `/register` | Enregistre un nouvel utilisateur. | `redirect:/login` ou `redirect:/register?exists` | 
| **GET** | `/login` | Affiche le formulaire de connexion. | `formLog`                                        | 
| **POST** | `/login` | Authentifie l'utilisateur. | `redirect:/cars` ou `/login?error`               |
| **GET** | `/logout` | Déconnecte l'utilisateur. | `redirect:/login`                                | 
| **GET** | `/car` | Affiche le formulaire d'ajout de voiture. | `formCar`                                        |
| **POST** | `/car` | Ajoute une voiture au compte connecté. | `redirect:/cars`                                 | 
| **GET** | `/cars` | Liste les voitures de l'utilisateur connecté. | `listCar`                                        | 

---
