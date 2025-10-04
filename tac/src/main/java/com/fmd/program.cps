// ============================================
// PRUEBAS DE PRIORIDAD 1
// ============================================

// --------------------------------------------
// 1. DECLARACIÓN DE VARIABLES
// --------------------------------------------

let x: integer = 10;
let y: integer = 20;
let z: integer;  // Sin inicializador

let nombre: string = "Juan";
let activo: boolean = true;

// --------------------------------------------
// 2. OPERACIONES ARITMÉTICAS
// --------------------------------------------

// Suma y resta
let suma: integer = 5 + 3;
let resta: integer = 10 - 4;
let compleja: integer = 10 + 5 - 2;

// Multiplicación, división, módulo
let mult: integer = 4 * 5;
let div: integer = 20 / 4;
let mod: integer = 17 % 5;

// Combinadas (respeta precedencia)
let expr1: integer = 2 + 3 * 4;        // 2 + 12 = 14
let expr2: integer = (2 + 3) * 4;      // 5 * 4 = 20
let expr3: integer = 10 + 20 / 2 - 3;  // 10 + 10 - 3 = 17

// --------------------------------------------
// 3. OPERADORES UNARIOS
// --------------------------------------------

let negativo: integer = -10;
let negExpr: integer = -(5 + 3);
let negVar: integer = -x;

let invertido: boolean = !true;
let invertVar: boolean = !activo;

// --------------------------------------------
// 4. ASIGNACIONES SIMPLES
// --------------------------------------------

x = 100;
y = x;
z = x + y;

nombre = "Pedro";
activo = false;

// Reasignaciones múltiples
x = 50;
x = x + 10;
x = x * 2;

// --------------------------------------------
// 5. EXPRESIONES COMPLEJAS
// --------------------------------------------

let temp1: integer = 5 * 3 + 2 * 4;      // 15 + 8 = 23
let temp2: integer = (10 + 5) * (20 - 10); // 15 * 10 = 150
let temp3: integer = -5 * 3 + 10;         // -15 + 10 = -5

// --------------------------------------------
// 6. PRINT STATEMENTS
// --------------------------------------------

print(x);
print(nombre);
print(activo);

print(5 + 3);
print(x + y);
print(-10);

// --------------------------------------------
// 7. LITERALES DE DIFERENTES TIPOS
// --------------------------------------------

let entero: integer = 42;
let texto: string = "Hola Mundo";
let verdadero: boolean = true;
let falso: boolean = false;

print(entero);
print(texto);
print(verdadero);
print(falso);

// --------------------------------------------
// 8. COMBINACIÓN
// --------------------------------------------

let resultado: integer = 0;
resultado = 10;
resultado = resultado + 5;
resultado = resultado * 2;
resultado = resultado - 3;
print(resultado);  // (10 + 5) * 2 - 3 = 27

let total: integer = (100 + 50) / 3 * 2 + 10;
print(total);  // 150 / 3 * 2 + 10 = 50 * 2 + 10 = 110

// --------------------------------------------
// 9. USO DE FUNCIONES
// --------------------------------------------
function speak(name: string): string {
   let printVar: string = " makes a sound.";
   return name + printVar;
}

speak("Firulais");

let name: string = "Alberto";
print(name + " is here.");

for (var j = 0; j < 3; j = j + 1) {
    print(j);
};