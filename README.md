# Generador de Código de Tres Direcciones (TAC)

Un generador robusto de **Three-Address Code** para el lenguaje **Compiscript**, diseñado para traducir código fuente en una representación intermedia optimizada que facilita la generación de código máquina.

---

## ¿Qué es TAC?

**Three-Address Code (TAC)** es una representación intermedia de bajo nivel que descompone expresiones complejas en instrucciones simples donde cada operación tiene como máximo tres direcciones:

```
resultado = operando1 operador operando2
```


## Características Principales

###  Generación Completa de TAC

- **Expresiones Aritméticas**: `+`, `-`, `*`, `/`, `%`
- **Expresiones Lógicas**: `&&`, `||`, `!`
- **Comparaciones**: `==`, `!=`, `<`, `<=`, `>`, `>=`
- **Asignaciones**: Variables, constantes, arreglos, propiedades
- **Llamadas a Funciones**: Con parámetros y valores de retorno
- **Operaciones Ternarias**: Condiciones con `? :`

### Control de Flujo

- **Condicionales**: `if-else` con etiquetas y saltos
- **Bucles**: `while`, `do-while`, `for`, `foreach`
- **Switch**: Con múltiples cases y default
- **Saltos**: `break`, `continue`, `goto`
- **Excepciones**: `try-catch` con handlers

###  Programación Orientada a Objetos

- **Clases**: Declaración y métodos
- **Constructores**: Con parámetros e inicialización
- **Herencia**: Soporte completo
- **Instanciación**: `new` con llamadas a constructores
- **Acceso a Miembros**: Propiedades y métodos con dot notation

### Gestión de Memoria

- **Temporales**: Generación automática de variables temporales (`t1`, `t2`, ...)
- **Etiquetas**: Creación única de etiquetas (`L1`, `L2`, ...)
- **Offsets**: Cálculo automático de desplazamientos en memoria
- **Tabla de Símbolos**: Integración con análisis semántico

---

## Prioridades, el proyecto esta pensado de la siguiente forma, así fue como se implementaron las funcionalidades del proyecto 

### 🟢 Prioridad 1: BÁSICO 

- Literales (integer, string, boolean, null)
- Identificadores
- Operaciones aritméticas (+, -, *, /, %)
- Declaración de variables (let, var)
- Asignación simple
- Print

### 🟡 Prioridad 2: CONTROL DE FLUJO

- Comparaciones (<, >, <=, >=, ==, !=)
- Operaciones lógicas (&&, ||, !)
- If-else
- While
- For
- Do-while

### 🟠 Prioridad 3: FUNCIONES

- Declaración de funciones
- Llamadas a funciones
- Parámetros
- Return
- Recursión

### 🔴 Prioridad 4: AVANZADO

- Arrays y acceso a índices
- Foreach
- Switch-case
- Break/Continue
- Try-catch
- Constantes (const)

### 🟣 Prioridad 5: POO 

- Clases
- Constructores
- Herencia
- this
- new
- Acceso a propiedades
- Closures
>

---
## Estructura del Proyecto

```
tac/
├── src/
│   ├── main/
│   │   ├── java/com/fmd/
│   │   │   ├── Main.java                    # Punto de entrada principal
│   │   │   ├── TACVisitor.java              # Coordinador principal del TAC
│   │   │   ├── TACGenerator.java            # Generador de TAC y estado compartido
│   │   │   ├── TACExprVisitor.java          # Visitor para expresiones
│   │   │   ├── TACStmtVisitor.java          # Visitor para statements
│   │   │   ├── TACFuncsVisitor.java         # Visitor para funciones
│   │   │   ├── SemanticVisitor.java         # Visitor de análisis semántico
│   │   │   ├── CompiscriptLexer.java        # Lexer generado por ANTLR4
│   │   │   ├── CompiscriptParser.java       # Parser generado por ANTLR4
│   │   │   ├── CompiscriptBaseVisitor.java  # Base visitor generado por ANTLR4
│   │   │   ├── modules/
│   │   │   │   ├── TACInstruction.java      # Representación de instrucciones TAC
│   │   │   │   ├── Symbol.java              # Representación de símbolos
│   │   │   │   └── SemanticError.java       # Sistema de errores semánticos
│   │   │   └── program.cps                  # Archivo de ejemplo Compiscript
│   │   └── antlr4/
│   │       └── Compiscript.g4               # Gramática ANTLR4 completa
│   │
│   └── test/
│       └── java/com/fmd/
│           ├── TestInit.java                #  Utilidades base para testing
│           │
│           ├── P1Tests.java                 #  Tests Prioridad 1: Básicos
│           │
│           ├── P2Tests.java                 #  Tests Prioridad 2: Control de Flujo
│           │
│           ├── P3Tests.java                 #  Tests Prioridad 3: Funciones
│           │
│           ├── P4Tests.java                 #  Tests Prioridad 4: Avanzados
│           │
│           ├── P5Tests.java                 #  Tests Prioridad 5: POO
│           │
│           ├── MatrixTests.java            #  Tests de Matrices
│           │
│           ├── OffsetTests.java            #  Tests de Gestión de Memoria
│           │
│           ├── TryCatchTests.java          #  Tests de Excepciones
│           │
│           └── SwitchBreakTests.java       #  Tests de Switch y Break
│
├── pom.xml                                  # Configuración Maven
└── README.md                                # Documentación del proyecto TAC
```
## Arquitectura del Sistema

