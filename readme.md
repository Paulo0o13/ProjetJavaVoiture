# 🚗 Documentation du Projet : Application de Gestion de Véhicules

## 1. Présentation Générale
Ce projet est une application web développée en **Java avec Spring Boot**. Elle permet à des utilisateurs de se créer un compte, de s'authentifier, et de gérer une collection personnelle de véhicules (voitures).

L'application repose sur un rendu côté serveur (Server-Side Rendering) où les contrôleurs Spring MVC renvoient des vues (probablement via un moteur de template comme Thymeleaf, au vu des retours de type `String` dans les contrôleurs). La gestion de l'état (connexion utilisateur) est maintenue via les sessions HTTP natives de Jakarta EE.

---

## 2. Fonctionnalités Principales
L'application couvre un périmètre fonctionnel simple mais complet autour de l'identité et de la gestion de ressources :

* **Gestion des utilisateurs (Identity & Access) :**
    * Inscription d'un nouvel utilisateur (Pseudo, Mot de passe).
    * Connexion sécurisée par session (Login).
    * Déconnexion avec invalidation de la session (Logout).
* **Gestion des voitures (Ressources métier) :**
    * Ajout d'une nouvelle voiture au catalogue (Marque, Modèle, Couleur, Année).
    * Liaison automatique de la voiture à l'utilisateur actuellement connecté.
    * Consultation de la liste des voitures appartenant **exclusivement** à l'utilisateur connecté.
* **Protection des routes :**
    * Redirection automatique vers la page de connexion si un utilisateur non authentifié tente d'accéder au formulaire d'ajout ou à la liste des voitures.

---

## 3. Stack Technique et Dépendances

* **Langage :** Java
* **Framework Principal :** Spring Boot
* **Architecture Web :** Spring Web (MVC)
* **Persistance des données :** * Spring Data JPA (Hibernate en arrière-plan)
    * Jakarta Persistence API (`jakarta.persistence.*`)
* **Outils de productivité :** Lombok (`@Getter`, `@Setter`) pour réduire le code boilerplate.
* **Gestion de session :** `jakarta.servlet.http.HttpSession`

---

## 4. Architecture de l'Application

L'application respecte globalement le standard **MVC (Model-View-Controller)** couplé à une **architecture en couches (N-Tiers)**, très classique dans l'écosystème Spring.

### 4.1. Modèle de Données (Entités JPA)
La base de données relationnelle s'articule autour de deux tables principales, avec une relation **One-to-Many** (Un utilisateur peut posséder plusieurs voitures) :

* **Entité `User` (Table `users`)** :
    * `pseudo` (String) : Clé primaire (`@Id`). Sert d'identifiant unique.
    * `password` (String) : Mot de passe de l'utilisateur.
* **Entité `Car` (Table `cars`)** :
    * `id` (Long) : Clé primaire auto-générée (`GenerationType.IDENTITY`).
    * Attributs descriptifs : `marque`, `modele`, `couleur`, `annee`.
    * `owner` (User) : Clé étrangère (`user_pseudo`) gérée via une relation `@ManyToOne`.

### 4.2. Flux de données (Architecture N-Tiers)
Les responsabilités sont divisées de la manière suivante :
1.  **Couche Web (Controllers)** : `UserController` et `CarsController` interceptent les requêtes HTTP, valident l'état de la session, appellent la logique métier et retournent le nom de la vue à afficher.
2.  **Couche Métier (Services)** : `CarService` et son implémentation `CarsServiceImpl`. Cette couche est censée encapsuler la logique métier complexe. *(Note : Actuellement, le service agit principalement comme un passe-plat vers le Repository)*.
3.  **Couche Accès aux Données (Repositories)** : Interfaces `UserRepository` et `CarRepository` étendant `JpaRepository`. Elles fournissent toutes les méthodes CRUD de base sans avoir à écrire de requêtes SQL.

---

## 5. Documentation des Composants (API Interne)

### 5.1. Contrôleurs (Controllers)

#### `UserController`
Gère tout le cycle de vie de l'authentification.
* Il utilise directement le `UserRepository` pour vérifier l'existence de l'utilisateur lors du login et pour sauvegarder un nouvel utilisateur lors de l'inscription.
* **Mécanisme de session** : Lors d'un login réussi, l'objet `User` complet est injecté dans la session HTTP sous l'attribut `"loggedUser"`.

