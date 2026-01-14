# 📋 CAHIER DES CHARGES
## Convertisseur Excel vers JSON

---

## 1. CONTEXTE ET OBJECTIFS

### 1.1 Contexte
Besoin d'un outil en ligne de commande permettant de convertir automatiquement des fichiers Excel (.xlsx) au format JSON, avec un suivi détaillé du traitement.

### 1.2 Objectifs
- Automatiser la conversion Excel → JSON
- Fournir des statistiques détaillées sur le traitement
- Garantir la fiabilité et la robustesse
- Faciliter l'utilisation via un script bash
- Gérer tous les types de données Excel

### 1.3 Périmètre
**Inclus :**
- Lecture de fichiers .xlsx (Excel 2007+)
- Support multi-feuilles
- Conversion de tous types de cellules (texte, nombre, date, formule, booléen)
- Génération de statistiques
- Interface en ligne de commande

**Exclus :**
- Fichiers .xls (Excel 97-2003) - version 1.0
- Interface graphique
- Conversion inverse (JSON → Excel)
- Modification du fichier Excel source

---

## 2. SPÉCIFICATIONS FONCTIONNELLES

### 2.1 Cas d'usage principal

**Acteur :** Utilisateur (développeur, analyste de données)

**Préconditions :**
- Java 17+ installé
- Fichier Excel .xlsx existant et accessible
- Permissions de lecture sur le fichier source
- Permissions d'écriture sur le répertoire de destination

**Scénario nominal :**
1. L'utilisateur lance le script bash avec les paramètres
2. Le système valide les paramètres d'entrée
3. Le système lit le fichier Excel
4. Le système extrait les données de chaque feuille
5. Le système convertit les données en JSON
6. Le système écrit le fichier JSON
7. Le système affiche les statistiques
8. Le traitement se termine avec succès

**Postconditions :**
- Fichier JSON créé
- Statistiques affichées
- Code de sortie = 0

### 2.2 Cas d'usage secondaires

#### 2.2.1 Affichage de l'aide
- Commande : `./excel-to-json.sh --help`
- Affiche la documentation d'utilisation

#### 2.2.2 Compilation automatique
- Commande : `./excel-to-json.sh --build input.xlsx output.json`
- Compile le projet avant exécution

#### 2.2.3 Vérification de version
- Commande : `./excel-to-json.sh --version`
- Affiche la version de l'application

### 2.3 Exigences fonctionnelles

| ID | Exigence | Priorité |
|----|----------|----------|
| EF-01 | Le système doit lire les fichiers .xlsx | Essentielle |
| EF-02 | Le système doit supporter plusieurs feuilles Excel | Essentielle |
| EF-03 | Le système doit détecter automatiquement les en-têtes | Essentielle |
| EF-04 | Le système doit convertir tous les types de cellules | Essentielle |
| EF-05 | Le système doit ignorer les lignes vides | Importante |
| EF-06 | Le système doit générer un JSON valide et indenté | Essentielle |
| EF-07 | Le système doit afficher des statistiques détaillées | Importante |
| EF-08 | Le système doit valider les fichiers d'entrée | Essentielle |
| EF-09 | Le système doit créer les répertoires manquants | Importante |
| EF-10 | Le système doit évaluer les formules Excel | Souhaitable |

---

## 3. ARCHITECTURE TECHNIQUE

### 3.1 Stack technologique

| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| Framework | Spring Boot | 3.2.0 | Robustesse, injection de dépendances |
| Langage | Java | 17 | Performance, typage fort |
| Build Tool | Maven | 3.6+ | Gestion des dépendances |
| Lecture Excel | Apache POI | 5.2.5 | Standard de l'industrie |
| JSON | Jackson | 2.15+ | Performance, flexibilité |
| Scripting | Bash | 4.0+ | Portabilité Unix/Linux |
| Logging | SLF4J + Logback | 2.0+ | Standard Spring Boot |

### 3.2 Architecture en couches

```
┌─────────────────────────────────────────┐
│     COUCHE PRÉSENTATION (CLI)           │
│  ExcelToJsonApplication (main)          │
│  - Validation des paramètres            │
│  - Affichage des résultats              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│     COUCHE SERVICE (Logique métier)     │
│  ExcelToJsonConverterService            │
│  - Orchestration du traitement          │
│  - Calcul des statistiques              │
│                                          │
│  ExcelReaderService                     │
│  - Lecture du fichier Excel             │
│  - Extraction des données                │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│     COUCHE MODÈLE (Données)             │
│  ExcelData, ExcelSheet                  │
│  ConversionResult                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│     COUCHE INFRASTRUCTURE               │
│  Apache POI (XSSFWorkbook)              │
│  Jackson (ObjectMapper)                 │
│  Système de fichiers                    │
└─────────────────────────────────────────┘
```