### Componentes Principales

```
tac/
├── TACVisitor.java          # Coordinador principal
├── TACGenerator.java        # Generador y estado compartido
├── TACExprVisitor.java      # Visitor para expresiones
├── TACStmtVisitor.java      # Visitor para statements
├── TACFuncsVisitor.java     # Visitor para funciones
└── modules/
    └── TACInstruction.java  # Representación de instrucciones
```

### Patrón de Diseño

El sistema utiliza el **patrón Visitor** con responsabilidades bien definidas:

```
┌─────────────────┐
│   TACVisitor    │ ← Punto de entrada
└────────┬────────┘
         │
    ┌────┴────┐
    │ Generator│ ← Estado compartido
    └────┬────┘
         │
    ┌────┴──────┬──────────┬───────────┐
    │           │          │           │
┌───▼────┐ ┌───▼────┐ ┌──▼─────┐ ┌───▼────┐
│ Expr   │ │ Stmt   │ │ Funcs  │ │ Classes│
│Visitor │ │Visitor │ │Visitor │ │Visitor │
└────────┘ └────────┘ └────────┘ └────────┘
```

### Flujo de Generación

1. **Análisis Semántico** → Validación del código fuente
2. **Inicialización** → Crear `TACVisitor` con tabla de símbolos
3. **Recorrido AST** → Visitar cada nodo del árbol
4. **Generación TAC** → Emitir instrucciones de tres direcciones
5. **Actualización** → Completar tabla de símbolos con offsets y direcciones
6. **Salida** → Imprimir TAC e información de símbolos

---

## Tipos de Instrucciones TAC

### Instrucciones Básicas

| Tipo | Formato | Descripción | Ejemplo |
|------|---------|-------------|---------|
| **ASSIGN** | `x = y` | Asignación simple | `t1 = 5` |
| **BINARY_OP** | `x = y op z` | Operación binaria | `t2 = t1 + 3` |
| **UNARY_OP** | `x = op y` | Operación unaria | `t3 = -t2` |
| **LABEL** | `L1:` | Etiqueta | `L1:` |
| **GOTO** | `goto L` | Salto incondicional | `goto L2` |
| **IF_GOTO** | `if x relop y goto L` | Salto condicional | `if t1 < 10 goto L1` |

### Instrucciones Avanzadas

| Tipo | Formato | Descripción | Ejemplo |
|------|---------|-------------|---------|
| **CALL** | `call f(a, b)` | Llamada a función sin retorno | `call print(t1)` |
| **ASSIGN_CALL** | `x = call f(a, b)` | Llamada con retorno | `t2 = call suma(5, 3)` |
| **RETURN** | `return x` | Retorno de función | `return t5` |
| **NEW** | `x = new Class(args)` | Instanciación de objeto | `t1 = new Perro("Rex")` |
| **END** | `end f` | Fin de función | `end suma` |

### Instrucciones de Excepciones

| Tipo | Formato | Descripción |
|------|---------|-------------|
| **TRY_BEGIN** | `try_begin Lcatch` | Inicio de bloque try |
| **TRY_END** | `try_end` | Fin de bloque try |

---

## Instalación y Configuración

### Prerrequisitos

- **JDK 17** o superior
- **Maven 3.6+**
- **Análisis Semántico** completado correctamente

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/Fabiola-cc/TAC.git
   cd tac
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar main**
   ```bash
   mvn exec:java '-Dexec.mainClass=com.fmd.Main' '-Dexec.args=src\main\java\com\fmd\program.cps'
   ```

