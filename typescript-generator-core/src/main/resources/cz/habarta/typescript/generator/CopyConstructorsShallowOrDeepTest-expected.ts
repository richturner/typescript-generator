
export class User {
    name: string;
    authentication: Authentication;
    childAccount: boolean;
    age: number;
    address: Address;
    addresses: Address[];
    taggedAddresses: { [index: string]: Address };
    groupedAddresses: { [index: string]: Address[] };
    listOfTaggedAddresses: { [index: string]: Address }[];
    orders: PagedList<Order, Authentication>;
    allOrders: PagedList<Order, Authentication>[];
    shape: ShapeUnion;
    shapes: ShapeUnion[];

    constructor(data?: User, deepCopy?: boolean) {
        if (data) {
            this.name = data.name;
            this.authentication = data.authentication;
            this.childAccount = data.childAccount;
            this.age = data.age;
            this.address = deepCopy ? data.address && new Address(data.address, true) : data.address;
            this.addresses = deepCopy ? __copyArray(data.addresses, data => new Address(data, true)) : data.addresses;
            this.taggedAddresses = deepCopy ? __copyObject(data.taggedAddresses, data => new Address(data, true)) : data.taggedAddresses;
            this.groupedAddresses = deepCopy ? __copyObject(data.groupedAddresses, __copyArrayFn(data => new Address(data, true))) : data.groupedAddresses;
            this.listOfTaggedAddresses = deepCopy ? __copyArray(data.listOfTaggedAddresses, __copyObjectFn(data => new Address(data, true))) : data.listOfTaggedAddresses;
            this.orders = deepCopy ? data.orders && new PagedList(data.orders, true, data => data && new Order(data), __identity) : data.orders;
            this.allOrders = deepCopy ? __copyArray(data.allOrders, data => new PagedList(data, true, data => data && new Order(data), __identity)) : data.allOrders;
            this.shape = deepCopy ? Shape.__copyOf(data.shape) : data.shape;
            this.shapes = deepCopy ? __copyArray(data.shapes, item => Shape.__copyOf(item)) : data.shapes;
        }
    }
}

export class Address {
    street: string;
    city: string;

    constructor(data?: Address, deepCopy?: boolean) {
        if (data) {
            this.street = data.street;
            this.city = data.city;
        }
    }
}

export class PagedList<T, A> {
    page: number;
    items: T[];
    additionalInfo: A;
    
    constructor(data?: PagedList<T, A>, deepCopy?: boolean, constructorFnT?: (data: T) => T, constructorFnA?: (data: A) => A, target?: PagedList<T, A>) {
        if (data) {
            this.page = data.page;
            this.items = deepCopy ? __copyArray(data.items, constructorFnT!) : data.items;
            this.additionalInfo = deepCopy ? constructorFnA!(data.additionalInfo) : data.additionalInfo;
        }
    }
}

export class Order {
    id: string;

    constructor(data?: Order, deepCopy?: boolean) {
        if (data) {
            this.id = data.id;
        }
    }
}

export class Shape {
    kind: "square" | "rectangle" | "circle";
    metadata: ShapeMetadata;

    constructor(data?: Shape, deepCopy?: boolean) {
        if (data) {
            this.kind = data.kind;
            this.metadata = deepCopy ? new ShapeMetadata(data.metadata, true) : data.metadata;
        }
    }

    static __copyOf(data: ShapeUnion): ShapeUnion {
        if (!data) {
            return data;
        }
        switch (data.kind) {
            case "square":
                return new Square(data, true);
            case "rectangle":
                return new Rectangle(data, true);
            case "circle":
                return new Circle(data, true);
        }
    }
}

export class ShapeMetadata {
    group: string;

    constructor(data?: ShapeMetadata, deepCopy?: boolean) {
        if (data) {
            this.group = data.group;
        }
    }
}

export class Square extends Shape {
    kind: "square";
    size: number;

    constructor(data?: Square, deepCopy?: boolean) {
        super(data);
        if (data) {
            this.size = data.size;
        }
    }
}

export class Rectangle extends Shape {
    kind: "rectangle";
    width: number;
    height: number;

    constructor(data?: Rectangle, deepCopy?: boolean) {
        super(data);
        if (data) {
            this.width = data.width;
            this.height = data.height;
        }
    }
}

export class Circle extends Shape {
    kind: "circle";
    radius: number;

    constructor(data?: Circle, deepCopy?: boolean) {
        super(data);
        if (data) {
            this.radius = data.radius;
        }
    }
}

export type Authentication = "Password" | "Token" | "Fingerprint" | "Voice";

export type ShapeUnion = Square | Rectangle | Circle;

function __copyArrayFn<T>(copyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, copyFn);
}

function __copyArray<T>(array: T[], copyFn: (item: T) => T): T[] {
    return array && array.map(item => item && copyFn(item));
}

function __copyObjectFn<T>(copyFn: (item: T) => T): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, copyFn);
}

function __copyObject<T>(object: { [index: string]: T }, copyFn: (item: T) => T): { [index: string]: T } {
    if (!object) {
        return object;
    }
    const result: any = {};
    for (const key in object) {
        if (object.hasOwnProperty(key)) {
            const value = object[key];
            result[key] = value && copyFn(value);
        }
    }
    return result;
}

function __identity<T>(value: T): T {
    return value;
}