---

## 4. MODÈLES DE DONNÉES

### 4.1 Diagramme de classes

```
┌─────────────────────────────┐
│     ExcelData               │
├─────────────────────────────┤
│ - fileName: String          │
│ - totalSheets: int          │
│ - totalRows: int            │
│ - sheets: List<ExcelSheet>  │
├─────────────────────────────┤
│ + getters/setters           │
└──────────────┬──────────────┘
               │ 1
               │
               │ *
┌──────────────▼──────────────┐
│     ExcelSheet              │
├─────────────────────────────┤
│ - sheetName: String         │
│ - headers: List<String>     │
│ - rows: List<Map<String,    │
│         Object>>            │
│ - rowCount: int             │
│ - columnCount: int          │
├─────────────────────────────┤
│ + getters/setters           │
└─────────────────────────────┘

┌─────────────────────────────┐
│   ConversionResult          │
├─────────────────────────────┤
│ - success: boolean          │
│ - outputPath: String        │
│ - outputFileSize: long      │
│ - sheetsProcessed: int      │
│ - totalRows: int            │
│ - rowsConverted: int        │
│ - emptyRowsSkipped: int     │
│ - columnsDetected: int      │
│ - warnings: List<String>    │
├─────────────────────────────┤
│ + addWarning(String)        │
│ + getters/setters           │
└─────────────────────────────┘
```

### 4.2 Structure des données

#### 4.2.1 ExcelData
**Responsabilité :** Représente l'ensemble du fichier Excel

| Attribut | Type | Description | Contraintes |
|----------|------|-------------|-------------|
| fileName | String | Nom du fichier source | Non null, .xlsx |
| totalSheets | int | Nombre total de feuilles | >= 0 |
| totalRows | int | Nombre total de lignes | >= 0 |
| sheets | List\<ExcelSheet\> | Liste des feuilles | Non null |

#### 4.2.2 ExcelSheet
**Responsabilité :** Représente une feuille Excel

| Attribut | Type | Description | Contraintes |
|----------|------|-------------|-------------|
| sheetName | String | Nom de la feuille | Non null |
| headers | List\<String\> | En-têtes des colonnes | Non null |
| rows | List\<Map\<String, Object\>\> | Données des lignes | Non null |
| rowCount | int | Nombre de lignes | >= 0 |
| columnCount | int | Nombre de colonnes | >= 0 |

**Structure d'une ligne (Map) :**
```json
{
  "Colonne1": "valeur1",
  "Colonne2": 123,
  "Colonne3": "2024-01-15",
  "Colonne4": true
}
```

#### 4.2.3 ConversionResult
**Responsabilité :** Résultat et statistiques du traitement

| Attribut | Type | Description | Contraintes |
|----------|------|-------------|-------------|
| success | boolean | Succès de la conversion | true/false |
| outputPath | String | Chemin du fichier JSON | Non null |
| outputFileSize | long | Taille du fichier en octets | >= 0 |
| sheetsProcessed | int | Feuilles traitées | >= 0 |
| totalRows | int | Total de lignes | >= 0 |
| rowsConverted | int | Lignes converties | >= 0 |
| emptyRowsSkipped | int | Lignes vides ignorées | >= 0 |
| columnsDetected | int | Colonnes détectées | >= 0 |
| warnings | List\<String\> | Liste des avertissements | Nullable |

### 4.3 Format JSON de sortie

```json
{
  "fileName": "rapport_ventes.xlsx",
  "totalSheets": 2,
  "totalRows": 145,
  "sheets": [
    {
      "sheetName": "Ventes 2024",
      "headers": ["Date", "Produit", "Quantité", "Prix Unitaire", "Total"],
      "rows": [
        {
          "Date": "2024-01-15",
          "Produit": "Ordinateur Portable",
          "Quantité": 5,
          "Prix Unitaire": 1200.50,
          "Total": 6002.50
        },
        {
          "Date": "2024-01-16",
          "Produit": "Souris",
          "Quantité": 25,
          "Prix Unitaire": 15.99,
          "Total": 399.75
        }
      ],
      "rowCount": 100,
      "columnCount": 5
    },
    {
      "sheetName": "Clients",
      "headers": ["ID", "Nom", "Email", "Actif"],
      "rows": [
        {
          "ID": 1,
          "Nom": "Dupont",
          "Email": "dupont@example.com",
          "Actif": true
        }
      ],
      "rowCount": 45,
      "columnCount": 4
    }
  ]
}
```

