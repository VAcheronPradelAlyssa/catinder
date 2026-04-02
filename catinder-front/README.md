# Catinder - Frontend 🐱

Application de swipe de chats avec Angular 21 standalone components.

## Démarrage rapide

```bash
npm install
npm start
```

Naviguez vers `http://127.0.0.1:4200/`

## Features

- 🐾 Authentification (register/login) avec JWT
- 😺 Visualisation de profils de chats avec âge et description
- ❤️ Like/Dislike avec système de match
- 🎨 Design cat-themed avec emoji et animations
- 📱 Interface responsive

## Architecture

- **Pages**: login, register, swipe
- **Services**: auth, cat (appels API to backend)
- **Composants standalone** avec reactive forms
- **Styles SCSS** avec animations et transitions

## Configuration

L'API backend démarre sur `http://localhost:8080` (configurable dans `environment.ts`)

## Build

```bash
npm run build
```

Les fichiers compilés se trouvent dans `dist/`

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
