package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;

public class CircularParticleSystem extends ParticleSystem{
    private int direction;
    private int range;
    private Color color = Color.RED;

    public CircularParticleSystem(int x, int y, int maxNum, Shape shape, int direction, int range) {
        super(x, y, maxNum, shape);
        this.direction = direction;
        this.range = range;

        particles.fill(Particle::new);
        particles.forEach(particle -> {
            particle.setShape(shape);
            particle.setSpeed((float) (Math.random() * 3 + 2));
            particle.setDirection((float) (Math.random() * range + direction));
            particle.setScale((float) (Math.random() * 20 + 5));
            particle.setColor(color);
            particle.setX(x);
            particle.setY(y);
            float angle = (float) (Math.random() * range + direction);
            particle.setxVel((float) (particle.getSpeed() / Math.tan(Math.toRadians(angle))));
            particle.setyVel((float) (particle.getSpeed() / Math.cos(Math.toRadians(angle))));
        });
    }

    private void generateParticles() {
        particles.forEach(particle -> {
            particle.update();
            if(particle.isDone()) {
                particle.setShape(shape);
                particle.setSpeed((float) (Math.random() * 3 + 2));
                particle.setDirection((float) (Math.random() * range + direction));
                particle.setScale((float) (Math.random() * 20 + 5));
                particle.setColor(color);
                particle.setX(x);
                particle.setY(y);
                float angle = (float) (Math.random() * range + direction);
                particle.setxVel((float) (particle.getSpeed() / Math.tan(Math.toRadians(angle))));
                particle.setyVel((float) (particle.getSpeed() / Math.cos(Math.toRadians(angle))));
            }
        });
    }

    @Override
    public void draw(DrawContext2D ctx2D) {
        generateParticles();
        particles.forEach(p -> p.draw(ctx2D));
    }
}
