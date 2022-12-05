package day_12;

public class Moon {
    
    private final int initialX;
    public int x;
    public int vel_x;
    
    private final int initialY;
    public int y;
    public int vel_y;

    private final int initialZ;
    public int z;
    public int vel_z;
    
    public Moon(int x, int y, int z) {
        this.initialX = x;
        this.x = x;
        
        this.initialY = y;
        this.y = y;
        
        this.initialZ = z;
        this.z = z;
    }
    
    public void reset() {
        x = initialX;
        y = initialY;
        z = initialZ;
        
        vel_x = vel_y = vel_z = 0;
    }
    
    public boolean xMatchesInitial() {
        return (x == initialX && vel_x == 0);
    }
    
    public boolean yMatchesInitial() {
        return (y == initialY && vel_y == 0);
    }
    
    public boolean zMatchesInitial() {
        return (z == initialZ && vel_z == 0);
    }
    
    public int potentialEnergy() {
        return (Math.abs(x) + Math.abs(y) + Math.abs(z));
    }
    
    public int kineticEnergy() {
        return (Math.abs(vel_x) + Math.abs(vel_y) + Math.abs(vel_z));
    }
    
    public int totalEnergy() {
        return potentialEnergy() * kineticEnergy();
    }
    
    public void move() {
        x += vel_x;
        y += vel_y;
        z += vel_z;
    }
}