---

## 5. GESTION DES TYPES DE CELLULES

### 5.1 Mapping des types Excel → JSON

| Type Excel | Type POI | Type Java | Type JSON | Exemple Excel | Exemple JSON |
|------------|----------|-----------|-----------|---------------|--------------|
| Texte | STRING | String | string | "Bonjour" | "Bonjour" |
| Nombre entier | NUMERIC | Long | number | 42 | 42 |
| Nombre décimal | NUMERIC | Double | number | 3.14 | 3.14 |
| Date | NUMERIC + DateFormat | String | string | 15/01/2024 | "2024-01-15" |
| Booléen | BOOLEAN | Boolean | boolean | VRAI | true |
| Formule | FORMULA | Évaluée | variable | =A1+B1 | 150 |
| Vide | BLANK | null | null | (vide) | null |
| Erreur | ERROR | null | null | #DIV/0! | null |

### 5.2 Règles de conversion

#### 5.2.1 Nombres
```
Excel: 42.0000
↓ Détection
Si nombre entier (42.0 == 42)
  → JSON: 42 (Long)
Sinon
  → JSON: 42.5 (Double)
```

#### 5.2.2 Dates
```
Excel: 15/01/2024 ou 2024-01-15
↓ Détection via DateUtil.isCellDateFormatted(cell)
↓ Format: SimpleDateFormat("yyyy-MM-dd")
→ JSON: "2024-01-15"
```

#### 5.2.3 Formules
```
Excel: =SUM(A1:A10)
↓ Évaluation via FormulaEvaluator
↓ Obtention du résultat calculé
→ JSON: 1250 (ou autre type selon le résultat)
```

#### 5.2.4 Cellules vides
```
Excel: (cellule vide)
↓ Détection
→ JSON: null
```

---

## 6. GESTION DES ERREURS

### 6.1 Taxonomie des erreurs

```
Erreurs
├── Erreurs de validation (avant traitement)
│   ├── Paramètres manquants
│   ├── Fichier inexistant
│   ├── Extension invalide
│   ├── Permissions insuffisantes
│   └── Répertoire de sortie inaccessible
│
├── Erreurs de lecture (pendant traitement)
│   ├── Fichier Excel corrompu
│   ├── Format non supporté
│   ├── Erreur de lecture I/O
│   ├── Mémoire insuffisante
│   └── Formule non évaluable
│
└── Erreurs d'écriture (fin de traitement)
    ├── Espace disque insuffisant
    ├── Permissions d'écriture refusées
    ├── Erreur de sérialisation JSON
    └── Chemin de sortie invalide
```

### 6.2 Catalogue des erreurs

| Code | Type | Message | Cause | Action | Code sortie |
|------|------|---------|-------|--------|-------------|
| E001 | Validation | "Nombre d'arguments incorrect" | < 2 paramètres | Afficher usage | 1 |
| E002 | Validation | "Le fichier d'entrée n'existe pas" | Fichier absent | Vérifier chemin | 1 |
| E003 | Validation | "Extension .xlsx requise" | Mauvaise extension | Renommer fichier | 1 |
| E004 | Validation | "Impossible de lire le fichier" | Permissions | chmod +r | 1 |
| E005 | Validation | "Extension .json requise pour sortie" | Mauvaise extension | Corriger nom | 1 |
| E006 | Validation | "Java non installé" | Java absent | Installer Java 17+ | 1 |
| E007 | Validation | "Version Java insuffisante" | Java < 17 | Mettre à jour Java | 1 |
| E008 | Validation | "JAR non trouvé" | Compilation manquante | mvn package | 1 |
| E009 | Lecture | "Fichier Excel corrompu" | Format invalide | Vérifier fichier | 2 |
| E010 | Lecture | "Erreur de lecture I/O" | Problème disque | Vérifier disque | 2 |
| E011 | Lecture | "Mémoire insuffisante" | Fichier trop gros | Augmenter heap | 2 |
| E012 | Écriture | "Impossible de créer le répertoire" | Permissions | chmod +w | 3 |
| E013 | Écriture | "Espace disque insuffisant" | Disque plein | Libérer espace | 3 |
| E014 | Écriture | "Erreur d'écriture JSON" | Sérialisation | Vérifier données | 3 |

