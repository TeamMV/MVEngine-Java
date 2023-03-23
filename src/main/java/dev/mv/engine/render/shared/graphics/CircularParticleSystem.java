package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.utils.Utils;
import dev.mv.utils.async.PromiseNull;

public class CircularParticleSystem extends ParticleSystem{
    private int direction;
    private int range;
    private Color color = Color.RED;

    public CircularParticleSystem(int x, int y, int maxNum, Shape shape, int direction, int range) {
        super(x, y, maxNum, shape);
        this.direction = direction;
        this.range = range;

        particles.fill(Particle::new);
        new PromiseNull(resolverNull -> {
            particles.forEach(particle -> {
                particle.setShape(shape);
                particle.setSpeed((float) (Math.random() * 5 + 5) * speed);
                particle.setDirection((float) (Math.random() * range + direction));
                particle.setScale((float) (Math.random() * 10 + 10));
                particle.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                particle.setX(x);
                particle.setY(y);
                float angle = (float) (Math.random() * range + direction);
                particle.setxVel((float) (Math.cos(Math.toRadians(90 - angle))) * particle.getSpeed() * 5);
                particle.setyVel((float) (Math.sin(Math.toRadians(90 - angle))) * particle.getSpeed() * 5);
                Utils.await(Utils.sleep(20));
            });
        });
    }

    private void generateParticles() {
        particles.forEach(particle -> {
            particle.update();
            if(particle.isDone()) {
                particle.setShape(shape);
                particle.setSpeed((float) (Math.random() * 3 + 2)); //TODO: include speed var
                particle.setDirection((float) (Math.random() * range + direction));
                particle.setScale((float) (Math.random() * 10 + 5));
                particle.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                particle.setX(x);
                particle.setY(y);
                float angle = (float) (Math.random() * range + direction);
                particle.setxVel((float) (Math.cos(Math.toRadians(90 - angle))) * particle.getSpeed() * 5);
                particle.setyVel((float) (Math.sin(Math.toRadians(90 - angle))) * particle.getSpeed() * 5);
            }
        });
    }

    @Override
    public void draw(DrawContext2D ctx2D) {
        generateParticles();
        particles.forEach(p -> p.draw(ctx2D));
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
