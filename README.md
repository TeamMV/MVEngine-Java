# MVEngine

Game engine/framework made in Java.

Editor will be added in a different project, this is just the rendering, gui and physics framework.

This is a graphical game engine (aka with editor) that can be used to create 2D and 3D games in java. We will make it,
so you don't need to know java to code in it. Kotlin support will be added as well. But you will also be able to create
node-like scripts (like unreal blueprints) instead of coding it in java.

Modern JRE (Java runtime environment) is almost if not just as fast as native code, since it uses JIT compilation.
Therefore, speed is not really an issue with using Java over C++. We might make it in C++ later, after we are done with
this. For now, I think this is adequate to be a good game engine.

Example code of how we plan this to work:

```java
public class PlayerScript implements GameObjectScript {

    @Override
    public void start(GameObject object) {
        object.setPosition(new Vector3f(0, 0, 0));
    }

    @Override
    public void update(GameObject object) {
        if (Input.isKeyDown(Key.W)) {
            object.setVelocity(new Vector3f(1f, 0, 0));
        }
    }
}

//AUTO GENERATED CLASSES
public class Player extends GameObject {
    private List<GameObjectScript> scripts = new ArrayList<>();

    @Override
    public void start() {
        super.start();
        scripts.forEach(script -> script.start(this));
    }

    @Override
    public void update() {
        super.update();
        scripts.forEach(script -> script.update(this));
    }

    public void addScript(GameObjectScript script) {
        scripts.add(script);
    }

    public void removeScript(GameObjectScript script) {
        scripts.remove(script);
    }
}

//ENGINE CLASSES

public interface GameObjectScript {
    void start(GameObject object);

    void update(GameObject object);
}

public class GameObject {
    //FUNCTION DEFINITIONS HERE
}

//On Game Launch
public class GameLauncher {

    public void launch() {
        //Get game objects from some data or code
        List<GameObject> gameObjects = new ArrayList<>();
        for (DefinedObject object : Data.getDefinedObjects()) {
            gameObjects.add(object.getGameObject());
            object.getGameObject().addScript(gameObjects.getScript());
        }
    }

}
```

Using the nodes, you can also do the same thing, editing the script through the node editor, this will have two started
nodes: start and update. You can do whatever with those nodes, and then make the script generate java code from the
nodes.