### 6.3 Stratégies de gestion

#### 6.3.1 Erreurs bloquantes (Exception)
```java
try {
    validateInputFile(inputPath);
} catch (Exception e) {
    log.error("Validation échouée: {}", e.getMessage());
    System.err.println("❌ ERREUR : " + e.getMessage());
    System.exit(1);
}
```

#### 6.3.2 Avertissements (Warning)
```java
if (emptyRowsSkipped > 0) {
    result.addWarning(emptyRowsSkipped + " ligne(s) vide(s) ignorées");
    log.warn("Lignes vides ignorées: {}", emptyRowsSkipped);
}
```

#### 6.3.3 Erreurs silencieuses (Log)
```java
try {
    evaluateFormula(cell);
} catch (Exception e) {
    log.warn("Formule non évaluable: {}", e.getMessage());
    return null; // Continuer le traitement
}
```

### 6.4 Codes de sortie

| Code | Signification | Cas d'usage |
|------|---------------|-------------|
| 0 | Succès | Conversion réussie |
| 1 | Erreur de validation | Paramètres invalides, fichier absent |
| 2 | Erreur de lecture | Fichier corrompu, mémoire insuffisante |
| 3 | Erreur d'écriture | Permissions, espace disque |
| 99 | Erreur inconnue | Exception non gérée |

---

## 7. FLUX DE TRAITEMENT

### 7.1 Diagramme de séquence global

```
Utilisateur    Script Bash    Application Java    ExcelReader    JsonConverter    Filesystem
    │              │                 │                   │               │             │
    │  Commande    │                 │                   │               │             │
    ├─────────────>│                 │                   │               │             │
    │              │                 │                   │               │             │
    │              │  1. Validation  │                   │               │             │
    │              ├────────────────>│                   │               │             │
    │              │                 │                   │               │             │
    │              │  2. Lancement   │                   │               │             │
    │              ├────────────────>│                   │               │             │
    │              │                 │                   │               │             │
    │              │                 │  3. Lecture Excel │               │             │
    │              │                 ├──────────────────>│               │             │
    │              │                 │                   │               │             │
    │              │                 │                   │  4. Ouvrir    │             │
    │              │                 │                   ├──────────────────────────>│
    │              │                 │                   │               │             │
    │              │                 │                   │  5. Lire      │             │
    │              │                 │                   │<──────────────────────────┤
    │              │                 │                   │               │             │
    │              │                 │  6. Données       │               │             │
    │              │                 │<──────────────────┤               │             │
    │              │                 │                   │               │             │
    │              │                 │  7. Conversion JSON               │             │
    │              │                 ├──────────────────────────────────>│             │
    │              │                 │                   │               │             │
    │              │                 │                   │               │  8. Écrire  │
    │              │                 │                   │               ├────────────>│
    │              │                 │                   │               │             │
    │              │                 │  9. Résultat      │               │             │
    │              │                 │<──────────────────────────────────┤             │
    │              │                 │                   │               │             │
    │              │  10. Stats      │                   │               │             │
    │              │<────────────────┤                   │               │             │
    │              │                 │                   │               │             │
    │  Affichage   │                 │                   │               │             │
    │<─────────────┤                 │                   │               │             │
```

### 7.2 Algorithme détaillé

