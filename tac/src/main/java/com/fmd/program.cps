// Class definition and usage
class Animal {
  let name: string = "Hola";
  let casa: string;

  function constructor(name: string) {
    this.name = name;
  }

  function speak(): string {
    let hello: string = "hi";
    return this.name + " makes a sound.";
  }
}

function hope(): string {
    let hello: string = "hi";
    return " makes a sound.";
}