#### `CarsController`
Gère les actions liées aux véhicules.
* **Sécurité** : Chaque méthode commence par vérifier si `"loggedUser"` est présent dans la session. Si ce n'est pas le cas, l'utilisateur est redirigé vers `/login`.
* **Particularité architecturale** : Le contrôleur utilise le `CarService` pour la sauvegarde (`saveCar`), mais fait appel directement au `CarRepository` pour la lecture (`findByOwnerPseudo`). *Voir la section "Pistes d'amélioration".*

### 5.2. Repositories

* **`UserRepository`** : Méthodes CRUD standards. La recherche par pseudo se fait naturellement via `findById(pseudo)` puisque le pseudo est la clé primaire.
* **`CarRepository`** : Introduction d'une méthode dérivée de Spring Data JPA : `findByOwnerPseudo(String pseudo)`. Cette méthode permet de générer automatiquement une requête SQL filtrant les voitures en fonction de la clé étrangère de l'utilisateur.

---

## 6. Référentiel des Routes (Endpoints HTTP)

| Méthode HTTP | Endpoint | Description | Vue retournée / Redirection | Action Session |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/register` | Affiche le formulaire d'inscription. | `formRegister` | Aucune |
| **POST** | `/register` | Enregistre un nouvel utilisateur. | `redirect:/login` | Aucune |
| **GET** | `/login` | Affiche le formulaire de connexion. | `formLog` | Aucune |
| **POST** | `/login` | Authentifie l'utilisateur. | `redirect:/cars` (ou `/login?error`) | Ajoute `loggedUser` |
| **GET** | `/logout` | Déconnecte l'utilisateur. | `redirect:/login` | `session.invalidate()` |
| **GET** | `/car` | Affiche le formulaire d'ajout de voiture. | `formCar` | *Nécessite session* |
| **POST** | `/car` | Ajoute une voiture au compte connecté. | `redirect:/cars` | *Nécessite session* |
| **GET** | `/cars` | Liste les voitures de l'utilisateur connecté. | `listCar` | *Nécessite session* |

---

## 7. Sécurité et Gestion d'État (Analyse Critique)

Ce projet propose une implémentation fonctionnelle d'un système d'authentification "fait maison" à but éducatif ou de prototypage.

* **Authentification par Session** : C'est une méthode robuste (Server-Side Sessions) qui évite de manipuler des tokens complexes côté client. L'invalidation à la déconnexion est correctement gérée.
* **Stockage des mots de passe** : Actuellement, la validation se fait par comparaison stricte : `user.getPassword().equals(password)`. Cela implique que les mots de passe sont stockés en clair dans la base de données. *Ceci est à proscrire en production.*

---

## 8. Pistes d'Amélioration (Next Steps)

Bien que l'application soit fonctionnelle, voici quelques recommandations techniques pour la faire évoluer vers les standards de l'industrie :

1.  **Sécurité (Hachage des mots de passe)** : Intégrer `Spring Security` et utiliser `BCryptPasswordEncoder` avant de sauvegarder le `User` dans la base de données.
2.  **Cohérence de l'architecture N-Tiers** : Dans `CarsController`, l'appel direct à `carRepository.findByOwnerPseudo(...)` court-circuite le `CarService`. Il serait préférable de déplacer cette logique métier dans le `CarService`.
3.  **Utilisation de DTOs (Data Transfer Objects)** : Actuellement, les entités JPA (`User`, `Car`) sont directement exposées au contrôleur et potentiellement aux vues. Créer des classes DTO (ex: `UserRegistrationDto`, `CarDto`) permettrait de découpler la base de données de l'interface utilisateur.
4.  **Validation des données** : Ajouter les annotations `jakarta.validation` (`@NotBlank`, `@Size`, etc.) sur les modèles pour s'assurer qu'un utilisateur ne peut pas s'inscrire avec un pseudo vide ou créer une voiture sans marque.
5.  **Nettoyage de code** : Retirer l'import inutilisé `import java.awt.print.Book;` dans le fichier `UserController.java`.