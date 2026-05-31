package com.scale.modelModifier.utils.model;

import lombok.AllArgsConstructor;
import org.joml.Vector3f;

// iirc there is a default class for this, but i dont recall the name, and this is stolen from my old project
@AllArgsConstructor
public class Triangle {
    public static final Triangle EMPTY = new Triangle(new Vector3f(0), new Vector3f(0), new Vector3f(0));

    public Vector3f pointA;
    public Vector3f pointB;
    public Vector3f pointC;

    public Vector3f getMax() {
        return new Vector3f(
                Math.max(pointA.x, Math.max(pointB.x, pointC.x)),
                Math.max(pointA.y, Math.max(pointB.y, pointC.y)),
                Math.max(pointA.z, Math.max(pointB.z, pointC.z))
        );
    }

    public Triangle subtract(Vector3f vector3f) {
        // i hate Vector3f, .sub returns AND OVERWRITES if u dont give it a blank destination, why, ew
        return new Triangle(
                pointA.sub(vector3f, new Vector3f()),
                pointB.sub(vector3f, new Vector3f()),
                pointC.sub(vector3f, new Vector3f())
        );
    }

    public Triangle negate() {
        // i hate Vector3f, EVERTHING returns AND OVERWRITES if u dont give it a blank destination, why, ew
        return new Triangle(
                pointA.negate(new Vector3f()),
                pointB.negate(new Vector3f()),
                pointC.negate(new Vector3f())
        );
    }

    public Triangle multiply(Vector3f vector3f) {
        // i hate Vector3f, EVERTHING returns AND OVERWRITES if u dont give it a blank destination, why, ew
        return new Triangle(
                pointA.mul(vector3f, new Vector3f()),
                pointB.mul(vector3f, new Vector3f()),
                pointC.mul(vector3f, new Vector3f())
        );
    }
}