```
DÉBUT Programme
│
├─> VALIDATION DES PARAMÈTRES
│   │
│   ├─> Si nbArgs ≠ 2
│   │   └─> Afficher usage → QUITTER(1)
│   │
│   ├─> Si Java absent
│   │   └─> Erreur "Java non installé" → QUITTER(1)
│   │
│   ├─> Si Java < 17
│   │   └─> Erreur "Version insuffisante" → QUITTER(1)
│   │
│   ├─> Si JAR absent
│   │   └─> Erreur "Compiler d'abord" → QUITTER(1)
│   │
│   ├─> Validation fichier entrée
│   │   ├─> Si fichier n'existe pas → QUITTER(1)
│   │   ├─> Si non lisible → QUITTER(1)
│   │   └─> Si extension ≠ .xlsx → QUITTER(1)
│   │
│   └─> Validation chemin sortie
│       ├─> Si extension ≠ .json → QUITTER(1)
│       └─> Si répertoire n'existe pas → CRÉER
│
├─> LECTURE DU FICHIER EXCEL
│   │
│   ├─> Ouvrir FileInputStream
│   │   └─> Si erreur I/O → Exception → QUITTER(2)
│   │
│   ├─> Créer XSSFWorkbook
│   │   └─> Si fichier corrompu → Exception → QUITTER(2)
│   │
│   ├─> POUR chaque feuille (i = 0 à nbSheets-1)
│   │   │
│   │   ├─> Obtenir Sheet(i)
│   │   │
│   │   ├─> Lire première ligne (en-têtes)
│   │   │   ├─> POUR chaque cellule
│   │   │   │   └─> headers.add(valeur)
│   │   │   └─> Si header vide → "Column_N"
│   │   │
│   │   ├─> POUR chaque ligne (j = 2 à dernièreLigne)
│   │   │   │
│   │   │   ├─> Si ligne vide
│   │   │   │   └─> emptyRowsSkipped++ → CONTINUER
│   │   │   │
│   │   │   ├─> Créer Map<String, Object>
│   │   │   │
│   │   │   ├─> POUR chaque colonne (k = 0 à nbColonnes-1)
│   │   │   │   │
│   │   │   │   ├─> Obtenir cellule(j, k)
│   │   │   │   │
│   │   │   │   ├─> Déterminer type cellule
│   │   │   │   │   ├─> STRING → String
│   │   │   │   │   ├─> NUMERIC
│   │   │   │   │   │   ├─> Si date → formater "yyyy-MM-dd"
│   │   │   │   │   │   └─> Sinon
│   │   │   │   │   │       ├─> Si entier → Long
│   │   │   │   │   │       └─> Sinon → Double
│   │   │   │   │   ├─> BOOLEAN → Boolean
│   │   │   │   │   ├─> FORMULA → évaluer → type résultat
│   │   │   │   │   └─> BLANK → null
│   │   │   │   │
│   │   │   │   └─> map.put(header[k], valeur)
│   │   │   │
│   │   │   └─> rows.add(map)
│   │   │
│   │   └─> excelSheets.add(sheet)
│   │
│   └─> Fermer workbook
│
├─> CONVERSION EN JSON
│   │
│   ├─> Créer structure JSON
│   │   {
│   │     "fileName": nom,
│   │     "totalSheets": nb,
│   │     "totalRows": total,
│   │     "sheets": excelSheets
│   │   }
│   │
│   ├─> Configurer ObjectMapper
│   │   ├─> Indentation activée
│   │   └─> Dates sans timestamp
│   │
│   └─> Écrire JSON dans fichier
│       └─> Si erreur → Exception → QUITTER(3)
│
├─> CALCUL DES STATISTIQUES
│   │
│   ├─> sheetsProcessed = nbSheets
│   ├─> totalRows = somme(rowCount par sheet)
│   ├─> rowsConverted = somme(rows.size() par sheet)
│   ├─> emptyRowsSkipped = totalRows - rowsConverted
│   ├─> columnsDetected = max(columnCount par sheet)
│   └─> outputFileSize = taille fichier JSON
│
├─> GÉNÉRATION DES AVERTISSEMENTS
│   │
│   ├─> Si emptyRowsSkipped > 0
│   │   └─> warnings.add("N lignes vides ignorées")
│   │
│   └─> Si feuilles vides détectées
│       └─> warnings.add("M feuilles vides")
│
└─> AFFICHAGE DES RÉSULTATS
    │
    ├─> Afficher statistiques
    │   ├─> Nombre de feuilles
    │   ├─> Total de lignes
    │   ├─> Lignes converties
    │   ├─> Lignes vides ignorées
    │   ├─> Colonnes détectées
    │   ├─> Temps d'exécution
    │   └─> Taille du fichier
    │
    ├─> Afficher avertissements (si présents)
    │
    └─> QUITTER(0) ✅ Succès

FIN Programme
```

---

## 8. EXIGENCES NON FONCTIONNELLES

### 8.1 Performance

| Critère | Objectif | Mesure |
|---------|----------|--------|
| Fichier < 10 MB | < 5 secondes | Temps total |
| Fichier 10-50 MB | < 30 secondes | Temps total |
| Fichier > 50 MB | < 2 minutes | Temps total |
| Mémoire RAM | < 512 MB | Heap Java |
| CPU | < 80% | Utilisation moyenne |

### 8.2 Fiabilité