4. **Desplegar el sevidor para usar usado por el IDE**
   ```bash
   mvn spring-boot:run
   ```

5. **Ejecutar tests**
   ```bash
   mvn test
   ```

---

## Testing

### Estructura de Tests

El módulo TAC incluye tests comprehensivos para validar la correcta generación de código:

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=TestInit
```

### Categorías de Tests

-  **Expresiones**: Aritméticas, lógicas, comparaciones
-  **Control de Flujo**: If-else, loops, switch
-  **Funciones**: Declaración, llamadas, return
-  **POO**: Clases, constructores, herencia
-  **Arreglos**: Inicialización, acceso, foreach
-  **Excepciones**: Try-catch, manejo de errores

---

## Integración con Análisis Semántico

### Dependencias

El generador TAC **requiere** que el análisis semántico se complete sin errores:

```java
// Análisis semántico primero
SemanticVisitor semanticVisitor = new SemanticVisitor();
semanticVisitor.visit(tree);

// Verificar errores
if (!semanticVisitor.getErrores().isEmpty()) {
    System.out.println("¡No se puede generar TAC con errores semánticos!");
    return;
}

// Solo entonces generar TAC
TACVisitor tacVisitor = new TACVisitor(semanticVisitor.getExistingScopes());
tacVisitor.visit(tree);
```

### Información Compartida

El TAC utiliza la **tabla de símbolos** del análisis semántico para:

- Resolver tipos de variables
- Calcular offsets en memoria
- Validar existencia de funciones y clases
- Obtener información de parámetros

### Actualización de Símbolos

Durante la generación TAC, se actualizan los símbolos con:

```java
Symbol variable = generator.getSymbol("x");
variable.setTacAddress("x");           // Dirección en TAC
variable.setOffset(0);                 // Offset en memoria
variable.setSize(4);                   // Tamaño en bytes
```

---

## Salida del Sistema

### Ejemplo de Salida Completa

```
 CÓDIGO FUENTE 
let x: integer = 5;
let y: integer = x + 3;
print(y);


 ANÁLISIS SEMÁNTICO 

✓ No hay errores semánticos

 GENERACIÓN DE TAC 

TAC GENERADO

  0: t1 = 5
  1: x = t1
  2: t2 = 3
  3: t3 = x + t2
  4: y = t3
  5: t4 = y
  6: call print(t4)

 TABLA DE SÍMBOLOS ACTUALIZADA 

===== TABLAS DE SÍMBOLOS POR SCOPE =====
Scope (2 symbols)
  VARIABLE x:integer [addr=x, size=4, offset=0] (line 1:0)
  VARIABLE y:integer [addr=y, size=4, offset=4] (line 2:0)

========================================
```


## Notas Técnicas

### Convenciones de Nombres

- **Temporales**: `t1`, `t2`, `t3`, ... (únicos en todo el programa)
- **Etiquetas**: `L1`, `L2`, `L3`, ... (únicas en todo el programa)
- **Variables**: Mismo nombre que en código fuente
- **Funciones**: Mismo nombre que en código fuente

### Gestión de Memoria

```
┌──────────────────────┐
│   Stack Frame        │
├──────────────────────┤
│ Return Address       │
│ Previous Frame Ptr   │
├──────────────────────┤
│ Parámetros           │
│   param1 (offset: 0) │
│   param2 (offset: 4) │
├──────────────────────┤
│ Variables Locales    │
│   var1   (offset: 8) │
│   var2   (offset: 12)│
└──────────────────────┘
```

### Tamaños de Tipos

| Tipo | Tamaño (bytes) | Descripción |
|------|----------------|-------------|
| `boolean` | 1 | Valor booleano |
| `integer` | 4 | Entero de 32 bits |
| `string` | 8 | Referencia a cadena |
| `Class` | 8 | Referencia a objeto |
| `Array` | 8 | Referencia a arreglo |

---

## Equipo de Desarrollo

- **Fabiola Contreras** - 22787
- **Diego Duarte** - 22075
- **María José Villafuerte** - 22129

---

## Repositorios Adicionales

- [Fase semántica importada en este proyecto](https://github.com/DiegoDuaS/Checker)
- [IDE funcional para el pryecto](https://github.com/DiegoDuaS/ANTLR-IDE)