- **Disponibilité** : 99.9% (dépend de l'infrastructure)
- **Taux d'erreur acceptable** : < 0.1% des conversions
- **Récupération sur erreur** : Messages clairs + codes de sortie
- **Intégrité des données** : 100% (validation JSON)

### 8.3 Sécurité

| Aspect | Mesure |
|--------|--------|
| Validation des entrées | Vérification extension, existence, permissions |
| Injection | Pas d'exécution de code externe |
| Données sensibles | Pas de logging de données métier |
| Permissions fichiers | Respect des permissions système |

### 8.4 Maintenabilité

- **Code coverage** : > 70% (si tests unitaires)
- **Complexité cyclomatique** : < 10 par méthode
- **Documentation** : Javadoc sur classes publiques
- **Convention de nommage** : Java standard (camelCase)
- **Logging** : SLF4J avec niveaux appropriés

### 8.5 Portabilité

| OS | Support | Version |
|----|---------|---------|
| Linux | ✅ Complet | Toutes distributions |
| macOS | ✅ Complet | 10.14+ |
| Windows | ⚠️ Partiel | Via Git Bash / WSL |

### 8.6 Scalabilité

**Limitations actuelles :**
- Fichiers > 100 MB : Performance dégradée
- Mémoire : Fichier chargé intégralement

**Améliorations futures (v2.0) :**
- Streaming pour gros fichiers (SXSSFWorkbook)
- Traitement par lots
- Mode multi-thread

---

## 9. TESTS ET VALIDATION

### 9.1 Stratégie de tests

```
Tests
├── Tests unitaires (JUnit 5)
│   ├── ExcelReaderService
│   │   ├── testReadSimpleFile()
│   │   ├── testReadMultipleSheets()
│   │   ├── testReadEmptySheet()
│   │   ├── testReadFormulas()
│   │   └── testReadDifferentTypes()
│   │
│   └── ExcelToJsonConverterService
│       ├── testSuccessfulConversion()
│       ├── testStatisticsCalculation()
│       └── testWarningGeneration()
│
├── Tests d'intégration
│   ├── testEndToEndConversion()
│   ├── testMultipleSheetsConversion()
│   └── testLargeFileConversion()
│
└── Tests de validation
    ├── testInvalidInputFile()
    ├── testMissingPermissions()
    ├── testInvalidOutputPath()
    └── testCorruptedFile()
```

### 9.2 Cas de tests

#### 9.2.1 Tests fonctionnels

| ID | Cas de test | Données d'entrée | Résultat attendu | Priorité |
|----|-------------|------------------|------------------|----------|
| TC-01 | Fichier simple 1 feuille | 10 lignes, 4 colonnes | JSON valide, stats OK | Haute |
| TC-02 | Fichier multi-feuilles | 3 feuilles, 100 lignes total | JSON avec 3 sheets | Haute |
| TC-03 | Fichier avec lignes vides | 20 lignes dont 5 vides | 15 lignes converties, warning | Moyenne |
| TC-04 | Fichier avec formules | =SUM(), =IF(), etc. | Formules évaluées | Moyenne |
| TC-05 | Fichier avec dates | Dates variées | Format "yyyy-MM-dd" | Haute |
| TC-06 | Fichier avec types mixtes | String, Number, Boolean | Types corrects en JSON | Haute |
| TC-07 | Fichier vide | 0 ligne de données | JSON vide, warning | Basse |
| TC-08 | Feuille sans en-têtes | Données sans première ligne | Headers = "Column_N" | Moyenne |

#### 9.2.2 Tests d'erreurs

| ID | Scénario d'erreur | Entrée | Comportement attendu | Code sortie |
|----|-------------------|--------|----------------------|-------------|
| TE-01 | Fichier inexistant | /fake/path.xlsx | Message erreur + usage | 1 |
| TE-02 | Extension invalide | fichier.xls | "Extension .xlsx requise" | 1 |
| TE-03 | Permissions lecture | chmod 000 fichier.xlsx | "Impossible de lire" | 1 |
| TE-04 | Fichier corrompu | Fichier binaire renommé | "Fichier Excel corrompu" | 2 |
| TE-05 | Espace disque plein | Disque à 100% | "Espace insuffisant" | 3 |
| TE-06 | Paramètres manquants | 0 ou 1 paramètre | Afficher usage | 1 |
| TE-07 | Java absent | PATH sans Java | "Java non installé" | 1 |

#### 9.2.3 Tests de performance

| ID | Scénario | Taille fichier | Nb lignes | Temps max | Mémoire max |
|----|----------|----------------|-----------|-----------|-------------|
| TP-01 | Petit fichier | 100 KB | 100 | 2s | 100 MB |
| TP-02 | Fichier moyen | 5 MB | 5,000 | 10s | 250 MB |
| TP-03 | Gros fichier | 50 MB | 50,000 | 60s | 512 MB |
| TP-04 | Multi-feuilles | 10 MB | 10,000 (5 sheets) | 15s | 300 MB |

### 9.3 Jeux de données de test

#### Fichier test 1 : `test_simple.xlsx`
```
Feuille : Données
┌──────────┬──────────┬──────────┬──────────┐
│   Nom    │   Age    │   Ville  │   Actif  │
├──────────┼──────────┼──────────┼──────────┤
│  Alice   │    25    │  Paris   │   true   │
│  Bob     │    30    │  Lyon    │   false  │
│  Charlie │    35    │  Marseille│  true   │
└──────────┴──────────┴──────────┴──────────┘
```

#### Fichier test 2 : `test_types.xlsx`
```
Feuille : Types Variés
┌──────────┬──────────┬──────────┬──────────┬──────────┐
│  Texte   │  Entier  │  Décimal │   Date   │ Booléen  │
├──────────┼──────────┼──────────┼──────────┼──────────┤
│  Hello   │    42    │   3.14   │01/01/2024│   true   │
│  World   │   100    │  99.99   │15/06/2024│   false  │
└──────────┴──────────┴──────────┴──────────┴──────────┘
```

#### Fichier test 3 : `test_formulas.xlsx`
```
Feuille : Formules
┌──────────┬──────────┬──────────┬──────────┐
│    A     │    B     │  Total   │  Moyenne │
├──────────┼──────────┼──────────┼──────────┤
│   10     │   20     │ =A2+B2   │ =(A2+B2)/2│
│   30     │   40     │ =A3+B3   │ =(A3+B3)/2│
└──────────┴──────────┴──────────┴──────────┘
```

---

## 10. LIVRABLES

### 10.1 Code source

```
Livrables
├── Code Java
│   ├── ExcelToJsonApplication.java
│   ├── model/
│   │   ├── ExcelData.java
│   │   ├── ExcelSheet.java
│   │   └── ConversionResult.java
│   └── service/
│       ├── ExcelReaderService.java
│       └── ExcelToJsonConverterService.java
│
├── Configuration
│   ├── pom.xml
│   └── application.properties
│
├── Scripts
│   └── excel-to-json.sh
│
└── Documentation
    ├── README.md
    ├── CAHIER_DES_CHARGES.md (ce document)
    └── JAVADOC/ (généré par Maven)
```

### 10.2 Artefacts de build

- **JAR exécutable** : `excel-to-json-converter-1.0.0.jar`
- **Sources** : `excel-to-json-converter-1.0.0-sources.jar`
- **Documentation** : `excel-to-json-converter-1.0.0-javadoc.jar`

### 10.3 Documentation

- Guide d'installation (README.md)
- Guide d'utilisation
- Documentation API (Javadoc)
- Ce cahier des charges

---

## 11. PLANIFICATION ET PHASES

### 11.1 Phase 1 : Conception (Fait ✅)
- Analyse des besoins
- Architecture technique
- Modélisation des données
- Cahier des charges

### 11.2 Phase 2 : Développement
**Durée estimée : 2-3 jours**

| Tâche | Durée | Dépendances |
|-------|-------|-------------|
| Setup projet Maven | 2h | - |
| Modèles de données | 2h | Setup |
| ExcelReaderService | 6h | Modèles |
| ExcelToJsonConverterService | 4h | ExcelReader |
| Application principale | 4h | Services |
| Script Bash | 3h | Application |
| Tests unitaires | 4h | Toutes |

### 11.3 Phase 3 : Tests et validation
**Durée estimée : 1 jour**

- Tests unitaires
- Tests d'intégration
- Tests de performance
- Validation manuelle

### 11.4 Phase 4 : Documentation et déploiement
**Durée estimée : 0.5 jour**

- Finalisation README
- Génération Javadoc
- Guide utilisateur
- Package de release

---

## 12. ÉVOLUTIONS FUTURES (v2.0)

### 12.1 Fonctionnalités envisagées

| Fonctionnalité | Priorité | Complexité | Impact |
|----------------|----------|------------|--------|
| Support .xls (ancien format) | Moyenne | Faible | Compatibilité |
| Mode streaming (gros fichiers) | Haute | Moyenne | Performance |
| Format CSV en sortie | Basse | Faible | Flexibilité |
| Interface graphique | Basse | Haute | UX |
| API REST | Moyenne | Moyenne | Intégration |
| Conversion inverse (JSON→Excel) | Moyenne | Moyenne | Bidirectionnalité |
| Support multi-langues | Basse | Moyenne | i18n |
| Configuration avancée (YAML) | Moyenne | Faible | Flexibilité |
| Mode batch (plusieurs fichiers) | Haute | Moyenne | Productivité |
| Export vers base de données | Basse | Haute | Intégration |

### 12.2 Améliorations techniques

- Migration vers Java 21 (Virtual Threads)
- Support GraalVM (binaire natif)
- Cache des conversions
- Pool de threads pour multi-sheets
- Compression JSON (gzip)
- Validation JSON Schema

---

## 13. CONTRAINTES ET RISQUES

### 13.1 Contraintes

| Type | Contrainte | Impact |
|------|------------|--------|
| Technique | Java 17+ requis | Installation préalable |
| Technique | Mémoire limitée (< 512 MB) | Taille fichiers limitée |
| Fonctionnelle | Format .xlsx uniquement | Pas de support .xls |
| Environnement | Script bash (Unix/Linux) | Pas natif Windows |
| Performance | Fichier chargé en mémoire | Limite ~100 MB |

### 13.2 Risques

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| Fichier Excel corrompu | Moyenne | Moyen | Validation + gestion erreur |
| Mémoire insuffisante | Faible | Élevé | Documentation limites |
| Formules complexes non évaluables | Moyenne | Faible | Retourner null + log |
| Incompatibilité version POI | Faible | Moyen | Tests + version fixée |
| Performances sur gros fichiers | Moyenne | Moyen | Documentation + SXSSFWorkbook v2 |

---

## 14. GLOSSAIRE

| Terme | Définition |
|-------|------------|
| **Apache POI** | Bibliothèque Java pour manipuler les fichiers Microsoft Office |
| **XSSFWorkbook** | Classe POI pour les fichiers Excel .xlsx |
| **Sheet** | Feuille dans un classeur Excel |
| **Row** | Ligne dans une feuille Excel |
| **Cell** | Cellule dans une ligne Excel |
| **Workbook** | Classeur Excel (fichier complet) |
| **Jackson** | Bibliothèque Java de sérialisation/désérialisation JSON |
| **ObjectMapper** | Classe Jackson pour convertir objets ↔ JSON |
| **Try-with-resources** | Syntaxe Java pour fermer automatiquement les ressources |
| **CommandLineRunner** | Interface Spring Boot pour exécuter du code au démarrage |
| **Artifact** | Fichier produit par Maven (JAR, sources, javadoc) |

---

## 15. CRITÈRES D'ACCEPTATION

### 15.1 Critères fonctionnels

- ✅ Le système convertit correctement un fichier .xlsx en JSON
- ✅ Les statistiques affichées sont exactes
- ✅ Les types de données sont préservés
- ✅ Les lignes vides sont ignorées
- ✅ Les formules sont évaluées
- ✅ Le JSON généré est valide et indenté
- ✅ Les messages d'erreur sont clairs

### 15.2 Critères techniques

- ✅ Code respecte les conventions Java
- ✅ Pas de warning à la compilation
- ✅ Logging approprié (INFO, WARN, ERROR)
- ✅ Gestion propre des ressources (fermeture fichiers)
- ✅ Code commenté (Javadoc sur classes publiques)

### 15.3 Critères de performance

- ✅ Fichier 5 MB converti en < 10 secondes
- ✅ Utilisation mémoire < 512 MB
- ✅ Pas de fuite mémoire

### 15.4 Critères d'utilisabilité

- ✅ Script bash facile à utiliser
- ✅ Messages d'aide clairs (--help)
- ✅ Affichage formaté et lisible
- ✅ Documentation complète (README)

---

## 16. ANNEXES

### 16.1 Commandes Maven utiles

```bash
# Compiler
mvn clean compile

# Packager
mvn clean package

# Exécuter les tests
mvn test

# Générer la Javadoc
mvn javadoc:javadoc

# Analyser les dépendances
mvn dependency:tree

# Vérifier les mises à jour
mvn versions:display-dependency-updates
```

### 16.2 Variables d'environnement

```bash
# Augmenter la mémoire heap Java
export JAVA_OPTS="-Xmx1024m -Xms512m"

# Activer le debugging
export JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Logs en mode DEBUG
export LOGGING_LEVEL_ROOT=DEBUG
```

### 16.3 Dépendances Maven (versions)

```xml
<!-- Apache POI -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- Jackson -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.3</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

---

**Document approuvé par :** [Nom]  
**Date :** [Date]  
**Version :** 1.0  
**Statut :** Validé ✅
